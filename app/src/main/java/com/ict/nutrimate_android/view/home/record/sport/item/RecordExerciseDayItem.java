package com.ict.nutrimate_android.view.home.record.sport.item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordExerciseDayItem {
    private int userId;
    private int sportId;
    private String sportName;
    private int sportMet;
    private RecordExerciseDayListItem record;
    private int exerciseId;
    private int sportCal;
    private int sportWeight;
    private int sportSet;
    private int sportTime;
}
