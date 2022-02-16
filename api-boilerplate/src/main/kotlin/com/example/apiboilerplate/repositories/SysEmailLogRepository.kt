package com.example.apiboilerplate.repositories

import com.example.apiboilerplate.models.base.SysEmailLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SysEmailLogRepository: JpaRepository<SysEmailLog, Long>