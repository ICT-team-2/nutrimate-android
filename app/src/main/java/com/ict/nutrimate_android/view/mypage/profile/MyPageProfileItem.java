package com.ict.nutrimate_android.view.mypage.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageProfileItem {
    private String userId;
    private String userNick;
    private String userUid;
    private String userProfile;
    private String userIntro;
    private String userWeight;
    private String userHeight;
    private String postCount;
    private String followerCount;
    private String followingCount;
}
