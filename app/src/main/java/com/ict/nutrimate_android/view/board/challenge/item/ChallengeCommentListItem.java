package com.ict.nutrimate_android.view.board.challenge.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeCommentListItem {
    private int cmtId;
//    private int userId;
    private String cmtContent;
//    private String challengeNick;
    private String userNick;
    private String createdDate;
    private String label;
    private String nowPage;
    private String userProfile;
    private String deleted;
    private String cmtDepth;
    private String cmtRef;
//    private String cmtRefWriter;
}
