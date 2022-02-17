package com.example.apiboilerplate.services.base

import com.example.apiboilerplate.base.logger.ApiLogger
import com.example.apiboilerplate.utils.FileUtil
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Paths
import kotlin.io.path.name

@Service
class StorageService {

    companion object { private val log by ApiLogger() }

    fun save(file: MultipartFile, pathStr: String) {
        val path = Paths.get(pathStr)
        log.info("Saving file [${file.name}] to ${path.name}")
        FileUtil.write(file, path)
        log.debug("File [${file.name}] saved to ${path.name}")
    }

    fun getCompletePathFor(name: String, dir: String): String {
        return dir+name
    }

}