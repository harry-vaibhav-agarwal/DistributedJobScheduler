package com.vaibhav.distributedjobscheduler.service.worker

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WorkerServiceImpl: WorkerService {
    override fun ensureRunning(): Boolean {
        return true
    }

    override fun stop(): Boolean {
        return true
    }

    override fun start() {
       log.info("Starting worker thread")
    }

    companion object {
        private val log = LoggerFactory.getLogger(WorkerServiceImpl::class.java)
    }
}