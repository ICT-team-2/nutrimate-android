package com.ict.nutrimate_android.view.board.view.sport;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.BoardViewBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.boarditem.BoardViewPlusItem;
import com.ict.nutrimate_android.view.board.comment.BoardCommentActivity;
import com.ict.nutrimate_android.view.board.view.BoardViewPushItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.ict.nutrimate_android.view.mypage.follow.page.FollowPageActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardSportViewActivity extends AppCompatActivity implements OnMapReadyCallback {

    private BoardViewBinding binding;

    private boolean isLiked = false; // 좋아요 플래그
    private boolean isBookmarked = false; // 북마크 플래그
    private ValueAnimator animator; // 애니메이션
    private String boardId;
    private int userId; // 유저 아이디
    private String boardUserId; // 글 작성자 아이디

    // Google Map 관련 필드
    private GoogleMap googleMap;
    private double lat=0.0;
    private double lng=0.0;
    private double mapCenterLat=0.0;
    private double mapCenterLng=0.0;

    @SuppressLint("ServiceCast")
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
            //Toast.makeText(this, "운동 게시판 상세보기 화면입니다 boardId:"+boardId, Toast.LENGTH_SHORT).show();
            // Retrofit을 사용하여 서버에서 해당 boardId에 대한 조회수 +1
            putBoardViewCountPlus(boardId);
        }

        // 유저 프로필 버튼 클릭 이벤트 처리
        binding.boardViewProfile.setOnClickListener(v -> {
            Log.i("tag","boardUserId:"+boardUserId);
            if (boardUserId!=null) {
                Intent intent2 = new Intent(BoardSportViewActivity.this, FollowPageActivity.class);
                intent2.putExtra("follow", boardUserId);
                startActivity(intent2);
            }
        });

        // 프로필 사진 (안가져옴)
        binding.boardViewProfile.setVisibility(View.GONE);
        // 팔로우 체크 불러오지않음
        binding.followBtn.setVisibility(View.GONE);
        if (boardUserId==null || userId == Integer.parseInt(boardUserId)) {
            binding.editBtn.setVisibility(View.VISIBLE);
            binding.deleteBtn.setVisibility(View.VISIBLE);
        }
        // 운동 글 삭제 처리
        binding.deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setTitle("게시글 삭제");
            builder.setMessage("정말로 이 게시글을 삭제하시겠습니까?");
            builder.setPositiveButton("예", (dialog, which) -> {
                SpringService service = RetrofitClient.getSpringService();
                Call<String> call = service.boardsportdelete(Integer.parseInt(boardId));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(BoardSportViewActivity.this, "게시글 삭제가 완료되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });
            });
            builder.setNegativeButton("아니오", null);
            builder.show();
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
            Intent intent3 = new Intent(BoardSportViewActivity.this, BoardCommentActivity.class);
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.board_write_googleMap);
        mapFragment.getMapAsync(this);

    }////onCreate

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap=googleMap;

        // Retrofit을 사용하여 서버에서 해당 boardId에 대한 상세 정보 가져오기
        SpringService service = RetrofitClient.getSpringService();
        Call<BoardSportViewItem> call = service.boardsportview(Integer.parseInt(boardId),userId);
        call.enqueue(new Callback<BoardSportViewItem>() {
            @Override
            public void onResponse(Call<BoardSportViewItem> call, Response<BoardSportViewItem> response) {
                if (response.isSuccessful()){
                    BoardSportViewItem viewItem = response.body();
                    if (viewItem != null) {
                        mapCenterLat = viewItem.getMapCenterLat();
                        mapCenterLng = viewItem.getMapCenterLng();
                        // 서버에서 받은 데이터를 화면에 적용
                        setBoardDetail(viewItem);

                        // 서버로부터 받은 JSON 데이터에서 mapPaths와 mapDistances를 추출하여 폴리라인 그리기
                        String mapPaths = viewItem.getMapPaths();
                        String mapDistances = viewItem.getMapDistances();
                        if (mapPaths != null && mapDistances != null) {
                            List<PolylineOptions> polylines = parseMapData(mapPaths, mapDistances);
                            // 그려진 폴리라인을 지도에 추가
                            for (PolylineOptions polylineOptions : polylines) {
                                googleMap.addPolyline(polylineOptions);
                            }
                        }
                        try {
                            //mapCenterLat, mapCenterLng 로 구글맵의 카메라를 이동시키자
                            // 지도 유형
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            // 카메라 이동
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mapCenterLat, mapCenterLng), 16));
                        } catch (SecurityException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // mapPaths, mapDistances 파싱
            private List<PolylineOptions> parseMapData(String mapPaths, String mapDistances) {
                List<PolylineOptions> polylines = new ArrayList<>();

                try {
                    JSONArray pathsArray = new JSONArray(mapPaths);
                    JSONArray distancesArray = new JSONArray(mapDistances);

                    // mapPaths 파싱
                    List<LatLng> points = new ArrayList<>();
                    for (int i = 0; i < pathsArray.length(); i++) {
                        lat = pathsArray.getJSONObject(i).getDouble("lat");
                        lng = pathsArray.getJSONObject(i).getDouble("lng");
                        points.add(new LatLng(lat, lng));
                    }

                    // mapDistances 파싱
                    List<Integer> distances = new ArrayList<>();
                    for (int i = 0; i < distancesArray.length(); i++) {
                        distances.add(distancesArray.getInt(i));
                    }

                    // 폴리라인 그리기
                    PolylineOptions polylineOptions = new PolylineOptions();
                    for (int i = 0; i < points.size(); i++) {
                        polylineOptions.add(points.get(i));
                        if (i < distances.size() && distances.get(i) != 0) {
                            polylines.add(polylineOptions);
                            polylineOptions = new PolylineOptions();
                            polylineOptions.add(points.get(i));
                        }
                    }
                    // 마지막 좌표에 마커 추가
                    if (!points.isEmpty()) {
                        LatLng lastPoint = points.get(points.size() - 1);
                        googleMap.addMarker(new MarkerOptions().position(lastPoint));
                    }
                    // 마지막 폴리라인 추가
                    polylines.add(polylineOptions);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return polylines;
            }

            @Override
            public void onFailure(Call<BoardSportViewItem> call, Throwable t) {}
        });
    }

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
    }


    // 서버에서 가져온 상세 정보를 화면에 설정하는 메서드
    private void setBoardDetail(BoardSportViewItem viewItem) {
        if (viewItem == null) { return; }

        // 이미지 비활성화 후 지도 표시
        binding.detailviewitemImageviewContent.setVisibility(View.GONE);
        binding.frameLayout.setVisibility(View.VISIBLE);

        // 작성자 닉네임
        binding.boardViewNickname.setText(viewItem.getUserNick() != null ? viewItem.getUserNick() : "");
        // 글 제목
        binding.boardViewTitle.setText(viewItem.getBoardTitle() != null ? viewItem.getBoardTitle() : "");
        // 글 내용
        String boardContent = viewItem.getBoardContent();
        binding.boardViewContent.setText(boardContent != null ? Html.fromHtml(boardContent) : "");

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
    }
    
}
