package com.msp31.storage1c.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "storage1c.unpack")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class UnpackProperties {
    String root;
    String v8unpackPath;
}
