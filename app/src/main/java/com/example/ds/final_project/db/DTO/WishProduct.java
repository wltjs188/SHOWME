package com.example.ds.final_project.db.DTO;

public class WishProduct extends Product {
    String uid;
    String alias;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
