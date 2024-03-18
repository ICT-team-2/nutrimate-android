package com.ict.nutrimate_android.view.board.comment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.BoardChallengeCommentItemBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.comment.item.BoardCommentItem;
import com.ict.nutrimate_android.view.board.comment.item.BoardCommentsDeleteItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardCommentAdapter extends RecyclerView.Adapter<BoardCommentAdapter.MyViewHolder> {
    private Context context;
    private List<BoardCommentItem> items;
    private BoardChallengeCommentItemBinding binding;
    private int userId;

    //생성자
    public BoardCommentAdapter(Context context, List<BoardCommentItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //아이템 뷰 전개
        binding = BoardChallengeCommentItemBinding.inflate(LayoutInflater.from(context),parent,false);
        View itemView = binding.getRoot();

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(context);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BoardCommentItem item=items.get(position);
        //아이템 뷰의 각 위젯에 데이타 바인딩
        Picasso.get().load(RetrofitClient.NUTRI_SPRING + items.get(position).getUserProfile())
                .placeholder(R.drawable.ic_mypage_profile) // 기본 이미지 지정
                .into(holder.userProfile);
        holder.cmtId.setText(item.getCmtId());
        holder.userNick.setText(item.getUserNick());
        holder.cmtContent.setText(item.getCmtContent());
        // 내 댓글이라면 댓글 삭제 활성화
        if (userId==Integer.parseInt(item.getUserId())) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
        }
        // 댓글 삭제
        holder.deleteBtn.setOnClickListener(v -> {
            Map<String,Integer> data = new HashMap<>();
            data.put("cmtId",Integer.parseInt(item.getCmtId()));
            SpringService service = RetrofitClient.getSpringService();
            Call<BoardCommentsDeleteItem> call = service.boardcommentdelete(data);
            call.enqueue(new Callback<BoardCommentsDeleteItem>() {
                @Override
                public void onResponse(Call<BoardCommentsDeleteItem> call, Response<BoardCommentsDeleteItem> response) {
                    Log.i("tag","댓글 삭제 통신 완료");
                    if (response.isSuccessful()){
                        // RecyclerView에서 해당 아이템 제거
                        items.remove(position);
                        // 어댑터에 변경 사항을 알림
                        notifyItemRemoved(position);
                        Log.i("tag","댓글 삭제 완료");
                    }
                }
                @Override
                public void onFailure(Call<BoardCommentsDeleteItem> call, Throwable t) {}
            });

        });
        // 루트 뷰(카드 뷰)에 이벤트 부착
//        holder.cardView.setOnClickListener(v-> {
//            Toast.makeText(context, item.getCmtId(), Toast.LENGTH_SHORT).show();
//        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addItems(List<BoardCommentItem> comments) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.addAll(comments);
        notifyDataSetChanged(); // 데이터가 변경되었음을 알려줌
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        //※값으로 셋팅할 위젯들만을 필드로 나열하자
        //속성에 private을 역시 붙이지 않는다.(외부 클래스의 메소드에서 접근)
        ImageView userProfile, deleteBtn;
        TextView cmtId,userNick,cmtContent;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView)itemView;
            userProfile = binding.boardChallengeCommentProfile;
            cmtId = binding.boardChallengeCommentId;
            userNick = binding.boardChallengeCommentNickname;
            cmtContent = binding.boardChallengeCommentContent;
            deleteBtn = binding.boardCommentDelete;
        }
    }
}
