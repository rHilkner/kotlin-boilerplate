package com.example.apiboilerplate.repositories.base

import com.example.apiboilerplate.models.base.SysCallLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SysCallLogRepository: JpaRepository<SysCallLog, Long>