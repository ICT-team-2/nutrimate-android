package com.ict.nutrimate_android.view.board.view;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.BoardViewBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.boarditem.BoardViewPlusItem;
import com.ict.nutrimate_android.view.board.comment.BoardCommentActivity;
import com.ict.nutrimate_android.view.board.view.item.BoardFoodViewItem;
import com.ict.nutrimate_android.view.board.view.item.BoardViewItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.ict.nutrimate_android.view.mypage.follow.item.FollowFollowItem;
import com.ict.nutrimate_android.view.mypage.follow.item.FollowUnfollowItem;
import com.ict.nutrimate_android.view.mypage.follow.page.FollowPageActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardViewActivity extends AppCompatActivity {
    private BoardViewBinding binding;
    private boolean isLiked = false; // 좋아요 플래그
    private boolean isBookmarked = false; // 북마크 플래그
    private ValueAnimator animator ; // 애니메이션
    private String boardId;
    private int userId; // 유저 아이디
    private String boardUserId; // 글 작성자 아이디


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BoardViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(this);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        // 인텐트에서 데이터 가져오기
        Intent intent = getIntent();
        if (intent != null) {
            boardId = intent.getStringExtra("boardId");
            boardUserId = intent.getStringExtra("boardUserId");
            //Toast.makeText(this, "boardId:"+boardId, Toast.LENGTH_SHORT).show();
            // Retrofit을 사용하여 서버에서 해당 boardId에 대한 조회수 +1
            putBoardViewCountPlus(boardId);
            // Retrofit을 사용하여 서버에서 해당 boardId에 대한 상세 정보 가져오기
            String food = intent.getStringExtra("food");
            if (food != null && !food.isEmpty()){ //식단 게시판
                // 프로필 사진 (안가져옴)
                binding.boardViewProfile.setVisibility(View.GONE);
                // 팔로우 체크 불러오지않음
                binding.followBtn.setVisibility(View.GONE);
                if (boardUserId==null || userId == Integer.parseInt(boardUserId)) {
                    binding.editBtn.setVisibility(View.VISIBLE);
                    binding.deleteBtn.setVisibility(View.VISIBLE);
                }
                getBoardFoodDetail(boardId);
            } else { // 피드 게시판
                getBoardDetail(boardId);
            }
        }

        // 유저 프로필 버튼 클릭 이벤트 처리
        binding.boardViewProfile.setOnClickListener(v -> {
            Log.i("tag","boardUserId:"+boardUserId);
            if (boardUserId!=null) {
                Intent intent2 = new Intent(BoardViewActivity.this, FollowPageActivity.class);
                intent2.putExtra("follow", boardUserId);
                startActivity(intent2);
            }
        });

        // 팔로우 버튼 클릭 이벤트 처리
        binding.followBtn.setOnClickListener(v -> {
            String txt = binding.followBtn.getText().toString();
            if (txt.equals("팔로우")) { //다시 팔로우한다
                binding.followBtn.setText("취소");
                Map<String, Object> data = new HashMap<>();
                data.put("followerId", userId); //내 userId
                data.put("followeeId", boardUserId); //상대의 userId
                SpringService service = RetrofitClient.getSpringService();
                Call<FollowFollowItem> call = service.followfollow(data);
                call.enqueue(new Callback<FollowFollowItem>() {
                    @Override
                    public void onResponse(Call<FollowFollowItem> call, Response<FollowFollowItem> response) {
                        if (response.isSuccessful()) {
                            FollowFollowItem viewItem = response.body();
                            if (viewItem != null) {
                                Log.i("tag","팔로우 성공. message:"+viewItem.getMessage());
                            }
                        } else {
                            Log.e("tag", "팔로우 실패");
                        }
                    }
                    @Override
                    public void onFailure(Call<FollowFollowItem> call, Throwable t) {
                        Log.e("tag", "팔로우 실패", t);
                    }
                });
            } else { // 팔로우를 삭제한다
                binding.followBtn.setText("팔로우");
                SpringService service = RetrofitClient.getSpringService();
                Call<FollowUnfollowItem> call = service.followunfollow(userId,Integer.parseInt(boardUserId));
                call.enqueue(new Callback<FollowUnfollowItem>() {
                    @Override
                    public void onResponse(Call<FollowUnfollowItem> call, Response<FollowUnfollowItem> response) {
                        if (response.isSuccessful()) {
                            FollowUnfollowItem viewItem = response.body();
                            if (viewItem != null) {
                                Log.i("tag","팔로우 취소. message:"+viewItem.getMessage());
                            }
                        } else {
                            Log.e("tag", "팔로우 삭제 실패");
                        }
                    }
                    @Override
                    public void onFailure(Call<FollowUnfollowItem> call, Throwable t) {
                        Log.e("tag", "팔로우 삭제 실패", t);
                    }
                });
            }
        });

        /** 좋아요, 댓글, 북마크 이벤트 처리 **/
        // 좋아요 클릭 이벤트 처리
        binding.likeBtn.setOnClickListener(v -> {
            Log.i("tag","isLiked:"+isLiked);
            Log.i("tag","isBookmarked:"+isBookmarked);
            if (!isLiked) { // 아직 좋아요 버튼을 누르지 않았을 때
                animator = ValueAnimator.ofFloat(0f, 0.5f);
                isLiked = true;
            } else { // 이미 좋아요 버튼이 눌려있는 상태일 때
                animator = ValueAnimator.ofFloat(0.5f, 1f);
                isLiked = false;
            }
            animator.setDuration(1800);
            animator.addUpdateListener(animation ->
                    binding.likeBtn.setProgress((Float) animation.getAnimatedValue())
            );
            // 좋아요 추가/해제
            boardlikepush();
            animator.start();
        });

        // 댓글 클릭 이벤트 처리
        binding.viewComment.setOnClickListener(v -> {
            Intent intent3 = new Intent(BoardViewActivity.this, BoardCommentActivity.class);
            intent3.putExtra("boardId",boardId);
            startActivity(intent3);
        });

        // 북마크 클릭 이벤트 처리
        binding.backmarkBtn.setOnClickListener(v -> {
            ValueAnimator animator;
            Log.i("tag","isLiked:"+isLiked);
            Log.i("tag","isBookmarked:"+isBookmarked);
            if (!isBookmarked) { // 아직 북마크 버튼을 누르지 않았을 때
                animator = ValueAnimator.ofFloat(0f, 0.9f);
                isBookmarked = true;
            } else { // 이미 북마크 버튼이 눌려있는 상태일 때
                animator = ValueAnimator.ofFloat(0.9f, 0f);
                isBookmarked = false;
            }
            animator.setDuration(1800);
            animator.addUpdateListener(animation ->
                    binding.backmarkBtn.setProgress((Float) animation.getAnimatedValue())
            );
            // 북마크 추가/해제
            boardbookmarkpush();
            animator.start();
        });
    }////////////////////////////////onCreate

    // 좋아요 추가/해제 메소드
    private void boardlikepush() {
        SpringService service = RetrofitClient.getSpringService();
        Map<String, Object> data = new HashMap<>();
        data.put("userId",userId);
        data.put("boardId",Integer.parseInt(boardId));
        Call<BoardViewPushItem> call = service.boardlikepush(data);
        call.enqueue(new Callback<BoardViewPushItem>() {
            @Override
            public void onResponse(Call<BoardViewPushItem> call, Response<BoardViewPushItem> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().contains("Delete")) {
                        Log.i("tag", "좋아요 삭제 성공");
                    } else {
                        Log.i("tag", "좋아요 등록 성공");
                    }
                }
            }
            @Override
            public void onFailure(Call<BoardViewPushItem> call, Throwable t) {}
        });
    }

    // 북마크 추가/해제 메소드
    private void boardbookmarkpush() {
        SpringService service = RetrofitClient.getSpringService();
        Map<String, Object> data = new HashMap<>();
        data.put("userId",userId);
        data.put("boardId",Integer.parseInt(boardId));
        Call<BoardViewPushItem> call = service.boardbookmarkpush(data);
        call.enqueue(new Callback<BoardViewPushItem>() {
            @Override
            public void onResponse(Call<BoardViewPushItem> call, Response<BoardViewPushItem> response) {
                if (response.isSuccessful()) {
                    if (response.body().getMessage().contains("Delete")) {
                        Log.i("tag", "북마크 삭제 성공");
                    } else {
                        Log.i("tag", "북마크 등록 성공");
                    }
                }
            }
            @Override
            public void onFailure(Call<BoardViewPushItem> call, Throwable t) {}
        });
    }

    // 해당 boardId에 조회수 +1하는 메소드
    private void putBoardViewCountPlus(String boardId) {
        SpringService service = RetrofitClient.getSpringService();
        Map<String, Object> data = new HashMap<>();
        data.put("boardId",Integer.parseInt(boardId));
        Call<BoardViewPlusItem> call = service.boardviewplus(data);

        call.enqueue(new Callback<BoardViewPlusItem>() {
            @Override
            public void onResponse(Call<BoardViewPlusItem> call, Response<BoardViewPlusItem> response) {
                if (response.isSuccessful()) {
                    Log.i("tagName","조회수 +1 성공");
                }
            }
            @Override
            public void onFailure(Call<BoardViewPlusItem> call, Throwable t) {}
        });
    }/////////////////////////////////////

    // 해당 boardId에 상세 정보 가져오는 메서드 (식단)
    private void getBoardFoodDetail(String boardId) {
        SpringService service = RetrofitClient.getSpringService();
        Call<BoardFoodViewItem> call = service.boardfoodview(Integer.parseInt(boardId),userId);

        call.enqueue(new Callback<BoardFoodViewItem>() {
            @Override
            public void onResponse(Call<BoardFoodViewItem> call, Response<BoardFoodViewItem> response) {
                if (response.isSuccessful()) {
                    BoardFoodViewItem viewItem = response.body();
                    if (viewItem != null) {
                        // 서버에서 받은 데이터를 화면에 적용
                        setBoardFoodDetail(viewItem);
                    }
                }
            }
            @Override
            public void onFailure(Call<BoardFoodViewItem> call, Throwable t) {}
        });
    }//////////////////////
    // 서버에서 가져온 상세 정보를 화면에 설정하는 메서드 (식단)
    private void setBoardFoodDetail(BoardFoodViewItem viewItem) {
        if (viewItem == null) { return; }
        Log.i("tag","boardId:"+viewItem.getBoardId());
        // 프로필 사진 안불러옴
        binding.boardViewProfile.setVisibility(View.GONE);
        // 작성자 닉네임
        binding.boardViewNickname.setText(viewItem.getUserNick() != null ? viewItem.getUserNick() : "");
        // 글 제목
        binding.boardViewTitle.setText(viewItem.getBoardTitle() != null ? viewItem.getBoardTitle() : "");
        // 글 내용
        String boardContent = viewItem.getBoardContent();
        binding.boardViewContent.setText(boardContent != null ? Html.fromHtml(boardContent) : "");

        // 프로필 사진 (안가져옴)
        binding.boardViewProfile.setVisibility(View.GONE);
        // 팔로우 체크 불러오지않음
        binding.followBtn.setVisibility(View.GONE);
        if (boardUserId==null || userId == Integer.parseInt(boardUserId)) {
            binding.editBtn.setVisibility(View.VISIBLE);
            binding.deleteBtn.setVisibility(View.VISIBLE);
        }
        // 식단 글 삭제 처리
        binding.deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setTitle("게시글 삭제");
            builder.setMessage("정말로 이 게시글을 삭제하시겠습니까?");
            builder.setPositiveButton("예", (dialog, which) -> {
                SpringService service = RetrofitClient.getSpringService();
                Call<String> call = service.boardfooddelete(Integer.parseInt(boardId));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(BoardViewActivity.this, "게시글 삭제가 완료되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {}
                });
            });
            builder.setNegativeButton("아니오", null);
            builder.show();
        });

        // 글 사진
        Picasso.get().load(RetrofitClient.NUTRI_SPRING + viewItem.getFbImg())
                .placeholder(R.drawable.test) // 기본 이미지 지정
                .into(binding.detailviewitemImageviewContent);

        // 좋아요, 북마크 초기 설정
        if (Integer.parseInt(viewItem.getCheckedLike()) == 1) { // 좋아요를 안눌렀다면
            binding.likeBtn.setProgress(0.5f);
            isLiked=true;
        }
        if (Integer.parseInt(viewItem.getCheckedBookmark()) == 1) { // 북마크를 안눌렀다면
            binding.backmarkBtn.setProgress(0.9f);
            isBookmarked=true;
        }

        // 해시태그 표시
        List<String> hashtags = viewItem.getTagNameList();
        if (hashtags != null) {
            String hashtagText = TextUtils.join("   # ", viewItem.getTagNameList());
            if (!TextUtils.isEmpty(hashtagText)) {
                hashtagText = "# " + hashtagText;
            }
            binding.boardViewHashtag.setText(hashtagText);
        } else {
            binding.boardViewHashtag.setText(""); // 빈 문자열로 설정
        }
        binding.boardViewLikeCount.setText(viewItem.getLikeCount());
    }/////////////////////////////////


    // 해당 boardId에 상세 정보 가져오는 메서드 (피드)
    private void getBoardDetail(String boardId) {
        SpringService service = RetrofitClient.getSpringService();
        Call<BoardViewItem> call = service.boardview(Integer.parseInt(boardId),userId);

        call.enqueue(new Callback<BoardViewItem>() {
            @Override
            public void onResponse(Call<BoardViewItem> call, Response<BoardViewItem> response) {
                if (response.isSuccessful()) {
                    BoardViewItem viewItem = response.body();
                    if (viewItem != null) {
                        // 서버에서 받은 데이터를 화면에 적용
                        setBoardDetail(viewItem);
                    }
                }
            }
            @Override
            public void onFailure(Call<BoardViewItem> call, Throwable t) {}
        });
    }//////////////////////
    // 서버에서 가져온 상세 정보를 화면에 설정하는 메서드 (피드)
    private void setBoardDetail(BoardViewItem viewItem) {
        if (viewItem == null) { return; }
        Log.i("tag","boardId:"+viewItem.getBoardId());
        // 프로필 사진
        Picasso.get().load(RetrofitClient.NUTRI_SPRING + viewItem.getUserProfile())
                .placeholder(R.drawable.ic_mypage_profile) // 기본 이미지 지정
                .into(binding.boardViewProfile);
        // 작성자 닉네임
        binding.boardViewNickname.setText(viewItem.getUserNick() != null ? viewItem.getUserNick() : "");
        // 글 제목
        binding.boardViewTitle.setText(viewItem.getBoardTitle() != null ? viewItem.getBoardTitle() : "");
        // 글 내용
        String boardContent = viewItem.getBoardContent();
        binding.boardViewContent.setText(boardContent != null ? Html.fromHtml(boardContent) : "");

        if (Integer.parseInt(viewItem.getUserId()) == userId){ //내 글이라면 팔로우 버튼 숨기기
            binding.followBtn.setVisibility(View.GONE);
            binding.editBtn.setVisibility(View.VISIBLE);
            binding.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            if (Integer.parseInt(viewItem.getCheckedFollowed()) == 0) { //팔로우가 아니라면
                binding.followBtn.setText("팔로우");
            } else binding.followBtn.setText("취소");
        }
        // 피드 글 삭제 처리
        binding.deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setTitle("게시글 삭제");
            builder.setMessage("정말로 이 게시글을 삭제하시겠습니까?");
            builder.setPositiveButton("예", (dialog, which) -> {
                SpringService service = RetrofitClient.getSpringService();
                Call<String> call = service.boardfooddelete(Integer.parseInt(boardId));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(BoardViewActivity.this, "게시글 삭제가 완료되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {}
                });
            });
            builder.setNegativeButton("아니오", null);
            builder.show();
        });

        // 글 사진
        Log.i("tag","viewItem.getBoardThumbnail():"+viewItem.getBoardThumbnail());
        Picasso.get().load(RetrofitClient.NUTRI_SPRING + viewItem.getBoardThumbnail())
                .placeholder(R.drawable.test) // 기본 이미지 지정
                .into(binding.detailviewitemImageviewContent);

        // 좋아요, 북마크 초기 설정
        if (Integer.parseInt(viewItem.getCheckedLike()) == 1) { // 좋아요를 안눌렀다면
            binding.likeBtn.setProgress(0.5f);
            isLiked=true;
        }
        if (Integer.parseInt(viewItem.getCheckedBookmark()) == 1) { // 북마크를 안눌렀다면
            binding.backmarkBtn.setProgress(0.9f);
            isBookmarked=true;
        }

        // 해시태그 표시
        List<String> hashtags = viewItem.getHashtag();
        if (hashtags != null) {
            String hashtagText = TextUtils.join("   # ", viewItem.getHashtag());
            if (!TextUtils.isEmpty(hashtagText)) {
                hashtagText = "# " + hashtagText;
            }
            binding.boardViewHashtag.setText(hashtagText);
        } else {
            binding.boardViewHashtag.setText(""); // 빈 문자열로 설정
        }
        binding.boardViewLikeCount.setText(viewItem.getLikeCount());
    }

}