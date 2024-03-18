package com.ict.nutrimate_android.view.home.record.food.item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecoardFoodItem {
    private String searchWord;
    private String nowPage;
    private String totalPage;
    private String receivePage;
    private String totalCount;
    private String userId;
    private List<RecoardFoodListItem> foodList;
}
