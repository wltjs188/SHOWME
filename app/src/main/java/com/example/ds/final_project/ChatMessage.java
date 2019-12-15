package com.example.ds.final_project;

import com.example.ds.final_project.db.DTO.Product;

import java.util.ArrayList;

public class ChatMessage {
    //메세지

    private String content="";
    private boolean isMine;
    private boolean isButton=false; //버튼 선택 메세지 확인
    private int isButtonType=0;
    private boolean isProduct=false;
    ArrayList<Product> products=new ArrayList<Product>();
    ArrayList<String> produtinfo = new ArrayList<String>();
    String check ="";

    public ChatMessage(String content, boolean isMine) {
        this.content = content;
        this.isMine = isMine;
    }
    public ChatMessage(boolean isMine,boolean isProduct,ArrayList<Product> products){ //상품 메세지 생성자
        this.isProduct = true;
        this.products = products;
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
    public boolean isProduct(){return isProduct;}
    public int isButtonType(){return isButtonType;}
    public ArrayList<Product> getProducts(){return products;}
    public ArrayList<String> getProdutinfo(){
        ArrayList<String> infoList=new ArrayList<String>();
        String info;
        Product p;
        for(int i=0;i<products.size();i++){
            p=products.get(i);
            info="상품이름:"+p.getName()+"\n가격"+p.getPrice()+"원"+"\n스타일"+p.getStyle()+"\n색상"+p.getColor()+
                    "\n사이즈"+p.getSize();
            infoList.add(info);
        }
        return infoList;
    }
    public ArrayList<String> getImage(){
        ArrayList<String> imageList=new ArrayList<String>();
        String image;
        Product p;
        for(int i=0;i<products.size();i++){
            p=products.get(i);
            image=p.getImage();
            imageList.add(image);
        }
        return imageList;
    }
}