package com.dantegarden.updatehosts.crawler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.dantegarden.updatehosts.entity.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.*;

/**
 * @description:
 * @author: lij
 * @create: 2020-02-09 00:07
 */
@Component
public class JobProcessor implements PageProcessor {

    private static final String IP_ADDRESS_URL = "https://{}.ipaddress.com/{}";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36";

    @Autowired
    private MyPipeline pipeline;

    //负责解析页面
    @Override
    public void process(Page page) { //page是发起请求获取到的页面
        List<Selectable> ipList = page.getHtml().css("table li", "text").regex(".*\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}.*").nodes();
        if(CollectionUtil.isNotEmpty(ipList)){
            Set<String> ips = new HashSet<>();
            for (Selectable selectable : ipList) {
                ips.add(selectable.toString());
            }
            page.putField("ip", ips);
        }
    }

    private Site site = Site.me()
            .setCharset("utf8")
            .setUserAgent(USER_AGENT)
            .setTimeOut(5000) //超时时间
            .setRetrySleepTime(500) //重试间隔时间
            .setRetryTimes(3); //重试次数

    //爬虫配置
    @Override
    public Site getSite() {
        return site;
    }

    //爬虫入口
    public void spider(Domain domain){
        String targetUrl = getTargetUrl(domain.getHost());
        String uuid = UUID.randomUUID().toString();
        DomainContext.setDomain(uuid, domain);
        Spider.create(new JobProcessor())
                .addUrl(targetUrl)  //设置爬取的页面
                .addPipeline(pipeline)
                .setUUID(uuid) //设置spider唯一标识
//                .thread(3)
                .run();
    }

    private String getTargetUrl(String host) {
        List<String> arr = new ArrayList<>();
        String hostsPrefix = getHostsPrefix(host);
        arr.add(hostsPrefix);
        arr.add((!host.startsWith("www.") && StrUtil.count(host, ".") >= 2)? getHostsSuffix(host): "");
        return StrUtil.format(IP_ADDRESS_URL, arr.toArray());
    }

    public String getHostsPrefix(String host){
        String hostWithoutPrefix = host;
        if(host.startsWith("www.")){
            hostWithoutPrefix = StrUtil.removePrefix(host, "www.");
        }

        if(StrUtil.count(hostWithoutPrefix, ".") >= 2){
            hostWithoutPrefix = StrUtil.subAfter(hostWithoutPrefix, ".", false);
        }
        return hostWithoutPrefix;
    }

    public String getHostsSuffix(String host){
        String hostWithoutSuffix = host;
        if(host.startsWith("www.")){
            hostWithoutSuffix = StrUtil.removePrefix(host, "www.");
        }
        return hostWithoutSuffix;
    }
}
