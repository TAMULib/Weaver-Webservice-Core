/* 
 * ScheduleConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/** 
 * Schedule Configuration.
 * 
 * @author
 *
 */
@Configuration
@EnableScheduling
public class CoreScheduleConfig implements SchedulingConfigurer
{
	
	/**
	 * Thread pool task scheduler bean.
	 * 
	 * @return		ThreadPoolTaskScheduler
	 */
	@Bean()
	public ThreadPoolTaskScheduler taskScheduler() {
		return new ThreadPoolTaskScheduler();
	}

	/**
	 * Configure task registrar.
	 * 
	 * @param		taskRegistrar	ScheduledTaskRegistrar
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setTaskScheduler(taskScheduler());
	}
	
}
