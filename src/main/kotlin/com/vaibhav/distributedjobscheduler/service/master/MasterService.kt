package com.vaibhav.distributedjobscheduler.service.master

interface MasterService {
    fun stop(): Boolean
    fun start()
    fun ensureRunning()
}