package org.upyog.dpdp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI dpdpOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("upyog-dpdp-service")
                .version("1.0.0")
                .description("UPYOG DPDP core capability APIs"));
    }
}
