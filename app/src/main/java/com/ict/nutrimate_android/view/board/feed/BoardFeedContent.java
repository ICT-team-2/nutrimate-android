package com.ict.nutrimate_android.view.board.feed;

import android.content.Context;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.databinding.BoardFeedBinding;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.view.board.feed.item.BoardFeedItem;
import com.ict.nutrimate_android.view.board.feed.item.BoardFeedListItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * board_feed
 * board_feed_item
 */

public class BoardFeedContent extends Fragment {

    private BoardFeedBinding binding;
    private BoardFeedAdapter adapter; // 리사이클러뷰 어댑터
    private boolean SearchFlag = false;
    private String searchText="";
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BoardFeedBinding.inflate(inflater, container, false);

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(requireContext());
        userId = userSessionManager.getUserIdFromSharedPreferences();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText search = binding.boardFeedSearch;
        // 엔터키로 키보드 내리기
        search.setOnEditorActionListener((textView, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                // 데이터 입력
                searchText = search.getText().toString().trim();
                // 입력된 데이터를 처리하는 코드 추가
                if (!searchText.equals("")) {
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

        RecyclerView mRecyclerView = binding.boardFeedRecyclerview;
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new BoardFeedAdapter(getActivity(), new ArrayList<>());
        mRecyclerView.setAdapter(adapter);

        loadData();
    }//////////////////////////onViewCreated

    // Retrofit을 사용하여 데이터를 가져오기
    private void loadData() {
        SpringService service = RetrofitClient.getSpringService();
        Call<BoardFeedItem> call = service.boardfeedlist(userId,1,99);

        call.enqueue(new Callback<BoardFeedItem>() {
            @Override
            public void onResponse(Call<BoardFeedItem> call, Response<BoardFeedItem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BoardFeedItem boardFeedItems = response.body();
                    List<BoardFeedListItem> feedItems = boardFeedItems.getFeedList();
                    if (feedItems != null && !feedItems.isEmpty()) {
                        if (SearchFlag){
                            List<BoardFeedListItem> searchResults = new ArrayList<>();
                            for (BoardFeedListItem item : feedItems) {
                                if (item.getHashtag() != null && item.getHashtag().contains(searchText)) {
                                    searchResults.add(item);
                                }
                            }
                            adapter.clearItems(); // 목록을 지우고
                            adapter.addItems(searchResults); // 검색 결과만 추가
                            SearchFlag = false;
                        } else {
                            adapter.clearItems(); // 목록을 지우고
                            adapter.addItems(feedItems); // 모든 게시물 추가
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<BoardFeedItem> call, Throwable t) {}
        });
    }/////////////////////////loadData

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}/////////////////////////Fragment