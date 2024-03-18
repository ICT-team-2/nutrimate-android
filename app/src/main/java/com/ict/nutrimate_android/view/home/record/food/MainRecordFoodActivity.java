package com.ict.nutrimate_android.view.home.record.food;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.ict.nutrimate_android.MainActivity;
import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.HomeMainRecordFoodBinding;
import com.ict.nutrimate_android.databinding.HomeMainRecordFoodGalleryBinding;
import com.ict.nutrimate_android.retrofit.FlaskService;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.home.record.food.item.gallery.FoodImageAnalyzeFoodsItem;
import com.ict.nutrimate_android.view.home.record.food.item.gallery.FoodImageAnalyzeItem;
import com.ict.nutrimate_android.view.home.record.food.item.FoodRecord;
import com.ict.nutrimate_android.view.home.record.food.item.RecoardDietItem;
import com.ict.nutrimate_android.view.home.record.food.item.RecoardFoodItem;
import com.ict.nutrimate_android.view.home.record.food.item.RecoardFoodListItem;
import com.ict.nutrimate_android.view.home.record.food.item.Record;
import com.ict.nutrimate_android.view.home.record.food.item.RecordDietDayItem;
import com.ict.nutrimate_android.view.home.record.food.item.RecordDietDeleteItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainRecordFoodActivity extends AppCompatActivity {

    private HomeMainRecordFoodBinding binding;
    private AutoCompleteTextView autoCompleteTextView;
    private TextView totalKcal;
    private MainRecordFoodAdapter adapter;
    private RecoardFoodItem items;
    private int userId;
    private String mealTime;
    private List<Integer> recordIds = new ArrayList<>(); // 기록을 불러오고 삭제하기 위한 리스트

    // 식단 이미지 추가
    private HomeMainRecordFoodGalleryBinding galleryBinding;
    private SharedPreferences preferences;
    private Uri photoUri; // 찍은 사진의 URI
    private Bitmap rotatedBitmap; // 회전된 비트맵
    private String photoFileName; // 사진 파일 이름
    private LottieAnimationView lottieAnimationView;
    private ImageView imageView;
    private android.app.AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeMainRecordFoodBinding.inflate(getLayoutInflater());
        galleryBinding = HomeMainRecordFoodGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(this);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        TextView menuName = binding.menuName;
        Intent intented = getIntent();
        if (intented != null) {
            String menuType = intented.getStringExtra("menuType");
            if (menuType != null) {
                menuName.setText(menuType+" 메뉴");
            }
            if (menuType.equals("아침")){
                mealTime="BREAKFAST";
            } else if(menuType.equals("점심")){
                mealTime="LUNCH";
            } else if(menuType.equals("저녁")){
                mealTime="DINNER";
            } else{
                mealTime="SNACK";
            }
        }


        // 식단 이미지 추가
        preferences = getSharedPreferences("camera", Context.MODE_PRIVATE);
        lottieAnimationView = binding.imageView;
        imageView = binding.imageViewSelect;

        // 로티 이미지 클릭 이벤트 설정
        lottieAnimationView.setOnClickListener(v -> {
            openImagePicker();
        });

        if (binding != null && binding.autoCompleteTextView != null && binding.totalKcal != null && binding.recyclerView != null) {
            autoCompleteTextView = binding.autoCompleteTextView;
            totalKcal = binding.totalKcal;
            adapter = new MainRecordFoodAdapter(this, new ArrayList<>(), mealTime);
            binding.recyclerView.setAdapter(adapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            binding.recyclerView.setLayoutManager(layoutManager);
        }
        // 식단 기록 가져오기(하루)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(new Date());

        SpringService service = RetrofitClient.getSpringService();
        Call<List<RecordDietDayItem>> call = service.recorddietday(userId, mealTime, formattedDate);
        call.enqueue(new Callback<List<RecordDietDayItem>>() {
            @Override
            public void onResponse(Call<List<RecordDietDayItem>> call, Response<List<RecordDietDayItem>> response) {
                if (response.isSuccessful()) {
                    List<RecordDietDayItem> items = response.body();
                    if (items!=null){
                        for (RecordDietDayItem item : items) {
                            // 필요한 속성 가져오기
                            String foodName = item.getFoodName();
                            int foodId = item.getFoodId();
                            int foodCal = item.getFoodCal();
                            int foodIntake = item.getFoodIntake(); // 섭취량
                            int foodCarbo = item.getFoodCarbo(); // 탄수화물
                            int foodProtein = item.getFoodProtein(); // 단백질
                            int foodProvi = item.getFoodProvi(); // 지방

                            // RecyclerView에 새로운 음식을 추가하기 위해 adapter에 업데이트 메소드 호출
                            MainRecordFoodItem item_load = new MainRecordFoodItem(foodName, foodCal, foodId, foodIntake,foodCarbo,foodProtein,foodProvi);
                            adapter.addItem(item_load);
                            // 기록 삭제처리를 위한 recordId 저장
                            recordIds.add(item.getRecord().getRecordId());
                            // 총 칼로리 업데이트
                            totalKcal.setText(adapter.totalCalories());
                            // 탄,단,지 업데이트 메소드 호출
                            RecoardFoodListItem loadItem = createLoadItem(item);
                            updateNutrients(loadItem);
                        }
                        Log.i("tag","식단 불러오기 성공");
                    }
                }
             }
            private RecoardFoodListItem createLoadItem(RecordDietDayItem item) {
                RecoardFoodListItem loadItem = new RecoardFoodListItem();
                loadItem.setUserId(item.getUserId());
                loadItem.setFoodId(item.getFoodId());
                loadItem.setFoodName(item.getFoodName());
                loadItem.setFoodGroup(item.getFoodGroup());
                loadItem.setFoodCal(item.getFoodCal());
                loadItem.setFoodIntake(item.getFoodIntake());
                loadItem.setIntakeUnit(item.getIntakeUnit());
                loadItem.setFoodCarbo(item.getFoodCarbo());
                loadItem.setFoodProtein(item.getFoodProtein());
                loadItem.setFoodProvi(item.getFoodProvi());
                loadItem.setFoodChole(item.getFoodChole());
                loadItem.setFoodSalt(item.getFoodSalt());
                return loadItem;
            }
            @Override
            public void onFailure(Call<List<RecordDietDayItem>> call, Throwable t) {
                Log.i("tag","식단 불러오기 실패:",t);
            }
        });

        // 엔터키로 키보드 내리기
        autoCompleteTextView.setOnEditorActionListener((textView,actionId,event)->{
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

                    Call<RecoardFoodItem> call_list = service.recoardfoodlist(inputText);
                    call_list.enqueue(new Callback<RecoardFoodItem>() {
                         @Override
                         public void onResponse(Call<RecoardFoodItem> call, Response<RecoardFoodItem> response) {
                             if (response.isSuccessful()) {
                                 items = response.body();
                                 // 다이얼로그에 음식 목록 표시
                                 showFoodSelectionDialog(items);
                                 dialog.dismiss(); // 다이얼로그 닫기
                             }
                         }
                        @Override
                         public void onFailure(Call<RecoardFoodItem> call, Throwable t) {
                             // 실패 시 처리
                             t.printStackTrace();
                             dialog.dismiss(); // 다이얼로그 닫기
                         }
                        private void showFoodSelectionDialog(RecoardFoodItem items) {
                            if (items==null) {
                                // 검색된 목록이 없다면
                                new AlertDialog.Builder(MainRecordFoodActivity.this, R.style.AlertDialogTheme)
                                        .setMessage("찾는 음식이 없습니다. 자유 입력을 이용해주세요").setPositiveButton("확인", null).show();
                            } else {
                                List<RecoardFoodListItem> foodList = items.getFoodList();
                                // 다이얼로그에 표시될 음식 이름 목록
                                String[] foodNames = new String[foodList.size()];
                                for (int i = 0; i < foodList.size(); i++) {
                                    foodNames[i] = foodList.get(i).getFoodName();
                                }
                                // 다이얼로그에 음식 이름 목록 표시
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainRecordFoodActivity.this, R.style.AlertDialogTheme);
                                builder.setTitle("음식을 선택하세요")
                                        .setNegativeButton("취소", null)
                                        .setItems(foodNames, (dialog, which) -> {
                                            // 사용자가 선택한 음식의 정보를 가져옴
                                            RecoardFoodListItem selectedFood = foodList.get(which);
                                            // 선택한 음식의 정보를 MainRecordFoodItem으로 생성하고 리스트에 추가
                                            MainRecordFoodItem item = new MainRecordFoodItem(selectedFood.getFoodName(), selectedFood.getFoodCal(), selectedFood.getFoodId(),
                                                    selectedFood.getFoodIntake(), selectedFood.getFoodCarbo(),selectedFood.getFoodProtein(), selectedFood.getFoodProvi());
                                            // RecyclerView에 새로운 음식을 추가하기 위해 adapter에 업데이트 메소드 호출
                                            adapter.addItem(item);
                                            // 총 칼로리 업데이트
                                            totalKcal.setText(adapter.totalCalories());
                                            // 탄,단,지 업데이트
                                            updateNutrients(selectedFood);
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }

                        }
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
        binding.recordFoodBtn.setOnClickListener(v -> {
            // 추가하기전, 기록들을 우선 삭제처리한다
            for (Integer recordId : recordIds) {
                Call<RecordDietDeleteItem> call_delete = service.recorddietdelete(recordId);
                call_delete.enqueue(new Callback<RecordDietDeleteItem>() {
                    @Override
                    public void onResponse(Call<RecordDietDeleteItem> call, Response<RecordDietDeleteItem> response) {
                        if (response.isSuccessful()) {
                            Log.i("tag","식단 삭제 성공");
                            Log.i("tag","response.body().getMessage():"+response.body().getMessage());
                        }
                    }
                    @Override
                    public void onFailure(Call<RecordDietDeleteItem> call, Throwable t) {
                        Log.e("tag","식단 삭제 실패");
                    }
                });
            }
            
            // 기록 입력를 위해 데이터를 추가한다
            List<Integer> getAllFoodIds = adapter.getAllFoodIds();
            List<Integer> getAllIntake = adapter.getAllFoodIntake();

            // FoodRecord 객체 생성
            FoodRecord data = new FoodRecord();

            // Record 객체 생성 및 userId, doDate 설정
            Record record = new Record();
            record.setUserId(userId);
            record.setDoDate(formattedDate);
            data.setRecord(record);
            data.setMealTime(mealTime);
            int foodId;
            int intake;

            for (int i = 0; i < getAllFoodIds.size(); i++) {
                // FoodRecord에 foodId와 recordIntake 설정
                foodId = getAllFoodIds.get(i);
                intake = getAllIntake.get(i);
                data.setFoodId(foodId);
                data.setRecordIntake(intake);

                // Retrofit을 통한 서버 통신
                Call<RecoardDietItem> call_record = service.recorddite(data);
                call_record.enqueue(new Callback<RecoardDietItem>() {
                    @Override
                    public void onResponse(Call<RecoardDietItem> call, Response<RecoardDietItem> response) {
                        if (response.isSuccessful()) {
                            Log.i("tag","식단 입력 성공");
                        }
                    }
                    @Override
                    public void onFailure(Call<RecoardDietItem> call, Throwable t) {
                        Log.e("tag","식단 입력 실패");
                    }
                });

            }/////////////////////for
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }//////////////////////////////onCreate

    // 아이템 삭제시 탄, 단, 지 값 업데이트 메소드
    private void updateNutrients(RecoardFoodListItem selectedFood) {
        binding.foodCarbo.setText(String.valueOf(Integer.parseInt(binding.foodCarbo.getText().toString()) + selectedFood.getFoodCarbo()));
        binding.foodProtein.setText(String.valueOf(Integer.parseInt(binding.foodProtein.getText().toString()) + selectedFood.getFoodProtein()));
        binding.foodProvi.setText(String.valueOf(Integer.parseInt(binding.foodProvi.getText().toString()) + selectedFood.getFoodProvi()));
    }


    // 식단 이미지 추가
    // 갤러리 또는 카메라 앱을 열기 위한 메소드
    private void openImagePicker() {
        // 다이얼로그를 열어 갤러리 또는 카메라 중 하나를 선택할 수 있도록 함
        dialog = new android.app.AlertDialog.Builder(this)
                .setTitle("이미지 선택")
                .setView(R.layout.home_main_record_food_gallery)
                .create();
        dialog.show();
    }
    // 카메라 버튼 클릭 이벤트 처리
    public void onCameraButtonClick(View view) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        photoFileName = dateFormat.format(new Date()) + "_camera.png"; // 사진 파일 이름 설정
        ContentResolver resolver = getContentResolver(); // 콘텐트 리졸버
        ContentValues values = new ContentValues(); // 값 초기화
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, photoFileName); // 파일 이름 설정
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png"); // MIME 타입 설정
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM); // 저장 경로 설정
        photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); // 사진 URI 삽입

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 카메라 앱 호출 인텐트
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); // 사진 URI 전달
        cameraLauncher.launch(intent); // 카메라 앱 실행
        // 카메라로 사진을 찍은 후에 다이얼로그를 닫습니다.
        dialog.dismiss();
    }
    // 갤러리 버튼 클릭 이벤트 처리
    public void onGalleryButtonClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK); // 갤러리 호출 인텐트
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"); // 이미지 선택
        galleryLauncher.launch(intent); // 갤러리 앱 실행
        // 갤러리 앱으로부터 이미지를 선택한 후에 다이얼로그를 닫습니다.
        dialog.dismiss();
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    InputStream is = null;
                    try {
                        // 사진 URI에서 입력 스트림 열기
                        is = getContentResolver().openInputStream(photoUri);
                        // 비트맵 디코딩
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                        // 이미지 회전 메소드 호출
                        rotatedBitmap = rotateImage(bitmap);
                        // 이미지뷰에 설정
                        binding.imageView.setVisibility(View.GONE);
                        binding.imageViewSelect.setVisibility(View.VISIBLE);
                        binding.imageViewSelect.setImageBitmap(rotatedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        // 선택한 이미지 URI
                        Uri selectedImageUri = result.getData().getData();
                        // 이미지뷰에 설정
                        binding.imageView.setVisibility(View.GONE);
                        binding.imageViewSelect.setVisibility(View.VISIBLE);
                        binding.imageViewSelect.setImageURI(selectedImageUri);

                        // 이미지를 Base64로 변환하여 서버에 전송
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                byteArrayOutputStream.write(buffer, 0, bytesRead);
                            }
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();
                            String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                            //Log.i("tag","base64Image:"+base64Image);

                            FlaskService service = RetrofitClient.getFlaskService();
                            Call<FoodImageAnalyzeItem> call = service.foodimageanalyze(base64Image);
                            call.enqueue(new Callback<FoodImageAnalyzeItem>() {
                                @Override
                                public void onResponse(Call<FoodImageAnalyzeItem> call, Response<FoodImageAnalyzeItem> response) {
                                    FoodImageAnalyzeItem foodImageAnalyzeItem = response.body();
                                    Log.i("tag","foodImageAnalyzeItem.getMessage():"+foodImageAnalyzeItem.getMessage());
                                    if (response.isSuccessful() && foodImageAnalyzeItem!=null){
                                        Log.i("tag","이미지 분석 통신은 성공");
                                        List<FoodImageAnalyzeFoodsItem> items = foodImageAnalyzeItem.getFoods();
                                        if (items.isEmpty())
                                            Toast.makeText(MainRecordFoodActivity.this, "이미지 인식에 실패했어요", Toast.LENGTH_SHORT).show();
                                        List<Integer> foodList = new ArrayList<>();
                                        for (FoodImageAnalyzeFoodsItem item : items){
                                            Log.i("tag","item.getFoodId():"+item.getFoodId());
                                            Log.i("tag","item.getFoodName():"+item.getFoodName());
                                            foodList.add(item.getFoodId());
                                        }

                                        SpringService springService = RetrofitClient.getSpringService();
                                        Call<List<RecoardFoodListItem>> call_foodList = springService.recoardfoodlistById(foodList);

                                        call_foodList.enqueue(new Callback<List<RecoardFoodListItem>>() {
                                            @Override
                                            public void onResponse(Call<List<RecoardFoodListItem>> call, Response<List<RecoardFoodListItem>> response) {
                                                List<RecoardFoodListItem> items = response.body();
                                                if (response.isSuccessful() && foodList!=null){
                                                    Log.i("tag","리사이클러뷰 추가 통신은 성공");
                                                    for (RecoardFoodListItem item : items){
                                                        Log.i("tag","리사이클러뷰 추가 item.getFoodId():"+item.getFoodId());
                                                        // 선택한 음식의 정보를 MainRecordFoodItem으로 생성하고 리스트에 추가
                                                        MainRecordFoodItem mainRecordFoodItem = new MainRecordFoodItem(item.getFoodName(), item.getFoodCal(), item.getFoodId(),
                                                                item.getFoodIntake(), item.getFoodCarbo(),item.getFoodProtein(), item.getFoodProvi());
                                                        // RecyclerView에 새로운 음식을 추가하기 위해 adapter에 업데이트 메소드 호출
                                                        adapter.addItem(mainRecordFoodItem);
                                                        // 총 칼로리 업데이트
                                                        totalKcal.setText(adapter.totalCalories());
                                                        // 탄,단,지 업데이트
                                                        updateNutrients(item);
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<List<RecoardFoodListItem>> call, Throwable t) {
                                                Log.i("tag","검색 실패:"+t.getMessage());
                                            }
                                        });
                                    }///////////////////////foodId로 검색 레트로핏 끝
                                }/////////////////////base64로 식단 이미지 분석 레트로핏 끝
                                @Override
                                public void onFailure(Call<FoodImageAnalyzeItem> call, Throwable t) {}
                            });
                            // 전송 후에는 inputStream을 닫아줍니다.
                            inputStream.close();
                        } catch (IOException e) { e.printStackTrace(); }
                    }
                }
            });//////////////////////////////////////////

    //이미지 회전
    private Bitmap rotateImage(Bitmap bitmap) {
        float degree = 0;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    // 액티비티가 다시 시작될 때
    @Override
    protected void onResume() {
        super.onResume();
        String camera = preferences.getString("camera_no", "N");
        galleryBinding.btnCamera.setEnabled(!camera.equals("Y"));
    }

}