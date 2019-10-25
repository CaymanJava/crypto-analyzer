package pro.crypto.configuration.quartz;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;
import pro.crypto.configuration.infrastructure.JobInfo;
import pro.crypto.configuration.infrastructure.JobService;
import pro.crypto.configuration.infrastructure.SchedulerInitializationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

@Slf4j
@Configuration
@AllArgsConstructor
public class JobConfiguration {

    private final QuartzProperties properties;

    @Bean
    public SchedulerFactoryBean quartzScheduler() {
        log.trace("Initialize Quartz scheduler with properties {}", properties.getProperties());
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setQuartzProperties(properties.getProperties());
        factoryBean.setJobFactory(springBeanJobFactory());
        return factoryBean;
    }

    @Bean
    public JobConfiguration.AutowiringSpringBeanJobFactory springBeanJobFactory() {
        return new JobConfiguration.AutowiringSpringBeanJobFactory();
    }

    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {

        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(ApplicationContext context) {
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object job = super.createJobInstance(bundle);
            beanFactory.autowireBean(job);
            return job;
        }
    }

    @Component
    @Slf4j
    @AllArgsConstructor
    public static class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

        private final Scheduler quartzScheduler;
        private final QuartzProperties quartzProperties;
        private final Set<JobService> jobServices;

        @Override
        public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
            if (quartzProperties.getDisabled()) {
                return;
            }
            configureScheduler();
        }

        private void configureScheduler() {
            log.trace("Initializing scheduler...");
            List<JobInfo> jobsToScheduling = new ArrayList<>();
            jobServices
                    .forEach(jobService -> jobsToScheduling.addAll(jobService.getJobsToScheduling()));

            jobsToScheduling
                    .forEach(jobInfo -> scheduleJob(jobInfo.getName(), jobInfo.getGroupName(), jobInfo.getDescription(), jobInfo.getJobClass(), jobInfo.getScheduleExpression()));
        }

        private void scheduleJob(String name, String groupName, String description, Class<? extends Job> jobClass, String scheduleExpression) {
            try {
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                        .cronSchedule(scheduleExpression)
                        .withMisfireHandlingInstructionDoNothing();
                initJob(name, groupName, description, jobClass, scheduleBuilder);
                log.info("Initialized schedule {group: {}, name: {} expression: {}} ", groupName, name, scheduleExpression);
            } catch (SchedulerException e) {
                log.error("Initializing scheduler", e);
                throw new SchedulerInitializationFailure("Initializing scheduler", e);
            }
        }

        private void initJob(String name, String groupName, String description, Class<? extends Job> jobClass, ScheduleBuilder<CronTrigger> schedule) throws SchedulerException {
            JobDetail jobDetail = quartzScheduler.getJobDetail(JobKey.jobKey(name, groupName));
            if (isNull(jobDetail)) {
                scheduleJob(name, groupName, description, jobClass, schedule);
            } else {
                rescheduleJob(name, groupName, schedule);
            }
        }

        private void rescheduleJob(String name, String groupName, ScheduleBuilder<CronTrigger> schedule) throws SchedulerException {
            Trigger oldTrigger = quartzScheduler.getTrigger(triggerKey(name, groupName));
            quartzScheduler.rescheduleJob(oldTrigger.getKey(), updateTrigger(oldTrigger, schedule));
        }

        private void scheduleJob(String name, String groupName, String description, Class<? extends Job> jobClass, ScheduleBuilder<CronTrigger> schedule) throws SchedulerException {
            quartzScheduler.scheduleJob(createJobDetail(name, groupName, description, jobClass), createTrigger(name, groupName, schedule));
        }

        private JobDetail createJobDetail(String name, String groupName, String description, Class<? extends Job> jobClass) {
            return newJob(jobClass).withIdentity(name, groupName).withDescription(description).build();
        }

        private Trigger updateTrigger(Trigger oldTrigger, ScheduleBuilder schedule) {
            TriggerBuilder builder = oldTrigger.getTriggerBuilder();
            return builder.withSchedule(schedule).build();
        }

        private Trigger createTrigger(String name, String groupName, ScheduleBuilder<CronTrigger> schedule) {
            return newTrigger()
                    .withIdentity(name, groupName)
                    .withSchedule(schedule)
                    .startNow()
                    .build();
        }
    }

}
