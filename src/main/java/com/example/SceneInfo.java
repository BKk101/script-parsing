package com.example;

public class SceneInfo {
    private String sceneNumber;
    private String place;
    private String time;

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

    public String getSceneNumber() {
        return sceneNumber;
    }

    public String getPlace() {
        return place;
    }

    public String getTime() {
        return time;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(sceneNumber).append("/").append(place).append("/").append(time);
        return sb.toString();
    }

}
