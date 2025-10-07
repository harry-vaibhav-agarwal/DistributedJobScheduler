package com.vaibhav.distributedjobscheduler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DistributedJobSchedulerApplication

fun main(args: Array<String>) {
    runApplication<DistributedJobSchedulerApplication>(*args)
}
