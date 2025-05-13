package com.yc.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "security.exclude")
public class ExcludePathConfig {
    private List<String> urls = new ArrayList<>();
}