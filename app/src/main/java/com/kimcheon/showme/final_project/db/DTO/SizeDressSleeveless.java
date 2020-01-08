package com.kimcheon.showme.final_project.db.DTO;

public class SizeDressSleeveless extends Size {
    float total;
    float waist;
    float tail;

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getWaist() {
        return waist;
    }

    public void setWaist(float waist) {
        this.waist = waist;
    }

    public float getTail() {
        return tail;
    }

    public void setTail(float tail) {
        this.tail = tail;
    }

    public String toString() {
        String str="";

        String s1=( (total==0.0) ? "" : "총장:"+total +"\n");
        String s2=( (waist==0.0) ? "" : "허리단면: "+waist+"\n");
        String s3=( (tail==0.0) ? "" : "밑단단면: "+tail+"\n");

        str=s1+s2+s3;
        return str;
    }
}
