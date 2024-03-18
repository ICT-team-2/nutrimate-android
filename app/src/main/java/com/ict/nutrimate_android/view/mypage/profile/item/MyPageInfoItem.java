package com.ict.nutrimate_android.view.mypage.profile.item;

import com.ict.nutrimate_android.view.board.Info.item.BoardInfoListItem;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageInfoItem {
    private boolean bookmark;
    private boolean mypage;
    private int totalPage;
    private String boardCategory;
    private String searchColumn;
    private String searchKeyword;
    private int receivePage;
    private int userId;
    private List<BoardInfoListItem> infoBoardList;
    private int nowPage;
}
