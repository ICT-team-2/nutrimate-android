package com.ict.nutrimate_android.view.board.view.item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardFoodViewItem {
    private String userNick;
    private int boardId;
    private int userId;
    private String label;
    private String boardCategory;
    private String boardTitle;
    private String boardContent;
    private String boardViewCount;
    private String createdDate;
    private String deleted;
    private String blocked;
    private String searchUser;
    private String searchContent;
    private String searchTitle;
    private String searchHashTag;
    private String updateViewCount;
    private List<String> foodId;
    private String fbImg;
    private List<BoardFoodListViewItem> foodList;
    private String totalPage;
    private String nowPage;
    private String receivePage;
    private String prevBoardId;
    private String nextBoardId;
    private String checkedLike;
    private String checkedBookmark;
    private String bookmarkCount;
    private String likeCount;
    private List<String> tagNameList;
    private String tagName;
    private String tagId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class BoardFoodListViewItem {
        private String userId;
        private String foodId;
        private String foodName;
        private String foodGroup;
        private String foodCal;
        private String foodIntake;
        private String intakeUnit;
        private String foodCarbo;
        private String foodProtein;
        private String foodProvi;
        private String foodChole;
        private String foodSalt;
    }

}
