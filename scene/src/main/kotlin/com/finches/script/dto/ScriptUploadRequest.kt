package com.finches.script.dto

import com.finches.web.validation.validator.FileExtension
import io.swagger.annotations.ApiModelProperty
import org.springframework.web.multipart.MultipartFile

data class ScriptUploadRequest(
    @ApiModelProperty(hidden = true)
    var projectNo: Long,
    @field:FileExtension(message = "{file.file-extension}")
    val file: MultipartFile,
    val userId: String,
    val episode: String = "",
)
