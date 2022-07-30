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

    private static final Pattern scenePattern = Pattern.compile("S*#*[0-9]+\\.*[^가-힣][^a-zA-z]"); //씬넘버 정규식
    private static final Pattern timePattern = Pattern.compile("낮?밤?(새벽)?(저녁)?(아침)?");

    public static void main(String[] args) throws Exception
    {
        String name1 = "술도녀 10부(완).hwp";
        String name2 = "오주여 2부 (완).hwp";
        String name3 = "파더_당신의별_1부_제이에스픽쳐스.hwp";
        String name4 = "흙역사-대본1부-수정.hwp";
        String name5 = "[오펜] 이은희_엔딩크레딧 1부.hwp";
        String name6 = "test.hwp";

        boolean a = timePattern.matcher("이밤이 가").matches();
        
        ArrayList<SceneInfo> sceneInfoList = new ArrayList<>();
        sceneInfoList.add(new SceneInfo("1","home","day"));
    
        String filename = "script" + File.separator + name3;
        HWPFile hwpFile = HWPReader.fromFile(filename);

        TextExtractOption opt = new TextExtractOption();
        opt.setWithControlChar(true);
        opt.setMethod(TextExtractMethod.AppendControlTextAfterParagraphText);
        opt.setInsertParaHead(true);
       
        String str = TextExtractor.extract(hwpFile, opt);
        // String str = TextExtractor.extract(hwpFile, TextExtractMethod.AppendControlTextAfterParagraphText);
                
        
        String[] line = str.split("\n");
        for (String l : line) {
            String[] phrase = l.strip().split(" ", 2);
            Matcher sceneMatcher = scenePattern.matcher(phrase[0]);
            if (sceneMatcher.matches()) {
                Matcher timeMatcher = timePattern.matcher(phrase[1]);
                String time = timeMatcher.matches() ? timeMatcher.group() : "";
                String place = phrase[1].replaceAll(time, "");
                sceneInfoList.add(new SceneInfo(phrase[0],place,time));
            }

            
        }

        FileOutputStream output = new FileOutputStream("c:\\Users\\kimbk101\\demo\\in.txt");
        OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
        BufferedWriter out = new BufferedWriter(writer);
        out.write(str);
        for (SceneInfo sci: sceneInfoList) {
            out.write(sci.toString()+"\n");
        }
        out.close();

        // str = str.replaceAll(" ", red+"."+exit);
        str = str.replaceAll("\n", blue+"_\n"+exit);
        str = str.replaceAll("\t", BACKGROUND_YELLOW+" "+exit);

        System.out.println(str);

    }
}
