package com.ict.nutrimate_android.view.home.record.sport;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ict.nutrimate_android.MainActivity;
import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.HomeMainRecordSportBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.home.record.food.item.Record;
import com.ict.nutrimate_android.view.home.record.food.item.RecordDietDeleteItem;
import com.ict.nutrimate_android.view.home.record.sport.item.RecoardExerciseItem;
import com.ict.nutrimate_android.view.home.record.sport.item.RecoardSportItem;
import com.ict.nutrimate_android.view.home.record.sport.item.RecoardSportListItem;
import com.ict.nutrimate_android.view.home.record.sport.item.RecordExerciseDayItem;
import com.ict.nutrimate_android.view.home.record.sport.item.SportRecord;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainRecordSportActivity extends AppCompatActivity {

    private HomeMainRecordSportBinding binding;
    private AutoCompleteTextView autoCompleteTextView;
    private MainRecordSportAdapter adapter;
    private RecoardSportItem items;
    private int userId;
    private List<Integer> recordIds = new ArrayList<>(); // 기록을 불러오고 삭제하기 위한 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeMainRecordSportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(this);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        autoCompleteTextView=binding.autoCompleteTextView;
        adapter = new MainRecordSportAdapter(this, new ArrayList<>());
        binding.recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);

        // 애니메이션 2배속
        binding.sportAnimation.setSpeed(2f);

        // 운동 기록 가져오기(하루)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(new Date());
        Log.i("tag","formattedDate:"+formattedDate);

        SpringService service = RetrofitClient.getSpringService();
        Call<List<RecordExerciseDayItem>> call = service.recordexerciseday(userId, formattedDate);
        call.enqueue(new Callback<List<RecordExerciseDayItem>>() {
            @Override
            public void onResponse(Call<List<RecordExerciseDayItem>> call, Response<List<RecordExerciseDayItem>> response) {
                if (response.isSuccessful()) {
                    List<RecordExerciseDayItem> items = response.body();
                    if (items!=null){
                        for (RecordExerciseDayItem item : items) {
                            // 필요한 속성 가져오기
                            String sportName = item.getSportName();
                            int sportWeight = item.getSportWeight();
                            int sportTime = item.getSportTime();
                            int sportMet = item.getSportMet();
                            int sportId = item.getSportId();

                            // RecyclerView에 새로운 음식을 추가하기 위해 adapter에 업데이트 메소드 호출
                            MainRecordSportItem item_load = new MainRecordSportItem(sportName, sportWeight, sportTime, sportMet,sportId);
                            adapter.addItem(item_load);
                            // 기록 삭제처리를 위한 recordId 저장
                            recordIds.add(item.getRecord().getRecordId());
                            // 총 칼로리 + 운동 시간 업데이트
                            adapter.updateTotalCalories();
                        }
                        Log.i("tag","운동 불러오기 성공");
                    }
                }
            }
            @Override
            public void onFailure(Call<List<RecordExerciseDayItem>> call, Throwable t) {
                Log.i("tag","식단 불러오기 실패:",t);
            }
        });

        // 엔터키로 키보드 내리기
        autoCompleteTextView.setOnEditorActionListener((textView, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                // 데이터 입력
                String inputText = autoCompleteTextView.getText().toString().trim();
                // 입력된 데이터를 처리하는 코드 추가
                if (!inputText.equals("")) {
                    //다이얼로그 띄우기
                    AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                            .setCancelable(false)
                            .setView(R.layout.progress_layout).show();

                    Call<RecoardSportItem> call_list = service.recordsportlist(inputText);
                    call_list.enqueue(new Callback<RecoardSportItem>() {
                        @Override
                        public void onResponse(Call<RecoardSportItem> call, Response<RecoardSportItem> response) {
                            if (response.isSuccessful()) {
                                items = response.body();
                                // 다이얼로그에 운동 목록 표시
                                showSportSelectionDialog(items);
                                dialog.dismiss(); // 다이얼로그 닫기
                            }
                        }
                        @Override
                        public void onFailure(Call<RecoardSportItem> call, Throwable t) {
                            // 실패 시 처리
                            t.printStackTrace();
                            dialog.dismiss(); // 다이얼로그 닫기
                        }
                        private void showSportSelectionDialog(RecoardSportItem items) {
                            if (items==null) {
                                // 검색된 목록이 없다면
                                new AlertDialog.Builder(MainRecordSportActivity.this, R.style.AlertDialogTheme)
                                        .setMessage("찾는 운동이 없습니다. 자유 입력을 이용해주세요").setPositiveButton("확인", null).show();
                            } else {
                                List<RecoardSportListItem> sportList = items.getSportList();
                                // 다이얼로그에 표시될 운동 이름 목록
                                String[] sportNames = new String[sportList.size()];
                                for (int i = 0; i < sportList.size(); i++) {
                                    sportNames[i] = sportList.get(i).getSportName();
                                }
                                // 다이얼로그에 운동 이름 목록 표시
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainRecordSportActivity.this, R.style.AlertDialogTheme);
                                builder.setTitle("운동을 선택하세요")
                                        .setNegativeButton("취소", null)
                                        .setItems(sportNames, (dialog, which) -> {
                                            // 사용자가 선택한 운동의 정보 가져오기
                                            RecoardSportListItem selectedSport = sportList.get(which);
                                            // 몸무게, 운동 시간 입력받을 다이얼로그
                                            sportFreeShowDialog(selectedSport);
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }/////////////////////////showSportSelectionDialog
                    });
                }
                // 키보드 내리기
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                }
                handled = true;
            }
            // 텍스트 뷰 초기화
            autoCompleteTextView.setText("");
            return handled;
        });

        // 입력버튼 클릭
        binding.recordSportBtn.setOnClickListener(v -> {
            // 추가하기전, 기록들을 우선 삭제처리한다
            for (Integer recordId : recordIds) {
                Call<RecordDietDeleteItem> call_delete = service.recordsportdelete(recordId);
                call_delete.enqueue(new Callback<RecordDietDeleteItem>() {
                    @Override
                    public void onResponse(Call<RecordDietDeleteItem> call, Response<RecordDietDeleteItem> response) {
                        if (response.isSuccessful()) {
                            Log.i("tag","운동 삭제 성공");
                            Log.i("tag","response.body().getMessage():"+response.body().getMessage());
                        }
                    }
                    @Override
                    public void onFailure(Call<RecordDietDeleteItem> call, Throwable t) {
                        Log.e("tag","운동 삭제 실패");
                    }
                });
            }

            // 기록 입력를 위해 데이터를 추가한다
            List<Integer> getAllSportIds = adapter.getAllSportIds();
            List<Integer> getAllSportWeight = adapter.getAllSportWeight(); // 기록을 불러오고 삭제하기 위한 리스트 - 몸무게
            List<Integer> getAllSportTime = adapter.getAllSportTime(); // 기록을 불러오고 삭제하기 위한 리스트 - 운동 시간
            Log.i("tag","getAllSportIds:"+getAllSportIds);
            Log.i("tag","getAllSportWeight:"+getAllSportWeight);
            Log.i("tag","getAllSportTime:"+getAllSportTime);

            // SportRecord 객체 생성
            SportRecord data = new SportRecord();

            // Record 객체 생성 및 userId, doDate 설정
            Record record = new Record();
            record.setUserId(userId);
            record.setDoDate(formattedDate);
            data.setRecord(record);
            int sportId;
            int sportWeight;
            int sportTime;

            for (int i = 0; i < getAllSportIds.size(); i++) {
                // SportRecord에 foodId와 recordIntake 설정
                sportId = getAllSportIds.get(i);
                sportWeight = getAllSportWeight.get(i);
                sportTime = getAllSportTime.get(i);
                data.setSportId(sportId);
                data.setSportWeight(sportWeight);
                data.setSportTime(sportTime);

                Log.i("tag","data.getSportId():"+data.getSportId()+"data.getsportWeight():"+data.getSportWeight()+
                        "data.getSportTime():"+data.getSportTime());

                // Retrofit을 통한 서버 통신
                Call<RecoardExerciseItem> call_record = service.recordexercise(data);
                call_record.enqueue(new Callback<RecoardExerciseItem>() {
                    @Override
                    public void onResponse(Call<RecoardExerciseItem> call, Response<RecoardExerciseItem> response) {
                        if (response.isSuccessful()) {
                            Log.i("tag","식단 입력 성공");
                            Log.i("tag","response.body().getMessage():"+response.body().getMessage());
                        }
                    }
                    @Override
                    public void onFailure(Call<RecoardExerciseItem> call, Throwable t) {
                        Log.e("tag","식단 입력 실패");
                    }
                });
            }/////////////////////for
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }////////////////////////////onCreate

    // 몸무게, 운동 시간 입력 다이얼로그 메소드
    private void sportFreeShowDialog(RecoardSportListItem sportList) {
        // 다이얼로그 생성
        final Dialog dialog = new Dialog(MainRecordSportActivity.this);
        dialog.setContentView(R.layout.home_main_record_sport_dialog);

        // 다이얼로그 크기 설정
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);

        // 다이얼로그 내의 뷰 요소 찾기
        EditText sportName = dialog.findViewById(R.id.edit_editName3);
        EditText sportWeight = dialog.findViewById(R.id.edit_editWeight3);
        EditText sportTime = dialog.findViewById(R.id.edit_editTime3);
        Button btn_edit3 = dialog.findViewById(R.id.btn_edit3);
        Button btn_cancel3 = dialog.findViewById(R.id.btn_cancel3);
        sportName.setText(sportList.getSportName());

        // 확인 버튼 클릭 리스너 설정
        btn_edit3.setOnClickListener(v -> {
            // 입력된 정보로 처리하는 작업 수행
            if (sportName.getText().length()==0 || sportWeight.getText().length()==0 || sportTime.getText().length()==0) {
                Toast.makeText(this, "정보를 모두 입력해주세요", Toast.LENGTH_SHORT).show();
            } else {
                try{
                    // 몸무게와 운동 시간은 숫자만 받아야한다
                    int sportWeight_ = Integer.parseInt(sportWeight.getText().toString());
                    int sportTime_ = Integer.parseInt(sportTime.getText().toString());
                    // 변환에 성공하면 입력 진행
                    String weight = sportWeight.getText().toString();
                    String time = sportTime.getText().toString();
                    sportName.setText(sportList.getSportName());
                    sportWeight.setText(weight);
                    sportTime.setText(time);
                    int sportWeightUpdate = sportWeight_;
                    int sportTimeUpdate = sportTime_;
                    MainRecordSportItem item = new MainRecordSportItem(sportList.getSportName(),sportWeightUpdate,sportTimeUpdate,sportList.getSportMet(),sportList.getSportId());
                    adapter.addItem(item);
                    // 총 칼로리 + 운동 시간 업데이트
                    adapter.updateTotalCalories();
                    // 다이얼로그 닫기
                    dialog.dismiss();
                } catch (NumberFormatException e) {
                    // 숫자로 변환 실패 시, 적절한 에러 처리
                    Toast.makeText(this, "운동 시간과 몸무게는 숫자 형식이어야 합니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 취소 버튼
        btn_cancel3.setOnClickListener(v -> dialog.dismiss());
        // 다이얼로그 표시
        dialog.show();
    }//////////////////////////////////////
}
