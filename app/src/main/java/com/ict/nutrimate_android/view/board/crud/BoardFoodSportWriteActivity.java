package com.ict.nutrimate_android.view.board.crud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.android.SphericalUtil;
import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.BoardFoodSportWriteBinding;
import com.ict.nutrimate_android.databinding.HomeMainRecordFoodGalleryBinding;
import com.ict.nutrimate_android.databinding.TestMapInfoBinding;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.board.BoardMainContent;
import com.ict.nutrimate_android.view.board.crud.item.BoardFeedWriteItem;
import com.ict.nutrimate_android.view.board.crud.item.BoardFoodWriteItem;
import com.ict.nutrimate_android.view.board.crud.item.BoardSportWriteItem;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardFoodSportWriteActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,LocationListener {

    private BoardFoodSportWriteBinding binding;
    private String selectedItem;
    private int userId;

    // 카메라 / 갤러리 관련 필드 선언
    private boolean galleryFlag = false;
    private HomeMainRecordFoodGalleryBinding galleryBinding;
    private SharedPreferences preferences;
    private Uri photoUri; // 찍은 사진의 URI
    private Bitmap rotatedBitmap; // 회전된 비트맵
    private String photoFileName; // 사진 파일 이름
    private LottieAnimationView lottieAnimationView;
    private ImageView imageView;
    private AlertDialog dialog;

    // Google Map 관련 필드 선언
    private boolean googleMapFlag = false;
//    private TestMapInfoBinding mapInfoBinding;
    //사용자 위치 정보 관련 API들
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker mMarker;
    //이동한 경로에 따라 Polyline을 긋기 위한 좌표 저장용
    private List<LatLng> movePoints=new Vector<>();
    //클릭한 경로에 따라 Polyline을 긋 기위한 좌표 저장용
    private List<LatLng> clickPoints=new Vector<>();
    private GoogleMap googleMap;

    // 운동 글 등록을 위해 필요한 필드
    private String mapPaths;
    private String mapDistances;
    private double mapCenterLat;
    private double mapCenterLng;

    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BoardFoodSportWriteBinding.inflate(getLayoutInflater());
        galleryBinding = HomeMainRecordFoodGalleryBinding.inflate(getLayoutInflater());
//        mapInfoBinding = TestMapInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(this);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        preferences = getSharedPreferences("camera", Context.MODE_PRIVATE);
        Spinner spinner = binding.boardWriteCategory; // 게시판 카테고리
        String[] items = new String[]{"식단","운동","피드"};

        // ArrayAdapter를 사용하여 스피너에 값을 설정
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 스피너에 선택 리스너 추가
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = (String) parent.getItemAtPosition(position);
                // 선택된 값에 따라 적절한 레이아웃 로드
                switch (selectedItem) {
                    case "식단":
                        galleryFlag = true;
                        googleMapFlag = false;
                        loadDietLayout();
                        break;
                    case "운동":
                        galleryFlag = false;
                        googleMapFlag = true;
                        loadExerciseLayout();
                        break;
                    case "피드":
                        galleryFlag = true;
                        googleMapFlag = false;
                        loadFeedLayout();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택되지 않았을 때 초기화면으로 식단 게시판 카테고리로 설정
                loadDietLayout();
            }
        });////////////////// 스피너 선택 리스너

        // googleMap 관련
        //위치 관리자
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //지도를 이미지로 저장 이벤트
        binding.btnSaveMap.setOnClickListener(v -> {
            googleMap.snapshot(bitmap -> {
                //bitmap:현재 지도를 내부 저장소 캐쉬 디렉토리에 저장
                File file = getCacheDir();
                try {
                    FileOutputStream fos = new FileOutputStream(file.getAbsolutePath()+File.separator+"maps.jpg");
                    //비트맵을 이미지 파일로 압축
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                    fos.close();
                    Toast.makeText(this, "이미지 저장에 성공했어요", Toast.LENGTH_SHORT).show();
                } catch (IOException e) { e.printStackTrace(); }
            });
        });

        // 완료(글 작성) 버튼 클릭 이벤트 처리
        binding.boardWriteComplete.setOnClickListener(v -> {

            String title = binding.editTextTitle.getText().toString();
            String hashtag = binding.editTextHashtag.getText().toString();
            String content = binding.editTextContent.getText().toString();

            switch (selectedItem) {
                case "식단":
                    dietWrite(title,hashtag,content);
                    break;
                case "운동":
                    exerciseWrite(title,hashtag,content);
                    break;
                case "피드":
                    feedWrite(title,hashtag,content);
                    break;
            }
        });

    }//////////////////////////////////onCreate

    // 식단 레이아웃 로드
    private void loadDietLayout() {
        binding.boardWriteTextView.setText("오늘의 식단을 공유해주세요!");
        binding.boardWriteFoodFeed.setVisibility(View.VISIBLE);
        binding.boardWriteSport.setVisibility(View.GONE);
        binding.boardWriteFoodFeed.setOnClickListener(v -> {
            openImagePicker();
        });
    }
    // 식단 글 작성
    private void dietWrite(String title,String hashtag,String content) {
        File file = new File(getFilesDir(), "boardWriteImage.jpg");
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("files", file.getName(), requestFile);

        SpringService service = RetrofitClient.getSpringService();
        Call<BoardFoodWriteItem> call = service.boardinfodietwrite(userId,title,content,hashtag,imagePart);

        call.enqueue(new Callback<BoardFoodWriteItem>() {
            @Override
            public void onResponse(Call<BoardFoodWriteItem> call, Response<BoardFoodWriteItem> response) {
                if (response.isSuccessful()) {
                    // 업로드 성공
                    Log.i("tag", "response.body().getMessage():"+response.body().getWriteOK());
                    Toast.makeText(BoardFoodSportWriteActivity.this, "글작성에 성공했어요", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(BoardFoodSportWriteActivity.this, "글작성에 실패했어요", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BoardFoodWriteItem> call, Throwable t) {
                Log.i("tag","서버 통신 실패"+t.getMessage());
            }
        });
    }

    // 운동 레이아웃 로드
    private void loadExerciseLayout() {
        binding.boardWriteTextView.setText("오늘의 산책 코스를 공유해주세요!");
        binding.boardWriteFoodFeed.setVisibility(View.GONE);
        binding.boardWriteSport.setVisibility(View.VISIBLE);
    }
    // 운동 글 작성
    private void exerciseWrite(String title,String hashtag,String content) {

        // mapPaths를 입력형식에 맞게 파싱
        // JSON 문자열을 JsonArray로 파싱
        JsonArray jsonArray = new Gson().fromJson(mapPaths, JsonArray.class);
        // JsonArray를 원하는 형식으로 변환
        JsonArray resultArray = new JsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("lat", jsonArray.get(i).getAsJsonObject().get("latitude").getAsDouble());
            jsonObject.addProperty("lng", jsonArray.get(i).getAsJsonObject().get("longitude").getAsDouble());
            resultArray.add(jsonObject);
        }
        String result = resultArray.toString();

        Log.i("tag","입력될 mapPaths:"+result);
        Log.i("tag","입력될 mapDistances:"+mapDistances);
        Log.i("tag","입력될 mapCenterLat:"+mapCenterLat);
        Log.i("tag","입력될 mapCenterLng:"+mapCenterLng);


        // 서버에 보낼 데이터 준비
        Map<String, Object> data = new HashMap<>();
        data.put("boardCategory", "exercise");
        data.put("boardTitle", title);
        data.put("mapPaths", result);
        data.put("mapDistances", mapDistances);
        data.put("mapCenterLat", mapCenterLat);
        data.put("mapCenterLng", mapCenterLng);
        data.put("mapZoomlevel", 16);
        data.put("boardContent", content);
        data.put("userId", userId);
        List<String> hashtags = new ArrayList<>();
        hashtags.add(hashtag);
        data.put("hashtag", hashtags);

        SpringService service = RetrofitClient.getSpringService();
        Call<BoardSportWriteItem> call = service.boardsportwrite(data);

        call.enqueue(new Callback<BoardSportWriteItem>() {
            @Override
            public void onResponse(Call<BoardSportWriteItem> call, Response<BoardSportWriteItem> response) {
                if (response.isSuccessful()) {
                    // 업로드 성공
                    Log.i("tag", "response.body().getMessage():"+response.body().getMessage());
                    Toast.makeText(BoardFoodSportWriteActivity.this, "글작성에 성공했어요", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(BoardFoodSportWriteActivity.this, "글작성에 실패했어요", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BoardSportWriteItem> call, Throwable t) {
                Log.i("tag","서버 통신 실패"+t.getMessage());
            }
        });


    }

    // 피드 레이아웃 로드
    private void loadFeedLayout() {
        binding.boardWriteTextView.setText("오늘의 일상을 공유해주세요!");
        binding.boardWriteFoodFeed.setVisibility(View.VISIBLE);
        binding.boardWriteSport.setVisibility(View.GONE);
        binding.boardWriteFoodFeed.setOnClickListener(v -> {
            openImagePicker();
        });
    }
    // 피드 글 작성
    private void feedWrite(String title,String hashtag,String content) {
        File file = new File(getFilesDir(), "boardWriteImage.jpg");
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("files", file.getName(), requestFile);

        SpringService service = RetrofitClient.getSpringService();
        Call<BoardFeedWriteItem> call = service.boardfeedwrite(userId,title,content,hashtag,imagePart);

        call.enqueue(new Callback<BoardFeedWriteItem>() {
            @Override
            public void onResponse(Call<BoardFeedWriteItem> call, Response<BoardFeedWriteItem> response) {
                if (response.isSuccessful()) {
                    // 업로드 성공
                    Log.i("tag", "response.body().getMessage():"+response.body().getMessage());
                    Toast.makeText(BoardFoodSportWriteActivity.this, "글작성에 성공했어요", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(BoardFoodSportWriteActivity.this, "글작성에 실패했어요", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BoardFeedWriteItem> call, Throwable t) {
                Log.i("tag","서버 통신 실패"+t.getMessage());
            }
        });
    }

    /////////////////////////////////////// 카메라, 갤러리 시작
    private void openImagePicker() {
        // 다이얼로그를 열어 갤러리 또는 카메라 중 하나를 선택할 수 있도록 함
        dialog = new AlertDialog.Builder(this)
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
                        binding.cameraImage.setImageBitmap(rotatedBitmap);
                        binding.cameraText.setText("");
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
                        binding.cameraImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        binding.cameraImage.setImageURI(selectedImageUri);
                        binding.cameraText.setText("");

                        // 선택한 이미지를 파일로 저장
                        saveImageToFile(selectedImageUri);
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

    // 이미지를 파일로 저장하는 메서드
    private void saveImageToFile(Uri imageUri) {
        try {
            // URI를 파일 경로로 변환
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();

            // 파일을 복사하여 새로운 파일 생성
            File originalFile = new File(imagePath);
            File destinationFile = new File(getFilesDir(), "boardWriteImage.jpg");
            InputStream in = new FileInputStream(originalFile);
            OutputStream out = new FileOutputStream(destinationFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /////////////////////////////////////// 카메라, 갤러리 끝

    ///////////////////////////////////////////////// googleMap 시작
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap=googleMap;
        //지도에 이벤트 리스너 부착(클릭/롱클릭 이벤트)
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLongClickListener(this);
        try {
            //최초 앱 실행시 내 현재 위치(위도/경도)로 구글맵의 카메라를 이동시키자
            Location location=locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER);
            if(location !=null){
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                //지도 유형
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //카메라 이동
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),16));
                addMarker(location);
                clickPoints.add(new LatLng(lat,lng));
            }
        }
        catch(SecurityException e){e.printStackTrace();}
    }

    //마커 표시용 메소드
    private void addMarker(Location location){

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        String position = String.format("위도:%.3s,경도:%.3s",lat,lng);
        if(mMarker ==null) {
            MarkerOptions options = new MarkerOptions();
            //마커를 표시할 위치 설정
            options.position(new LatLng(lat, lng));
            //마커 아이콘 설정
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
            //마커 클릭 이벤트
            options.snippet(position);
            //구글 맵에 마커 추가(add라 검색시 계속 마커가 추가된다)
            googleMap.addMarker(options);
        }
        else{
            //위치정보 변경시 위치와 스니펫만 수정
            mMarker.setPosition(new LatLng(lat, lng));
            mMarker.setSnippet(position);
        }
        //커스텀 인포 원도우 설정
//        MyInfoWindowAdapter myInfoWindowAdapter=new MyInfoWindowAdapter(mapInfoBinding.getRoot(),position);
//        googleMap.setInfoWindowAdapter(myInfoWindowAdapter);
    }/////////////addMarker

    //지도 클릭시 이벤트-구글 지도의 어느 한 지점 클릭시
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        //클릭 지점에 마커 추가
        MarkerOptions options = new MarkerOptions();
        //마커를 표시할 위치 설정
        options.position(latLng);
        // 마커 아이콘 설정
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
        //구글맵에 마커 추가
        googleMap.clear(); // 마커가 계속 찍히지 않도록 clear
        googleMap.addMarker(options);

        //마커와 마커와 사이 Polyline 그리기
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        //라인을 그릴 위치 저장
        clickPoints.add(latLng);
        // 클릭한 지점들을 폴리라인에 추가(라인을 그릴 위치를 갖고 있는 컬렉션 설정)
        polylineOptions.addAll(clickPoints);
        // 인자로 받은 위치까지 라인 그리기
        googleMap.addPolyline(polylineOptions);
        // 인자로 받은 위치로 카메라 이동하기
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));


        // 폴리라인 정보 계산 및 값 할당 + 로그에 출력
        mapPaths = new Gson().toJson(clickPoints);
        Log.i("tag", "mapPaths: " + new Gson().toJson(clickPoints));

        // 거리 계산
        List<Integer> distances = calculateDistances(clickPoints);
        mapDistances = new Gson().toJson(distances);
        Log.i("tag", "mapDistances: " + new Gson().toJson(distances));

        // 중심점 계산
        LatLng centerLatLng = calculateCenter(clickPoints);
        mapCenterLat = centerLatLng.latitude;
        Log.i("tag", "mapCenterLat: " + centerLatLng.latitude);
        mapCenterLng = centerLatLng.longitude;
        Log.i("tag", "mapCenterLng: " + centerLatLng.longitude);
    }
    // 거리 계산 메서드
    private List<Integer> calculateDistances(List<LatLng> points) {
        List<Integer> distances = new ArrayList<>();
        if (points.size() > 1) {
            for (int i = 1; i < points.size(); i++) {
                double distance = SphericalUtil.computeDistanceBetween(points.get(i - 1), points.get(i));
                distances.add((int) distance);
            }
        }
        return distances;
    }
    // 중심점 계산 메서드
    private LatLng calculateCenter(List<LatLng> points) {
        double latSum = 0, lngSum = 0;
        for (LatLng point : points) {
            latSum += point.latitude;
            lngSum += point.longitude;
        }
        double latAvg = latSum / points.size();
        double lngAvg = lngSum / points.size();
        return new LatLng(latAvg, lngAvg);
    }


    //지도 롱 클릭시 이벤트-지도위의 마커와 곡선 지우기
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        //지도위에 표시된 마커 지우기
        googleMap.clear();
        //Polyline과 관련된 데이타 지우기
        clickPoints.clear();
        movePoints.clear();
        try {
            //내 현재 위치(위도/경도)로 구글맵의 카메라를 이동시키자
            Location location=locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER);
            if(location !=null){
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                //지도 유형
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //카메라 이동
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),16));
                addMarker(location);
                clickPoints.add(new LatLng(lat,lng));
            }
        }
        catch(SecurityException e){e.printStackTrace();}
    }

    //위치 변경시마다  아래 메소드 호출된다 즉 이때 그 위치로 카메라 이동하기
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        //수신한 위치로 카메라 이동하면서 이동 경로에 곡선 그리기
        drawPolyline(new LatLng(lat,lng));
        googleMap.clear(); // 마커가 계속 찍히지 않도록 clear
        //이동한 현재 위치에 마커 표시하기
        addMarker(location);
    }

    //이동에 따른 Polyline 그리기용 메소드
    private void drawPolyline(LatLng latLng){
        //라인을 그리기 위한 옵션 설정(색 및 두께)
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GREEN);
        polylineOptions.width(5);
        //라인을 그릴 위치 저장
        movePoints.add(latLng);
        //라인을 그릴 위치를 갖고 있는 컬렉션 설정
        polylineOptions.addAll(movePoints);
        //인자로 받은 위치까지 라인 그리기
        googleMap.addPolyline(polylineOptions);
        //인자로 받은 위치로 카메라 이동하기
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
    }////////////////
    ///////////////////////////////////////////////// googleMap 끝

    @Override
    protected void onResume() {
        super.onResume();
        if (galleryFlag) {
            String camera = preferences.getString("camera_no", "N");
            galleryBinding.btnCamera.setEnabled(!camera.equals("Y"));
        }
        try {
            //위치 서비스 시작-onPause에서 중지
            locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 3000, 3, this);
            if (googleMap != null)//백 버튼 혹은 홈 버튼 클릭시 앱은 onPause-onStop->onRestart->onStart->onResume
                //구글맵의 내 위치 서비스 활성화
                googleMap.setMyLocationEnabled(true);//선택사항. 추가시 내위치에 dot모양 점이 생성된다
            else
                //아래 메소드 호출시 onMapReady()가 실행된다
                ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleMapFlag) {
            //위치 서비스 중지와
            locationManager.removeUpdates(this);
            try {
                //구글맵의 내 위치 서비스 활성화 중지
                googleMap.setMyLocationEnabled(false);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }/////////////

}

