package com.example.ds.final_project.db.DTO;

public class SizeTopVest extends Size {
    String total;
    String shoulder;
    String breast;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getShoulder() {
        return shoulder;
    }

    public void setShoulder(String shoulder) {
        this.shoulder = shoulder;
    }

    public String getBreast() {
        return breast;
    }

    public void setBreast(String breast) {
        this.breast = breast;
    }

    @Override
    public String toString() {
        String str="";
        str+="총장: "+getTotal();
        str+="\n어깨너비: "+getShoulder();
        str+="\n가슴단면: "+getBreast();
        return str;
    }
}
