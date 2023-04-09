package com.finches.script.service

import com.finches.character.model.Character
import com.finches.character.model.CharacterGender
import com.finches.character.model.CharacterRole
import com.finches.character.repository.CharacterRepository
import com.finches.place.model.Place
import com.finches.place.repository.PlaceRepository
import com.finches.scene.code.TimeCode
import com.finches.scene.model.Scene
import com.finches.scene.model.SceneInfo
import com.finches.scene.model.SceneSiteCode
import com.finches.scene.model.SiteCode
import com.finches.scene.repository.SceneCharacterRepository
import com.finches.scene.repository.ScenePlaceRepository
import com.finches.scene.repository.SceneRepository
import com.finches.script.dto.ScriptAnalyzeRequest
import com.finches.script.dto.ScriptUploadRequest
import com.finches.script.dto.ScriptUploadResponse
import com.finches.script.dto.ScriptsGetRequest
import com.finches.script.model.Script
import com.finches.script.parser.DefaultScriptParser
import com.finches.script.parser.parserMap
import com.finches.script.pattern.ScriptPattern
import com.finches.script.repository.ScriptRepository
import com.finches.strip.model.Strip
import com.finches.strip.repository.StripRepository
import com.finches.user.exception.UserNotFoundException
import com.finches.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.regex.Matcher


@Service
class ScriptServiceImpl(
    private val scriptRepository: ScriptRepository,
    private val userRepository: UserRepository,
    private val sceneRepository: SceneRepository,
    private val placeRepository: PlaceRepository,
    private val stripRepository: StripRepository,
    private val scenePlaceRepository: ScenePlaceRepository,
    private val characterRepository: CharacterRepository,
    private val sceneCharacterRepository: SceneCharacterRepository,
): ScriptService {
    private val filePath = "/uploaded/"
    private val emptyLine = Regex("^\\s+$")
    private val DES = "지문"

    private val dialogPattern = "((?<=\\.))".toPattern()

    private val log = KotlinLogging.logger {  }

    @Transactional
    override fun analyzeScript(request: ScriptAnalyzeRequest) {

        val absolutePath = File("").absolutePath
        val script = scriptRepository.findById(request.id).orElseThrow {
            throw IllegalArgumentException("script not found $request")
        }
        val sourceFileName = script.fileName
        val fileName = "$absolutePath$filePath$sourceFileName"


        val sceneInfoList: ArrayList<SceneInfo> = ArrayList()
        var isSceneStart: Boolean = false
        var isDescriptionStart: Boolean = false
        var isPrevNewline: Boolean = false

        // 대본 분석 반복 방지
        if (!sceneRepository.findAllByScriptNo(request.id, PageRequest.of(1,1)).isEmpty) {
            throw Exception("Analysis already done")
        }

        val (fileType, parser) = parserMap
            .filter { (k, _) -> sourceFileName.endsWith(".$k") }
            .firstNotNullOfOrNull { (k, v) -> k.uppercase() to v } ?: ("" to DefaultScriptParser)

        val str = parser.parse(fileName)


        val line = str.split(Regex("((?<=\n))"))
        var tempDialog: String = ""
        var tempDescription: String = ""
        var tempCharacter: String = ""

        for (l in line) {
            var phrase = l.trim()
            val sceneMatcher: Matcher = ScriptPattern.scenePattern.matcher(phrase)
            if (sceneMatcher.find()) { //씬넘버 형식과 일치하는지 확인
                var sceneNumber: String = ""
                var place: String = "-"
                var time: TimeCode?= null

                sceneNumber = sceneMatcher.group(1)
                phrase = phrase.replace(sceneMatcher.group().toRegex(), "")
                if (!isSceneStart && sceneNumber != "1") { //대본 표지등에 날짜표현(2020.) 필터를 위함
                    continue
                } else {
                    isSceneStart = true
                    isPrevNewline = true // 씬정보 이후 개행 없어도 지문으로 인식 (pdf용)
                }

                // 새로운 씬 시작되면 대사, 지문정보 저장
                if (tempDialog != "") { //dialog save & clear
                    tempDialog = dialogSaveAndClear(DialogWrapper(sceneInfoList, tempDialog, tempCharacter), script.projectNo)
                }
                if (tempDescription != "") { //description save & clear
                    tempDescription = dialogSaveAndClear(DialogWrapper(sceneInfoList, tempDescription, DES), script.projectNo)
                    isDescriptionStart = false
                }


                time = TimeCode.NA
                place = phrase.trim()

                var timeMatcher: Matcher = ScriptPattern.timePattern.matcher(phrase)
                while (timeMatcher.find()) { //시간 표현 형식과 일치하는지 확인
                    val tar = timeMatcher.group(0)
                    var repl: String
                    if (("(" in tar) xor (")" in tar)) {
                        repl = if ("(" in tar) " (" else ") "
                    } else {
                        repl = " "
                    }
                    place = place.replace(tar, repl)
                    // timePattern에 따라서 group 번호 수정해야 함
                    time = if (timeMatcher.group(4) in ScriptPattern.night) TimeCode.N else TimeCode.D
                    timeMatcher = ScriptPattern.timePattern.matcher(place)
                }

                val inoutMatcher: Matcher = ScriptPattern.inoutPattern.matcher(place)


                // 위치정보가 있는지 여부
                val isSiteExist = inoutMatcher.find()
                // 위치정보가 외부를 나타내는지 여부
                val isOutside = isSiteExist && inoutMatcher.group(1) in ScriptPattern.outdoor


                val scene = Scene(
                    scriptNo = request.id,
                    sceneNum = sceneNumber.toInt().toString(),
                    site = if (isSiteExist && isOutside) SiteCode.L else if (isSiteExist) SiteCode.S else SiteCode.NA,
                    sceneSite = if (isSiteExist && isOutside) SceneSiteCode.E else if (isSiteExist) SceneSiteCode.I else SceneSiteCode.NA,
                    timeslot = time,
                    shootingTime = 0,
                )

                val playPlace = this.saveAndGetPlayPlaces(place)
                val sceneInfo = SceneInfo(scene)
                sceneInfo.playPlaces.add(playPlace)

                sceneInfoList.add(sceneInfo)
            } else if (isSceneStart) { // 씬정보를 제외한 나머지 텍스트
                // TODO 개선 필요
                if (fileType == "PDF") { // 쪽번호 제거
                    val pageMatcher: Matcher = ScriptPattern.pagePattern.matcher(phrase)
                    if (pageMatcher.find())
                        continue
                }

                if (emptyLine.matches(l)) { // 빈줄인 경우
                    if (tempDialog != "") { //dialog save & clear
                        tempDialog = dialogSaveAndClear(DialogWrapper(sceneInfoList, tempDialog, tempCharacter), script.projectNo)
                    }
                    if (tempDescription != "") { //description save & clear
                        tempDescription = dialogSaveAndClear(DialogWrapper(sceneInfoList, tempDescription, DES), script.projectNo)
                        isDescriptionStart = false
                    }
                    isPrevNewline = true
                } else { // 대사 또는 지문인 경우
                    val dialogMatcher: Matcher = ScriptPattern.dialogPattern.matcher(phrase)
                    if (dialogMatcher.find()) {
                        if (tempDialog != "") { //dialog save & clear
                            tempDialog = dialogSaveAndClear(DialogWrapper(sceneInfoList, tempDialog, tempCharacter), script.projectNo)
                        }
                        tempDialog += dialogMatcher.group(1).trim() + dialogMatcher.group(2).replace("\t", "").trim() + "\n"
                        tempCharacter = dialogMatcher.group(1).trim() //character add
                        if (isDescriptionStart) { //description save & clear
                            tempDescription = dialogSaveAndClear(DialogWrapper(sceneInfoList, tempDescription, DES), script.projectNo)
                            isDescriptionStart = false
                        }
                    } else {
                        if (!isPrevNewline && !isDescriptionStart) {
                            tempDialog += phrase.replace("\t", "")+"\n" //dialog add
                        } else {
                            isDescriptionStart = true;
                            tempDescription += phrase.replace("\t", "")+"\n" //description add
                        }
                    }
                    isPrevNewline = false
                }

            }
        }
        // 개행으로 끝나지 않은 경우 처리
        if (tempDialog != "") { //dialog save & clear
            tempDialog = dialogSaveAndClear(DialogWrapper(sceneInfoList, tempDialog, tempCharacter), script.projectNo)
        }
        if (tempDescription != "") { //description save & clear
            tempDescription = dialogSaveAndClear(DialogWrapper(sceneInfoList, tempDescription, DES), script.projectNo)
            isDescriptionStart = false
        }



        sceneInfoList.forEachIndexed { idx, it ->
            val firstDialog: String = it.strips.firstOrNull()?.dialog ?: "" // 씬의 첫 문장
            val scene = sceneRepository.save(
                it.scene
                    .copy(summarize = firstDialog.split(dialogPattern, limit=2).first().replace("\n", ""))
            )
            it.strips.forEach {
                it.sceneNo = scene.sceneNo
                stripRepository.save(it)
            }
            scenePlaceRepository.saveAll(scene, it.playPlaces)
            sceneCharacterRepository.saveAll(scene, it.characters)
        }
    }

    data class DialogWrapper(
        var list: ArrayList<SceneInfo>,
        var dialog: String?,
        var character: String?
        )

    private fun saveAndGetPlayPlaces(placeName: String): Place {
        var tmpPlace = placeName
        if (tmpPlace.length > 45) {
            tmpPlace = tmpPlace.substring(0, 45)
        }

        return placeRepository.findByProjectNoAndName(1L, tmpPlace).ifEmpty {
            listOf(placeRepository.save(Place().apply {
                projectNo = 1L
                name = tmpPlace
            }))
        }[0]
    }

    private fun saveAndGetCharacter(characterName: String, projectNo: Long): Character {
        var tmpCharacterName = characterName
        if (tmpCharacterName.length > 45) {
            tmpCharacterName = tmpCharacterName.substring(0,45)
        }
        return characterRepository.findByProjectNoAndName(projectNo, tmpCharacterName).ifEmpty {
            listOf(characterRepository.save(Character(
                projectNo = projectNo,
                name = tmpCharacterName,
                actorNo = 0,
                role = CharacterRole.MAIN,
                gender = CharacterGender.NA,
            )))
        }[0]
    }

    private fun dialogSaveAndClear(target: ScriptServiceImpl.DialogWrapper, projectNo: Long): String {
        val strip = Strip()
        strip.dialog = target.dialog
        val character = this.saveAndGetCharacter(target.character!!, projectNo);
        strip.characterNo = character.characterNo
        target.list[target.list.size-1].strips.add(strip)
        target.character?.let {
            if (it != DES)
                target.list[target.list.size-1].characters.add(character)
        }


        return ""
    }
}
