package com.ict.nutrimate_android.view.mypage.info;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ict.nutrimate_android.databinding.MypageFoodSportBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.Info.item.BoardInfoListItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.ict.nutrimate_android.view.mypage.profile.item.MyPageInfoItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyPageInfoFragment extends Fragment {
    private Context context;
    private MypageFoodSportBinding binding;
    private int userId;
    private MyPageInfoAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        binding = MypageFoodSportBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(context);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        SpringService service = RetrofitClient.getSpringService();
        Call<MyPageInfoItem> call = service.mypageinfolist(userId, 1, 99);

        call.enqueue(new Callback<MyPageInfoItem>() {
            @Override
            public void onResponse(Call<MyPageInfoItem> call, Response<MyPageInfoItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyPageInfoItem myPageInfoItems = response.body();
                    List<BoardInfoListItem> infoItems = myPageInfoItems.getInfoBoardList();
                    if (infoItems != null && !infoItems.isEmpty()) {
                        adapter = new MyPageInfoAdapter(context, infoItems);
                        if (binding != null && binding.recyclerViewInfo != null) {
                            binding.recyclerViewInfo.setLayoutManager(new GridLayoutManager(context,3));
                            binding.recyclerViewInfo.setAdapter(adapter);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<MyPageInfoItem> call, Throwable t) {}
        });
        return view; //전개된 뷰 반환
    }//////////////////////////onCreateView

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

}