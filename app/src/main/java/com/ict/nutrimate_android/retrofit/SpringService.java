package com.ict.nutrimate_android.retrofit;

import com.ict.nutrimate_android.view.board.challenge.item.ChallengeSuccessItem;
import com.ict.nutrimate_android.view.board.comment.item.BoardCommentsDeleteItem;
import com.ict.nutrimate_android.view.board.crud.item.BoardSportWriteItem;
import com.ict.nutrimate_android.view.board.crud.item.BoardFeedWriteItem;
import com.ict.nutrimate_android.view.board.crud.item.BoardFoodWriteItem;
import com.ict.nutrimate_android.view.board.boarditem.BoardViewPlusItem;
import com.ict.nutrimate_android.view.board.challenge.chating.item.ChallengeChatPrevItem;
import com.ict.nutrimate_android.view.board.challenge.item.ChallengeCommentListItem;
import com.ict.nutrimate_android.view.board.comment.item.BoardCommentItem;
import com.ict.nutrimate_android.view.board.feed.item.BoardFeedItem;
import com.ict.nutrimate_android.view.board.Info.item.BoardInfoItem;
import com.ict.nutrimate_android.view.board.view.BoardViewPushItem;
import com.ict.nutrimate_android.view.board.view.item.BoardFoodViewItem;
import com.ict.nutrimate_android.view.board.view.item.BoardViewItem;
import com.ict.nutrimate_android.view.board.view.sport.BoardSportViewItem;
import com.ict.nutrimate_android.view.calendar.fcm.item.PushItem;
import com.ict.nutrimate_android.view.home.record.food.item.FoodRecord;
import com.ict.nutrimate_android.view.home.record.food.item.RecoardDietItem;
import com.ict.nutrimate_android.view.home.record.food.item.RecoardFoodItem;
import com.ict.nutrimate_android.view.home.record.food.item.RecoardFoodListItem;
import com.ict.nutrimate_android.view.home.record.food.item.RecordDietDayItem;
import com.ict.nutrimate_android.view.home.record.food.item.RecordDietDeleteItem;
import com.ict.nutrimate_android.view.home.record.sport.item.RecoardExerciseItem;
import com.ict.nutrimate_android.view.home.record.sport.item.RecoardSportItem;
import com.ict.nutrimate_android.view.home.record.sport.item.RecordExerciseDayItem;
import com.ict.nutrimate_android.view.home.record.sport.item.SportRecord;
import com.ict.nutrimate_android.view.home.statistics.item.RecordTotalCalories;
import com.ict.nutrimate_android.view.login.login.LoginItem;
import com.ict.nutrimate_android.view.mypage.follow.item.FollowFollowItem;
import com.ict.nutrimate_android.view.mypage.follow.item.FollowFolloweeListItem;
import com.ict.nutrimate_android.view.mypage.follow.item.FollowUnfollowItem;
import com.ict.nutrimate_android.view.mypage.profile.MyPageProfileItem;
import com.ict.nutrimate_android.view.mypage.profile.item.MyPageInfoItem;
import com.ict.nutrimate_android.view.mypage.profile.item.ProfileImageChangeItem;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpringService {

    /** 기록 - home **/
    // 음식 목록 가져오기
    @GET("/food/search")
    Call<RecoardFoodItem> recoardfoodlist(@Query("searchWord") String searchWord);

    // FoodId로 음식 목록 가져오기
    @GET("/food/id")
    Call<List<RecoardFoodListItem>> recoardfoodlistById(@Query("foodId") List<Integer> foodId);

    // 자신이 먹은 음식 기록하기 (식단DB 데이터 사용)
    @POST("/record/diet/db")
    Call<RecoardDietItem> recorddite(@Body FoodRecord foodRecord);

    // 식단 기록 가져오기
    @GET("/record/diet")
    Call<List<RecordDietDayItem>> recorddietday(
            @Query("userId") int userId,
            @Query("mealTime") String mealTimes,
            @Query("doDate") String... doDate
    );
    // 식단 기록 삭제
    @DELETE("/record/diet")
    Call<RecordDietDeleteItem> recorddietdelete(@Query("recordId") int recordId);

    // 운동 목록 가져오기
    @GET("/sport")
    Call<RecoardSportItem> recordsportlist(@Query("searchWord") String searchWord);

    // 운동으로 소모한 칼로리를 기록하기 (운동DB 데이터 사용)
    @POST("/record/sport/db")
    Call<RecoardExerciseItem> recordexercise(@Body SportRecord data);

    // 운동 기록 가져오기
    @GET("/record/sport")
    Call<List<RecordExerciseDayItem>> recordexerciseday(
            @Query("userId") int userId,
            @Query("doDate") String doDate
    );
    // 운동 기록 삭제
    @DELETE("/record/sport")
    Call<RecordDietDeleteItem> recordsportdelete(@Query("recordId") int recordId); // 똑같으니까 식단 기록 삭제 dto씀

    // 오늘의 통계 내용
    @GET("/record/analysis")
    Call<RecordTotalCalories> recordanalysis(
            @Query("userId") int userId,
            @Query("doDate") String doDate // 오늘 날짜
    );

    // 자신이 기록했던 칼로리 정보를 가져오기(식단,운동 분석 그래프)
    @GET("/record/analysis/graph")
    Call<List<RecordTotalCalories>> recordtotalcalories(
            @Query("userId") int userId,
            @Query("endDate") String endDate, // 오늘 날짜
            @Query("periodType") String periodType, // DAY
            @Query("periodCount") int periodCount // 기간 수
    );

    ///////////////////////////////////////////////////////////////////기록

    /** 댓글 - board **/
    // 댓글 목록 보기
    @GET("/board/comments/list/{boardId}")
    Call<List<BoardCommentItem>> getComments(@Path("boardId") int boardId);

    // 댓글 작성
    @POST("/board/comments/write")
    Call<Map<String,Integer>> boardcommentwrite(@Body Map<String,Object> data);

    // 댓글 삭제
    @POST("/board/comments/delete")
    Call<BoardCommentsDeleteItem> boardcommentdelete(@Body Map<String,Integer> data);

    ///////////////////////////////////////////////////////////////////댓글
    
    /** 북마크, 좋아요 - board **/
    @POST("/board/feed/bookmark/push")
    Call<BoardViewPushItem> boardbookmarkpush(@Body Map<String, Object> data);

    // 좋아요 추가/해제
    @POST("/board/feed/like/push")
    Call<BoardViewPushItem> boardlikepush(@Body Map<String, Object> data);

    ///////////////////////////////////////////////////////북마크, 좋아요

    /** 게시판 공통 - board **/
    @PUT("/board/view/count")
    Call<BoardViewPlusItem> boardviewplus(@Body Map<String, Object> data);

    // 게시글 상세보기 (식단)
    @GET("/board/info/diet/view")
    Call<BoardFoodViewItem> boardfoodview(
            @Query("boardId") int boardId,
            @Query("userId") int userId
    );

    // 게시글 상세보기 (운동)
    @GET("/boards/sport/{boardId}")
    Call<BoardSportViewItem> boardsportview(
            @Path("boardId") int boardId,
            @Query("userId") int userId
    );

    // 게시글 상세보기 (피드)
    @GET("/board/feed/view")
    Call<BoardViewItem> boardview(@Query("boardId") int boardId, @Query("userId") int userId);

    // 정보공유 게시판 전체 리스트
    @GET("/board/info/list")
    Call<BoardInfoItem> boardinfolist(
            @Query("userId") int userId,
            @Query("nowPage") int nowPage,
            @Query("receivePage") int receivePage,
            @Query("boardCategory") String boardCategory // 카테고리 FOOD / EXERCISE
//            ,@Query("searchColumn") String searchColumn // 검색 칼럼
//            ,@Query("searchKeyword") String... searchKeywords // 가변 인수. 검색어
    );

    // 피드 게시판 전체 리스트
    @GET("/board/feed/list")
    Call<BoardFeedItem> boardfeedlist(
            @Query("userId") int userId,
            @Query("nowPage") int nowPage,
            @Query("receivePage") int receivePage
    );

    // 식단 글 입력
    @Multipart
    @POST("/board/info/diet")
    Call<BoardFoodWriteItem> boardinfodietwrite(
            @Part("userId") int userId,
            @Part("boardTitle") String boardTitle,
            @Part("boardContent") String boardContent,
            @Part("tagNameList") String tagNameList,
            @Part MultipartBody.Part files
//            @Part("foodId") List<Integer> foodIds
    );

    // 식단 글 삭제
    @DELETE("/board/info/diet")
    Call<String> boardfooddelete(@Query("boardId") int boardId);

    // 운동 글 입력
    @POST("/boards/sport")
    Call<BoardSportWriteItem> boardsportwrite(@Body Map<String,Object> data);

    // 운동 글 삭제
    @DELETE("/boards/sport/{boardId}")
    Call<String> boardsportdelete(@Path("boardId") int boardId);

    // 피드 글 입력
    @Multipart
    @POST("/board/feed/write")
    Call<BoardFeedWriteItem> boardfeedwrite(
            @Part("userId") int userId,
            @Part("boardTitle") String boardTitle,
            @Part("boardContent") String boardContent,
            @Part("hashtag") String hashtag,
            @Part MultipartBody.Part files
    );


    /** 챌린지 - board **/
    // 댓글 내용 가지고 오기
    @GET("/challenge/comment/list")
    Call<List<ChallengeCommentListItem>> challengecommentlist(@Query("nowPage") int nowPage);

    // 채팅 내용 가지고 오기
    @GET("/challenge/chat/prev")
    Call<List<ChallengeChatPrevItem>> challengechatprev(@Query("chatroomId") int chatroomId); // 1, 3 나옴

    // 챌린지 등수 가지고 오기
    @GET("/challenge/success")
    Call<List<ChallengeSuccessItem>> challengesuccess(@Query("chatroomId") int chatroomId);

    ///////////////////////////////////////////////////////////////////게시판

    /** 팔로우 - mypage **/
    @POST("/follow/follow")
    Call<FollowFollowItem> followfollow(@Body Map<String,Object> data);

    // 팔로우 취소(내가 등록한 사람 삭제)
    @DELETE("/follow/unfollow")
    Call<FollowUnfollowItem> followunfollow(@Query("userId") int userId, @Query("followeeId") int followeeId);

    ///////////////////////////////////////////////////////////////////팔로우

    /** 마이페이지 - mypage **/
    // 로그인
    @POST("/login")
    Call<LoginItem> login(@Body Map<String,String> loginData);

    // 유저의 정보 가져오기(프로필 페이지)
    @GET("/profile")
    Call<MyPageProfileItem> mypageprofile(@Query("userId") int userId);
    
    // 자신이 작성한 정보공유 게시글 목록 가져오기
    @GET("/profile/board/info")
    Call<MyPageInfoItem> mypageinfolist(@Query("userId") int userId, @Query("nowPage") int nowPage, @Query("receivePage") int receivePage);

    // 자신이 북마크한 정보공유 게시글 목록 가져오기
    @GET("/profile/board/info/bookmark")
    Call<MyPageInfoItem> mypageinfobookmark(@Query("userId") int userId, @Query("nowPage") int nowPage, @Query("receivePage") int receivePage);

    /* 피드의 경우 - board */
    // 프로필 페이지의 작성한 피드 게시글 가져오기
    @GET("/board/feed/list")
    Call<BoardFeedItem> mypagefeedlist(
            @Query("userId") int userId,
            @Query("nowPage") int nowPage,
            @Query("receivePage") int receivePage,
            @Query("profileUserId") int profileUserId, // 들어간 프로필 페이지의 유저 아이디
            @Query("profile") boolean profile // 작성한 피드 게시글을 가져오려면 true
    );

    // 프로필 페이지의 북마크한 피드 게시글 가져오기
    @GET("/board/feed/list")
    Call<BoardFeedItem> mypagefeedbookmark(
            @Query("userId") int userId,
            @Query("nowPage") int nowPage,
            @Query("receivePage") int receivePage,
            @Query("profileUserId") int profileUserId, // 들어간 프로필 페이지의 유저 아이디
            @Query("bookmark") boolean bookmark // 북마크 목록을 가져오려면 true
    );

    // 프로필 이미지 변경
    /**
     userId : 27
     profileImage : 파일
     */
    @Multipart
    @PUT("/profile/image")
    Call<ProfileImageChangeItem> profileimagechange(
            @Part("userId") int userId,
            @Part MultipartBody.Part profileImage
    );

    // 내 팔로잉(내가 등록한 사람) 목록
    @GET("/follow/following/list")
    Call<List<FollowFolloweeListItem>> followfollowinglist(@Query("userId") int userId);

    // 내 팔로워(나를 등록한 사람) 목록
    @GET("/follow/follower/list")
    Call<List<FollowFolloweeListItem>> followfollowerlist(@Query("userId") int userId);

    ///////////////////////////////////////////////////////////////////마이페이지

    /** 회원가입, 회원정보 수정 **/
    // 회원 가입 (안해)
    /**
     Map<String, Object> data = new HashMap<>();
     data.put("userNick", "qwe12");
     data.put("userHeight", "100");
     data.put("userWeight", "100");
     data.put("userEmail", "qwe@gmail.com");
     data.put("userGender", "M");
     data.put("userSportHard", "A");
     data.put("userCal", "123");
     data.put("userName", "q");
     data.put("userUid", "qqq");
     data.put("userPwd", "qqq");
     data.put("userPhone", "1234");
     */
    @POST("/member/join")
    Call<ErrorItem> joinmember(@Body Map<String,Object> data);

    // 회원정보 수정 (안해)
    /**
     {
     Map<String, Object> data = new HashMap<>();
     data.put("userNick", "qwe12");
     data.put("userHeight", "100");
     data.put("userWeight", "100");
     data.put("userEmail", "qwe@gmail.com");
     data.put("userGender", "M");
     data.put("userSportHard", "A");
     data.put("userCal", "123");
     data.put("userName", "q");
     data.put("userPhone", "1234");
     data.put("userId", "1");
     }
     */
    @PUT("/member/mypage")
    Call<ErrorItem> editmember(@Body Map<String,Object> data);

    ////////////////////////////////////////////////////////////////회원가입, 회원정보 수정

    /** FCM **/
    // 최초 설치, 재설치 시 FCM 토큰값 DB에 저장
    @POST("/fcm/token")
    Call<Map<String,String>> newToken(@Body Map<String,String> token);

    // FCM 알림을 보내기 위해 title, body 전달
    @FormUrlEncoded
    @POST("/fcm/pushToPhone")
    Call<PushItem> pushToPhone(
            @Field("title") String title,
            @Field("body") String body
    );

}