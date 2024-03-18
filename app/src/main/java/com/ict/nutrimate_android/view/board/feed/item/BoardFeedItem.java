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
public class BoardFeedItem {
    private List<BoardFeedListItem> feedList; //피드 목록
    private String totalPages; //총 페이지 수
    private String receivePage; //페이지 강 글 개수
    private String nowPage; //현재 페이지
    private String totalRecordCount; //총 글 개수
}
