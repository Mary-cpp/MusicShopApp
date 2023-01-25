package com.example.myapp.model;

import java.util.Date;

public class User {
    private String name;
    private String login;
    private String password;
    private Date birthday;

    public User(){}

    public User(String name, String login, String password, Date birthday) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
