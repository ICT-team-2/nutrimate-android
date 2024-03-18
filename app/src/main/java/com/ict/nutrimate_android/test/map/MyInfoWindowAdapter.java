package com.ict.nutrimate_android.test.map;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.ict.nutrimate_android.R;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View infoView;//커스텀뷰의 루트 뷰
    private String searchAddress;//커스텀 뷰에 설정할 값(찾는 주소)

    public MyInfoWindowAdapter(View infoView, String searchAddress) {
        this.infoView = infoView;
        this.searchAddress = searchAddress;
    }
    //아래 두 메소드 중 하나만 구현:두 메소드 중 하나가 null을 반환하면 다른 하나가 실행되기때문에...
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        ((TextView)infoView.findViewById(com.ict.nutrimate_android.R.id.searchAddress)).setText(searchAddress);
        //생성자에서 초기화 한 커스텀 뷰(인포 윈도우용)을 값 설정한 후 반환
        return infoView;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }
}
