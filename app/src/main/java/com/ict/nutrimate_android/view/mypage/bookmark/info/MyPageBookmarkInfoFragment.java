package com.ict.nutrimate_android.view.mypage.bookmark.info;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ict.nutrimate_android.databinding.MypageBookmarkFoodSportBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.Info.item.BoardInfoListItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.ict.nutrimate_android.view.mypage.profile.item.MyPageInfoItem;

import java.util.List;
import java.util.Vector;
import java.util.stream.IntStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageBookmarkInfoFragment extends Fragment {

    private Context context;
    private MypageBookmarkFoodSportBinding binding;
    private int userId;
    private MyPageBookmarkInfoAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        binding = MypageBookmarkFoodSportBinding.inflate(inflater,parent,false);
        View view = binding.getRoot();

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(context);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        SpringService service = RetrofitClient.getSpringService();
        Call<MyPageInfoItem> call = service.mypageinfobookmark(userId, 1, 99);

        call.enqueue(new Callback<MyPageInfoItem>() {
            @Override
            public void onResponse(Call<MyPageInfoItem> call, Response<MyPageInfoItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyPageInfoItem myPageInfoItems = response.body();
                    List<BoardInfoListItem> infoItems = myPageInfoItems.getInfoBoardList();
                    Log.i("tag","북마크 여기까지");
                    if (infoItems != null && !infoItems.isEmpty()) {
                        Log.i("tag","북마크한 아이템:"+infoItems.get(0).getBoardTitle());
                        adapter = new MyPageBookmarkInfoAdapter(context, infoItems);
                        if (binding != null && context != null) {
                            binding.recyclerViewBookmarkInfo.setLayoutManager(new GridLayoutManager(context, 3));
                            binding.recyclerViewBookmarkInfo.setAdapter(adapter);
                        }
                    }
                } else {
                    Log.i("tag","통신 실패");
                }
            }
            @Override
            public void onFailure(Call<MyPageInfoItem> call, Throwable t) {
                Log.e("tag",t.getMessage());
            }
        });
        return view;
    }////////////////////////////////onCreateView

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

}

