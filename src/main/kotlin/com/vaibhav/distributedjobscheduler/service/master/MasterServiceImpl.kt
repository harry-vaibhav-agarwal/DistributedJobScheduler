package com.vaibhav.distributedjobscheduler.service.master

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MasterServiceImpl: MasterService {
    override fun stop(): Boolean {
        return true
    }

    override fun start() {
       log.info("Starting master service")
    }

    override fun ensureRunning() {
       log.info("Ensure master is running")
    }

    companion object {
        private val log = LoggerFactory.getLogger(MasterServiceImpl::class.java)
    }
}