package org.upyog.dpdp.sdk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.upyog.dpdp.sdk.client.DpdpCoreClient;
import org.upyog.dpdp.sdk.interceptor.DpdpHandlerInterceptor;

import java.time.Duration;

@AutoConfiguration
@ConditionalOnProperty(prefix = "dpdp", name = "enabled", havingValue = "true")
public class DpdpAutoConfiguration {

    @Value("${dpdp.tenant-id:pg}")
    private String tenantId;

    @Value("${dpdp.core-url:http://localhost:8096/upyog-dpdp-service}")
    private String coreUrl;

    @Value("${dpdp.enforcement.consent:true}")
    private boolean enforceConsent;

    @Value("${dpdp.enforcement.purpose:true}")
    private boolean enforcePurpose;

    @Value("${dpdp.fail-mode:FAIL_CLOSED}")
    private String failMode;

    @Value("${dpdp.request-timeout-ms:800}")
    private int timeoutMs;

    @Bean
    public RestTemplate dpdpRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(timeoutMs))
                .setReadTimeout(Duration.ofMillis(timeoutMs))
                .build();
    }

    @Bean
    public DpdpCoreClient dpdpCoreClient(RestTemplate dpdpRestTemplate) {
        return new DpdpCoreClient(dpdpRestTemplate, coreUrl, timeoutMs);
    }

    @Bean
    public DpdpHandlerInterceptor dpdpHandlerInterceptor(DpdpCoreClient dpdpCoreClient) {
        return new DpdpHandlerInterceptor(true, enforceConsent, enforcePurpose,
                DpdpHandlerInterceptor.FailMode.valueOf(failMode), tenantId, dpdpCoreClient);
    }

    @Bean
    public WebMvcConfigurer dpdpWebMvcConfigurer(DpdpHandlerInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(interceptor);
            }
        };
    }
}
