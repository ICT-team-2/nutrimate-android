package com.ict.nutrimate_android.view.info.recommend.food;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.databinding.InfoRecommendFoodItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InfoRecommendFoodAdapter extends RecyclerView.Adapter<InfoRecommendFoodAdapter.MyViewHolder> {

    private Context context;
    private List<InfoRecommendFoodItem> items;
    private InfoRecommendFoodItemBinding binding;

    public InfoRecommendFoodAdapter(Context context, List<InfoRecommendFoodItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = InfoRecommendFoodItemBinding.inflate(LayoutInflater.from(context),parent,false);
        View view = binding.getRoot();
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(items.get(position).getImage()).into(holder.itemImage);
        holder.itemTitle.setText(items.get(position).getTitle());
        // 카드뷰 클릭시 이벤트 처리
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(position).getUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView itemImage;
        TextView itemTitle;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=(CardView)itemView;
            itemImage=binding.infoRecommendFoodImage;
            itemTitle=binding.infoRecommendFoodTitle;
        }
    }
}
