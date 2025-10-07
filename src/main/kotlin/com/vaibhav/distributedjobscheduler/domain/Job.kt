package com.vaibhav.distributedjobscheduler.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "jobs")
data class Job(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String = "",

    @Column(name = "cron_expression", nullable = false)
    val cronExpression: String = "",

    @Column(name = "payload_template", columnDefinition = "TEXT", nullable = false)
    val payloadTemplate: String = "",

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true
)