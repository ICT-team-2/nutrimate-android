package com.ict.nutrimate_android.test;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.HomeMainRecordFoodBinding;
import com.ict.nutrimate_android.databinding.HomeMainRecordFoodGalleryBinding;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GalleryActivity extends AppCompatActivity {

    private HomeMainRecordFoodBinding binding;
    private HomeMainRecordFoodGalleryBinding galleryBinding;
    private SharedPreferences preferences;
    private Uri photoUri; // 찍은 사진의 URI
    private Bitmap rotatedBitmap; // 회전된 비트맵
    private String photoFileName; // 사진 파일 이름

    private LottieAnimationView lottieAnimationView;
    private ImageView imageView;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HomeMainRecordFoodBinding.inflate(getLayoutInflater());
        galleryBinding = HomeMainRecordFoodGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = getSharedPreferences("camera", Context.MODE_PRIVATE);
        lottieAnimationView = binding.imageView;
        imageView = binding.imageViewSelect;

        // 로티 이미지 클릭 이벤트 설정
        lottieAnimationView.setOnClickListener(v -> {
            openImagePicker();
        });
    }//////////////onCreate

    // 갤러리 또는 카메라 앱을 열기 위한 메소드
    private void openImagePicker() {
        // 다이얼로그를 열어 갤러리 또는 카메라 중 하나를 선택할 수 있도록 함
        dialog = new AlertDialog.Builder(this)
                .setTitle("이미지 선택")
                .setView(R.layout.home_main_record_food_gallery)
                .create();
        dialog.show();
    }

    // 카메라 버튼 클릭 이벤트 처리
//    public void onCameraButtonClick(View view) {
//        // 카메라를 열거나 카메라 촬영 메서드 호출 등의 작업을 수행합니다.
//        // 예시: launchCamera();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        photoFileName = dateFormat.format(new Date()) + "_camera.png"; // 사진 파일 이름 설정
//        ContentResolver resolver = getContentResolver(); // 콘텐트 리졸버
//        ContentValues values = new ContentValues(); // 값 초기화
//        values.put(MediaStore.MediaColumns.DISPLAY_NAME, photoFileName); // 파일 이름 설정
//        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png"); // MIME 타입 설정
//        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM); // 저장 경로 설정
//        photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); // 사진 URI 삽입
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 카메라 앱 호출 인텐트
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); // 사진 URI 전달
//        cameraLauncher.launch(intent); // 카메라 앱 실행
//        // 카메라로 사진을 찍은 후에 다이얼로그를 닫습니다.
//        dialog.dismiss();
//    }

    // 갤러리 버튼 클릭 이벤트 처리
//    public void onGalleryButtonClick(View view) {
//        Intent intent = new Intent(Intent.ACTION_PICK); // 갤러리 호출 인텐트
//        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"); // 이미지 선택
//        galleryLauncher.launch(intent); // 갤러리 앱 실행
//        // 갤러리 앱으로부터 이미지를 선택한 후에 다이얼로그를 닫습니다.
//        dialog.dismiss();
//    }

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





                    }
                }
            });

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