package com.example.ds.final_project.db.DTO;

public class User {
    String id="";
    String name="";
    String address="";
    String phoneNum="";
    public User(String id,String name, String address, String phoneNum){
        this.id=id;
        this.name=name;
        this.address=address;
        this.phoneNum=phoneNum;
    }
    public User(String id){
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
}
