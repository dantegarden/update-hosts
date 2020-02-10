package com.dantegarden.updatehosts.crawler;

import cn.hutool.core.collection.CollectionUtil;
import com.dantegarden.updatehosts.dao.DomainRepository;
import com.dantegarden.updatehosts.entity.Domain;
import com.dantegarden.updatehosts.service.ProgressHolder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @description:
 * @author: lij
 * @create: 2020-02-09 01:12
 */
@Component
@Setter
@Slf4j
public class MyPipeline implements Pipeline {

    private List<Domain> domainList;
    @Autowired
    private DomainRepository domainDao;
    @Autowired
    private ProgressHolder progressHolder;

    @Override
    public void process(ResultItems resultItems, Task task) {
        HashSet<String> ips = (HashSet<String>)resultItems.get("ip");
        if(CollectionUtil.isNotEmpty(ips)){
            String ipsStr = ips.stream().reduce((s1,s2) -> s1.concat(",").concat(s2)).get();
            Domain domain = DomainContext.getDomain(task.getUUID());
            log.info("domain:{}, ips:{}", domain.getHost(), ipsStr);
            //保存
            domain.setIp(ipsStr);
            domain.setUpdateDate(new Date());
            domainDao.save(domain);
            DomainContext.removeDomain(task.getUUID());
            progressHolder.go();
        }
    }
}
