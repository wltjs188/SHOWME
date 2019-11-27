package com.example.ds.final_project.db.DTO;

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
}
