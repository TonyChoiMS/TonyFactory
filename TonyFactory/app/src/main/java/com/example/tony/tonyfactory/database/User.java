package com.example.tony.tonyfactory.database;

/**
 * Created by Administrator on 2016-11-28.
 */

public class User {

    public String name;
    public String email;


    //Default 생성자
    public User() {
    }

    //이름, 이메일 파라메터가 있는 생성자
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
