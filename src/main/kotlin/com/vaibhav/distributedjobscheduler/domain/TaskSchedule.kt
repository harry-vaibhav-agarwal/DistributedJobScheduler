package com.vaibhav.distributedjobscheduler.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "task_schedule")
data class TaskSchedule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "job_id")
    val jobId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val taskStatus: TaskStatus = TaskStatus.SCHEDULED,

    @Column(name = "next_run_time", nullable = false)
    val nextRunTime: Instant = Instant.EPOCH,

    @Column(name = "segment_id")
    val segmentId: String = "",

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "execution_parameters", columnDefinition = "TEXT", nullable = false)
    val executionParametersJson: String = ""
)


enum class TaskStatus {
    SCHEDULED,
    DISPATCHED,
    IN_PROGRESS,
    FAILED,
    COMPLETED,
    CANCELLED
}