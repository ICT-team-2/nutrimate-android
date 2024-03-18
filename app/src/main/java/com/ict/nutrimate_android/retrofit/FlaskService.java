package com.ict.nutrimate_android.retrofit;

import com.ict.nutrimate_android.view.home.record.food.item.gallery.FoodImageAnalyzeItem;
import com.ict.nutrimate_android.view.info.news.NewsItem;
import com.ict.nutrimate_android.view.info.recommend.food.InfoRecommendFoodItem;
import com.ict.nutrimate_android.view.info.recommend.nutri.InfoRecommendNutriItem;
import com.ict.nutrimate_android.view.info.recommend.sport.InfoRecommendSportItem;
import com.ict.nutrimate_android.view.mypage.profile.ProfileAIItem;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FlaskService {

    /** 식단 이미지 분석 - home **/
    @FormUrlEncoded
    @POST("/food")
    Call<FoodImageAnalyzeItem> foodimageanalyze(@Field("base64Encoded") String base64Encoded);

    /** 추천 - info **/
    // 식단 뉴스 크롤링
    @GET("/navernews")
    Call<List<NewsItem>> navernews();

    // 식단 추천 크롤링
    @GET("/recipe-info")
    Call<List<InfoRecommendFoodItem>> recipeinfo();

    // 운동 추천 크롤링
    @GET("/exercise-info")
    Call<List<InfoRecommendSportItem>> exerciseinfo();

    // 영양제 추천 크롤링
    @GET("/nutrients-info")
    Call<List<InfoRecommendNutriItem>> nutrientsinfo();

    /** AI - mypage **/
    // 프로필 이미지 생성 (미완)
    @Multipart
    @POST("/profile/img")
    Call<ProfileAIItem> profileAI(@Part("prompt") RequestBody prompt);
}
