package com.ict.nutrimate_android.view.home.record.food.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordDietDayItem {
    private int userId;
    private int foodId;
    private String foodName;
    private String foodGroup;
    private int foodCal;
    private int foodIntake;
    private String intakeUnit;
    private int foodCarbo;
    private int foodProtein;
    private int foodProvi;
    private int foodChole;
    private int foodSalt;
    private RecordDietDayListItem record;
    private int dietId;
    private String mealTime;
    private String recordIntake;
    private String serving;
    private String foodIds;
    private String recordIntakes;
}
