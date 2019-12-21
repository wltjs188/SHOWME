package com.example.ds.final_project.db.DTO;

public class SizeTopSleeveless extends Size {
    int total;
    int breast;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getBreast() {
        return breast;
    }

    public void setBreast(int breast) {
        this.breast = breast;
    }

    @Override
    public String toString() {
        String str="";
        str+="총장: "+getTotal();
        str+="\n가슴단면: "+getBreast();
        return str;
    }
}
