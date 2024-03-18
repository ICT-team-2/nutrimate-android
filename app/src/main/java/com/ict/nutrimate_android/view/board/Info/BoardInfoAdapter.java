package com.ict.nutrimate_android.view.board.Info;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.databinding.BoardFoodSportItemBinding;
import com.ict.nutrimate_android.view.board.Info.item.BoardInfoListItem;
import com.ict.nutrimate_android.view.board.view.BoardViewActivity;
import com.ict.nutrimate_android.view.board.view.sport.BoardSportViewActivity;

import java.util.ArrayList;
import java.util.List;

public class BoardInfoAdapter extends RecyclerView.Adapter<BoardInfoAdapter.FoodViewHolder> {

    private BoardFoodSportItemBinding binding;
    private Context context;
    private List<BoardInfoListItem> items;
    private String boardCategory;

    public BoardInfoAdapter(Context context, List<BoardInfoListItem> items, String boardCategory) {
        this.context = context;
        this.items = items;
        this.boardCategory = boardCategory;
    }
    // 카테고리 업데이트를 위한 setter 메서드
    public void setBoardCategory(String boardCategory) {
        this.boardCategory = boardCategory;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = BoardFoodSportItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new FoodViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        holder.boardId.setText(items.get(position).getBoardId());
        holder.userNick.setText(items.get(position).getUserNick());
        holder.boardTitle.setText(items.get(position).getBoardTitle());
        holder.createdDate.setText(items.get(position).getCreatedDate());
        holder.boardViewCount.setText(items.get(position).getBoardViewCount());
//        holder.commentCount.setText(items.get(position).getCount);
//        holder.likeCount.setText(items.get(position).getcount());
        //이벤트 처리
        holder.cardView.setOnClickListener(v -> {
            Intent intent;
            if (boardCategory.equals("EXERCISE")) {
                intent = new Intent(v.getContext(), BoardSportViewActivity.class);
            } else {
                intent = new Intent(v.getContext(), BoardViewActivity.class);
                intent.putExtra("food","food");
            }
            intent.putExtra("boardId", items.get(position).getBoardId());
            intent.putExtra("boardUserId",items.get(position).getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // 아이템을 추가하는 메서드
    public void addItems(List<BoardInfoListItem> infoItems) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.addAll(infoItems);
        notifyDataSetChanged(); // 데이터가 변경되었음을 알려줌
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView boardId, userNick, boardTitle, createdDate, boardViewCount, commentCount, likeCount;
        CardView cardView;

        public FoodViewHolder(View itemsView) {
            super(itemsView);
            cardView = (CardView) itemsView;
            boardId = binding.boardFoodSportBoardId;
            userNick = binding.boardFoodSportNickname;
            boardTitle = binding.boardFoodSportTitle;
            createdDate = binding.boardFoodSportCreateDate;
            boardViewCount = binding.boardFoodSportViewCount;
            //commentCount = binding.boardFoodSportCommentCount;
            //likeCount = binding.boardFoodSportLikeCount;
        }
    }
}