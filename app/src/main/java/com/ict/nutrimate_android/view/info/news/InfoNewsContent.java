package com.ict.nutrimate_android.view.info.news;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.ict.nutrimate_android.databinding.InfoNewsBinding;
import com.ict.nutrimate_android.retrofit.FlaskService;
import com.ict.nutrimate_android.retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * info_news
 * info_news_item
 */
public class InfoNewsContent extends Fragment {

    private NewsPagerAdapter adapter;
    private InfoNewsBinding binding;
    private Handler sliderHandler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = InfoNewsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrofit을 사용하여 서버에서 뉴스 정보를 가져옴
        FlaskService service = RetrofitClient.getFlaskService();
        Call<List<NewsItem>> call = service.navernews();
        call.enqueue(new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Call<List<NewsItem>> call, Response<List<NewsItem>> response) {
                if(response.isSuccessful()) {
                    // 서버에서 [{},{},...] JSON배열로 응답]
                    List<NewsItem> newItems= response.body();
                    if (binding != null && binding.vpImageSlider != null) {
                        adapter = new NewsPagerAdapter(getActivity(), newItems);
                        binding.vpImageSlider.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<NewsItem>> call, Throwable t) {}
        });

        // ViewPager2의 내용이 패딩 안에 들어가지 않도록 설정합니다.
        binding.vpImageSlider.setClipToPadding(false);
        // ViewPager2의 자식 뷰가 자신의 경계를 벗어나도록 허용합니다.
        binding.vpImageSlider.setClipChildren(false);
        // ViewPager2에서 한 번에 로드할 페이지 수를 설정합니다.
        // 현재 페이지를 중심으로 좌우로 설정된 페이지 수만큼의 페이지를 미리 로드합니다.
        binding.vpImageSlider.setOffscreenPageLimit(3);
        // ViewPager2의 스크롤에서 오버 스크롤 동작을 설정합니다.
        // RecyclerView.OVER_SCROLL_NEVER는 오버 스크롤 동작을 사용하지 않도록 합니다.
        binding.vpImageSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        // 여러 개의 페이지 변환 효과를 결합할 수 있는 컨테이너 역할
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        // 페이지 사이의 여백을 40으로 설정
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        // 페이지의 크기를 조정하여 확대/축소 효과를 부여
        compositePageTransformer.addTransformer((page, position) -> {
            // 페이지가 중앙에 가까울수록 큰 값을 가지도록 설정
            // 현재 페이지의 위치(position)을 기반으로 한 값을 계산
            float r = 1 - Math.abs(position);
            // 페이지의 세로 스케일(scale)을 설정.
            // 페이지가 중앙에 있을 때는 최대 확대, 페이지가 화면의 끝에 가깝다면 축소
            page.setScaleY(0.85f + r * 0.15f);
        });
        binding.vpImageSlider.setPageTransformer(compositePageTransformer);

        // ViewPager2 페이지 변경 콜백 설정
        binding.vpImageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 슬라이더 핸들러를 제거하고 재설정하여 페이지 슬라이드를 제어
                // 현재 실행 중인 슬라이더 작업을 제거. 이를 통해 이전에 등록된 슬라이더 작업이 중단됩니다.
                sliderHandler.removeCallbacks(sliderRunnable);
                // 슬라이더 핸들러에게 지연된 작업을 요청하여 슬라이드가 2초(2000 밀리초)마다 변경되도록 설정
                sliderHandler.postDelayed(sliderRunnable, 2000);
            }
        });
    }//////////////////////////////////////////////////////onViewCreated

    // Fragment가 소멸될 때 binding 해제
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ViewPager2 자동 슬라이더 핸들러
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (binding != null && binding.vpImageSlider != null) {
                binding.vpImageSlider.setCurrentItem(binding.vpImageSlider.getCurrentItem() + 1);
            }
        }
    };

    // Fragment가 일시 중지될 때 슬라이더 핸들러 제거
    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    // Fragment가 다시 시작될 때 슬라이더 핸들러 2초마다 페이지 변경
    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }
}