package com.ict.nutrimate_android.view.mypage.follow.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.MypageMainFollowBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.Info.item.BoardInfoListItem;
import com.ict.nutrimate_android.view.mypage.profile.MyPageProfileItem;
import com.ict.nutrimate_android.view.mypage.profile.item.MyPageInfoItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowPageActivity extends Activity {
    private MypageMainFollowBinding binding;
    private int followUserId;
    private FollowPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MypageMainFollowBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // 유저 id값 받기
        Intent intent = getIntent();
        if (intent != null) {
            followUserId = Integer.parseInt(intent.getStringExtra("follow"));
        }

        // 서버에서 받은 데이터를 화면에 적용
        getMyPageDetail(followUserId);

        SpringService service = RetrofitClient.getSpringService();
        Call<MyPageInfoItem> call = service.mypageinfolist(followUserId, 1, 99);

        call.enqueue(new Callback<MyPageInfoItem>() {
            @Override
            public void onResponse(Call<MyPageInfoItem> call, Response<MyPageInfoItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MyPageInfoItem myPageInfoItems = response.body();
                    List<BoardInfoListItem> infoItems = myPageInfoItems.getInfoBoardList();
                    if (FollowPageActivity.this != null && infoItems != null && !infoItems.isEmpty() && binding != null && binding.recyclerViewFollow != null) {
                        adapter = new FollowPageAdapter(FollowPageActivity.this, infoItems);
                        binding.recyclerViewFollow.setLayoutManager(new GridLayoutManager(FollowPageActivity.this,3));
                        binding.recyclerViewFollow.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<MyPageInfoItem> call, Throwable t) {}
        });
    }

    // 팔로워페이지의 정보 불러오기
    private void getMyPageDetail(int userId) {
        SpringService service = RetrofitClient.getSpringService();
        Call<MyPageProfileItem> call = service.mypageprofile(userId);

        call.enqueue(new Callback<MyPageProfileItem>() {
            @Override
            public void onResponse(Call<MyPageProfileItem> call, Response<MyPageProfileItem> response) {
                if (response.isSuccessful()) {
                    MyPageProfileItem viewItem = response.body();
                    if (viewItem != null) {
                        // 서버에서 받은 데이터를 화면에 적용
                        setMyPageDetail(viewItem);
                    }
                }
            }
            @Override
            public void onFailure(Call<MyPageProfileItem> call, Throwable t) {}
        });
    }

    // Retrofit을 사용하여 서버에서 해당 userId에 대한 프로필 정보 가져오는 메서드
    private void setMyPageDetail(MyPageProfileItem viewItem) {
        // 프로필 사진
        Picasso.get().load(RetrofitClient.NUTRI_SPRING+viewItem.getUserProfile())
                .placeholder(R.drawable.ic_mypage_profile) // 기본 이미지 지정
                .into(binding.mypageProfile);
        binding.postCount.setText(viewItem.getPostCount()); // 게시물 수
        binding.followerCount.setText(viewItem.getFollowerCount()); // 팔로워 수
        binding.followingCount.setText(viewItem.getFollowingCount()); // 팔로잉 수
        binding.userNick.setText(viewItem.getUserNick()); // 유저 닉네임
        binding.userIntro.setText(viewItem.getUserIntro()); // 자기소개
    }

}
