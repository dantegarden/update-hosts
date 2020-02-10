package com.dantegarden.updatehosts.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: lij
 * @create: 2020-02-10 21:05
 */
@Component
@ConfigurationProperties(prefix = "git")
@Data
public class GitSource {
    private String username;
    private String password;
}
