package com.example.tomato.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
//import android.support.v4.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tomato.R;
import com.example.tomato.UserInfoConfig;
import com.example.tomato.pagefragments.mappage;
import com.example.tomato.pagefragments.personalpage;
import com.example.tomato.pagefragments.searchpage;
import com.example.tomato.pagefragments.videowallpage;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainPage extends AppCompatActivity {

    ListView VideoListView;
    public String login_username, login_role;

    static final int MIN_TIME = 5000; //位置更新條件：5000 毫秒
    static final float MIN_DIST = 0;   //位置更新條件：5 公尺

    LocationManager mgr;    // 定位管理員

    videowallpage VideoWall;
    searchpage SearchWall;
    personalpage PersonalWall;
    mappage Mappage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpagelayout);
        //設定隱藏標題
        getSupportActionBar().hide();
        /*設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);*/
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        login_username = getIntent().getStringExtra("login_user");
        login_role = UserInfoConfig.getConfig(this,"UserInfo","role","User");

        VideoWall = new videowallpage();
        SearchWall = new searchpage();
        PersonalWall = new personalpage();
        Mappage = new mappage();


        VideoListView = findViewById(R.id.VideoListView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.Bottom_Navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        Fragment Homepage = VideoWall;

 //       Fragment Homepage = new searchpage();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                Homepage).commit();

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    FragmentActivity fragmentActivity = null;

                    switch (menuItem.getItemId()){
                        case  R.id.VideoWall:
                            selectedFragment = VideoWall;
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    selectedFragment).commit();
                            break;
                        case  R.id.searchwall:
                            selectedFragment = SearchWall;
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    selectedFragment).commit();
                            break;
                        case  R.id.PersonalWall:
                            selectedFragment = PersonalWall;
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    selectedFragment).commit();
                            break;
                        case R.id.google_map:
                            checkPermission();
                            break;
                    }


                    return true;
                }
            };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 200) {
            if (grantResults.length >= 1 &&
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {  // 使用者不允許權限
                Toast.makeText(this, "程式需要定位權限才能運作", Toast.LENGTH_LONG).show();
            }else {
                Intent intent = new Intent (MainPage.this,mappage.class);
                startActivity(intent);
            }
        }
    }

    /*
    private void enableLocationUpdates(boolean isTurnOn) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (isTurnOn) {
                boolean isGPSEnabled = mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isNetworkEnabled && !isGPSEnabled) {
                    Toast.makeText(this, "請確認已開啟定位服務", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "取得定位資訊中...", Toast.LENGTH_LONG).show();
                    if (isGPSEnabled)
                        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, (LocationListener) this);
                    if (isNetworkEnabled)
                        mgr.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, (LocationListener) this);
                }
            } else {
                mgr.removeUpdates((LocationListener) MainPage.this);
            }
        }
    }

     */
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        //第一次開啟時
    }

    @Override
    protected void onPause() {
        super.onPause();
        //剛關閉時
       /* Fragment Homepage = SearchWall;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                Homepage).commit();*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        Fragment Homepage = VideoWall;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                Homepage).commit();*/
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}