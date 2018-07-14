package com.cleanup.data.job;

import java.io.IOException;

import org.codehaus.jettison.json.JSONException;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cleanup.data.utils.FileUtils;

@DisallowConcurrentExecution
public class APGCleanJob implements Job {

	private static Logger LOGGER = LoggerFactory.getLogger(APGCleanJob.class);

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		LOGGER.info("===================================start clean job=======================================");
		try {
			FileUtils.deleteFilesOlder();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		} catch (JSONException e) {
			LOGGER.error(e.getMessage());
		}
		LOGGER.info("===================================finish clean job======================================");
	}

}
