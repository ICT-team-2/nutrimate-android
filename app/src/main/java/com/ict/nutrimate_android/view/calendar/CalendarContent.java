package com.ict.nutrimate_android.view.calendar;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.ict.nutrimate_android.MainActivity;
import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.view.calendar.fcm.MessageActivity;
import com.ict.nutrimate_android.view.calendar.fcm.NotificationPushActivity;
import com.ict.nutrimate_android.databinding.RecordCalendarBinding;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public class CalendarContent extends Fragment {

    private RecordCalendarBinding binding;

    private CalendarView calendarView;
    private TimePicker timePicker;
    private Button setAlarmButton;
    private TextView diaryTextView;

    // 알람
    private NotificationManager notificationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecordCalendarBinding.inflate(inflater, container, false);
        Context context = getActivity(); // Context 가져오기


        super.onCreate(savedInstanceState);
        binding = RecordCalendarBinding.inflate(getLayoutInflater());

        calendarView = binding.calendarView;
        timePicker = binding.timePicker;
        setAlarmButton = binding.setAlarmButton;
        diaryTextView = binding.diaryTextView;

        // calendarView에 선택한 날짜 보여주기
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));
        });

        // 타임피커 오전/오후 표시 설정
        timePicker.setIs24HourView(false);

        // 알림
        binding.pushAlarmButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationPushActivity.class);
            startActivity(intent);
        });

        // 알림 설정 버튼 클릭시 이벤트 처리
        setAlarmButton.setOnClickListener(v -> {
                // 현재 날짜 가져오기
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // 타임피커에서 선택한 시간 가져오기
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                String amPm;

                // 오전/오후 설정
                if (hour >= 12) {
                    amPm = "오후";
                    hour -= 12;
                } else {
                    amPm = "오전";
                }

                // 시간이 0시일 경우 12시로 변경
                if (hour == 0) {
                    hour = 12;
                }

                // 토스트로 시, 분, 오전/오후 출력
                String dateMessage="";
                String timeMessage = String.format("%s %s시 %s분", amPm, hour, minute);

                if (!diaryTextView.getText().equals("")) {
                    String[] seletedDate = diaryTextView.getText().toString().split(" / ");
                    dateMessage = String.format("%s년 %s월 %s일 ", seletedDate[0], seletedDate[1], seletedDate[2]);
                }
                else {
                    dateMessage = String.format("%s년 %s월 %s일 ", year, month + 1, day);
                }

                calendar.set(year, month, day, hour, minute);
                setAlarm(dateMessage, timeMessage, calendar);
        });

        // 알람
        //시스템 서비스로 NotificationManager객체 얻기
        notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        //오레오부터 아래 코드 추가해야 함 시작
        NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID","CHANNEL_NAME",NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);//스마트폰에 노티가 도착했을때 빛을 표시할지 안할지 설정
        notificationChannel.setLightColor(Color.RED);//위 true설정시 빛의 색상
        notificationChannel.enableVibration(true);//노티 도착시 진동 설정
        notificationChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,100});//진동 시간(1000분의 1초)
        //노피케이션 매니저와 연결
        notificationManager.createNotificationChannel(notificationChannel);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    // 알람 설정 메소드
    private void setAlarm(String dateMessage, String timeMessage, Calendar calendar) {

        // 현재 시간 가져오기
        Calendar currentTime = Calendar.getInstance();
        // 설정된 알람 시간 가져오기
        int alarmYear = calendar.get(Calendar.YEAR);
        int alarmMonth = calendar.get(Calendar.MONTH);
        int alarmDay = calendar.get(Calendar.DAY_OF_MONTH);
        int alarmHour = timePicker.getHour();
        int alarmMinute = timePicker.getMinute();

        if (!diaryTextView.getText().equals("")) {
            // diaryTextView가 비어있지 않다면 선택한 날짜를 가져온다
            String[] seletedDate = diaryTextView.getText().toString().split(" / ");
            alarmYear = Integer.parseInt(seletedDate[0]);
            alarmMonth = Integer.parseInt(seletedDate[1]);
            alarmDay = Integer.parseInt(seletedDate[2]);
        }

        // 이전 시간 비교를 위해 calendar에 선택된 날짜 및 시간 세팅
        calendar.set(Calendar.YEAR,alarmYear);
        calendar.set(Calendar.MONTH,alarmMonth-1);
        calendar.set(Calendar.DAY_OF_MONTH,alarmDay);
        calendar.set(Calendar.HOUR_OF_DAY,alarmHour);
        calendar.set(Calendar.MINUTE,alarmMinute);

        // 오늘 날짜 선택
        if (diaryTextView.getText().equals("") && currentTime.get(Calendar.HOUR_OF_DAY)==alarmHour &&
                currentTime.get(Calendar.MINUTE)==alarmMinute ||
                currentTime.get(Calendar.YEAR) == alarmYear &&
                currentTime.get(Calendar.MONTH)+1 == alarmMonth &&
                currentTime.get(Calendar.DAY_OF_MONTH) == alarmDay &&
                currentTime.get(Calendar.HOUR_OF_DAY) == alarmHour &&
                currentTime.get(Calendar.MINUTE) == alarmMinute) {
            // 오늘날짜 선택 + 현재 시간이면 알람과 토스트 메시지 출력
            NotificationCompat.Builder builder = createNotificationBuilder();
            builder.setContentIntent(createPendingIntent());
            Notification notification = builder.build();
            notificationManager.notify(0, notification);
            Toast.makeText(getActivity(), "현재 시간입니다. 알람을 보내드렸어요!", Toast.LENGTH_SHORT).show();
        } else if (diaryTextView.getText().equals("") && currentTime.get(Calendar.HOUR_OF_DAY)<alarmHour ||
                        diaryTextView.getText().equals("") && currentTime.get(Calendar.HOUR_OF_DAY)<=alarmHour &&
                        currentTime.get(Calendar.MINUTE)<alarmMinute) {
            // 알람 완료 토스트 메시지 출력
            String toastMessage = dateMessage + timeMessage + "에 알람이 설정되었습니다!";
            Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
        } else if (currentTime.after(calendar)) {
            // 선택된 시간이 현재 시간보다 이전인 경우
            Toast.makeText(getActivity(), "현재 시간보다 이전의 시간을 선택할 수 없습니다!", Toast.LENGTH_SHORT).show();
        } else {
            // 알람 완료 토스트 메시지 출력
            String toastMessage = dateMessage + timeMessage + "에 알람이 설정되었습니다!";
            Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
        }

        Log.i("tagaa","currentTime.get(Calendar.HOUR_OF_DAY)"+currentTime.get(Calendar.HOUR_OF_DAY)+
                "\ntimePicker.getHour()"+timePicker.getHour()+
                "\ncurrentTime.get(Calendar.MINUTE)"+currentTime.get(Calendar.MINUTE)+
                "\nalarmMinute"+alarmMinute
        );

    }//////////////////////setAlarm

    private NotificationCompat.Builder createNotificationBuilder(){
        return new NotificationCompat.Builder(getActivity(),"CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_instagram_notification)//노티 도착시 상태바에 표시되는 아이콘
                .setContentTitle("Nutri Mate")//노티 드래그시 보이는 제목
                .setContentText("약 복용 시간이에요")//노티 드래그시 보이는 내용
                .setAutoCancel(true)//노티 드래그후 클릭시 상태바에서 자동으로 사라지도록 설정
                .setWhen(SystemClock.currentThreadTimeMillis())//노티 전달 시간
                .setDefaults(Notification.DEFAULT_VIBRATE);//노티시 알림 방법
    }
    private PendingIntent createPendingIntent(){
        // 상태바에 표시된 알람 클릭시 Notification객체에 등록할 인텐트 생성
        Intent intent = new Intent(getActivity(), MessageActivity.class);
        return PendingIntent.getActivity(getActivity(),0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
    }

}
