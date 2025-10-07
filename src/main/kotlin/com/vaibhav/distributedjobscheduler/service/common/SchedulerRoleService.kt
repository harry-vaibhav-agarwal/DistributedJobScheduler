package com.vaibhav.distributedjobscheduler.service.common

import com.vaibhav.distributedjobscheduler.service.master.MasterService
import com.vaibhav.distributedjobscheduler.service.worker.WorkerService
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@Service
class SchedulerRoleService(
    private val redisTemplate: StringRedisTemplate,
    private val workerService: WorkerService,
    private val masterService: MasterService
) {

    private val isMaster = AtomicBoolean(false)
    private var roleFuture: ScheduledFuture<*>? = null

    fun startRoleDetermination(executor: ScheduledExecutorService) {
        if (roleFuture == null || roleFuture!!.isDone) {
            log.info("Starting Master/Worker role determination heartbeat...")
            roleFuture = executor.scheduleAtFixedRate(
                this::determineCurrentRole, 0, // initial delay
                HEARTBEAT_INTERVAL_SECONDS, TimeUnit.SECONDS
            )
        }
    }

    private fun determineCurrentRole() {
        try {
            val currentMasterId = redisTemplate.opsForValue().get(SCHEDULER_MASTER_KEY)
            if (currentMasterId == INSTANCE_ID) {
                renewMasterRole()
            } else {
                attemptToAcquireMasterRole()
            }
        } catch (e: Exception) {
            handleFailure(e)
        }
    }

    private fun renewMasterRole() {
        val renewed = redisTemplate.expire(SCHEDULER_MASTER_KEY, Duration.ofSeconds(LOCK_EXPIRY_TIMEOUT_IN_SECONDS))
        if (renewed == true) {
            if (isMaster.compareAndSet(false, true)) {
                log.info("Renewed leadership as master: $INSTANCE_ID")
                transitionToMaster()
            } else {
                masterService.ensureRunning()
            }
        } else {
            transitionToWorker()
        }
    }

    private fun attemptToAcquireMasterRole() {
        val acquired = redisTemplate.opsForValue().setIfAbsent(
            SCHEDULER_MASTER_KEY, INSTANCE_ID, Duration.ofSeconds(LOCK_EXPIRY_TIMEOUT_IN_SECONDS)
        )
        if (acquired == true) {
            if (isMaster.compareAndSet(false, true)) {
                log.info("Acquired leadership as master: $INSTANCE_ID")
                transitionToMaster()
            }
        } else {
            if (isMaster.compareAndSet(true, false)) {
                log.info("Lost leadership, transitioning to worker: $INSTANCE_ID")
                transitionToWorker()
            } else {
                workerService.ensureRunning()
            }
        }
    }

    private fun transitionToMaster() {
        workerService.stop()
        masterService.start()
    }

    private fun transitionToWorker() {
        if (isMaster.compareAndSet(true, false)) {
            log.warn("Stepping down to worker: $INSTANCE_ID")
            masterService.stop()
        }
        workerService.start()
    }

    private fun handleFailure(e: Exception) {
        log.error("Error during role determination, transitioning to worker.", e)
        transitionToWorker()
    }


    companion object {
        private const val SCHEDULER_MASTER_KEY = "scheduler:master:lock"
        private const val LOCK_EXPIRY_TIMEOUT_IN_SECONDS = 30L
        private val INSTANCE_ID = UUID.randomUUID().toString()
        private const val HEARTBEAT_INTERVAL_SECONDS = 5L
        private val log = LoggerFactory.getLogger(SchedulerRoleService::class.java)
    }
}
