package com.ict.nutrimate_android.view.board.view.sport;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardSportViewItem {
    private int boardId;
    private int userId;
    private String boardCategory;
    private String boardTitle;
    private String boardContent;
    private String boardThumbnail;
    private String boardViewcount;
    private String createdDate;
    private String deleted;
    private String blocked;
    private String userNick;
    private String idRank;
    private String searchUser;
    private String searchTitle;
    private String searchContent;
    private String searchTag;
    private String prevBoardId;
    private String nextBoardId;
    private String likeCount;
    private String checkedLike;
    private String bookmarkCount;
    private String checkedBookmark;
    private String tagId;
    private String tagName;
    private String hashtag;
    private String checkTagId;
    private List<String> tagNameList;
    private String mapPaths;
    private String mapDistances;
    private double mapCenterLat;
    private double mapCenterLng;
    private int mapZoomlevel;
}
