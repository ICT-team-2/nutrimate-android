package com.ict.nutrimate_android.test.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ict.nutrimate_android.R;
import com.ict.nutrimate_android.databinding.TestMapBinding;
import com.ict.nutrimate_android.databinding.TestMapInfoBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class GoogleMapActivityTest extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,LocationListener {
    private TestMapBinding binding;
    private TestMapInfoBinding infoLayoutBinding;
    //사용자 위치 정보 관련 API들
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker mMarker;
    //이동한 경로에 따라 Polyline을 긋기 위한 좌표 저장용
    private List<LatLng> movePoints=new Vector<>();
    //클릭한 경로에 따라 Polyline을 긋 기위한 좌표 저장용
    private List<LatLng> clickPoints=new Vector<>();

    private GoogleMap googleMap;

    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TestMapBinding.inflate(getLayoutInflater());
        infoLayoutBinding = TestMapInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
                } catch (IOException e) { e.printStackTrace(); }
            });
        });
    }////onCreate

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
        MyInfoWindowAdapter myInfoWindowAdapter=new MyInfoWindowAdapter(infoLayoutBinding.getRoot(),position);
        googleMap.setInfoWindowAdapter(myInfoWindowAdapter);
    }/////////////addMarker

    //지도 클릭시 이벤트-구글 지도의 어느 한 지점 클릭시
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        //클릭 지점에 마커 추가
        MarkerOptions options = new MarkerOptions();
        //마커를 표시할 위치 설정
        options.position(latLng);
        //구글맵에 마커 추가
        googleMap.clear(); // 임의로 추가
        googleMap.addMarker(options);
        //마커와 마커와 사이 Polyline 그리기
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);
        //라인을 그릴 위치 저장
        clickPoints.add(latLng);
        //라인을 그릴 위치를 갖고 있는 컬렉션 설정
        polylineOptions.addAll(clickPoints);
        //인자로 받은 위치까지 라인 그리기
        googleMap.addPolyline(polylineOptions);
        //인자로 받은 위치로 카메라 이동하기
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
    }
    //지도 롱 클릭시 이벤트-지도위의 마커와 곡선 지우기
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        //지도위에 표시된 마커 지우기
        googleMap.clear();
        //Polyline과 관련된 데이타 지우기
        clickPoints.clear();
        movePoints.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //위치 서비스 시작-onPause에서 중지
            locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 3000, 3, this);
            if(googleMap !=null)//백 버튼 혹은 홈 버튼 클릭시 앱은 onPause-onStop->onRestart->onStart->onResume
                //구글맵의 내 위치 서비스 활성화
                googleMap.setMyLocationEnabled(true);//선택사항. 추가시 내위치에 dot모양 점이 생성된다
            else
                //아래 메소드 호출시 onMapReady()가 실행된다
                ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(this);
        } catch(SecurityException e){e.printStackTrace();}
    }

    @Override
    protected void onPause() {
        super.onPause();
        //위치 서비스 중지와
        locationManager.removeUpdates(this);
        try {
            //구글맵의 내 위치 서비스 활성화 중지
            googleMap.setMyLocationEnabled(false);
        }
        catch(SecurityException e){e.printStackTrace();}
    }/////////////
    //위치 변경시마다  아래 메소드 호출된다 즉 이때 그 위치로 카메라 이동하기
    @Override
    public void onLocationChanged(@NonNull Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        //수신한 위치로 카메라 이동하면서 이동 경로에 곡선 그리기
        drawPolyline(new LatLng(lat,lng));
        //이동한 현재 위치에 마커 표시하기
        addMarker(location);
    }
    //이동에 따른 Polyline 그리기용 메소드
    private void drawPolyline(LatLng latLng){
        //라인을 그리기 위한 옵션 설정(색 및 두께)
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
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
}////class