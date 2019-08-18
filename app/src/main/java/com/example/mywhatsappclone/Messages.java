package com.example.mywhatsappclone;

public class Messages {
    private String name;
    private String messages;
    private String date;
    private String time;
    private String charId;
    private String image;

    Messages(){

    }

    Messages(String name,String messages,String date,String time){
        this.name = name;
        this.messages = messages;
        this.date = date;
        this.time = time;

    }
    Messages(String image,String messages,String name,String date,String time){
        this.name = name;
        this.image = image;
        this.date = date;
        this.time = time;
        this.messages = messages;

    }

    Messages(String name,String messages,String date,String time,String charId,String img){
        this.name = name;
        this.image = image;
        this.date = date;
        this.time = time;
        this.messages = messages;
        this.charId = charId;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCharId() {
        return charId;
    }

    public void setCharId(String charId) {
        this.charId = charId;
    }
}
