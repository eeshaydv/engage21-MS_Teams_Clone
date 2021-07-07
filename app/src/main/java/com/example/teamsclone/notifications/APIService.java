package com.example.teamsclone.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:Key="
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body sender body);

}
