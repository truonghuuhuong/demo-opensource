package com.cleanup.data.scheduler;

import javax.annotation.PostConstruct;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.cleanup.data.job.APGCleanJob;
import com.cleanup.data.utils.PropertiesUtils;

/**
 * The APGCleanData class.
 * 
 * @author CuongPD1
 */
@Component
public class APGCleanData {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(APGCleanData.class);

	private static final PropertiesUtils properties = PropertiesUtils
			.getInstance();

	private static String JOB_NAME = "APGCleanJob";

	private static String GROUP_NAME = "APGCleanGroup";

	private static String TRIGGER_NAME = "APGCleanTrigger";

	private static String CRON_CONFIG = "job.cronexpression";

	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void scheduleCleanDataJob() {
		try {
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
			jobFactory.setApplicationContext(applicationContext);
			scheduler.setJobFactory(jobFactory);

			JobDetail job = JobBuilder.newJob(APGCleanJob.class)
					.withIdentity(JOB_NAME, GROUP_NAME).build();
			String cronExp = getNotificationjobCronexpression();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(TRIGGER_NAME, GROUP_NAME)
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
					.build();

			scheduler.start();
			scheduler.scheduleJob(job, trigger);
			LOGGER.info("has clean data job");
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private String getNotificationjobCronexpression() {
		LOGGER.info("clean data cron expression = " + properties.getPropertyValue(CRON_CONFIG));
		return properties.getPropertyValue(CRON_CONFIG);
	}

}
