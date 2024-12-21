package io.datadynamics.prometheus.micrometer;

import io.prometheus.client.Gauge;
import io.prometheus.metrics.exporter.pushgateway.PushGateway;
import io.prometheus.metrics.model.snapshots.Unit;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;

public class PushGatewayTest {

    private static PushGateway pushGateway = PushGateway.builder()
            .address("localhost:9091") // not needed as localhost:9091 is the default
            .job("example")
            .build();

    private static Gauge dataProcessedInBytes = Gauge.build()
            .name("data_processed")
            .help("data processed in the last batch job run")
            .unit(Unit.BYTES.toString())
            .register();

    public static void main(String[] args) throws IOException {
        try {
            long bytesProcessed = processData();
            dataProcessedInBytes.set(bytesProcessed);
        } finally {
            pushGateway.push();
        }
    }

    public static long processData() {
        return RandomUtils.secure().randomInt(1, 100);
    }

}
