package io.datadynamics.prometheus.micrometer;

import io.prometheus.metrics.core.metrics.Gauge;
import io.prometheus.metrics.exporter.pushgateway.HttpConnectionFactory;
import io.prometheus.metrics.exporter.pushgateway.PushGateway;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushGatewayTest {

    public static void main(String[] args) throws IOException {
        // Push Gateway 생성
        PushGateway pushGateway = PushGateway.builder()
                .address("localhost:9091")  // Push Gateway의 주소 (기본값: localhost:9091)
                .groupingKey("instance", "instance1.dd.io")
                .groupingKey("a", "b") // a="b"
                .groupingKey("c", "e") // c="e"
                .connectionFactory(new HttpConnectionFactory() {
                    @Override
                    public HttpURLConnection create(URL url) throws IOException {
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(1000);
                        conn.setReadTimeout(5000);
                        return conn;
                    }
                })
                .job("example_push_job")     // 작업(Job) 이름
                .build();

        // Gauge 메트릭 생성
        Gauge exampleGauge = Gauge.builder()
                .withoutExemplars()
                .name("example_processed_items")
                .help("Number of items processed in this job run")
                .register();

        while (true) {
            try {
                exampleGauge.set(processData());
                pushGateway.pushAdd();
            } catch (IOException e) {
                // Ignored
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
    }

    public static long processData() {
        return RandomUtils.secure().randomInt(1, 100);
    }
}