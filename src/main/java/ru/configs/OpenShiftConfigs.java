package ru.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component

@ConfigurationProperties("openshift")
public class OpenShiftConfigs {
    private String host;
    private String[] namespaces;
    private String[] passwords;
    private String token;
}
