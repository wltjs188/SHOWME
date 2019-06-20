package com.example.ds.final_project;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

public class Option {
    private String productCode; //상품코드
    private String productDetailUrl; //상세url
    private String productImage; //상품이미지
    private String optionOrder; //옵션번호
    private String optionTitle; //상세 정보 이름
    private String optionValue; //상세 정보 값
    private String optionPrice;
    private int changeValue=0;

    @Override
    public String toString() {
        //    return "Product [productCode=" + productCode + ", productName=" + productName + ", productImage=" + productImage
        //            + ", productDetailUrl=" + productDetailUrl + ", productPrice=" + productPrice + "]";
        String str1 = optionTitle;
        String[] words = str1.split(",");
        String str2 =  optionValue;
        String[] words2 = str2.split(",");

        String str ="\n가격 : "+ optionPrice+"\n";
        for(int i=0;i<words.length;i++){
            str+=words[i]+" : "+words2[i]+"\n";
        }
      //  Log.d("채윤이",this.getProductDetailUrl());
        return str+"옵션번호:"+optionOrder+"\n";
    }
    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    public String getProductImage() {
        return productImage;
    }
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
    public String getProductDetailUrl() {
        return productDetailUrl;
    }
    public void setProductDetailUrl(String productDetailUrl) {
        this.productDetailUrl = productDetailUrl;
    }

    public void setOptionOrder(String optionOrder) {
        this.optionOrder = optionOrder;
    }
    public void setOptionTitle(String optionTitle) {
        this.optionTitle = optionTitle;
    }
    public String getOptionOrder() {
        return optionOrder;
    }
    public String getOptionTitle() {
        return optionTitle;
    }


    public String getOptionValue() {
        return optionValue;
    }
    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }
    public String getOptionPrice() {
        return optionPrice;
    }
    public void setOptionPrice(String optionPrice) {
        this.optionPrice = optionPrice;
    }


    public void setChagneValue(int add){ //1이면 추가된것,0이면 추가안된것
        changeValue=add;
    }
    public int getChangeValue(){ //1이면 추가된것, 0이면 추가안된것
        return changeValue;
    }
    public int errorMessage(String productCode, List optionValueList){
        if(productCode==null||optionValueList.size()==0) {
//            Log.i("사이즈",""+optionValueList.size());
            return 0; //검색결과 없을때 0
        }
        else return 1; //검색결과 있을때 1
    }
    public int errorMessage(String productName, HashMap<String,String> optionValueMap){
        if(productName==null||optionValueMap.size()==0) {
//            Log.i("사이즈",""+optionValueList.size());
            return 0; //검색결과 없을때 0
        }
        else return 1; //검색결과 있을때 1
    }
}
