package com.ict.nutrimate_android.view.info.recommend.nutri;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.databinding.InfoRecommendNutriItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InfoRecommendNutriAdapter extends RecyclerView.Adapter<InfoRecommendNutriAdapter.MyViewHolder> {

    private Context context;
    private List<InfoRecommendNutriItem> items;
    private InfoRecommendNutriItemBinding binding;

    public InfoRecommendNutriAdapter(Context context, List<InfoRecommendNutriItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public InfoRecommendNutriAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = InfoRecommendNutriItemBinding.inflate(LayoutInflater.from(context),parent,false);
        View view = binding.getRoot();
        return new InfoRecommendNutriAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(items.get(position).getImglink()).into(holder.imglink);
        holder.title.setText(items.get(position).getTitle());
        holder.company.setText(items.get(position).getCompany());
        holder.star.setText(String.valueOf(items.get(position).getStar()));
        holder.RatingBar.setRating(Float.parseFloat(items.get(position).getStar()));
        holder.effect1.setText(items.get(position).getEffect1());
        holder.effect2.setText(items.get(position).getEffect2());
        // 카드뷰 클릭시 이벤트 처리
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(position).getLink()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imglink;
        TextView title,company,star;
        Button effect1,effect2;
        RatingBar RatingBar;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=(CardView)itemView;
            imglink=binding.infoRecommendNutriImage;
            title=binding.infoRecommendNutriTitle;
            company=binding.infoRecommendNutriCompany;
            star=binding.infoRecommendNutriStar;
            RatingBar=binding.ratingBar;
            effect1=binding.infoRecommendNutriEffect1;
            effect2=binding.infoRecommendNutriEffect2;
        }
    }
}
