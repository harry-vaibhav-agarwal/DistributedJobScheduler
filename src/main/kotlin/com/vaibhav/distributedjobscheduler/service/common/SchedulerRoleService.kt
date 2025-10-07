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

    // Flags to track the current active role and ensure only one is running at a time.
    private val isMaster = AtomicBoolean(false)
    private var roleFuture: ScheduledFuture<*>? = null

    fun startRoleDetermination(executor: ScheduledExecutorService) {
        if (roleFuture == null || roleFuture!!.isDone) {
            log.info("Starting Master/Worker role determination heartbeat...")
            roleFuture = executor.scheduleAtFixedRate(
                this::attemptAcquireLock,
                0, // initial delay
                HEARTBEAT_INTERVAL_SECONDS,
                TimeUnit.SECONDS
            )
        }
    }

    private fun attemptAcquireLock() {
        runCatching {
            // 1. Check who currently holds the lock
            val currentMasterId = redisTemplate.opsForValue().get(SCHEDULER_MASTER_KEY)

            if (currentMasterId == INSTANCE_ID) {
                // Case A: I am the current Master. Renew the lock's expiry time.
                // Note: We use expire() which is fast and ensures we don't accidentally overwrite the key value.
                val renewed = redisTemplate.expire(
                    SCHEDULER_MASTER_KEY,
                    Duration.ofSeconds(LOCK_EXPIRY_TIMEOUT_IN_SECONDS)
                )

                if (renewed == true) {
                    if (isMaster.compareAndSet(false, true)) {
                        log.info("LEADERSHIP RENEWED AND ACTIVATED I am the master: $INSTANCE_ID")
                        workerService.stop()
                        masterService.start()
                    } else {
                        // Already running as master, just logging continuation (for stability)
                        masterService.ensureRunning()
                    }
                } else {
                    // Lock existed, but we failed to renew (e.g., Redis error). Transition to worker for safety.
                    if (isMaster.compareAndSet(true, false)) {
                        log.warn("Failed to renew lock. Stepping down to worker: $INSTANCE_ID")
                        masterService.stop()
                        workerService.start()
                    }
                }

            } else {
                // Case B: I am not the Master (either lock is held by someone else, or it expired). Attempt to acquire.
                val acquired = redisTemplate.opsForValue().setIfAbsent(
                    SCHEDULER_MASTER_KEY,
                    INSTANCE_ID,
                    Duration.ofSeconds(LOCK_EXPIRY_TIMEOUT_IN_SECONDS)
                )

                if (acquired == true) {
                    // Successfully acquired the lock. Transition to Master.
                    if (isMaster.compareAndSet(false, true)) {
                        log.info("LEADERSHIP ACQUIRED I am the new master: $INSTANCE_ID")
                        workerService.stop()
                        masterService.start()
                    }
                } else {
                    // Lock held by someone else. Transition or continue as Worker.
                    if (isMaster.compareAndSet(true, false)) {
                        log.info("LEADERSHIP LOST I am the worker now: $INSTANCE_ID")
                        masterService.stop()
                        workerService.start()
                    } else {
                        log.info("Worker logic continue to run: $INSTANCE_ID")
                        workerService.ensureRunning()
                    }
                }
            }
        }.onFailure { e ->
            log.error("Error during lock attempt. Transitioning to Worker for safety.", e)
            if(isMaster.compareAndSet(true, false)) {
                log.info("Stop master and remain a worker")
                masterService.stop()
            }
            workerService.start()
        }
    }

    companion object {
        private const val SCHEDULER_MASTER_KEY= "scheduler:master:lock"
        // Increased expiry timeout relative to heartbeat for better stability.
        private const val LOCK_EXPIRY_TIMEOUT_IN_SECONDS = 30L
        private val INSTANCE_ID = UUID.randomUUID().toString()
        private const val HEARTBEAT_INTERVAL_SECONDS = 5L
        private val log = LoggerFactory.getLogger(SchedulerRoleService::class.java)
    }
}
