package io.datadynamics.prometheus.micrometer.configuration;

import io.datadynamics.prometheus.micrometer.job.PrometheusJob;
import jakarta.annotation.PostConstruct;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Configuration
public class QuartzJobConfig {

    @Autowired
    private Scheduler scheduler;

    @PostConstruct
    public void postConstruct() throws Exception {
        scheduler.scheduleJob(jobDetail(), trigger());
    }

    public JobDetail jobDetail() {
        JobDetail jobDetail = JobBuilder.newJob(PrometheusJob.class)
                .withIdentity("prometheus", "pushgateway-job")
                .storeDurably()
                .build();
        return jobDetail;
    }

    public CronTrigger trigger() {
        CronTrigger trigger = newTrigger()
                .withIdentity("pushgateway-jobtrigger", "prometheus")
                .withSchedule(cronSchedule("0 * * * * ?"))
                .forJob(jobDetail())
                .build();
        return trigger;
    }
}