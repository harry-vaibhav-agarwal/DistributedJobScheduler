package com.vaibhav.distributedjobscheduler.repository

import com.vaibhav.distributedjobscheduler.domain.SegmentAssignment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SegmentAssignmentRepository: JpaRepository<SegmentAssignment, String> {
}