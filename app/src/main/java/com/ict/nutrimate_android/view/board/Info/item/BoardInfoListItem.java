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
public class BoardInfoListItem {
    private String boardTitle;
    private String boardContent;
    private String boardCategory;
    private String boardId;
    private String rank;
    private String userId;
    private String userNick;
    private String createdDate;
    private String boardViewCount;
    private List<String> hashtag;
}
