package com.ict.nutrimate_android.view.home.statistics;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.ict.nutrimate_android.databinding.HomeMainStatisticsBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.home.statistics.item.RecordTotalCalories;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainStatisticsContent extends Fragment {

    private HomeMainStatisticsBinding binding;
    private BarChart barChart;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = HomeMainStatisticsBinding.inflate(inflater, container, false);
        Context context = getActivity(); // Context 가져오기
        barChart = binding.chart;

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(context);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        // 나의 정보 가져오기
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(new Date());

        SpringService service = RetrofitClient.getSpringService();
        Call<RecordTotalCalories> call = service.recordanalysis(userId,formattedDate);
        call.enqueue(new Callback<RecordTotalCalories>() {
            @Override
            public void onResponse(Call<RecordTotalCalories> call, Response<RecordTotalCalories> response) {
                RecordTotalCalories totalCalories = response.body();
                if (response.isSuccessful() && response.body() != null) {
                    binding.statisticsFoodKcalRecommend.setText(String.valueOf(totalCalories.getRecommendCal()));
                    binding.statisticsFoodKcal.setText(String.valueOf(totalCalories.getTotalDietCal()));
                    binding.statisticsSportTime.setText(String.valueOf(totalCalories.getTotalSportTime()));
                    binding.statisticsSportKcal.setText(String.valueOf(totalCalories.getTotalSportCal()));
                }
            }
            @Override
            public void onFailure(Call<RecordTotalCalories> call, Throwable t) {}
        });

        // 통계 그래프에 표시할 데이터 가져오기
        Call<List<RecordTotalCalories>> call_graph = service.recordtotalcalories(userId,formattedDate,"DAY",7);
        call_graph.enqueue(new Callback<List<RecordTotalCalories>>() {
            @Override
            public void onResponse(Call<List<RecordTotalCalories>> call, Response<List<RecordTotalCalories>> response) {
                if (response.isSuccessful()) {
                    List<RecordTotalCalories> recordTotalCaloriesList = response.body();
                    if (recordTotalCaloriesList != null && !recordTotalCaloriesList.isEmpty()) {
                        List<BarEntry> entries = new ArrayList<>(); // 칼로리
                        List<String> labels = new ArrayList<>(); // 라벨

                        // 데이터 처리 및 차트에 추가
                        for (int i = recordTotalCaloriesList.size() - 1; i >= 0; i--) {
                            RecordTotalCalories record = recordTotalCaloriesList.get(i);
                            float totalCalories = record.getTotalDietCal() - record.getTotalSportCal();
                            Log.i("tag","barChart:"+totalCalories);
                            int index = recordTotalCaloriesList.size() - 1 - i; // 역순으로 인덱스 계산
                            entries.add(new BarEntry(index, totalCalories));
                            labels.add(String.valueOf(record.getStartDate())); // 시작 날짜를 라벨로 사용
                        }

                        // 차트에 데이터 설정
                        BarDataSet dataSet = new BarDataSet(entries, "");
                        BarData barData = new BarData(dataSet);
                        // 막대바 컬러 설정
                        dataSet.setColors(Color.parseColor("#81A3FB"));

                        // 바 차트 설정
                        barChart.setDrawGridBackground(false);
                        barChart.setDrawBarShadow(false);
                        barChart.setDrawBorders(false);
                        barChart.getDescription().setEnabled(false);
                        barChart.setTouchEnabled(true); // 터치 이벤트 활성화
                        barChart.setData(barData);

                        // x축 설정
                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setGranularity(1f);
                        xAxis.setTextColor(Color.BLACK); // X축 숫자 색상 설정
                        xAxis.setDrawAxisLine(false);
                        xAxis.setDrawGridLines(false);
                        // 오늘의 날짜를 가져오기
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd", Locale.getDefault());
                        String today = sdf.format(calendar.getTime());
                        // 1일 전부터 7일 전까지의 날짜 가져오기
                        String[] previousDays = new String[7];
                        previousDays[0] = "오늘"; // 첫 번째 인덱스에 "오늘" 저장
                        for (int i = 1; i < previousDays.length; i++) {
                            calendar.add(Calendar.DAY_OF_MONTH, -1); // 하루씩 감소
                            previousDays[i] = sdf.format(calendar.getTime()); // 현재 날짜를 저장
                        }

                        // X축 레이블 설정
                        xAxis.setValueFormatter(new IndexAxisValueFormatter(previousDays));

                        // 왼쪽 Y축 설정
                        YAxis leftAxis = barChart.getAxisLeft();
                        leftAxis.setDrawAxisLine(false);
                        leftAxis.setTextColor(Color.BLACK); //왼쪽 숫자 색상

                        // 오른쪽 Y축 설정
                        YAxis rightAxis = barChart.getAxisRight();
                        rightAxis.setDrawAxisLine(false);
                        rightAxis.setDrawLabels(false); // 오른쪽 숫자 없애기

                        // 범례 설정
                        Legend legend = barChart.getLegend();
                        legend.setEnabled(false);

                        // 차트 갱신
                        barChart.invalidate();

                    } else {
                        Log.e("Error", "데이터가 비어 있습니다.");
                    }
                } else {
                    Log.e("Error", "데이터를 가져오지 못했습니다. 상태 코드: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<RecordTotalCalories>> call, Throwable t) {}
        });
        return binding.getRoot();
    }

}