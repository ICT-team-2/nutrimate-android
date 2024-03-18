package com.ict.nutrimate_android.view.board.comment.item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardCommentItem {
    private String cmtId;
    private String boardId;
    private String userId;
    private String cmtContent;
    private String createdDate;
    private String cmtDepth;
    private String deleted;
    private String allDeleted;
    private String cmtRef;
    private String userName;
    private String userNick;
    private List<BoardCommentItem> replies;
    private String mycmtId;
    private String userProfile;
}
