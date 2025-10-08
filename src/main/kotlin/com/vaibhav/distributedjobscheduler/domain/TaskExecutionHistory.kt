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

/**
 * Entity to store the history of every task execution.
 * This is used for auditing, reporting, and debugging.
 */
@Entity
@Table(name = "task_execution_history")
data class TaskExecutionHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "job_run_id", nullable = false)
    val jobRunId: String = "",

    @Column(name = "worker_id", nullable = false)
    val workerId: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ExecutionStatus = ExecutionStatus.RUNNING,

    @Column(name = "start_time", nullable = false)
    val startTime: Instant = Instant.now(),

    @Column(name = "end_time")
    var endTime: Instant? = null,

    @Column(columnDefinition = "TEXT")
    var logOutput: String? = null
)

/**
 * Defines the possible states of a task execution.
 */
enum class ExecutionStatus {
    RUNNING,
    SUCCESS,
    FAILURE,
    TIMEOUT
}
