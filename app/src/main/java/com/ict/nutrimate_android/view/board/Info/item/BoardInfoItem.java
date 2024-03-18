package com.ict.nutrimate_android.view.board.Info.item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardInfoItem {
    private boolean bookmark;
    private boolean mypage;
    private String totalPage;
    private String boardCategory;
    private String searchColumn;
    private List<BoardInfoListItem> boardList;

    private String searchKeyword;
    private String receivePage;
    private String userId;
    private String nowPage;
}
