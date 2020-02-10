package com.dantegarden.updatehosts.service;

import cn.hutool.core.collection.CollectionUtil;
import com.dantegarden.updatehosts.crawler.JobProcessor;
import com.dantegarden.updatehosts.entity.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: lij
 * @create: 2020-02-08 23:14
 */
@Service
public class HostsService {

    @Autowired
    public JobProcessor jobProcessor;

    public void doCrawler(List<Domain> domainList){
        if(CollectionUtil.isEmpty(domainList))
            return;

        domainList.forEach(domain -> {
            jobProcessor.spider(domain);
        });
    }
}
