package io.datadynamics.prometheus.micrometer.controller;

import ch.qos.logback.core.util.NetworkAddressUtil;
import io.datadynamics.prometheus.micrometer.prometheus.PushGateway;
import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.model.registry.Collector;
import io.prometheus.metrics.model.registry.MultiCollector;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PrometheusService {

    @Value("${app.prometheus.job-name}")
    private String prometheusJobName;

    @Value("${app.prometheus.grouping-key}")
    private String prometheusGroupingKey;

    @Value("${app.prometheus.push-gateway.url}")
    private String prometheusPushGatewayUrl;

    @Autowired
    private PrometheusRegistry registry;

    private PushGateway pushGateway;

    private Map<String, Object> metricMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void postConstruct() throws Exception {
        this.pushGateway = PushGateway.builder().address(prometheusPushGatewayUrl)  // Push Gateway의 주소 (기본값: localhost:9091)
                .groupingKey("instance", NetworkAddressUtil.getLocalHostName()) // instance="instance1.dd.io"
                .groupingKey("app_name", prometheusGroupingKey)
                .job(prometheusJobName) // 작업(Job) 이름
                .registry(registry)
                .build();
    }

    public Counter getCounter(String name) {
        if (metricMap.containsKey(name)) {
            if (metricMap.get(name) instanceof Counter) {
                return (Counter) metricMap.get(name);
            } else {
                throw new NullPointerException("해당 이름의 Metric은 Counter가 아닙니다. Type : " + metricMap.get(name).getClass().getName());
            }
        }
        throw new NullPointerException("해당 이름의 Metric가 존재하지 않습니다.");
    }

    public Counter getCounterOrNew(String name, String help) {
        if (metricMap.containsKey(name)) {
            if (metricMap.get(name) instanceof Counter) {
                return (Counter) metricMap.get(name);
            } else {
                throw new NullPointerException("해당 이름의 Metric은 Counter가 아닙니다. Type : " + metricMap.get(name).getClass().getName());
            }
        }
        Counter counter = newCounter(name, help);
        metricMap.put(name, counter);
        return counter;
    }

    public Counter newCounter(String name, String help) {
        return Counter.builder()
                .name(name)
                .help(help)
                .register(registry);
    }

    public void push() throws IOException {
        pushGateway.push();
    }

    public void push(Collector collector) throws IOException {
        pushGateway.push(collector);
    }

    public void push(MultiCollector collector) throws IOException {
        pushGateway.push(collector);
    }

    public void pushAdd() throws IOException {
        pushGateway.pushAdd();
    }

    public void pushAdd(Collector collector) throws IOException {
        pushGateway.pushAdd(collector);
    }

    public void pushAdd(MultiCollector collector) throws IOException {
        pushGateway.pushAdd(collector);
    }

    public void delete() throws IOException {
        pushGateway.delete();
    }
}
