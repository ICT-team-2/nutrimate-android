package com.ict.nutrimate_android.view.board.comment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.BoardCommentBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.comment.item.BoardCommentItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.ict.nutrimate_android.view.mypage.profile.MyPageProfileItem;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardCommentActivity extends AppCompatActivity {

    private BoardCommentBinding binding;
    private BoardCommentAdapter adapter;
    private String boardId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BoardCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(this);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        // 내 프로필 사진 불러오기
        getMyPageDetail();

        // 프래그먼트에서 전달된 boardId 가져오기
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boardId = extras.getString("boardId");
            // Retrofit을 사용하여 서버에서 해당 boardId에 대한 댓글 목록 가져오기
            getBoardComment(boardId);
        } else {
            // 전달된 boardId가 없는 경우 처리
            Log.i("tagName", "전달된 boardId가 없습니다.");
        }

        // 댓글 입력
        if (binding != null) {
            EditText editText = binding.boardCommentWrite;
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // 엔터키가 눌렸을 때 할 작업을 여기에 추가하세요.
                    String comment = editText.getText().toString();
                    Map<String, Object> data = new HashMap<>();
                    data.put("userId", userId);
                    data.put("boardId", boardId);
                    data.put("cmtContent", comment);
                    SpringService service = RetrofitClient.getSpringService();
                    Call<Map<String,Integer>> call = service.boardcommentwrite(data);

                    call.enqueue(new Callback<Map<String, Integer>>() {
                        @Override
                        public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                            if (response.isSuccessful()) {
                                if (response.body().get("cmtId") != null) {
                                    Log.i("tag","댓글 입력 완료");
                                    // 댓글 입력이 성공적으로 완료되었을 때 새로고침을 수행
                                    getBoardComment(boardId);
                                    editText.setText(""); // 댓글이 성공적으로 입력되면 EditText를 비움
                                    // 키보드를 내림
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<Map<String, Integer>> call, Throwable t) {}
                    });
                    return true; // 처리가 완료되었음을 시스템에 알립니다.
                }
                return false; // 이벤트가 처리되지 않았음을 시스템에 알립니다.
            });
        }

    }////////////////////////////onCreate

    // 내 프로필 사진 불러오기
    private void getMyPageDetail() {
        SpringService service = RetrofitClient.getSpringService();
        Call<MyPageProfileItem> call = service.mypageprofile(userId);

        call.enqueue(new Callback<MyPageProfileItem>() {
            @Override
            public void onResponse(Call<MyPageProfileItem> call, Response<MyPageProfileItem> response) {
                if (response.isSuccessful()) {
                    MyPageProfileItem viewItem = response.body();
                    if (viewItem != null) {
                        Picasso.get().load(RetrofitClient.NUTRI_SPRING + viewItem.getUserProfile())
                                .placeholder(R.drawable.ic_instagram_header_profile) // 기본 이미지 지정
                                .into(binding.boardCommentProfile);
                    }
                }
            }
            @Override
            public void onFailure(Call<MyPageProfileItem> call, Throwable t) {}
        });
    }

    // Retrofit을 사용하여 서버에서 해당 boardId에 대한 댓글 목록 가져오기
    private void getBoardComment(String boardId) {
        SpringService service = RetrofitClient.getSpringService();
        Call<List<BoardCommentItem>> call = service.getComments(Integer.parseInt(boardId)); // userId를 넣어야한다

        call.enqueue(new Callback<List<BoardCommentItem>>() {
            @Override
            public void onResponse(Call<List<BoardCommentItem>> call, Response<List<BoardCommentItem>> response) {
                if (response.isSuccessful()) {
                    List<BoardCommentItem> comments = response.body();
                    if (BoardCommentActivity.this != null && binding != null && binding.recyclerView != null && binding.boardCommentNoComment != null && comments != null) {
                        // 댓글이 있는 경우
                        binding.recyclerView.setVisibility(View.VISIBLE);
                        binding.boardCommentNoComment.setVisibility(View.GONE);
                        // RecyclerView에 데이터 추가
                        adapter = new BoardCommentAdapter(BoardCommentActivity.this, comments);
                        binding.recyclerView.setAdapter(adapter);
                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(BoardCommentActivity.this));
                    } else {
                        // 댓글이 없는 경우
                        binding.recyclerView.setVisibility(View.GONE);
                        binding.boardCommentNoComment.setVisibility(View.VISIBLE);
                    }
                } else {
                    // 서버에서 데이터를 가져오지 못한 경우 처리
                    Log.i("tagName", "서버 연결 실패");
                }
            }
            @Override
            public void onFailure(Call<List<BoardCommentItem>> call, Throwable t) {
                Log.i("tagName","통신 연결 실패:"+t.getMessage());
            }
        });
    }///////////////////////////////

}
