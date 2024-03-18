package com.ict.nutrimate_android.view.board.Info;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.databinding.BoardFoodSportBinding;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.view.board.crud.BoardFoodSportWriteActivity;
import com.ict.nutrimate_android.view.board.Info.item.BoardInfoItem;
import com.ict.nutrimate_android.view.board.Info.item.BoardInfoListItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * board_food_sport
 * board_food_sport_item
 * board_food_sport_item_progress
 */

public class BoardFoodSportContent extends Fragment {

    private BoardFoodSportBinding binding;
    private BoardInfoAdapter adapter; // 리사이클러뷰 어댑터
    private String boardCategory="FOOD"; // 카테고리
    private boolean SearchFlag = false; // 검색 플래그
    private String searchKeywords=""; // 검색어
    private int nowPage=1; // 현재 페이지
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BoardFoodSportBinding.inflate(inflater, container, false);

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(requireContext());
        userId = userSessionManager.getUserIdFromSharedPreferences();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText search = binding.boardFoodSportSearch;
        // 엔터키로 키보드 내리기
        search.setOnEditorActionListener((textView, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                // 데이터 입력
                searchKeywords = search.getText().toString().trim();
                // 입력된 데이터를 처리하는 코드 추가
                if (!searchKeywords.equals("")) {
                    SearchFlag = true;
                    loadData();
                } else {
                    // 검색어를 입력하지 않았을 때만 토스트 메시지 표시
                    Toast.makeText(getActivity(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                    handled = true;
                }
                // 키보드 내리기
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(search.getWindowToken(), 0);
                }
            }
            // 텍스트 뷰 초기화
            search.setText("");
            return handled;
        });

        RecyclerView mRecyclerView = binding.rvList2;
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (getActivity() != null && mRecyclerView != null) {
            adapter = new BoardInfoAdapter(getActivity(), new ArrayList<>(), boardCategory);
            mRecyclerView.setAdapter(adapter);
        }

        loadData();

        // 글 작성 버튼 클릭 이벤트 처리
        binding.writeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BoardFoodSportWriteActivity.class);
            startActivity(intent);
        });

        // 식단 버튼 클릭 이벤트 처리
        binding.boardFoodSportFood.setOnClickListener(v -> {
            binding.boardFoodSportFood.setTypeface(null, Typeface.BOLD);
            binding.boardFoodSportSport.setTypeface(null, Typeface.NORMAL);
            boardCategory = "FOOD";
            adapter.setBoardCategory(boardCategory); // 카테고리 업데이트
            nowPage = 1;
            loadData();
        });
        // 운동 버튼 클릭 이벤트 처리
        binding.boardFoodSportSport.setOnClickListener(v -> {
            binding.boardFoodSportFood.setTypeface(null, Typeface.NORMAL);
            binding.boardFoodSportSport.setTypeface(null, Typeface.BOLD);
            boardCategory = "EXERCISE";
            adapter.setBoardCategory(boardCategory); // 카테고리 업데이트
            nowPage = 1;
            adapter.clearItems();
            loadData();
        });

        // 리사이클러뷰의 스크롤 리스너 설정
//        binding.rvList2.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int totalItemCount = layoutManager.getItemCount();
//                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
//
//                if (!recyclerView.canScrollVertically(1) && lastVisibleItemPosition == totalItemCount - 1) {
//                    // 맨 아래로 스크롤되었을 때
//                    loadMoreData();
//                }
//            }
//            private void loadMoreData() {
//                nowPage++; // 다음 페이지를 요청하기 위해 페이지 번호 증가
//                loadData();
//            }
//        });

    }/////////////////////////////////////////////onViewCreated

    // 카테고리 별로 게시글 불러오기
    private void loadData() {

        SpringService service = RetrofitClient.getSpringService();
        Call<BoardInfoItem> call = service.boardinfolist(userId, nowPage, 10, boardCategory);

        call.enqueue(new Callback<BoardInfoItem>() {
            @Override
            public void onResponse(Call<BoardInfoItem> call, Response<BoardInfoItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BoardInfoItem boardInfoItems = response.body();
                    List<BoardInfoListItem> infoItems = boardInfoItems.getBoardList();
                    if (infoItems != null && !infoItems.isEmpty()) {
                        Log.i("tag","infoItems:"+infoItems.get(1).getBoardTitle());
                        if (SearchFlag){
                            Log.i("tag","searchText:"+searchKeywords+"\r\nboardTitle:"+infoItems.get(0).getBoardTitle());
                            List<BoardInfoListItem> searchResults = new ArrayList<>();
                            for (BoardInfoListItem item : infoItems) {
                                if (item.getBoardTitle() != null && item.getBoardTitle().contains(searchKeywords)) {
                                    searchResults.add(item);
                                }
                            }
                            adapter.clearItems(); // 목록을 지우고
                            adapter.addItems(searchResults); // 검색 결과만 추가
                            SearchFlag = false;
                        } else {
                            adapter.clearItems(); // 목록을 지우고
                            adapter.addItems(infoItems); // 모든 게시물 추가
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<BoardInfoItem> call, Throwable t) {}
        });
    }//////////////////////////////////////loadData

}