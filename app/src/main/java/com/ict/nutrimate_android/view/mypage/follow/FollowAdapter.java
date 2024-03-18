package com.ict.nutrimate_android.view.mypage.follow;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.MypageFollowListItemBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.login.join.JoinActivity2;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.ict.nutrimate_android.view.mypage.follow.item.FollowFollowItem;
import com.ict.nutrimate_android.view.mypage.follow.item.FollowFolloweeListItem;
import com.ict.nutrimate_android.view.mypage.follow.item.FollowUnfollowItem;
import com.ict.nutrimate_android.view.mypage.follow.page.FollowPageActivity;
import com.ict.nutrimate_android.view.mypage.profile.MyPageProfileItem;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.MyViewHolder> {

    private Context context;
    private List<FollowFolloweeListItem> items;
    private MypageFollowListItemBinding bindig;
    private int userId;

    //생성자
    public FollowAdapter(Context context, List<FollowFolloweeListItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public FollowAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //아이템 뷰 전개
        bindig = MypageFollowListItemBinding.inflate(LayoutInflater.from(context),parent,false);
        View itemView=bindig.getRoot();

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(context);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowAdapter.MyViewHolder holder, int position) {
        FollowFolloweeListItem item=items.get(position);
        //아이템 뷰의 각 위젯에 데이타 바인딩
        Picasso.get().load(RetrofitClient.NUTRI_SPRING + items.get(position).getUserProfile())
                .placeholder(R.drawable.ic_mypage_profile) // 기본 이미지 지정
                .into(holder.itemImage);
        holder.itemName.setText(item.getUserNick());
        holder.itemContent.setText(item.getUserIntro());
        // 카드뷰 클릭시 해당 팔로우 유저의 글 목록 보기
        holder.cardView.setOnClickListener(v -> {
            if (userId==Integer.parseInt(item.getFollowerId())){ //내 팔로잉(내가 등록한 사람 목록)
                Intent intent = new Intent(context, FollowPageActivity.class);
                intent.putExtra("follow", item.getFolloweeId()); // 추가 데이터를 전달하는 경우
                context.startActivity(intent);
            } else { //내 팔로워(나를 등록한 사람 목록)
                Intent intent = new Intent(context, FollowPageActivity.class);
                intent.putExtra("follow", item.getFollowerId()); // 추가 데이터를 전달하는 경우
                context.startActivity(intent);
            }
        });
        // 삭제/취소 버튼 클릭시 팔로우 삭제/취소
        holder.itemDelete.setOnClickListener(v -> {
            if (holder.itemDelete.getText().equals("취소")) { //다시 팔로우한다
                holder.itemDelete.setText("삭제");
                Map<String, Object> data = new HashMap<>();
                data.put("followerId", item.getFollowerId()); //내 userId
                data.put("followeeId", item.getFolloweeId()); //상대의 userId
                SpringService service = RetrofitClient.getSpringService();
                Call<FollowFollowItem> call = service.followfollow(data);
                call.enqueue(new Callback<FollowFollowItem>() {
                    @Override
                    public void onResponse(Call<FollowFollowItem> call, Response<FollowFollowItem> response) {
                        if (response.isSuccessful()) {
                            FollowFollowItem viewItem = response.body();
                            if (viewItem != null) {
                                Log.i("tag","팔로우 성공. message:"+viewItem.getMessage());
                            }
                        } else {
                            Log.e("tag", "팔로우 실패");
                        }
                    }
                    @Override
                    public void onFailure(Call<FollowFollowItem> call, Throwable t) {
                        Log.e("tag", "팔로우 실패", t);
                    }
                });
            } else { // 팔로우를 삭제한다
                holder.itemDelete.setText("취소");
                SpringService service = RetrofitClient.getSpringService();
                Call<FollowUnfollowItem> call = service.followunfollow(Integer.parseInt(item.getFollowerId()),Integer.parseInt(item.getFolloweeId()));
                call.enqueue(new Callback<FollowUnfollowItem>() {
                    @Override
                    public void onResponse(Call<FollowUnfollowItem> call, Response<FollowUnfollowItem> response) {
                        if (response.isSuccessful()) {
                            FollowUnfollowItem viewItem = response.body();
                            if (viewItem != null) {
                                Log.i("tag","팔로우 취소. message:"+viewItem.getMessage());
                            }
                        } else {
                            Log.e("tag", "팔로우 삭제 실패");
                        }
                    }
                    @Override
                    public void onFailure(Call<FollowUnfollowItem> call, Throwable t) {
                        Log.e("tag", "팔로우 삭제 실패", t);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //뷰 홀더(※private class 불가)
    class MyViewHolder extends RecyclerView.ViewHolder{
        //※값으로 셋팅할 위젯들만을 필드로 나열하자
        //속성에 private을 역시 붙이지 않는다.(외부 클래스의 메소드에서 접근)
        ImageView itemImage;
        TextView itemName,itemContent;
        Button itemDelete;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView)itemView;
            itemImage = bindig.mypageFollowProfile;
            itemName = bindig.followNickname;
            itemContent = bindig.followContent;
            itemDelete = bindig.followDelete;
        }
    }

}
