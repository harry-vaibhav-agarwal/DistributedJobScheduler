package com.vaibhav.distributedjobscheduler.service.worker

interface WorkerService {
    fun ensureRunning(): Boolean
    fun stop(): Boolean
    fun start()
}