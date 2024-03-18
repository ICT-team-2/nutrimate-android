package com.ict.nutrimate_android.view.board.feed;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.BoardFeedItemBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.feed.item.BoardFeedListItem;
import com.ict.nutrimate_android.view.board.view.BoardViewActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BoardFeedAdapter extends RecyclerView.Adapter<BoardFeedAdapter.FeedViewHolder> {

    private Context context;
    private List<BoardFeedListItem> items;
    private BoardFeedItemBinding binding;

    public BoardFeedAdapter(Context context, List<BoardFeedListItem> items) {
        this.context = context;
        this.items = items != null ? items : new ArrayList<>(); // Null 체크 추가
        //this.items = items;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = BoardFeedItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new FeedViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        holder.boardId.setText(items.get(position).getBoardId());
        Picasso.get().load(RetrofitClient.NUTRI_SPRING + items.get(position).getBoardThumbnail())
                .placeholder(R.drawable.test) // 기본 이미지 지정
                .into(holder.boardThumbnail);
        //Log.i("tag","RetrofitClient.NUTRI_SPRING + items.get(position).getBoardThumbnail():"+RetrofitClient.NUTRI_SPRING + items.get(position).getBoardThumbnail());
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), BoardViewActivity.class);
            intent.putExtra("boardId", items.get(position).getBoardId());
            intent.putExtra("boardUserId",items.get(position).getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<BoardFeedListItem> feedItems) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.addAll(feedItems);
        notifyDataSetChanged(); // 데이터가 변경되었음을 알려줌
    }

    public void clearItems() {
        if (items != null) {
            items.clear();
            notifyDataSetChanged(); // 변경된 데이터를 알려줌
        }
    }

    class FeedViewHolder extends RecyclerView.ViewHolder{
        TextView boardId;
        ImageView boardThumbnail;
        CardView cardView;

        public FeedViewHolder(View itemsView) {
            super(itemsView);
            cardView = (CardView)itemsView;
            boardId = binding.boardFeedBoardId;
            boardThumbnail = binding.boardFeedThumbnail;
        }
    }

}