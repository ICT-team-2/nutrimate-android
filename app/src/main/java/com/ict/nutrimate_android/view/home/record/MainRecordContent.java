package com.ict.nutrimate_android.view.home.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ict.nutrimate_android.databinding.HomeMainRecordBinding;
import com.ict.nutrimate_android.view.home.record.food.MainRecordFoodActivity;
import com.ict.nutrimate_android.view.home.record.sport.MainRecordSportActivity;

public class MainRecordContent extends Fragment {

    private HomeMainRecordBinding binding;

    //2]onCraeteView() 오버라이딩
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = HomeMainRecordBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        // 식단 입력 분기
        binding.recordFoodBreakfast.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainRecordFoodActivity.class);
            intent.putExtra("menuType", "아침");
            startActivity(intent);
        });
        binding.recordFoodLunch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainRecordFoodActivity.class);
            intent.putExtra("menuType", "점심");
            startActivity(intent);
        });
        binding.recordFoodDinner.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainRecordFoodActivity.class);
            intent.putExtra("menuType", "저녁");
            startActivity(intent);
        });
        binding.recordFoodSnack.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainRecordFoodActivity.class);
            intent.putExtra("menuType", "간식");
            startActivity(intent);
        });

        // 운동 입력
        binding.recordSport.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainRecordSportActivity.class);
            startActivity(intent);
        });

        //루트 뷰 반환
        return view;
    }


    //STEP1.데이타 전송 이벤트 용 인터페이스 정의(프래그먼트 혹은 액티비티로 전송을 위한)
    public interface OnDataTransferListener{
        //data:전송할 데이타. 모든 타입 가능
        void onDataTransfer(String data);
    }
    //STEP2. 인터페이스 타입의 필드 정의
    private OnDataTransferListener onDataTransferListener;
    //STEP3. 다른 프래그먼트로 전송을 위한  세터 정의
    public void setOnDataTransferListener(OnDataTransferListener onDataTransferListener) {
        this.onDataTransferListener = onDataTransferListener;
    }
    //STEP3. 액티비티로 전송을 위한 onAttach 오버라이딩
    //※단,현재 프래그먼트(Content1)가 부착된 액티비티(MainActivity)는 OnDataTransferListener를 구현한다
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.onDataTransferListener = (OnDataTransferListener)context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 프래그먼트는 뷰보다 오래 지속됩니다.
        // 프래그먼트의 onDestroyView() 메서드에서 결합 클래스 인스턴스 참조를 정리해야 합니다.
        binding = null; //인스턴스 참조를 null로 설정
    }
}
