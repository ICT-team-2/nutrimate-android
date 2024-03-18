package com.ict.nutrimate_android.view.board.challenge.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeSuccessItem {
    private String channelId;
    private String messageId;
    private String chatroomId;
    private String userId;
    private String messageType;
    private String chatMessage;
    private String challengeNick; //////////////
    private String chatRoomType;
    private String roomType;
    private String createdDate;
    private String count; ////////////////
}
