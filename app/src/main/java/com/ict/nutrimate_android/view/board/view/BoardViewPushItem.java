package com.ict.nutrimate_android.view.board.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardViewPushItem {
    private int boardId;
    private String message;
    private int userId;
}
