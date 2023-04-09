package com.finches.script.controller

import com.finches.script.dto.ScriptAnalyzeRequest
import com.finches.script.dto.ScriptUploadRequest
import com.finches.script.dto.ScriptUploadResponse
import com.finches.script.dto.ScriptsGetRequest
import com.finches.script.model.Script
import com.finches.script.service.ScriptService
import com.finches.tag.service.TagService
import com.finches.tagged.service.TaggedService
import com.finches.web.dto.CommonResponse
import io.swagger.annotations.Api
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api(tags = ["Script"], description = "스크립트 관련 API")
@RestController
@RequestMapping("/strips/v1.0")
class ScriptController(
    private val scriptService: ScriptService,
) {
    @PostMapping(value = ["/scripts/{scriptNo}/analysis"])
    fun analysisScript(@PathVariable scriptNo: Long): CommonResponse<Any> {
        var results = scriptService.analyzeScript(ScriptAnalyzeRequest(scriptNo));
        return CommonResponse.success();
    }
}
