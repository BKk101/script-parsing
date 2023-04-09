package com.finches.script.parser

import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

object PdfScriptParser : ScriptParser {

    override fun parse(fileName: String): String {
        val document: PDDocument = Loader.loadPDF(File(fileName))
        val stripper = PDFTextStripper()

        stripper.sortByPosition = true
        stripper.lineSeparator = "\n"
        stripper.wordSeparator = "\t"
        return stripper.getText(document)
    }
}
