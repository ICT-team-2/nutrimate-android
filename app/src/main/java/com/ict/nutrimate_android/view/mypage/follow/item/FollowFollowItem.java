package com.ict.nutrimate_android.view.mypage.follow.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowFollowItem {
    private String recordId;
    private String followeeId;
    private String message;
    private String followerId;
}
