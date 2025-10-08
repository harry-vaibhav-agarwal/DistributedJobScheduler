package com.vaibhav.distributedjobscheduler.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.Instant

@Entity
@Table(name = "segment_assignment")
data class SegmentAssignment(
    @Id
    @Column(name = "segment_id", nullable = false, unique = true)
    val segmentId: String = "",

    @Column(name = "worker_instance_id")
    var workerInstanceId: String? = null,

    @Column(name = "last_claim_time")
    var lastClaimTime: Instant = Instant.now(),

    @Version
    var version: Int = 0
)
