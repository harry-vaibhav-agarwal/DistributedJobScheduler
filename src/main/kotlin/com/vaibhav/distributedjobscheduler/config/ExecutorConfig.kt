package com.vaibhav.distributedjobscheduler.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

@Configuration
class ExecutorConfig {

    @Bean(destroyMethod = "shutdown")
    fun scheduledTaskExecutor(): ScheduledExecutorService {
        val corePoolSize = 3
        log.info("Initializing ScheduledExecutorService with poolSize: $corePoolSize")
        return Executors.newScheduledThreadPool(corePoolSize)
    }

    companion object {
        private val log = LoggerFactory.getLogger(ExecutorConfig::class.java)
    }
}