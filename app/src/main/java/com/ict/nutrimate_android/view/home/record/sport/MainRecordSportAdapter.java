package com.ict.nutrimate_android.view.home.record.sport;

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
import com.ict.nutrimate_android.databinding.HomeMainRecordSportItemBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.home.record.food.MainRecordFoodItem;
import com.ict.nutrimate_android.view.home.record.sport.item.RecoardExerciseItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class MainRecordSportAdapter extends RecyclerView.Adapter<MainRecordSportAdapter.ViewHolder> {
    private Context context;
    private List<MainRecordSportItem> items;
    private HomeMainRecordSportItemBinding binding;
    private int userId;

    public MainRecordSportAdapter(Context context, List<MainRecordSportItem> items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //아이템 뷰 전개
        binding = HomeMainRecordSportItemBinding.inflate(LayoutInflater.from(context),parent,false);
        View itemView=binding.getRoot();

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(context);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        return new ViewHolder(itemView);
    }//////////////////////////////////////////

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainRecordSportItem item=items.get(position);
        holder.sportName.setText(item.getSportName());
        holder.sportWeight.setText(String.valueOf(item.getSportWeight()));
        holder.sportTime.setText(String.valueOf(item.getSportTime()));
        holder.cardView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
            View view = LayoutInflater.from(context).inflate(R.layout.home_main_record_sport_dialog, null, false);
            builder.setView(view);
            final AlertDialog dialog = builder.create();
            final Button btn_edit2 = view.findViewById(R.id.btn_edit3);
            final Button btn_cancel2 = view.findViewById(R.id.btn_cancel3);
            final EditText sportName = view.findViewById(R.id.edit_editName3); //자유입력-운동 이름
            final EditText sportTime = view.findViewById(R.id.edit_editTime3); //자유입력-운동 시간
            final EditText sportweight = view.findViewById(R.id.edit_editWeight3); //자유입력-운동 시간

            // 현재 아이템의 정보 가져오기
            MainRecordSportItem currentItem = items.get(position);
            String name = currentItem.getSportName();
            String time = String.valueOf(currentItem.getSportTime());
            String weight = String.valueOf(currentItem.getSportWeight());

            // 다이얼로그에 현재 아이템 정보 설정
            sportName.setText(name);
            sportTime.setText(time);
            sportweight.setText(weight);
            btn_edit2.setText("수정");

            // 수정 버튼 클릭 이벤트 처리
            btn_edit2.setOnClickListener(v_ -> {
                String sportName2 = sportName.getText().toString();
                String sportTime2 = sportTime.getText().toString();
                String sportWeight2 = sportweight.getText().toString();
                if (sportName2.isEmpty() || sportTime2.isEmpty() || sportWeight2.isEmpty()) {
                    Toast.makeText(context, "정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int sportTime_ = Integer.parseInt(sportTime2); // 운동 시간을 int로 변환 시도
                        int sportweight_ = Integer.parseInt(sportWeight2); // 몸무게를 int로 변환 시도
                        // 변환에 성공하면 수정 진행
                        items.get(position).setSportName(sportName2);
                        items.get(position).setSportTime(sportTime_);
                        items.get(position).setSportWeight(sportweight_);
                        notifyItemChanged(position);
                        dialog.dismiss();



                        // 위의 정보 업데이트
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
        holder.sportDelte.setOnClickListener(v -> {
        //위치가 유효한지 확인
        if (position != RecyclerView.NO_POSITION) {
            // 아이템 삭제
            items.remove(position);
            // 삭제된 아이템을 RecyclerView에 알림
            notifyItemRemoved(position);
            // 총 칼로리 업데이트
            updateTotalCalories();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Integer> getAllSportIds() {
        List<Integer> sportIds = new ArrayList<>();
        for (MainRecordSportItem item : items) {
            sportIds.add(item.getSportId());
        }
        return sportIds;
    }

    public List<Integer> getAllSportTime() {
        List<Integer> sportTime = new ArrayList<>();
        for (MainRecordSportItem item : items) {
            sportTime.add(item.getSportTime());
        }
        return sportTime;
    }

    public List<Integer> getAllSportWeight() {
        List<Integer> sportWeight = new ArrayList<>();
        for (MainRecordSportItem item : items) {
            sportWeight.add(item.getSportWeight());
        }
        return sportWeight;
    }

    public void addItem(MainRecordSportItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView sportName, sportTime, sportWeight;
        ImageView sportDelte;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super (itemView);
            cardView = (CardView) itemView;
            sportName = binding.sportName;
            sportTime = binding.sportTime;
            sportWeight = binding.sportWeight;
            sportDelte = binding.sportDelte;
        }
    }

    // 총 칼로리를 업데이트하는 메소드
    void updateTotalCalories() {
        int totalTime = 0;
        int totalCal = 0;
        for (MainRecordSportItem item : items) {
            totalTime += item.getSportTime();
            totalCal += (int)(item.getSportMet() * item.getSportWeight() * (Double.valueOf(String.valueOf(item.getSportTime()))/60.0));
        }
        // 총 칼로리 텍스트뷰에 설정
        ((TextView)((MainRecordSportActivity) context).findViewById(R.id.total_time)).setText(String.valueOf(totalTime));
        ((TextView)((MainRecordSportActivity) context).findViewById(R.id.total_kcal)).setText(String.valueOf(totalCal));
        ((TextView)((MainRecordSportActivity) context).findViewById(R.id.total_kcal2)).setText(String.valueOf(totalCal));
    }///////////////

    // 운동 기록하기 메소드
    void RecordSport(String sportName,int sportTime,int sportweight){
//        SimpleDateFormat doDate = new SimpleDateFormat("yyyy-MM-dd");
//        Map<String, Object> requestData = new HashMap<>();
//
//        // record 객체 생성
//        Map<String, Object> record = new HashMap<>();
//        record.put("userId", userId);
//        record.put("doDate", doDate.toString());
//
//        // requestData에 record 객체 추가
//        requestData.put("record", record);
//
//        // sportId, sportTime, sportWeight 추가
//        requestData.put("sportId", 3);
//        requestData.put("sportTime", sportTime);
//        requestData.put("sportWeight", sportweight);
//
//        SpringService service = RetrofitClient.getSpringService();
//        Call<RecoardExerciseItem> call = service.recordsport(requestData);
    }//////////////////

}