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
        String s1="총기장: "+total +"\n";
        String s2="허리단면: "+waist+"\n";
        String s3="가슴단면: "+breast+"\n";
        String s4="밑위길이: "+crotch+"\n";
        String s5="밑단면: "+tail+"\n";
        str=s1+s2+s3+s4+s5;
        return str;
    }
}
