package com.ict.nutrimate_android.view.info.news;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import com.ict.nutrimate_android.databinding.InfoNewsItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsPagerAdapter extends RecyclerView.Adapter<NewsPagerAdapter.NewsViewHolder> {
    private Context context;
    private List<NewsItem> items;
    private InfoNewsItemBinding binding;

    public NewsPagerAdapter(Context context, List<NewsItem> items) {
        this.context = context;
        this.items  = items;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = InfoNewsItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new NewsViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.content.setText(items.get(position).getContent());
        Picasso.get().load(items.get(position).getImglink()).into(holder.imglink);
        //이벤트 처리
        holder.cardView.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(items.get(position).getNewslink()));
            context.startActivity(intent);
        });
    }/////////////////////////////////////////

    @Override
    public int getItemCount() {
        return items.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imglink;
        TextView title, content;
        NewsItem newslink;
        CardView cardView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            imglink = binding.newsImage;
            title = binding.newsTitle;
            content = binding.newsContent;
        }
    }/////////////////////////
    //데이타 변화 감지용 추가 메소드:리사이클러 뷰의 데이타 갱신을 위한 메소드 추가
//    public void notifyItemsChanged(List<NewsItem> items){//items:갱신된 새로운 데이다
//        AdapterItemsDiff itemsDiff = new AdapterItemsDiff(this.items,items);
//        DiffUtil.DiffResult diffResult= DiffUtil.calculateDiff(itemsDiff);
//        this.items.clear();
//        this.items.addAll(items);
//        diffResult.dispatchUpdatesTo(this);
//    }/////////////////////////
}