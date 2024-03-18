package com.ict.nutrimate_android.view.home.record.food;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainRecordFoodItem {
    private String foodName;
    private int foodKcal;
    private int foodId;
    private int recordIntake;

    private int foodCarbo;
    private int foodProtein;
    private int foodProvi;
}
