package com.example.adi.gjm_app;

/**
 * Created by Adi on 6/8/2015.
 */
public class LoginHistory {
    private String date;
    private int user_id;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public LoginHistory(String date, int user_id) {

        this.date = date;
        this.user_id = user_id;
    }
}
