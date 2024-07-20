package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class User implements Serializable {
    @Id
    private String userid;
    private String password;
    private String phonenumber;

    public User() {}
    public User(String userid, String password, String phonenumber) {
        this.userid = userid;
        this.password = password;
        this.phonenumber = phonenumber;
    }
    public String getId() {
        return userid;
    }

    public void setId(String id) {
        this.userid = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getnumber() {
        return phonenumber;
    }

    public void setUsernumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "userid='" + userid + '\'' +
                ", password='" + password + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userid, user.userid) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid,password, phonenumber);
    }
}
