package com.cleanup.data.scheduler;

import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

public class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
    
    /** The bean factory. */
    private transient AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(final ApplicationContext context) {
        beanFactory = context.getAutowireCapableBeanFactory();
        LOGGER.info("Set bean factory to AutowireCapableBeanFactory");
        
        if(beanFactory != null) {
        	LOGGER.debug("beanFactory NOT null");
        } else {
        	LOGGER.debug("beanFactory is null");
        }
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        
        LOGGER.info("Create job instance: " + job.getClass());

        return job;
    }
    
    /** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AutowiringSpringBeanJobFactory.class);
}