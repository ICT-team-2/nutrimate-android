package com.ict.nutrimate_android.view.calendar.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Map<String,String> pushMessage= new HashMap<>();
        Log.i("tag","from:"+message.getFrom());
        // 알림 메시지 : title,body 변수값 변경x
        if(message.getNotification() !=null){
            String title=message.getNotification().getTitle();
            String body = message.getNotification().getBody();
            Log.i("tag","알림 제목:"+title);
            Log.i("tag","알림 텍스트:"+body);
            pushMessage.put("title",title);
            pushMessage.put("body",body);
        }
        // 상태바에 알림을 표시하기 위한 메소드 호출
        showNotification(pushMessage);
    }///////////////////////////////

    // 상태바에 알림을 표시하기 위한 메소드 + 알림 클릭 이벤트 처리
    private void showNotification(Map<String, String> pushMessage) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //오레오부터 아래 코드 추가해야 함 시작
        NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID", "CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);//스마트폰에 노티가 도착했을때 빛을 표시할지 안할지 설정
        notificationChannel.setLightColor(Color.RED);//위 true설정시 빛의 색상
        notificationChannel.enableVibration(true);//노티 도착시 진동 설정
        notificationChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,100});//진동 시간(1000분의 1초)

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(this);
        int userId = userSessionManager.getUserIdFromSharedPreferences();

        Intent intent = new Intent(getApplicationContext(),MessageActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("title",pushMessage.get("title"));
        bundle.putString("body",pushMessage.get("body"));
        bundle.putInt("userId",userId);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // 제목과 내용을 기반으로 알림 빌더를 생성
        NotificationCompat.Builder builder = createNotificationCompatBuilder(pushMessage.get("title"),pushMessage.get("body"));
        // 알림이 터치되었을 때 실행할 작업을 설정 설정
        builder.setContentIntent(pendingIntent);
        Notification notification=builder.build();
        // 노피케이션 매니저와 연결
        notificationManager.createNotificationChannel(notificationChannel);

        notificationManager.notify(1,notification);
    }//////////////////showNotification

    private NotificationCompat.Builder createNotificationCompatBuilder(String title,String content){
        return new NotificationCompat.Builder(this,"CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_instagram_notification)//노티 도착시 상태바에 표시되는 아이콘
                .setContentTitle(title)//노티 드래그시 보이는 제목
                .setContentText(content)//노티 드래그시 보이는 내용
                .setAutoCancel(true)//노티 드래그후 클릭시 상태바에서 자동으로 사라지도록 설정
                .setWhen(SystemClock.currentThreadTimeMillis())//노티 전달 시간
                .setDefaults(Notification.DEFAULT_VIBRATE);//노티시 알림 방법
    }/////////////////

    // 토큰이 변경될때마다 호출되는 콜백 메소드 - FCM에서 발행된 토큰을 우리의 서버로 전송
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.i("tag","FCM에서 발행한 토큰:"+token);
        // 생성된 토큰을 내 서버에 보내기
        sendNewTokenToMyServer(token);
    }/////////////////////

    // 서버로 토큰을 보내 데이타베이스에 저장하기 위한 HTTP 요청용 메소드
    private void sendNewTokenToMyServer(String token) {
        Log.i("tag","인자로 받은 토큰:"+token);

        SpringService service= RetrofitClient.getSpringService();
        Map<String,String> data = new HashMap<>();
        data.put("token",token);
        Call<Map<String,String>> call= service.newToken(data);
        Log.i("tag","요청 보내기");

        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                Log.i("tag",response.isSuccessful() ? response.body().toString():response.errorBody().toString());
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                t.printStackTrace();
            }
        });
        Log.i("tag","요청 보내기 끝");
    }///////////////////

}