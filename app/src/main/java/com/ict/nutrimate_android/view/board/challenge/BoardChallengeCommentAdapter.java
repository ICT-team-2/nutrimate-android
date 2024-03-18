package com.ict.nutrimate_android.view.board.challenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.BoardChallengeCommentItemBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.view.board.challenge.item.ChallengeCommentListItem;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BoardChallengeCommentAdapter extends RecyclerView.Adapter<BoardChallengeCommentAdapter.BoardChallengeCommentViewHolder> {

    private Context context;
    private List<ChallengeCommentListItem> items;
    private BoardChallengeCommentItemBinding binding;

    // 생성자
    public BoardChallengeCommentAdapter(Context context, List<ChallengeCommentListItem> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public BoardChallengeCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = BoardChallengeCommentItemBinding.inflate(LayoutInflater.from(context),parent,false);
        View item = binding.getRoot();
        return new BoardChallengeCommentViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardChallengeCommentViewHolder holder, int position) {
        ChallengeCommentListItem item = items.get(position);
        Picasso.get().load(RetrofitClient.NUTRI_SPRING + items.get(position).getUserProfile())
                .placeholder(R.drawable.ic_mypage_profile) // 기본 이미지 지정
                .into(holder.profile);
        holder.nickname.setText(item.getUserNick());
        holder.content.setText(item.getCmtContent());
        holder.createDate.setText(item.getCreatedDate().split("T")[0]);
//        holder.layout.setOnClickListener(v -> {
//            Toast.makeText(context, "댓글 수정 이벤트 추가예정", Toast.LENGTH_SHORT).show();
//        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class BoardChallengeCommentViewHolder extends RecyclerView.ViewHolder {
        ImageView profile;
        TextView nickname, content, createDate;
        CardView cardView;
        public BoardChallengeCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            profile = binding.boardChallengeCommentProfile;
            nickname = binding.boardChallengeCommentNickname;
            content = binding.boardChallengeCommentContent;
            createDate = binding.boardChallengeCreatedDate;
        }
    }
}
