package com.ict.nutrimate_android.view.login.join.join5;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinItem {
    private int joinAllergyResId;
    private String joinAllergyName;
    private boolean selected; // 추가: 선택 여부를 나타내는 변수

    // 선택 상태를 토글하는 메서드
    public void toggleSelected() {
        selected = !selected;
    }

    // 선택 상태를 토글하는 메서드
    public JoinItem(int joinAllergyResId, String joinAllergyName) {
        this.joinAllergyResId = joinAllergyResId;
        this.joinAllergyName = joinAllergyName;
        this.selected = false; // 기본적으로 선택되지 않도록 설정
    }
}
