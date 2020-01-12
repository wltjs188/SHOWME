package com.kimcheon.showme.final_project;

public class ReviewData {
    private int num; //리뷰번호
    private String title; //리뷰 제목
    private String review; //리뷰 내용

    public ReviewData(int num, String title, String review){
        this.num = num;
        this.title = title;
        this.review=review;
    }
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String rating) {
        this.title = title;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "ReviewData{" +
                "num=" + num +
                ", title='" + title + '\'' +
                ", review='" + review + '\'' +
                '}';
    }
}
