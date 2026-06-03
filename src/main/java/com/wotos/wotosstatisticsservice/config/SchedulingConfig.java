package com.wotos.wotosstatisticsservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables Spring's scheduling support so {@code @Scheduled} jobs (e.g. the snapshot
 * retention collapse) run.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
