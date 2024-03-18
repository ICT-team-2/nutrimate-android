package com.ict.nutrimate_android.view.mypage.feed;

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

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.MypageFeedBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.Info.BoardInfoAdapter;
import com.ict.nutrimate_android.view.board.Info.item.BoardInfoListItem;
import com.ict.nutrimate_android.view.board.feed.BoardFeedAdapter;
import com.ict.nutrimate_android.view.board.feed.item.BoardFeedItem;
import com.ict.nutrimate_android.view.board.feed.item.BoardFeedListItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.ict.nutrimate_android.view.mypage.MyPageItem;

import java.util.List;
import java.util.Vector;
import java.util.stream.IntStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageFeedFragment extends Fragment {
    private Context context;
    private MypageFeedBinding binding;
    private int userId;
    private MyPageFeedAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        binding = MypageFeedBinding.inflate(inflater,parent,false);
        View view = binding.getRoot();

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(context);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        SpringService service = RetrofitClient.getSpringService();
        Call<BoardFeedItem> call = service.mypagefeedlist(userId, 1, 99,userId,true);

        call.enqueue(new Callback<BoardFeedItem>() {
            @Override
            public void onResponse(Call<BoardFeedItem> call, Response<BoardFeedItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BoardFeedItem boardFeedItems = response.body();
                    List<BoardFeedListItem> feedItems = boardFeedItems.getFeedList();
                    if (feedItems != null && !feedItems.isEmpty()) {
                        adapter = new MyPageFeedAdapter(getActivity(), feedItems);
                        if (binding != null && binding.recyclerViewFeed != null) {
                            binding.recyclerViewFeed.setLayoutManager(new GridLayoutManager(context, 3));
                            binding.recyclerViewFeed.setAdapter(adapter);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<BoardFeedItem> call, Throwable t) {}
        });
        return view; //전개된 뷰 반환
    }//////////////////////////////onCreateView

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

}

