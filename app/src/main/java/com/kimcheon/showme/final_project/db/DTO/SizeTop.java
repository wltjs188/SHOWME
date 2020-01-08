package com.kimcheon.showme.final_project.db.DTO;

public class SizeTop extends Size {
    float total;
    float shoulder;
    float breast;
    float sleeve;

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getShoulder() {
        return shoulder;
    }

    public void setShoulder(float shoulder) {
        this.shoulder = shoulder;
    }

    public float getBreast() {
        return breast;
    }

    public void setBreast(float breast) {
        this.breast = breast;
    }

    public float getSleeve() {
        return sleeve;
    }

    public void setSleeve(float sleeve) {
        this.sleeve = sleeve;
    }

    @Override
    public String toString() {
        String str="";
        str+=getTotal()==0.0?"":"총장: "+getTotal();
        str+=getShoulder()==0.0?"":"\n어깨너비: "+getShoulder();
        str+=getBreast()==0.0?"":"\n가슴단면: "+getBreast();
        str+=getSleeve()==0.0?"":"\n소매길이: "+getSleeve();
        return str;
    }
}
