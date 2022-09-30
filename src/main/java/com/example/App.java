package com.example;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.tool.textextractor.*;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class App {
    public static final String black = "\u001B[30m";
    public static final String red = "\u001B[31m";
    public static final String green = "\u001B[32m";
    public static final String yellow = "\u001B[33m";
    public static final String blue = "\u001B[34m";
    public static final String purple = "\u001B[35m";
    public static final String cyan = "\u001B[36m";
    public static final String white = "\u001B[37m";
    public static final String BACKGROUND_BLACK = "\u001B[40m";
    public static final String BACKGROUND_RED = "\u001B[41m";
    public static final String BACKGROUND_GREEN = "\u001B[42m";
    public static final String BACKGROUND_YELLOW = "\u001B[43m";
    public static final String BACKGROUND_BLUE = "\u001B[44m";
    public static final String BACKGROUND_PURPLE = "\u001B[45m";
    public static final String BACKGROUND_CYAN = "\u001B[46m";
    public static final String BACKGROUND_WHITE = "\u001B[47m";
    public static final String exit = "\u001B[0m";

    private static final Pattern scenePattern = Pattern.compile("^S*#*[0-9]+\\.*\\s"); // 씬넘버 정규식
    private static final Pattern timePattern = Pattern
            .compile(
                    "[^가-힣a-zA-Z0-9)]*(초|(이른)|(늦은))?\\s?(낮|밤|(새벽)|(저녁)|(아침)|(해질녘)|(오전)|(오후)|(저녁)|D|N)[^가-힣a-zA-Z0-9(]*"); // 시간대
    private static final Pattern koreanPattern = Pattern.compile("[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]");
    private static final Pattern englishPattern = Pattern.compile("[a-zA-Z]");

    // 표현모음
    private static boolean sceneStart;

    public static void main(String[] args) throws Exception {
        String name1 = "술도녀 10부(완).hwp";
        String name2 = "오주여 2부 (완).hwp";
        String name3 = "파더_당신의별_1부_제이에스픽쳐스.hwp";
        String name4 = "흙역사-대본1부-수정.hwp";
        String name5 = "[오펜] 이은희_엔딩크레딧 1부.hwp";
        String name6 = "제갈길1부(줄임초).hwp";
        String name7 = "술도녀 11부(최종).hwp";

        ArrayList<SceneInfo> sceneInfoList = new ArrayList<>();
        sceneStart = false;

        String filename = "script" + File.separator + name1;
        HWPFile hwpFile = HWPReader.fromFile(filename);

        TextExtractOption opt = new TextExtractOption();
        opt.setWithControlChar(true);
        opt.setMethod(TextExtractMethod.AppendControlTextAfterParagraphText);
        opt.setInsertParaHead(true);

        String str = TextExtractor.extract(hwpFile, opt);

        String[] line = str.split("\n");
        ArrayList<String> ori = new ArrayList<>();
        SceneInfo currScene = new SceneInfo();
        for (String l : line) {
            String phrase = l.strip();
            Matcher sceneMatcher = scenePattern.matcher(phrase);
            if (sceneMatcher.find()) { // 씬넘버 형식과 일치하는지 확인
                String sceneNumber = "";
                String place = "-";
                String time = "-";

                sceneNumber = sceneMatcher.group().replaceAll("[^0-9]", "");
                phrase = phrase.replaceAll(sceneMatcher.group(), "");
                if (!sceneStart && !sceneNumber.equals("1") && !sceneNumber.equals("0")) { // 대본 표지등에 날짜표현(2020.) 필터를 위함
                    continue;
                } else {
                    sceneStart = true;
                }

                ori.add(phrase);

                place = phrase.strip();
                Matcher timeMatcher = timePattern.matcher(phrase);
                while (timeMatcher.find()) { // 시간 표현 형식과 일치하는지 확인
                    String tar = timeMatcher.group(0);
                    if (tar.contains("(") ^ tar.contains(")")) {
                        if (tar.contains("(")) {
                            place = place.replace(timeMatcher.group(0), " (");
                        } else {
                            place = place.replace(timeMatcher.group(0), ") ");
                        }
                    } else {
                        place = place.replace(timeMatcher.group(0), " ");
                    }
                    time += timeMatcher.group(1) != null ? timeMatcher.group(1) : "" + timeMatcher.group(4); // + 방식 수정
                    timeMatcher = timePattern.matcher(place);
                }

                sceneInfoList.add(new SceneInfo(sceneNumber, place, time));
                currScene = sceneInfoList.get(sceneInfoList.size() - 1);
            } else if (sceneStart) {
                currScene.setContent(phrase + "\n");
            }
        }

        FileOutputStream output = new FileOutputStream("c:\\Users\\kimbk101\\vscode\\script-parsing\\in.txt");
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        BufferedWriter out = new BufferedWriter(writer);

        Integer i = 0;
        for (SceneInfo sci : sceneInfoList) {
            out.write(sci.getPlace() + "\n");
            out.write(ori.get(i) + "\n");
            i += 1;
            // out.write(sci.getContent() + "\n");
        }

        out.close();

        // str = str.replaceAll(" ", red+"."+exit);
        str = str.replaceAll("\n", blue + "_\n" + exit);
        str = str.replaceAll("\t", BACKGROUND_YELLOW + " " + exit);

        System.out.println(str);

    }
}
