package com.example.apiboilerplate.utils

import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


object FileUtil {

    fun write(file: MultipartFile, dir: Path) {
        val filepath: Path = Paths.get(dir.toString(), file.originalFilename)
        Files.newOutputStream(filepath).use { os -> os.write(file.bytes) }
    }

}