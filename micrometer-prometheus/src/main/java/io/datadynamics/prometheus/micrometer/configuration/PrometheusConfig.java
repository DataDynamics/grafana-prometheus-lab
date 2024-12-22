package io.datadynamics.prometheus.micrometer.configuration;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.metrics.exporter.pushgateway.PushGateway;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class PrometheusConfig {

    @Value("${app.prometheus.job-name}")
    private String prometheusJobName;

    @Value("${app.prometheus.grouping-key}")
    private String prometheusGroupingKey;

    @Value("${app.prometheus.pushgateway-url}")
    private String prometheusPushGatewayUrl;

    private Map<String, String> groupingKey = new HashMap<>();

    private PushGateway pushGateway;

    private CollectorRegistry collectorRegistry;

    @PostConstruct
    public void init() {

/*
        PushGateway pg = PushGateway.builder()
                .address(prometheusPushGatewayUrl)
                .job(prometheusJobName)
                .registry(registry)
                .build();

        pushGateway = new PushGateway(prometheusPushGatewayUrl);
        groupingKey.put(prometheusGroupingKey, prometheusJobName);
        PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry();
        PrometheusRegistry prometheusRegistry = prometheusMeterRegistry.getPrometheusRegistry();
        Metrics.globalRegistry.add(prometheusMeterRegistry);

        pushGateway.pushAdd(collectorRegistry, prometheusJobName, groupingKey);
*/

    }

}
