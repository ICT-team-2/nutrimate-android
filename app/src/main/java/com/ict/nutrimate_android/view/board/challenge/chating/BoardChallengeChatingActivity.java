package com.ict.nutrimate_android.view.board.challenge.chating;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ict.nutrimate_android.databinding.BoardChallengeChatingBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.challenge.BoardChallengeCommentAdapter;
import com.ict.nutrimate_android.view.board.challenge.chating.item.ChallengeChatPrevItem;
import com.ict.nutrimate_android.view.board.challenge.item.ChallengeCommentListItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardChallengeChatingActivity extends AppCompatActivity {

    private BoardChallengeChatingBinding binding;
    private BoardChallengeChatingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BoardChallengeChatingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        int chatRoomId = intent.getIntExtra("ChatingRoom", 1);

        SpringService service = RetrofitClient.getSpringService();
        Call<List<ChallengeChatPrevItem>> call = service.challengechatprev(chatRoomId);
        call.enqueue(new Callback<List<ChallengeChatPrevItem>>() {
            @Override
            public void onResponse(Call<List<ChallengeChatPrevItem>> call, Response<List<ChallengeChatPrevItem>> response) {
                if (response.isSuccessful()) {
                    List<ChallengeChatPrevItem> chatings = response.body();
                    if (chatings != null && !chatings.isEmpty() && BoardChallengeChatingActivity.this != null && binding != null && binding.recyclerView != null) {
                        // RecyclerView에 데이터 추가
                        adapter = new BoardChallengeChatingAdapter(BoardChallengeChatingActivity.this, chatings);
                        binding.recyclerView.setAdapter(adapter);
                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(BoardChallengeChatingActivity.this));
                        Log.i("tag", "챌린지 내용 불러오기 성공");
                    }
                }
            }
            @Override
            public void onFailure(Call<List<ChallengeChatPrevItem>> call, Throwable t) {}
        });
    }
}