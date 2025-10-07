package com.vaibhav.distributedjobscheduler.repository

import com.vaibhav.distributedjobscheduler.domain.Job
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobRepository: JpaRepository<Job, Long> {
}