package com.ict.nutrimate_android.view.mypage.follow.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowFolloweeListItem {
    private String followId;
    private String followeeId;
    private String createdDate;
    private String followerId;
    private String isFollowing;
    private String userId;
    private String userProfile;
    private String userNick;
    private String userIntro;
    private String recordId;
}
