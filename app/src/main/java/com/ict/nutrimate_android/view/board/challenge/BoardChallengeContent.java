package com.ict.nutrimate_android.view.board.challenge;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.challenge.chating.BoardChallengeChatingActivity;
import com.ict.nutrimate_android.databinding.BoardChallengeBinding;
import com.ict.nutrimate_android.view.board.challenge.item.ChallengeCommentListItem;
import com.ict.nutrimate_android.view.board.challenge.item.ChallengeSuccessItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardChallengeContent extends Fragment {

    private BoardChallengeBinding binding;
    private BoardChallengeCommentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BoardChallengeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        SpringService service = RetrofitClient.getSpringService();
        Call<List<ChallengeCommentListItem>> call = service.challengecommentlist(1);
        call.enqueue(new Callback<List<ChallengeCommentListItem>>() {
            @Override
            public void onResponse(Call<List<ChallengeCommentListItem>> call, Response<List<ChallengeCommentListItem>> response) {
                if (response.isSuccessful()){
                    List<ChallengeCommentListItem> comments = response.body();
                    if (comments != null && !comments.isEmpty()) {
                        if (binding != null && binding.recyclerView != null) {
                            // RecyclerView에 데이터 추가
                            adapter = new BoardChallengeCommentAdapter(requireContext(), comments);
                            binding.recyclerView.setAdapter(adapter);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            Log.i("tag", "챌린지 댓글 불러오기 성공");
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<ChallengeCommentListItem>> call, Throwable t) {
                // 네트워크 요청이 실패한 경우 처리
                Log.e("tag", "Network request failed", t);
            }
        });

        // 챌린지 랭킹 애니메이션
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);;
        animator.setDuration(1800);
        animator.addUpdateListener(animation ->
                binding.chellangeRank.setProgress((Float) animation.getAnimatedValue())
        );
        animator.start();

        // 챌린지 채팅방 입장 이벤트 처리
        binding.challenge1.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BoardChallengeChatingActivity.class);
            intent.putExtra("ChatingRoom",1);
            startActivity(intent);
        });
        binding.challenge2.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BoardChallengeChatingActivity.class);
            intent.putExtra("ChatingRoom",3);
            startActivity(intent);
        });

        // 물 마시기 챌린지 버튼 클릭 이벤트 처리
        setChalleng1Rank(); // 로드시 불러오기
        binding.challenge1Rank.setOnClickListener(v -> {
            setChalleng1Rank();
        });
        binding.challenge2Rank.setOnClickListener(v -> {
            setChalleng2Rank();
        });
        return view;
    }

    // 물 마시기 챌린저 정보
    private void setChalleng1Rank(){
        binding.challenge1Rank.setTypeface(null, Typeface.BOLD);
        binding.challenge2Rank.setTypeface(null, Typeface.NORMAL);
        SpringService service = RetrofitClient.getSpringService();
        Call<List<ChallengeSuccessItem>> call = service.challengesuccess(1);
        call.enqueue(new Callback<List<ChallengeSuccessItem>>() {
            @Override
            public void onResponse(Call<List<ChallengeSuccessItem>> call, Response<List<ChallengeSuccessItem>> response) {
                List<ChallengeSuccessItem> items = response.body();
                if (response.isSuccessful() && items!=null){
                    binding.challengeName1.setText(items.get(0).getChallengeNick());
                    binding.challengeName2.setText(items.get(1).getChallengeNick());
                    binding.challengeName3.setText(items.get(2).getChallengeNick());
                    binding.challengeInfo1.setText(items.get(0).getCount());
                    binding.challengeInfo2.setText(items.get(1).getCount());
                    binding.challengeInfo3.setText(items.get(2).getCount());
                }
            }
            @Override
            public void onFailure(Call<List<ChallengeSuccessItem>> call, Throwable t) {}
        });
    }

    // 샐러드 챌린지 버튼 클릭 이벤트 처리
    private void setChalleng2Rank() {
        binding.challenge1Rank.setTypeface(null, Typeface.NORMAL);
        binding.challenge2Rank.setTypeface(null, Typeface.BOLD);
        SpringService service = RetrofitClient.getSpringService();
        Call<List<ChallengeSuccessItem>> call = service.challengesuccess(3);
        call.enqueue(new Callback<List<ChallengeSuccessItem>>() {
            @Override
            public void onResponse(Call<List<ChallengeSuccessItem>> call, Response<List<ChallengeSuccessItem>> response) {
                List<ChallengeSuccessItem> items = response.body();
                if (response.isSuccessful() && items!=null){
                    binding.challengeName1.setText(items.get(0).getChallengeNick());
                    binding.challengeName2.setText(items.get(1).getChallengeNick());
                    binding.challengeName3.setText(items.get(2).getChallengeNick());
                    binding.challengeInfo1.setText(items.get(0).getCount());
                    binding.challengeInfo2.setText(items.get(1).getCount());
                    binding.challengeInfo3.setText(items.get(2).getCount());
                }
            }
            @Override
            public void onFailure(Call<List<ChallengeSuccessItem>> call, Throwable t) {}
        });
    }

}////////class