package com.ict.nutrimate_android.view.home.record.food.item.gallery;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodImageAnalyzeItem {
    private String message;
    private String csv_file_path;
    private List<FoodImageAnalyzeFoodsItem> foods;
}
