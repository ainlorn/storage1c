package com.msp31.storage1c.config;

import com.msp31.storage1c.module.git.Git;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitConfig {
    @Value("${storage1c.git.root}")
    private String rootPath;

    @Bean
    public Git git() {
        return new Git(rootPath);
    }
}
