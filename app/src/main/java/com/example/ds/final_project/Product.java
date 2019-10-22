package com.example.ds.final_project;

public class Product {
    String category;
    String color;
    String length;
    String size;
    String pattern;
    public Product(){}
    public Product(String category, String color, String length, String size, String pattern) {
        this.category = category;
        this.color=color;
        this.length=length;
        this.size=size;
        this.pattern=pattern;
    }
    public void setInfo(String category, String color, String length, String size, String pattern){
        this.category = category;
        this.color=color;
        this.length=length;
        this.size=size;
        this.pattern=pattern;
    }
    @Override
    public String toString() {
        return "카테고리: " + category + '\'' +
                "색상: " + color + '\'' +
                "길이: " + length + '\'' +
                "사이즈: " + size + '\'' +
                "패턴" + pattern ;
    }
}
