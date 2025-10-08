package com.vaibhav.distributedjobscheduler.repository

import com.vaibhav.distributedjobscheduler.domain.TaskSchedule
import org.springframework.data.jpa.repository.JpaRepository

interface TaskScheduleRepository: JpaRepository<TaskSchedule, Long> {
}