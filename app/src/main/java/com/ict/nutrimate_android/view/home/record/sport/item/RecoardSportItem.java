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
public class RecoardSportItem {
    private String searchWord;
    private int nowPage;
    private int totalPage;
    private int receivePage;
    private int totalCount;
    private int userId;
    private List<RecoardSportListItem> sportList;
}
