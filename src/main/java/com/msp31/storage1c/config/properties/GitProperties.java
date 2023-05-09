package com.msp31.storage1c.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "storage1c.git")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class GitProperties {
    String root;
    String fileDownloadUrl;
    String fileDownloadKey;
}
