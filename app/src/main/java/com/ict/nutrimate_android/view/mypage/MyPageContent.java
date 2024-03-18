package com.ict.nutrimate_android.view.mypage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.MainPageActivity;
import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.HomeMainRecordFoodGalleryBinding;
import com.ict.nutrimate_android.databinding.MypageMainBinding;
import com.ict.nutrimate_android.retrofit.FlaskService;
import com.ict.nutrimate_android.retrofit.RetrofitClient;
import com.ict.nutrimate_android.retrofit.SpringService;
import com.ict.nutrimate_android.view.login.LoginActivity;
import com.ict.nutrimate_android.view.login.login.manager.UserSessionManager;
import com.ict.nutrimate_android.view.mypage.bookmark.feed.MyPageBookmarkFeedFragment;
import com.ict.nutrimate_android.view.mypage.bookmark.info.MyPageBookmarkInfoFragment;
import com.ict.nutrimate_android.view.mypage.feed.MyPageFeedFragment;
import com.ict.nutrimate_android.view.mypage.follow.FollowAdapter;
import com.ict.nutrimate_android.view.mypage.follow.item.FollowFolloweeListItem;
import com.ict.nutrimate_android.view.mypage.follow.item.FollowUnfollowItem;
import com.ict.nutrimate_android.view.mypage.info.MyPageInfoFragment;
import com.ict.nutrimate_android.view.mypage.profile.ProfileAIItem;
import com.ict.nutrimate_android.view.mypage.profile.item.ProfileImageChangeItem;
import com.ict.nutrimate_android.view.mypage.profileedit.MyPageProfileEditActivity;
import com.ict.nutrimate_android.view.mypage.profile.MyPageProfileItem;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageContent extends Fragment {

    private Context context;
    private MypageMainBinding binding;
    private AlertDialog dialog;

    // 프로그래스 바
    private AlertDialog progressDialog;
    private int userId;

    // 카메라 / 갤러리
    private HomeMainRecordFoodGalleryBinding galleryBinding;
    private SharedPreferences preferences;
    private Uri photoUri; // 찍은 사진의 URI
    private Bitmap rotatedBitmap; // 회전된 비트맵
    private String photoFileName; // 사진 파일 이름
    private AlertDialog dialogGellery;


    // 프래그먼트
    private MyPageInfoFragment myPageInfoFragment;
    private MyPageFeedFragment myPageFeedFragment;
    private MyPageBookmarkInfoFragment myPageBookmarkInfoFragment;
    private MyPageBookmarkFeedFragment myPageBookmarkFeedFragment;

    // 팔로워 / 팔로잉
    private String FollowFlag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MypageMainBinding.inflate(inflater, container, false);
        galleryBinding = HomeMainRecordFoodGalleryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        context = getContext();

        preferences = context.getSharedPreferences("camera", Context.MODE_PRIVATE);

        // 세션에서 userId 가져오기
        UserSessionManager userSessionManager = new UserSessionManager(context);
        userId = userSessionManager.getUserIdFromSharedPreferences();

        getMyPageDetail();

        //프래그먼트 생성
        myPageInfoFragment = new MyPageInfoFragment();
        myPageFeedFragment = new MyPageFeedFragment();
        myPageBookmarkInfoFragment = new MyPageBookmarkInfoFragment();
        myPageBookmarkFeedFragment = new MyPageBookmarkFeedFragment();

        // 화면 로드시 첫번째 프래그먼트로 화면 설정
        getChildFragmentManager().beginTransaction().replace(binding.container.getId(), myPageInfoFragment).commit();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 정보공유 버튼 클릭 이벤트 처리
        binding.mypageInfo.setOnClickListener(v -> {
            getChildFragmentManager().beginTransaction().replace(binding.container.getId(), myPageInfoFragment).commit();
            binding.mypageInfo.setTypeface(null, Typeface.BOLD);
            binding.mypageFeed.setTypeface(null, Typeface.NORMAL);
            binding.mypageBookmark.setTypeface(null, Typeface.NORMAL);
        });
        // 피드 버튼 클릭 이벤트 처리
        binding.mypageFeed.setOnClickListener(v -> {
            getChildFragmentManager().beginTransaction().replace(binding.container.getId(), myPageFeedFragment).commit();
            binding.mypageInfo.setTypeface(null, Typeface.NORMAL);
            binding.mypageFeed.setTypeface(null, Typeface.BOLD);
            binding.mypageBookmark.setTypeface(null, Typeface.NORMAL);
        });
        // 북마크 버튼 클릭 이벤트 처리
        binding.mypageBookmark.setOnClickListener(v -> {
            String[] bookmark = {"정보공유","피드"};
            new AlertDialog.Builder(v.getContext())
                    .setItems(bookmark, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    getChildFragmentManager().beginTransaction().replace(binding.container.getId(), myPageBookmarkInfoFragment).commit();
                                    break;
                                case 1:
                                    getChildFragmentManager().beginTransaction().replace(binding.container.getId(), myPageBookmarkFeedFragment).commit();
                                    break;
                            }
                        }
                    }).show();
            binding.mypageInfo.setTypeface(null, Typeface.NORMAL);
            binding.mypageFeed.setTypeface(null, Typeface.NORMAL);
            binding.mypageBookmark.setTypeface(null, Typeface.BOLD);
        });

        // 내 프로필 클릭 이벤트 처리 (목록형 대화상자 출력)
        String[] profile = getResources().getStringArray(R.array.mypageProfile);
        String[] profilechange = getResources().getStringArray(R.array.profileChange);
        binding.mypageProfile.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext(), R.style.AlertDialogTheme)
                    //.setCancelable(false)//기본값 true
                    .setItems(profile, (dialog,which)->{
                        //which:선택한 아이템의 인덱스
                        if (profile[which].equals("로그아웃")){
                            // 로그아웃 처리 로직 작성 필요
                            UserSessionManager userSessionManager = new UserSessionManager(context);
                            userSessionManager.logout();
                            Intent intent = new Intent(requireContext(), MainPageActivity.class);
                            startActivity(intent);
                        }
                        else if (profile[which].equals("프로필 변경")){
                            new AlertDialog.Builder(v.getContext(), R.style.AlertDialogTheme)
                                    .setItems(profilechange, (dialog_create,which_create)->{
                                        if (profilechange[which_create].equals("AI로 생성")) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme);
                                            builder.setTitle("생성할 이미지를 입력하세요");
                                            final EditText input = new EditText(requireContext());
                                            input.setPadding(35, 0, 0, 35);
                                            builder.setView(input);
                                            builder.setPositiveButton("확인", (dialog2, which2) -> {

                                                String userInput;
                                                if (input.getText().toString().trim().length()!=0) {
                                                    userInput = input.getText().toString();
                                                    // 프로그래스 바
                                                    progressDialog = new AlertDialog.Builder(requireContext())
                                                            .setCancelable(true)
                                                            .setView(R.layout.progress_layout)
                                                            .create();
                                                    /** 프로그래스 바 출력 **/
                                                    if (!progressDialog.isShowing()) {
                                                        progressDialog.show();
                                                    }
                                                    profileAIChange(userInput);
                                                    Log.i("tag", "프롬프트:" + userInput);
                                                } else {
                                                    Toast.makeText(requireContext(), "생성할 이미지를 입력해주세요",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            builder.setNegativeButton("취소", (dialog2, which2) -> dialog.cancel());
                                            builder.show();
                                            //return;
                                        } else{ // 카메라/갤러리에서 사진 가져와서 프로필 설정
                                            openImagePicker();
                                        }
                                    })
                                    .show();
                            return;
                        }
//                        else if (profile[which].equals("개인정보 변경")){
//                            // 개인정보 변경 로직 작성 필요
//                            startActivity(new Intent(requireContext(), MyPageProfileEditActivity.class));
//                        }
                        else if (profile[which].equals("회원 탈퇴")) {
                            new AlertDialog.Builder(v.getContext(), R.style.AlertDialogTheme)
                                    .setCancelable(false)
                                    .setTitle("정말 회원탈퇴 하시겠습니까?")
                                    .setPositiveButton("예", (dialog_quit, which_quit)->{
                                        //Toast.makeText(requireContext(), "회원탈퇴를 선택했습니다", Toast.LENGTH_SHORT).show();
                                        // 회원탈퇴 처리 로직 작성 필요
                                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                                        startActivity(intent);
                                    })
                                    .setNegativeButton("아니오",null)
                                    .show();
                            return;
                        }
                    })
                    .show();
        });//////////////mypageProfile

        // 팔로워 목록
        binding.mypageFollower.setOnClickListener(v -> {
            SpringService service = RetrofitClient.getSpringService();
            Call<List<FollowFolloweeListItem>> call = service.followfollowerlist(userId);

            call.enqueue(new Callback<List<FollowFolloweeListItem>>() {
                @Override
                public void onResponse(Call<List<FollowFolloweeListItem>> call, Response<List<FollowFolloweeListItem>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<FollowFolloweeListItem> followerList  = response.body();
                        // 팔로워 목록 다이얼로그 표시
                        FollowFlag = "follower";
                        followShowDialog(followerList);
                    }
                }
                @Override
                public void onFailure(Call<List<FollowFolloweeListItem>> call, Throwable t) {}
            });
        });///////////mypageFollower

        // 팔로잉 목록
        binding.mypageFollowing.setOnClickListener(v -> {
            SpringService service = RetrofitClient.getSpringService();
            Call<List<FollowFolloweeListItem>> call = service.followfollowinglist(userId);
            call.enqueue(new Callback<List<FollowFolloweeListItem>>() {
                @Override
                public void onResponse(Call<List<FollowFolloweeListItem>> call, Response<List<FollowFolloweeListItem>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<FollowFolloweeListItem> followeingList  = response.body();
                        // 팔로잉 목록 다이얼로그 표시
                        FollowFlag = "followeing";
                        followShowDialog(followeingList);
                    }
                }
                @Override
                public void onFailure(Call<List<FollowFolloweeListItem>> call, Throwable t) {}
            });

        });///////////mypageFollowing

    }/////////////onViewCreated

    // 갤러리 또는 카메라 앱을 열기 위한 메소드
    private void openImagePicker() {
        // 다이얼로그를 열어 갤러리 또는 카메라 중 하나를 선택할 수 있도록 함
//        dialogGellery = new AlertDialog.Builder(context)
//                .setTitle("이미지 선택")
//                .setView(R.layout.home_main_record_food_gallery)
//                .create();
//        dialogGellery.show();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.home_main_record_food_gallery, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("이미지 선택");
        ImageView cameraButton = dialogView.findViewById(R.id.btnCamera);
        cameraButton.setOnClickListener(v -> {
            // 카메라 버튼이 클릭되었을 때 실행할 동작을 정의합니다.
            onCameraButtonClick(v);
        });
        // 갤러리 버튼을 찾아 클릭 이벤트를 설정합니다.
        ImageView galleryButton = dialogView.findViewById(R.id.btnGallery);
        galleryButton.setOnClickListener(v -> {
            // 갤러리 버튼이 클릭되었을 때 실행할 동작을 정의합니다.
            onGalleryButtonClick(v);
        });
        dialogGellery = dialogBuilder.create();
        dialogGellery.show();
    }

    // 카메라 버튼 클릭 이벤트 처리
    public void onCameraButtonClick(View view) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        photoFileName = dateFormat.format(new Date()) + "_camera.png"; // 사진 파일 이름 설정
        ContentResolver resolver = getActivity().getContentResolver(); // 콘텐트 리졸버
        ContentValues values = new ContentValues(); // 값 초기화
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, photoFileName); // 파일 이름 설정
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png"); // MIME 타입 설정
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM); // 저장 경로 설정
        photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); // 사진 URI 삽입

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // 카메라 앱 호출 인텐트
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); // 사진 URI 전달
        cameraLauncher.launch(intent); // 카메라 앱 실행
        // 카메라로 사진을 찍은 후에 다이얼로그를 닫습니다.
        dialogGellery.dismiss();
    }

    // 갤러리 버튼 클릭 이벤트 처리
    public void onGalleryButtonClick(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK); // 갤러리 호출 인텐트
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*"); // 이미지 선택
        galleryLauncher.launch(intent); // 갤러리 앱 실행
        // 갤러리 앱으로부터 이미지를 선택한 후에 다이얼로그를 닫습니다.
        dialogGellery.dismiss();
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    InputStream is = null;
                    try {
                        // 사진 URI에서 입력 스트림 열기
                        is = getActivity().getContentResolver().openInputStream(photoUri);
                        // 비트맵 디코딩
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                        // 이미지 회전 메소드 호출
                        rotatedBitmap = rotateImage(bitmap);
                        // 이미지뷰에 설정
                        binding.mypageProfile.setImageBitmap(rotatedBitmap);
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
                        binding.mypageProfile.setImageURI(selectedImageUri);
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

    // 프로필 변경
//    private void sdfdsffsd(){
//        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
//        MultipartBody.Part body = MultipartBody.Part.createFormData("profileImage", file.getName(), requestFile);
//
//        // Retrofit 인터페이스를 구현합니다.
//        SpringService springService = RetrofitClient.getSpringService();
//        Call<ProfileImageChangeItem> springCall = springService.profileimagechange(userId, body);
//        springCall.enqueue(new Callback<ProfileImageChangeItem>() {
//            @Override
//            public void onResponse(Call<ProfileImageChangeItem> call, Response<ProfileImageChangeItem> response) {
//                Log.i("tag", "Spring 서버 응답 할거임");
//                if (response.isSuccessful()) {
//                    Log.i("tag", "Spring 서버 응답 성공");
//                    ProfileImageChangeItem items = response.body();
//                    Log.i("tag", "Spring 이미지 업로드 성공");
//                    //프로필 사진 변경
//                    Picasso.get().load(RetrofitClient.NUTRI_SPRING + items.getUserProfile())
//                            .into(binding.mypageProfile);
//                } else {
//                    Log.e("tag", "Spring 서버 응답 실패:"+response.code());
//                }
//            }
//            @Override
//            public void onFailure(Call<ProfileImageChangeItem> call, Throwable t) {
//                Log.e("tag", "Spring 이미지 업로드 실패", t);
//            }
//        });
//    }











    // 마이페이지 내 정보 불러오기
    private void getMyPageDetail() {
        SpringService service = RetrofitClient.getSpringService();
        Call<MyPageProfileItem> call = service.mypageprofile(userId);

        call.enqueue(new Callback<MyPageProfileItem>() {
            @Override
            public void onResponse(Call<MyPageProfileItem> call, Response<MyPageProfileItem> response) {
                if (response.isSuccessful()) {
                    MyPageProfileItem viewItem = response.body();
                    if (viewItem != null) {
                        // 서버에서 받은 데이터를 화면에 적용
                        setMyPageDetail(viewItem);
                    }
                }
            }
            @Override
            public void onFailure(Call<MyPageProfileItem> call, Throwable t) {}
        });
    }

    // Retrofit을 사용하여 서버에서 해당 userId에 대한 프로필 정보 가져오는 메서드
    private void setMyPageDetail(MyPageProfileItem viewItem) {
        if (binding != null && binding.mypageProfile != null) {
            // 프로필 사진 변경
            Picasso.get().load(RetrofitClient.NUTRI_SPRING+viewItem.getUserProfile())
                    .placeholder(R.drawable.ic_mypage_profile) // 기본 이미지 지정
                    .into(binding.mypageProfile);
            binding.postCount.setText(viewItem.getPostCount()); // 게시물 수
            binding.followerCount.setText(viewItem.getFollowerCount()); // 팔로워 수
            binding.followingCount.setText(viewItem.getFollowingCount()); // 팔로잉 수
            binding.userNick.setText(viewItem.getUserNick()); // 유저 닉네임
            binding.userIntro.setText(viewItem.getUserIntro()); // 자기소개
        }
    }

    // 팔로워 목록 다이얼로그 메소드
    private void followShowDialog(List<FollowFolloweeListItem> followList) {

        // 다이얼로그 생성
        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.mypage_follow_list);

        // 다이얼로그 크기 설정
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);

        if(FollowFlag.equals("follower")){ // 팔로워 목록
            ((TextView) dialog.findViewById(R.id.followList)).setText("팔로워 목록");
        } else { // 팔로잉 목록
            ((TextView) dialog.findViewById(R.id.followList)).setText("팔로잉 목록");
        }

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        // 팔로워 목록을 사용하여 어댑터에 데이터 설정
        FollowAdapter adapter = new FollowAdapter(getContext(), followList);
        recyclerView.setAdapter(adapter);

        // 다이얼로그 내의 뷰 요소 찾기
        Button buttonConfirm = dialog.findViewById(R.id.followerButtonConfirm);

        // 닫기 버튼 클릭 리스너 설정
        buttonConfirm.setOnClickListener(v -> {
            // 다이얼로그 닫기
            dialog.dismiss();
        });
        // 다이얼로그 표시
        dialog.show();
    }

    // AI프로필 설정
    private void profileAIChange(String prompt) {
        // Retrofit을 사용하여 Flask 서비스에 사용자 입력을 전달하고, 이미지를 생성한다.
        FlaskService flaskService = RetrofitClient.getFlaskService();
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), prompt);
        Call<ProfileAIItem> call = flaskService.profileAI(requestBody);

        call.enqueue(new Callback<ProfileAIItem>() {
            @Override
            public void onResponse(Call<ProfileAIItem> call, Response<ProfileAIItem> response) {
                if (response.isSuccessful()) {
                    ProfileAIItem profileAIItem = response.body();
                    if (profileAIItem != null) {
                        // Flask 서버로부터 받은 이미지 데이터를 Spring 서버에 전송한다.
                        Log.e("tag", "Flask 서버 응답 성공");
                        sendImageToSpringServer(profileAIItem.getImage());
                    }
                } else {
                    Log.e("tag", "Flask 서버 응답 실패");
                }
            }
            @Override
            public void onFailure(Call<ProfileAIItem> call, Throwable t) {
                Log.e("tag", "Flask 서버 호출 실패", t);
            }
        });
    }
    // 생성된 이미지 파일로 저장
    private void sendImageToSpringServer(String base64Image) {
        // base64 문자열을 디코딩하여 이미지 바이트 배열을 얻습니다.
        byte[] imageBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
        // 이미지 파일을 저장할 경로와 파일명을 지정합니다.
        String filePath = context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "image.jpg";
        try {
            // 이미지 바이트 배열을 파일로 저장합니다.
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(imageBytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // 저장된 이미지 파일을 API에 전송합니다.
        sendImageFileToAPI(new File(filePath));
    }////////////////////////
    // 생성된 이미지를 프로필로 설정
    private void sendImageFileToAPI(File imageFile) {

        File file = new File("/data/data/com.ict.nutrimate_android/files/image.jpg");
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profileImage", file.getName(), requestFile);

        // Retrofit 인터페이스를 구현합니다.
        SpringService springService = RetrofitClient.getSpringService();
        Call<ProfileImageChangeItem> springCall = springService.profileimagechange(userId, body);
        springCall.enqueue(new Callback<ProfileImageChangeItem>() {
            @Override
            public void onResponse(Call<ProfileImageChangeItem> call, Response<ProfileImageChangeItem> response) {
                Log.i("tag", "Spring 서버 응답 할거임");
                if (response.isSuccessful()) {
                    Log.i("tag", "Spring 서버 응답 성공");
                    ProfileImageChangeItem items = response.body();
                    Log.i("tag", "Spring 이미지 업로드 성공");
                    //프로필 사진 변경
                    Picasso.get().load(RetrofitClient.NUTRI_SPRING + items.getUserProfile())
                            .into(binding.mypageProfile);
                } else {
                    Log.e("tag", "Spring 서버 응답 실패:"+response.code());
                }
            }
            @Override
            public void onFailure(Call<ProfileImageChangeItem> call, Throwable t) {
                Log.e("tag", "Spring 이미지 업로드 실패", t);
            }
        });
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    // 액티비티가 다시 시작될 때
    @Override
    public void onResume() {
        super.onResume();
        String camera = preferences.getString("camera_no", "N");
        galleryBinding.btnCamera.setEnabled(!camera.equals("Y"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

}////////////main