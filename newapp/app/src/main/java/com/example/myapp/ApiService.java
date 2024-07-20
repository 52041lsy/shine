package com.example.myapp;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public class ApiService {
    public interface Register{
        @FormUrlEncoded
        @POST("user/add")
        Call<Boolean> toRegister(
                @Field("userid") String userid,
                @Field("password") String password,
                @Field("phonenumber") String phonenumber
        );

    }
    public interface Login{
        @POST("user/get")
        Call<User> toLogin(
                @Query("userid") String userid
                //@Field("password") String password
        );
    }

}
