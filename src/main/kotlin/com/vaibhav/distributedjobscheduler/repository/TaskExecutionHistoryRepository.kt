package com.vaibhav.distributedjobscheduler.repository

import com.vaibhav.distributedjobscheduler.domain.TaskExecutionHistory
import org.springframework.data.jpa.repository.JpaRepository

interface TaskExecutionHistoryRepository: JpaRepository<TaskExecutionHistory, Long> {
}