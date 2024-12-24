package io.datadynamics.prometheus.micrometer;

import io.datadynamics.prometheus.micrometer.prometheus.PushGateway;
import io.prometheus.metrics.core.metrics.Gauge;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;

public class PushGatewayTest {

    public static void main(String[] args) throws IOException {
        // Push Gateway 생성
        PushGateway pushGateway = PushGateway.builder()
                .address("localhost:9091")  // Push Gateway의 주소 (기본값: localhost:9091)
                .groupingKey("instance", "instance1.dd.io") // instance="instance1.dd.io"
                .groupingKey("a", "b") // a="b"
                .job("example_push_job")     // 작업(Job) 이름
                .build();

        // Gauge 메트릭 생성
        Gauge exampleGauge = Gauge.builder()
                .withoutExemplars()
                .name("example_processed_items")
                .help("Number of items processed in this job run")
                .register();

        try {
            exampleGauge.set(RandomUtils.secure().randomInt(1, 100));
            pushGateway.pushAdd();
        } catch (IOException e) {
            // Ignored
        }
    }

}