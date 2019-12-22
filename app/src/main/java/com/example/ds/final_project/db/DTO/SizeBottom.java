package com.example.ds.final_project.db.DTO;

public class SizeBottom extends Size {
    float total;
    float waist;
    float breast;
    float crotch;
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

    public float getBreast() {
        return breast;
    }

    public void setBreast(float breast) {
        this.breast = breast;
    }

    public float getCrotch() {
        return crotch;
    }

    public void setCrotch(float crotch) {
        this.crotch = crotch;
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
        String s3=( (breast==0.0) ? "" : "허벅지단면: "+breast+"\n");
        String s4=( (crotch==0.0) ? "" : "밑위: "+crotch+"\n");
        String s5=( (tail==0.0) ? "" : "밑단단면: "+tail+"\n");
        str=s1+s2+s3+s4+s5;
        return str;
    }
}
