package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.SysCallLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SysCallLogRepository: JpaRepository<SysCallLog, Long> {
}