package com.example.ds.final_project.db.DTO;

public class Product {

    int id;
    String name;
    String image;
    String category;
    String style;
    int price;
    String size;
    String brand;
    String color;
    String fabric;
    String texture;
    String stretch;
    String see_through;
    String thick;
    String season;
    String ave_dilevery;
    String size_table;



    String real_color;

    public Product(){

        size=null;
        brand=null;
        color=null;
        fabric=null;
        texture=null;
        stretch=null;
        see_through=null;
        thick=null;
        season=null;
        ave_dilevery=null;
        size_table=null;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFabric() {
        return fabric;
    }

    public void setFabric(String fabric) {
        this.fabric = fabric;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public String getStretch() {
        return stretch;
    }

    public void setStretch(String stretch) {
        this.stretch = stretch;
    }

    public String getSee_through() {
        return see_through;
    }

    public void setSee_through(String see_through) {
        this.see_through = see_through;
    }

    public String getThick() {
        return thick;
    }

    public void setThick(String thick) {
        this.thick = thick;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getAve_dilevery() {
        return ave_dilevery;
    }

    public void setAve_dilevery(String ave_dilevery) {
        this.ave_dilevery = ave_dilevery;
    }

    public String getSize_table() {
        return size_table;
    }

    public void setSize_table(String size_table) {
        this.size_table = size_table;
    }

    public String getReal_color() {
        return real_color;
    }

    public void setReal_color(String real_color) {
        this.real_color = real_color;
    }

    public String toString() {
        String str="";
        String s1="상품명: "+name +"\n";
        String s2="카테고리: "+category+" - "+style+"\n";
        String s3="가격: "+price+"원\n";
        String s4=(size==null||size=="null"?"":"사이즈: "+ size+"\n");
        String s5=(brand==null||brand=="null"?"":"브랜드명: "+ brand+"\n");
        String s6=real_color==null||real_color=="null"?"":"색상: "+ real_color+"\n";
        String s7=fabric==null||fabric=="null"?"":"소재: "+ fabric+"\n";
        String s8=texture==null||texture=="null"?"":"촉감: "+ texture+"\n";
        String s9=stretch==null||stretch=="null"?"":"신축성: "+ stretch+"\n";
        String s10=see_through==null||see_through=="null"?"":"비침: "+ see_through+"\n";
        String s11=thick==null||thick=="null"?"":"두께: "+ thick+"\n";
        String s12=season==null||season=="null"?"":"계절: "+ season+"\n";
        String s13=ave_dilevery==null||ave_dilevery=="null"?"":"평균 배송일: "+ ave_dilevery;
        str=s1+s2+s3+s4+s5+s6+s7+s8+s9+s10+s11+s12+s13;
        return str;
    }

    public String getSummary(){
        String str="";
        String s1="상품명: "+name +"\n";
        String s2="가격: "+price+"원\n";
        String s3=(brand==null||brand=="null"?"":"브랜드명: "+ brand+"\n");
        String s4=real_color==null||real_color=="null"?"":"색상: "+ real_color+"\n";
        str = s1+s2+s3+s4;
        return str;
    }
}
