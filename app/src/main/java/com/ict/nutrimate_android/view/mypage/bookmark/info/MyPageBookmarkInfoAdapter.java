package com.ict.nutrimate_android.view.mypage.bookmark.info;

import android.content.Context;
import android.content.Intent;
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
import com.ict.nutrimate_android.databinding.BoardFeedItemBinding;
import com.ict.nutrimate_android.view.board.Info.item.BoardInfoListItem;
import com.ict.nutrimate_android.view.board.view.BoardViewActivity;
import com.ict.nutrimate_android.view.board.view.sport.BoardSportViewActivity;
import com.ict.nutrimate_android.view.mypage.MyPageItem;
import com.ict.nutrimate_android.view.mypage.profile.item.MyPageInfoItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyPageBookmarkInfoAdapter extends RecyclerView.Adapter<MyPageBookmarkInfoAdapter.MyViewHolder> {

    private Context context;
    private List<BoardInfoListItem> items;
    private BoardFeedItemBinding binding;

    public MyPageBookmarkInfoAdapter(Context context, List<BoardInfoListItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = BoardFeedItemBinding.inflate(LayoutInflater.from(context),parent,false);
        View view = binding.getRoot();
        return new MyPageBookmarkInfoAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (items.get(position).getBoardCategory().equals("FOOD")) { // 식단 카테고리
            Picasso.get().load(items.get(position).getBoardTitle())
                    .placeholder(R.drawable.test) // 기본 이미지 지정
                    .into(holder.itemImage);
        } else { // 운동 카테고리
            Picasso.get().load(items.get(position).getBoardTitle())
                    .placeholder(R.drawable.test_sport) // 기본 이미지 지정
                    .into(holder.itemImage);
        }
        holder.itemBoardId.setText(items.get(position).getBoardId());
        // 카드뷰 클릭시 이벤트 처리
        holder.itemLayout.setOnClickListener(v -> {
            Intent intent;
            if (items.get(position).getBoardCategory().equals("FOOD")) {
                intent = new Intent(v.getContext(), BoardViewActivity.class);
                intent.putExtra("food","food");
            } else {
                intent = new Intent(v.getContext(), BoardSportViewActivity.class);
            }
            intent.putExtra("boardId", items.get(position).getBoardId());
            intent.putExtra("boardUserId",items.get(position).getUserId());
            //Toast.makeText(context, "boardId:" + items.get(position).getBoardId(), Toast.LENGTH_SHORT).show();
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView itemImage;
        TextView itemBoardId;
        CardView itemLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage=binding.boardFeedThumbnail;
            itemBoardId=binding.boardFeedBoardId;
            itemLayout=binding.boardFeedLayout;
        }
    }
}
