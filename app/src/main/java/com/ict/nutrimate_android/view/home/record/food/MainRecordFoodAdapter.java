package com.ict.nutrimate_android.view.home.record.food;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.HomeMainRecordFoodItemBinding;
import com.ict.nutrimate_android.retrofit.FlaskService;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.home.record.food.item.RecoardDietItem;
import com.ict.nutrimate_android.view.home.record.food.item.RecoardFoodListItem;
import com.ict.nutrimate_android.view.home.record.food.item.RecordDietDayItem;
import com.ict.nutrimate_android.view.home.record.food.item.RecordDietDeleteItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainRecordFoodAdapter extends RecyclerView.Adapter<MainRecordFoodAdapter.ViewHolder> {
    private Context context;
    private List<MainRecordFoodItem> items;
    private HomeMainRecordFoodItemBinding binding;
    private String mealTime;
    private int userId;

    public MainRecordFoodAdapter(Context context, List<MainRecordFoodItem> items, String mealTime) {
        this.context = context;
        this.items = items;
        this.mealTime = mealTime;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //아이템 뷰 전개
        binding = HomeMainRecordFoodItemBinding.inflate(LayoutInflater.from(context),parent,false);
        View itemView=binding.getRoot();

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(context);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainRecordFoodItem item=items.get(position);
        holder.foodName.setText(item.getFoodName());
        holder.foodKcal.setText(String.valueOf(item.getFoodKcal()));
        holder.cardView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
            View view = LayoutInflater.from(context).inflate(R.layout.home_main_record_food_dialog, null, false);
            builder.setView(view);
            final AlertDialog dialog = builder.create();
            final Button btn_edit2 = view.findViewById(R.id.btn_edit2);
            final Button btn_cancel2 = view.findViewById(R.id.btn_cancel2);
            final EditText edit_name2 = view.findViewById(R.id.edit_editName2);
            final EditText edit_number2 = view.findViewById(R.id.edit_editNumber2);

            // 현재 아이템의 정보 가져오기
            MainRecordFoodItem currentItem = items.get(position);
            String name = currentItem.getFoodName();
            String number = String.valueOf(currentItem.getFoodKcal());

            // 다이얼로그에 현재 아이템 정보 설정
            edit_name2.setText(name);
            edit_number2.setText(number);
            btn_edit2.setText("수정");

            // 수정 버튼 클릭 이벤트 처리
            btn_edit2.setOnClickListener(v_ -> {
                String editName2 = edit_name2.getText().toString();
                String editNumber2 = edit_number2.getText().toString();
                if (editName2.isEmpty() || editNumber2.isEmpty()) {
                    // 이름과 칼로리가 비어있을 때는 수정을 진행하지 않음
                    Toast.makeText(context, "이름과 칼로리를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int calorie = Integer.parseInt(editNumber2); // 칼로리를 int로 변환 시도
                        // 변환에 성공하면 수정 진행
                        items.get(position).setFoodName(editName2);
                        items.get(position).setFoodKcal(calorie);
                        notifyItemChanged(position);
                        dialog.dismiss();
                        // 총 칼로리 업데이트
                        updateTotalCalories();
                    } catch (NumberFormatException e) {
                        // 숫자로 변환 실패 시, 적절한 에러 처리
                        Toast.makeText(context, "칼로리는 숫자 형식이어야 합니다", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            // 취소 버튼 클릭 이벤트 처리
            btn_cancel2.setOnClickListener(view_ -> dialog.dismiss());
            dialog.show();
        });
        // 리사이클러뷰의 삭제 버튼 클릭 이벤트 처리
        holder.foodDelte.setOnClickListener(v -> {
            //위치가 유효한지 확인
            if (position != RecyclerView.NO_POSITION) {
                // 아이템 삭제
                items.remove(position);
                // 삭제된 아이템을 RecyclerView에 알림
                notifyItemRemoved(position);
                // 삭제 후에 인덱스가 변경되었으므로, 삭제된 아이템 이후의 아이템들의 인덱스를 업데이트
                notifyItemRangeChanged(position, items.size());
                // 총 칼로리 업데이트
                updateTotalCalories();
                // 총 탄,단,지 업데이트
                updateTotalNutrients();
            }
        });
    }

    //화면에 보여줄 데이터의 갯수를 반환.
    @Override
    public int getItemCount() {
        return items.size ();
    }

    public List<Integer> getAllFoodIds() {
        List<Integer> foodIds = new ArrayList<>();
        if (items!=null) {
            for (MainRecordFoodItem item : items) {
                foodIds.add(item.getFoodId());
            }
        }
        return foodIds;
    }
    public List<Integer> getAllFoodIntake(){
        List<Integer> foodIntakes = new ArrayList<>();
        for (MainRecordFoodItem item : items) {
            foodIntakes.add(item.getRecordIntake());
        }
        return foodIntakes;
    }

    // 리사이클러뷰에 추가
    public void addItem(MainRecordFoodItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodKcal;
        ImageView foodDelte;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            cardView = (CardView) itemView;
            foodName = binding.foodName;
            foodKcal = binding.foodKcal;
            foodDelte = binding.foodDelte;
        }
    }

    // 식단 삭제 메소드
//    private void recordDelete(int recordId){
//        SpringService service = RetrofitClient.getSpringService();
//        Call<RecordDietDeleteItem> call = service.recorddietdelete(recordId);
//
//        call.enqueue(new Callback<RecordDietDeleteItem>() {
//             @Override
//             public void onResponse(Call<RecordDietDeleteItem> call, Response<RecordDietDeleteItem> response) {
//                 if (response.isSuccessful()) {
//                     Log.i("tag","식단 삭제 성공");
//                 }
//             }
//             @Override
//             public void onFailure(Call<RecordDietDeleteItem> call, Throwable t) {
//                 Log.i("tag","식단 삭제 실패");
//             }
//         });
//    }//////////////////////////////////

    // 총 칼로리를 계산하는 메소드
    String totalCalories() {
        int totalCalories = 0;
        for (MainRecordFoodItem item : items) {
            totalCalories += item.getFoodKcal();
        }
        return String.valueOf(totalCalories);
    }
    // 총 칼로리를 계산하는 메소드
    private void updateTotalCalories() {
        int totalCalories = 0;
        for (MainRecordFoodItem item : items) {
            totalCalories += item.getFoodKcal();
        }
        // 총 칼로리 텍스트뷰에 설정
        ((TextView)((MainRecordFoodActivity) context).findViewById(R.id.total_kcal)).setText(String.valueOf(totalCalories));
    }
    // 총 탄,단,지를 계산하는 메소드
    private void updateTotalNutrients() {
        int totalFoodCarbo=0;
        int totalFoodProtein=0;
        int totalFoodProvi=0;
        for (MainRecordFoodItem item : items) {
            totalFoodCarbo += item.getFoodCarbo();
            totalFoodProtein += item.getFoodProtein();
            totalFoodProvi += item.getFoodProvi();
        }
        // 총 칼로리 텍스트뷰에 설정
        ((TextView)((MainRecordFoodActivity) context).findViewById(R.id.foodCarbo)).setText(String.valueOf(totalFoodCarbo));
        ((TextView)((MainRecordFoodActivity) context).findViewById(R.id.foodProtein)).setText(String.valueOf(totalFoodProtein));
        ((TextView)((MainRecordFoodActivity) context).findViewById(R.id.foodProvi)).setText(String.valueOf(totalFoodProvi));
    }
}