package com.example.apiboilerplate.repositories.base

import com.example.apiboilerplate.models.base.SysErrorLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SysErrorLogRepository: JpaRepository<SysErrorLog, Long>