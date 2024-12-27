package io.datadynamics.prometheus.micrometer.job;

import io.datadynamics.prometheus.micrometer.controller.PrometheusService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class PrometheusJob implements Job {

    @Autowired
    PrometheusService prometheusService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Push metrics to Prometheus");

        try {
            prometheusService.pushAdd();
        } catch (IOException e) {
            log.warn("Push Gateway로 데이터를 전송할 수 없습니다.", e);
        }
    }
}
