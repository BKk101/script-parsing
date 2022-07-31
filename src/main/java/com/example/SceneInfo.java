package com.example;

public class SceneInfo {
    private String sceneNumber ="";
    private String place ="";
    private String time ="";
    private String content = "";

    public SceneInfo() {}
    public SceneInfo(String sceneNumber, String place, String time) {
        this.sceneNumber = sceneNumber;
        this.place = place;
        this.time = time;
    }
    
    public void setSceneInfo(String sceneNumber, String place, String time) {
        this.sceneNumber = sceneNumber;
        this.place = place;
        this.time = time;
    }

    public void setContent(String content) {
        this.content = this.content + content;
    }

    public String getSceneNumber() {
        return sceneNumber;
    }

    public String getPlace() {
        return place;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getSceneInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append(sceneNumber).append("/").append(place).append("/").append(time);
        return sb.toString();
    }

}
