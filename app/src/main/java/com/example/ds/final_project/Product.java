package com.example.ds.final_project;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAMSUNG on 2019-03-19.
 */

public class Product {
    private String productCode; //상품코드
    private String productName; //상품명
    private String productImage; //상품이미지
    private String productDetailUrl; //상세url
    private String productPrice; //상품대표가격
    private String optionOrder; //상품옵션순서
    private String optionTitle; //옵션이름
    private List optionValueList =new ArrayList<>(); //옵션밸류값
    private List optionPriceList =new ArrayList<>(); //옵션가격
    private int changeValue=0;

    @Override
    public String toString() {
    //    return "Product [productCode=" + productCode + ", productName=" + productName + ", productImage=" + productImage
    //            + ", productDetailUrl=" + productDetailUrl + ", productPrice=" + productPrice + "]";
        String str1 = optionTitle;
        String[] words = str1.split(",");
        String str2 =  optionValueList.get(0)+"";
        String[] words2 = str2.split(",");

        String str ="\n가격 : "+ optionPriceList.get(0)+"\n";
        for(int i=0;i<words.length;i++){
            str+=words[i]+" : "+words2[i]+"\n";
        }
        return str;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDetailUrl() {
        return productDetailUrl;
    }

    public void setProductDetailUrl(String productDetailUrl) {
        this.productDetailUrl = productDetailUrl;
    }
    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
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

    public List getOptionValueList() {
        return optionValueList;
    }

    public void setOptionValueList(String optionValue) {
        this.optionValueList.add(optionValue);
    }

    public void setChagneValue(int add){ //1이면 추가된것,0이면 추가안된것
        changeValue=add;
    }
    public int getChangeValue(){ //1이면 추가된것, 0이면 추가안된것
        return changeValue;
    }
    public List getOptionPriceList() {
        return optionPriceList;
    }

    public void setOptionPriceList(String optionPrice) {
        this.optionPriceList.add(optionPrice);
    }

    public int errorMessage(String productName, List optionValueList){
        if(productName==null||optionValueList.size()==0) {
//            Log.i("사이즈",""+optionValueList.size());
            return 0; //검색결과 없을때 0
        }
        else return 1; //검색결과 있을때 1
    }
}
