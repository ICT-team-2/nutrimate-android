package com.ict.nutrimate_android.view.home.record.food.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordDietDayListItem {
    private int userId;
    private int recordId; // 삭제할 때 필요
    private String doDate;
    private String mealTime;
}
