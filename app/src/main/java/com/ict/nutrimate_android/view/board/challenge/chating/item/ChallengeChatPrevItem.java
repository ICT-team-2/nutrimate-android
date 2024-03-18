package com.ict.nutrimate_android.view.board.challenge.chating.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeChatPrevItem {
    private String channelId;
    private String messageId;
    private String chatroomId;
    private int userId; //
    private String messageType; // "ENTER"
    /*
        CHAT, // 채팅
        ENTER, // 채팅 입장
        LEAVE, // 채탕 나가기
        CHALLENGE
     */
    private String chatMessage; // "메시지'
    private String challengeNick; // "닉네임"
    private String chatRoomType;
    private String roomType;
    private String createdDate; // 입력일
    private String count;
}
