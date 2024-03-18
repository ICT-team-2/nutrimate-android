package com.ict.nutrimate_android.view.login.join.join5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.LoginJoinpage5Binding;
import com.ict.nutrimate_android.databinding.LoginJoinpage5ItemBinding;

import java.util.ArrayList;
import java.util.List;

public class JoinAdapter extends BaseAdapter {

    private LoginJoinpage5ItemBinding binding;
    private Context context;//그리드 뷰가 실행되는 컨텍스트(필수)
    private List<JoinItem> Items;//그리드 뷰에 뿌릴 데이타(필수)
    private OnItemClickListener listener;
    List<String> selectedAllergies = new ArrayList<>();


    //이벤트 처리용
    private AlertDialog dialog;

    // 인터페이스 정의
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    // 리스너 설정 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    //인자 생성자
    public JoinAdapter(Context context, List<JoinItem> Items){
        this.context = context;
        this.Items = Items;
    }//////////////////

    // 총 아이템 수 반환
    @Override
    public int getCount() {
        return Items.size();
    }

    // position에 해당하는 아이템 반환
    @Override
    public Object getItem(int position) {
        return Items.get(position);
    }

    // 아이템의 아이디 반환
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 뷰(convertView)를 생성해서 그리드 뷰에 반환
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            LoginJoinpage5ItemBinding binding = LoginJoinpage5ItemBinding.inflate(inflater, parent, false);
            viewHolder = new ViewHolder(binding.getRoot());
            convertView = binding.getRoot();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 아이템 뷰(convertView)에 position 위치에 해당하는 데이터 설정
        JoinItem currentItem = Items.get(position);
        viewHolder.imageView.setImageResource(currentItem.getJoinAllergyResId());

        // 이미지 클릭 이벤트 처리
        viewHolder.imageView.setOnClickListener(v -> {
            // 아이템의 선택 상태를 토글
            currentItem.toggleSelected();
            // 아이템의 선택 상태에 따라 백그라운드 색 변경
            if (currentItem.isSelected()) {
                viewHolder.imageView.setBackgroundColor(context.getResources().getColor(R.color.dark_gray)); // 선택되었을 때의 색상으로 변경
                // 선택된 이미지의 알레르기 이름을 리스트에 추가
                selectedAllergies.add(currentItem.getJoinAllergyName());
            } else {
                viewHolder.imageView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent)); // 선택되지 않았을 때 투명색으로 변경
                // 선택이 해제된 경우 리스트에서 알레르기 이름을 제거
                selectedAllergies.remove(currentItem.getJoinAllergyName());
            }
            // 클릭한 이미지의 알레르기 이름 가져오기
            String allergyName = Items.get(position).getJoinAllergyName();
            // 알레르기 이름을 토스트 메시지로 표시
//            Toast.makeText(context, allergyName, Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }

    // 뷰 홀더 클래스 정의
    private static class ViewHolder {
        ImageView imageView;
        ViewHolder(View view) {
            imageView = view.findViewById(R.id.joinAllergy); // Assuming your ImageView ID is joinAllergy
        }
    }
}
