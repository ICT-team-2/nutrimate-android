package com.ict.nutrimate_android.view.board.challenge.chating;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.databinding.BoardChallengeChatingBinding;
import com.ict.nutrimate_android.databinding.BoardChallengeChatingItemBinding;
import com.ict.nutrimate_android.view.board.challenge.chating.item.ChallengeChatPrevItem;

import java.util.List;

public class BoardChallengeChatingAdapter extends RecyclerView.Adapter<BoardChallengeChatingAdapter.BoardChallengeChatingViewHolder>{

    private Context context;
    private List<ChallengeChatPrevItem> items;
    private BoardChallengeChatingItemBinding binding;

    public BoardChallengeChatingAdapter(Context context, List<ChallengeChatPrevItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public BoardChallengeChatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = BoardChallengeChatingItemBinding.inflate(LayoutInflater.from(context),parent,false);
        View item = binding.getRoot();
        return new BoardChallengeChatingViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull BoardChallengeChatingViewHolder holder, int position) {
        ChallengeChatPrevItem item = items.get(position);
        holder.txtMessage.setText(item.getChatMessage());
        holder.txtDate.setText(item.getCreatedDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    class BoardChallengeChatingViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage,txtDate;
        public BoardChallengeChatingViewHolder(@NonNull View itemview){
            super(itemview);
            txtMessage=binding.txtMessage;
            txtDate=binding.txtDate;
        }
    }
}