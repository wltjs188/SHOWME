package com.example.ds.final_project;

public class ChatMessage {
    //메세지

    private String content;
    private boolean isMine;
    private boolean isButton=false; //버튼 선택 메세지 확인
    private int isButtonType=0;

    public ChatMessage(String content, boolean isMine) {
        this.content = content;
        this.isMine = isMine;
    }
    public String getContent() {
        return content;
    }
    public String toString(){
        return content;
    }

    public void setButton(int isButtonType){ //버튼설정
        this.isButton = true;
        this.isButtonType=isButtonType;
    }
    public boolean isMine() {
        return isMine;
    }
    public boolean isButton(){
        return isButton;
    }
    public int isButtonType(){return isButtonType;}
}