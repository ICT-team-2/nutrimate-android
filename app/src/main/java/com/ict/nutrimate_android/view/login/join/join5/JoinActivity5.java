package com.ict.nutrimate_android.view.login.join.join5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.LoginJoinpage5Binding;
import com.ict.nutrimate_android.view.login.LoginActivity;
import com.ict.nutrimate_android.view.login.join.JoinActivity6;
import com.ict.nutrimate_android.view.mypage.profileedit.MyPageProfileEditActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.IntStream;

/**
 * login_joinpage5
 * login_joinpage5_item
 */
public class JoinActivity5 extends AppCompatActivity implements JoinAdapter.OnItemClickListener {

    private LoginJoinpage5Binding binding;
    private List<JoinItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginJoinpage5Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1.어댑터에 설정할 데이타
        // 알레르기 리소스 이미지 배열]
        int[] resIds = {R.drawable.allergy1,R.drawable.allergy2,R.drawable.allergy3,R.drawable.allergy4,R.drawable.allergy5,R.drawable.allergy6,R.drawable.allergy7,R.drawable.allergy8,R.drawable.allergy9,R.drawable.allergy10};
        // 알레르기명]
        String[] allergy = {"샐러리","갑각류","유제품","계란","생선","땅콩","깨","조개류","콩","밀"};
        items = new Vector<>();
        IntStream.range(0, resIds.length).forEach(index -> items.add(new JoinItem(resIds[index], allergy[index], false)));


        //2.어댑터 생성
        JoinAdapter adapter = new JoinAdapter(this, items);
        adapter.setOnItemClickListener(this);
        //3.그리드뷰객체.setAdapter(어댑터객체) 호출로 연결
        binding.gridview.setAdapter(adapter);
        binding.gridview.setNumColumns(3);

        // 다음 버튼 클릭 이벤트 처리
        binding.joinBtn.setOnClickListener(v -> {
            // 선택된 알레르기 이름들을 토스트 메시지로 한 번에 보여줍니다.
            StringBuilder message = new StringBuilder("선택된 알레르기: ");
            List<String> selectedAllergies = adapter.selectedAllergies;
            for (String seletedallergy : selectedAllergies) {
                message.append(seletedallergy).append(", ");
            }
            // 마지막 쉼표 제거
            if (!selectedAllergies.isEmpty()) {
                message.delete(message.length() - 2, message.length());
            }
            Toast.makeText(JoinActivity5.this, message.toString(), Toast.LENGTH_SHORT).show();

            //Intent intent = new Intent(JoinActivity5.this, JoinActivity6.class);
            Intent intent = new Intent(JoinActivity5.this, MyPageProfileEditActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish(); // 이전 액티비티를 종료하여 스택에서 제거
        });
    }

    @Override
    public void onItemClick(int position) {
        // 클릭한 아이템의 데이터 가져오기
//        JoinItem clickedItem = items.get(position);
        // 가져온 데이터를 처리하거나 저장
        // 여기서는 간단히 Toast로 표시
//        Toast.makeText(this, "클릭한 알레르기: " + clickedItem.getJoinAllergyName(), Toast.LENGTH_SHORT).show();
        // 가져온 데이터를 다음 액티비티로 전달하려면 Intent에 추가하여 전송
    }

}
