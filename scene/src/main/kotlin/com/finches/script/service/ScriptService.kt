package com.finches.script.service

import com.finches.script.dto.ScriptAnalyzeRequest
import com.finches.script.dto.ScriptUploadRequest
import com.finches.script.dto.ScriptUploadResponse
import com.finches.script.dto.ScriptsGetRequest
import com.finches.script.model.Script

interface ScriptService {

    fun analyzeScript(request: ScriptAnalyzeRequest)
}
