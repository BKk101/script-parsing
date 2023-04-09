package com.finches.script.pattern

import java.util.regex.Pattern

class ScriptPattern {
    companion object {
        val scenePattern: Pattern = Pattern.compile("^S*#*([0-9]+)\\.*\\s") //씬넘버 패턴
        val timePattern:  Pattern = Pattern.compile(
            "[^가-힣a-zA-Z0-9)]*(초|(이른)|(늦은))?\\s?(낮|밤|(새벽)|(저녁)|(아침)|(해질녘)|(오전)|(오후)|D|N)[^가-힣a-zA-Z0-9(]*") //시간표현 패턴
        val korean: Pattern = Pattern.compile("[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]") //한글
        val english: Pattern = Pattern.compile("[a-zA-Z]") //영어
        const val night: String = "밤저녁새벽N"
        const val day: String = "낮아침해질녘오전오후D"
        val pagePattern: Pattern = Pattern.compile("^-?\\s+\\d+\\s+-?$") //쪽번호 패턴
        val dialogPattern: Pattern = Pattern.compile("^([\\S ]+)\t+(.+)") //대사 패턴
        val inoutPattern: Pattern = Pattern.compile("(안|앞|밖|(일각))") //실내외 패턴
        const val outdoor: String = "앞밖일각"
        const val indoor: String = "안"
    }
}
