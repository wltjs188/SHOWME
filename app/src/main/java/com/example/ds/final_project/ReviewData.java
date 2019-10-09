package com.example.ds.final_project;

public class ReviewData {
    private int num; //리뷰번호
    private String Rating; //개인 평점
    private String review; //리뷰

    public ReviewData(int num, String Rating,String review){
        this.num = num;
        this.Rating = Rating;
        this.review=review;
    }
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
