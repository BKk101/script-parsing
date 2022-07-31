package com.example;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.tool.textextractor.*;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class App 
{
    public static final String black    = "\u001B[30m" ;
    public static final String red      = "\u001B[31m" ;
    public static final String green    = "\u001B[32m" ;
    public static final String yellow   = "\u001B[33m" ;
    public static final String blue     = "\u001B[34m" ;
    public static final String purple   = "\u001B[35m" ;
    public static final String cyan     = "\u001B[36m" ;
    public static final String white     = "\u001B[37m" ;
    public static final String BACKGROUND_BLACK = "\u001B[40m";     
	public static final String BACKGROUND_RED = "\u001B[41m";     
	public static final String BACKGROUND_GREEN = "\u001B[42m";    
	public static final String BACKGROUND_YELLOW = "\u001B[43m";     
	public static final String BACKGROUND_BLUE = "\u001B[44m";    
	public static final String BACKGROUND_PURPLE = "\u001B[45m";     
	public static final String BACKGROUND_CYAN = "\u001B[46m";     
	public static final String BACKGROUND_WHITE = "\u001B[47m";
    public static final String exit     = "\u001B[0m" ;

    private static final Pattern scenePattern = Pattern.compile("^S*#*[0-9]+\\.*\\s"); //씬넘버 정규식
    private static final Pattern timePattern = Pattern.compile("\\s?.?(낮|밤|(새벽)|(저녁)|(아침)|(해질녘)|(오전)|(오후)|D|N).?\\s?"); //시간대 표현모음
    private boolean sceneStart;

    public static void main(String[] args) throws Exception
    {
        String name1 = "술도녀 10부(완).hwp";
        String name2 = "오주여 2부 (완).hwp";
        String name3 = "파더_당신의별_1부_제이에스픽쳐스.hwp";
        String name4 = "흙역사-대본1부-수정.hwp";
        String name5 = "[오펜] 이은희_엔딩크레딧 1부.hwp";
        String name6 = "제갈길1부(줄임초).hwp";
        

        ArrayList<SceneInfo> sceneInfoList = new ArrayList<>();
        boolean sceneStart = false;
    
        String filename = "script-parsing/script" + File.separator + name4;
        HWPFile hwpFile = HWPReader.fromFile(filename);

        TextExtractOption opt = new TextExtractOption();
        opt.setWithControlChar(true);
        opt.setMethod(TextExtractMethod.AppendControlTextAfterParagraphText);
        opt.setInsertParaHead(true);
       
        String str = TextExtractor.extract(hwpFile, opt);
        
        String[] line = str.split("\n");
        for (String l : line) {
            String phrase = l.strip();
            Matcher sceneMatcher = scenePattern.matcher(phrase); 
            if (sceneMatcher.find()) { //씬넘버 형식과 일치하는지 확인
                String sceneNumber = "";
                String place = "-";
                String time = "-";

                sceneNumber = sceneMatcher.group().replaceAll("[^0-9]", "");
                phrase = phrase.replaceAll(sceneMatcher.group(), "");
                if (!sceneStart && sceneNumber.compareTo("1")>0) { //대본 표지등에 날짜표현(2020.) 필터를 위함
                    continue;
                } else {
                    sceneStart = true;
                }

                Matcher timeMatcher = timePattern.matcher(phrase);
                if (timeMatcher.find()) { //시간 표현 형식과 일치하는지 확인
                    place = phrase.replace(timeMatcher.group(), "");
                    time = timeMatcher.group(1);
                } else {
                    place = phrase.strip();
                }
                sceneInfoList.add(new SceneInfo(sceneNumber,place,time));
            }
            else if (sceneStart) {
                SceneInfo currScene = sceneInfoList.get(sceneInfoList.size()-1);
                currScene.setContent(phrase+"\n");
            }
        }

        FileOutputStream output = new FileOutputStream("c:\\Users\\kbk10\\parsing\\script-parsing\\in.txt");
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        BufferedWriter out = new BufferedWriter(writer);

        for (SceneInfo sci: sceneInfoList) {
            out.write(sci.getSceneInfo()+"\n");
            out.write(sci.getContent()+"\n");
        }
        
        out.close();

        // str = str.replaceAll(" ", red+"."+exit);
        str = str.replaceAll("\n", blue+"_\n"+exit);
        str = str.replaceAll("\t", BACKGROUND_YELLOW+" "+exit);

        System.out.println(str);

    }
}
