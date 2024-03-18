package com.ict.nutrimate_android.view.board.feed.item;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardFeedListItem {
    private String files;
    private String userId; //작성자 번호
    private String userNick; //작성자 닉네임
    private String userProfile; //작성자 프로필
    private String userIntro;
    private String userName;
    private String boardId; //글 번호
    private String boardCategory;
    private String boardTitle;
    private String boardContent; //글 본문
    private String boardThumbnail; //피드 사진
    private String boardViewCount;
    private String boardCreatedDate;
    private String deleted;
    private String blocked;
    private String searchWord;
    private String nowPage;
    private String receivePage;
    private String totalPages;
    private String likeId;
    private String createdDate;
    private String likeCount; //좋아요 개수
    private String checkedLike; //로그인한 유저가 좋아요 체크했으면 1
    private String bookmarkCount; //글의 북마크 개수(안쓰긴 하는데 일단 가져옴)
    private String checkedBookmark; //로그인한 유저가 북마크 체크했으면 1
    private String tagId;
    private String tagName;
    private List<String> hashtag; //해시태그
    private String checkTagId;
    private String cmtId;
    private String cmtContent;
    private String cmtDepth;
    private String cmtRef;
    private String commentCount;
    private String checkedFollowed;
}