package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.SysErrorLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SysErrorLogRepository: JpaRepository<SysErrorLog, Long>