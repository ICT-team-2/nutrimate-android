package com.ict.nutrimate_android.view.home.record.sport.item;

import com.ict.nutrimate_android.view.home.record.food.item.Record;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SportRecord {
    private Record record; // 똑같으니까 식단 dto 씀
    private int sportId;
    private int sportTime; //분단위
    private int sportWeight; //운동 시 몸무게 kg단위
}
