package com.ict.nutrimate_android.view.home.record.food.item;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodRecord {
    private Record record;
    private int foodId;
    private String mealTime;
    private int recordIntake;
}
