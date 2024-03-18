package com.ict.nutrimate_android;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.ict.nutrimate_android.databinding.ActivityMainBinding;
import com.ict.nutrimate_android.view.board.BoardMainContent;
import com.ict.nutrimate_android.view.calendar.CalendarContent;
import com.ict.nutrimate_android.view.home.MainHomeContent;
import com.ict.nutrimate_android.view.home.record.MainRecordContent;
import com.ict.nutrimate_android.view.info.InfoMainContent;
import com.ict.nutrimate_android.view.mypage.MyPageContent;

import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity implements MainRecordContent.OnDataTransferListener {

    private ActivityMainBinding binding;
    private int currentSelectedItemId = R.id.home_menu;
    public int beforeSelectedItemId;
    private OnBackPressedCallback onBackPressedCallback;
    public List<String> menuItemTitles = new Vector<>();
    public String[] titles= new String[5];
    public int[] menuItemIds={R.id.home_menu,R.id.info_menu,R.id.board_menu,R.id.calendar_menu,R.id.mypage_menu};
    public boolean isBack=false;
//    private TokenManager tokenManager; // 토큰이 만료되었는지 확인


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        titles= getResources().getStringArray(R.array.menu_titles);
        menuItemTitles.add(titles[0]);

//        // TokenManager 초기화
//        tokenManager = new TokenManager(this);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(menuItemTitles.size()-1==0){
                    new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme)
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_delete)
                            .setMessage("앱을 종료하시겠습니까?")
                            .setPositiveButton("예", (dialog, which) -> {
                                System.exit(0);
                                //finish();
                            })
                            .setNegativeButton("아니오", null).show();
                    return;
                }
                menuItemTitles.remove(menuItemTitles.size()-1);
                String title = menuItemTitles.get(menuItemTitles.size()-1);
                int index = IntStream.range(0, titles.length)
                        .filter(i -> titles[i].equals(title))
                        .findFirst()
                        .orElse(-1);
                isBack=true;
                //선택된 아이템 아이디 설정
                binding.bottomNavigation.setSelectedItemId(menuItemIds[index]);//호출시마다 setOnItemSelectedListener호출됨 즉 메뉴 아이템 선택이 바뀜으로
            }
        });

        //프래그먼트 생성
        MainHomeContent mainContent =new MainHomeContent();
        InfoMainContent infoContent=new InfoMainContent();
        BoardMainContent boardContent=new BoardMainContent();
        CalendarContent calendarContent=new CalendarContent();
        MyPageContent myPageContent=new MyPageContent();

        //백버튼 클릭시 이전 프래그먼트 화면으로 전환하기 위한 용도
        getSupportFragmentManager().beginTransaction().replace(binding.containers.getId(), mainContent).addToBackStack("HOME").commit();
        //2. BottomNavigationView의 클릭 이벤트에 따라 프래그먼트(화면)을 교체
        binding.bottomNavigation.setOnItemSelectedListener(item->{

            String menuTitle= IntStream.range(0,menuItemIds.length)
                    .filter(i->menuItemIds[i]==item.getItemId())
                    .boxed()
                    .map(i->titles[i])
                    .collect(Collectors.joining(""));
            if(!isBack) { menuItemTitles.add(menuTitle);}
            else isBack=false;

            if(item.getItemId()==R.id.home_menu)
                getSupportFragmentManager().beginTransaction().replace(binding.containers.getId(), mainContent).addToBackStack("HOME").commit();
            else if(item.getItemId()==R.id.info_menu)
                getSupportFragmentManager().beginTransaction().replace(binding.containers.getId(),infoContent).addToBackStack("INFO").commit();
            else if(item.getItemId()==R.id.board_menu)
                getSupportFragmentManager().beginTransaction().replace(binding.containers.getId(),boardContent).addToBackStack("BOARD").commit();
            else if(item.getItemId()==R.id.calendar_menu)
                getSupportFragmentManager().beginTransaction().replace(binding.containers.getId(),calendarContent).addToBackStack("CALENDAR").commit();
            else if(item.getItemId()==R.id.mypage_menu)
                getSupportFragmentManager().beginTransaction().replace(binding.containers.getId(),myPageContent).addToBackStack("MYPAGE").commit();

            return true;//true반환해야 메뉴 아이템이 활성화 된다.
        });

    }/////onCreate

//    @Override
//    protected void onResume() {
//        super.onResume();
//        checkTokenExpiration();
//    }
//    private void checkTokenExpiration() {
//        if (tokenManager != null && tokenManager.isTokenExpired()) {
//            tokenManager.logout();
//            Toast.makeText(this, "토큰이 만료되었습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
//            // 여기서 로그인 화면으로 이동하는 등의 작업을 수행할 수 있습니다.
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//        }
//    }

    //STEP3.프래그먼트에서 전송한 데이타를 받기 위한 오버라이딩(내가 만든 이벤트 리스너의 추상 메소드)
    @Override
    public void onDataTransfer(String data) {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
    }
}//////class