package com.finches.script.parser

import kr.dogfoot.hwplib.`object`.HWPFile
import kr.dogfoot.hwplib.reader.HWPReader
import kr.dogfoot.hwplib.tool.textextractor.TextExtractMethod
import kr.dogfoot.hwplib.tool.textextractor.TextExtractOption
import kr.dogfoot.hwplib.tool.textextractor.TextExtractor

object HwpScriptParser : ScriptParser {
    override fun parse(fileName: String): String {
        val hwpFile: HWPFile = HWPReader.fromFile(fileName)
        val option = TextExtractOption().apply {
            isWithControlChar = true
            isInsertParaHead = true
            method = TextExtractMethod.AppendControlTextAfterParagraphText
        }

        return TextExtractor.extract(hwpFile, option)
    }
}
