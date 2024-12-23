package io.datadynamics.prometheus.micrometer.configuration;

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
    }

/*
    JobDetail jobDetail() {
        JobDetail jobDetail = JobBuilder.newJob(CronJob.class)
                .withIdentity("someJobKey", "immediateEmailsGroup")
                .storeDurably()
                .build();
        return jobDetail;
    }

    CronTrigger trigger() {
        CronTrigger trigger = newTrigger()
                .withIdentity("trigger3", "group1")
                .withSchedule(cronSchedule("0 * * * * ?"))
                .forJob(jobDetail())
                .build();
        return trigger;
    }
*/
}