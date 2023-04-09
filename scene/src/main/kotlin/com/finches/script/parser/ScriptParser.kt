package com.finches.script.parser

/**
 * 대본 파서
 */
interface ScriptParser {
    fun parse(fileName: String) : String = ""
}

val parserMap = mapOf("hwp" to HwpScriptParser, "pdf" to PdfScriptParser)
