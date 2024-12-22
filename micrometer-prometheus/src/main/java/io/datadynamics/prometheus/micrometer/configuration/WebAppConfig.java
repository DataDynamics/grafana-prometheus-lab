package io.datadynamics.prometheus.micrometer.configuration;

import io.micrometer.core.instrument.util.IOUtils;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@OpenAPIDefinition(
        info = @Info(
                title = "Micrometer Prometheus API",
                version = "1.0.0",
                description = "Micrometer Prometheus API"
        )
)
@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/5.18.2/")
                .resourceChain(false)
                .addResolver(new PathResourceResolver())
                .addTransformer(new IndexPageTransformer());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // /로 접속하면 Swagger Open API 페이지가 표시되도록 리다이렉트 규칙 추가
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
    }

    public static class IndexPageTransformer implements ResourceTransformer {

        private String overwriteDefaultUrl(String html) {
            return html.replace("https://petstore.swagger.io/v2/swagger.json", "/api/v1/api-docs");
        }

        @Override
        public Resource transform(HttpServletRequest httpServletRequest, Resource resource, ResourceTransformerChain resourceTransformerChain) throws IOException {
            if (resource.getURL().toString().endsWith("/swagger-ui/index.html")) {
                String html = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
                html = overwriteDefaultUrl(html);
                return new TransformedResource(resource, html.getBytes());
            } else {
                return resource;
            }
        }
    }

}