package com.ict.nutrimate_android.view.home.statistics.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordTotalCalories {
    private int userId;
    private int totalSportTime;
    private int totalSportCal;
    private int totalDietCal;
    private int totalCarbo;
    private int totalProtein;
    private int totalProvi;
    private int recommendCal;
    private int recommendCarbo;
    private int recommendProtein;
    private int recommendProvi;
    private String doDate;
    private String startDate;
    private String endDate;
}
