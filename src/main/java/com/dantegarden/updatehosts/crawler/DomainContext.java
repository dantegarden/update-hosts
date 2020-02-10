package com.dantegarden.updatehosts.crawler;

import com.dantegarden.updatehosts.entity.Domain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: lij
 * @create: 2020-02-09 12:26
 */
public class DomainContext {
    private static Map<String, Domain> domainHolder = new ConcurrentHashMap<>();

    public static void setDomain(String uuid, Domain domain) {
        domainHolder.put(uuid, domain);
    }

    public static Domain getDomain(String uuid) {
        return domainHolder.get(uuid);
    }
    public static Domain removeDomain(String uuid){
        return domainHolder.remove(uuid);
    }
    public static void clear(){
        domainHolder.clear();
    }
}
