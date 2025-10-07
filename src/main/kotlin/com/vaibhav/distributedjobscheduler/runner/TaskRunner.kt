package com.vaibhav.distributedjobscheduler.runner

import com.vaibhav.distributedjobscheduler.service.common.SchedulerRoleService
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ScheduledExecutorService

@Component
class TaskRunner(
    private val executor: ScheduledExecutorService,
    private val schedulerRoleService: SchedulerRoleService
) {
    private val log = LoggerFactory.getLogger(TaskRunner::class.java)

    @PostConstruct
    fun initializeScheduler() {
        log.info("TaskRunner initialized. Submitting Master/Worker determination loop to Executor.")
        schedulerRoleService.startRoleDetermination(executor)
        log.info("Master/Worker role determination started successfully.")
    }
}
