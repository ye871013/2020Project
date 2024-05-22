package com.example.tomato.pagefragments;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.tomato.Activity.LoginOrSingin;
import com.example.tomato.CustomInfoAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.example.tomato.R;
import com.example.tomato.UserInfoConfig;
import com.example.tomato.background.SearchListBackgroundWorker;
import com.example.tomato.usersees.UserSeesArtistPage;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class mappage extends FragmentActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnInfoWindowClickListener {

    static final int MIN_TIME = 5000; //位置更新條件：5000 毫秒
    static final float MIN_DIST = 0;   //位置更新條件：5 公尺
    LocationManager mgr;    // 定位管理員
    private View verify_loading_progress;

    boolean isGPSEnabled;      //GPS定位是否可用
    boolean isNetworkEnabled;  //網路定位是否可用
    private GoogleMap mMap;
    LatLng currPoint;
    String longitude, latitude, ip, user_id, map_id, type;
    Marker now;
    TextView gps_turn_on, gps_turn_off, text_open_gps;
    String[] performer_perform, performer_id, performer_name,
            performer_img, perform_longitude, perform_latitude;
    String[] performer_fans, follow_status;
    mappage mappage;
    LocationManager locationManager;
    Location location;
    MarkerOptions markerOpt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map_page);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gps_turn_on = findViewById(R.id.gps_status_on);
        gps_turn_off = findViewById(R.id.gps_status_off);
        text_open_gps = findViewById(R.id.text_open_gps);
        verify_loading_progress = findViewById(R.id.verify_loading_progress);
        gps_turn_on.setOnClickListener(new TextView.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                turn_on();
            }
        });
        gps_turn_off.setOnClickListener(new TextView.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                turn_off();
            }
        });

        //初始化變數--------------------------------------------------------------------
        ip = UserInfoConfig.getConfig(mappage.this, "link", "url", "localhost");
        user_id = UserInfoConfig.getConfig(mappage.this, "UserInfo", "ID", "");
        type = UserInfoConfig.getConfig(mappage.this, "UserInfo", "role", "User");
        map_id = UserInfoConfig.getConfig(mappage.this, "GPS_Status", "map_id", "0");
        if (UserInfoConfig.getConfig(mappage.this, "GPS_Status", "map_id", "0").equals("0")) {
            gps_turn_off.setEnabled(false);
            gps_turn_off.setBackgroundColor(getResources().getColor(R.color.back_ground_black));
            gps_turn_off.setTextColor(getResources().getColor(R.color.white));
            gps_turn_on.setEnabled(true);
            gps_turn_on.setBackgroundColor(getResources().getColor(R.color.orange));
            gps_turn_on.setTextColor(getResources().getColor(R.color.black));
        } else {
            gps_turn_on.setEnabled(false);
            gps_turn_on.setBackgroundColor(getResources().getColor(R.color.back_ground_black));
            gps_turn_on.setTextColor(getResources().getColor(R.color.white));
            gps_turn_off.setEnabled(true);
            gps_turn_off.setBackgroundColor(getResources().getColor(R.color.orange));
            gps_turn_off.setTextColor(getResources().getColor(R.color.black));
        }

        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        longitude = Double.toString(location.getLongitude());
        latitude = Double.toString(location.getLatitude());
        currPoint = new LatLng(Float.parseFloat(longitude), Float.parseFloat(latitude));

        if(!UserInfoConfig.getConfig(this,"UserInfo","role","Performer").equals("Performer")){
            gps_turn_on.setVisibility(View.GONE);
            gps_turn_off.setVisibility(View.GONE);
            text_open_gps.setVisibility(View.GONE);
        }else  {
            gps_turn_on.setVisibility(View.VISIBLE);
            gps_turn_off.setVisibility(View.VISIBLE);
            text_open_gps.setVisibility(View.VISIBLE);
        }

        markerOpt = new MarkerOptions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableLocationUpdates(true);    //開啟定位更新功能
    }

    @Override
    protected void onPause() {
        super.onPause();
        enableLocationUpdates(false);    //關閉定位更新功能
    }

    @Override
    public void onLocationChanged(Location location) { // 位置變更事件
        if (location != null) {
            currPoint = new LatLng(location.getLatitude(), location.getLongitude());

            if(mMap!=null){
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(currPoint));//移動畫面至目前位置
                if (now!=null){
                    now.remove();
                }
                now =mMap.addMarker(new MarkerOptions().position(currPoint).title("目前位置"));
            }
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 200) {
            if (grantResults.length >= 1 &&
                    grantResults[0] != PackageManager.PERMISSION_GRANTED) {  // 使用者不允許權限
                Toast.makeText(this, "程式需要定位權限才能運作", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void enableLocationUpdates(boolean isTurnOn) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (isTurnOn) {
                isGPSEnabled = mgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isNetworkEnabled && !isGPSEnabled) {
                    Toast.makeText(this, "請確認已開啟定位服務", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "取得定位資訊中...", Toast.LENGTH_LONG).show();
                    if (isGPSEnabled)
                        mgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, this);
                    if (isNetworkEnabled)
                        mgr.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, this);
                }
            } else {
                mgr.removeUpdates(this);
            }
        }
    }
    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(25));
        mMap.setOnInfoWindowClickListener(this);
        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(23.468204, 120.484163);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Defualt"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        GPS_SHOW gps_show = new GPS_SHOW();
        gps_show.mappage_upload_map_mark = mappage.this;
        gps_show.execute(ip, type, user_id);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(currPoint));

        LatLng temp = new LatLng(23.468204, 120.484163);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
        /*
        //標點樣式設定
        MarkerOptions markerOpt = new MarkerOptions();
        LatLng testmark = new LatLng(23.468204, 120.484163);
        String artist_name = "雷比王子";
        String artist_perform = "跳舞";
        markerOpt.position(testmark)
                .title(artist_name)
                .snippet(artist_perform);
        //設定infowindow
        CustomInfoAdapter adapter = new CustomInfoAdapter(this);
        mMap.setInfoWindowAdapter((GoogleMap.InfoWindowAdapter) adapter);
        mMap.addMarker(markerOpt);
         */
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currPoint));
    }

    public void Backtocurrent(View v){ //移到目前設定為目前位置的畫面
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(currPoint));
        //為測試所以回到目前只會到新民-------------------------------------------------------------------
        LatLng temp = new LatLng(23.468204, 120.484163);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
    }

    //開啟表演標記-----------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void turn_on(){
        if(UserInfoConfig.getConfig(mappage.this,"GPS_Status","map_id", "0").equals("0")) {
            gps_turn_on.setEnabled(false);
            gps_turn_on.setBackgroundColor(getResources().getColor(R.color.back_ground_black));
            gps_turn_on.setTextColor(getResources().getColor(R.color.white));
            gps_turn_off.setEnabled(true);
            gps_turn_off.setBackgroundColor(getResources().getColor(R.color.orange));
            gps_turn_off.setTextColor(getResources().getColor(R.color.black));
            upload_location(ip, longitude, latitude, user_id);
        }
    }

    public void set_map_id(String upload_map_id){
        map_id = upload_map_id;
        UserInfoConfig.setConfig(mappage.this,"GPS_Status","map_id", map_id);
        System.out.println(UserInfoConfig.getConfig(mappage.this,"GPS_Status","map_id", "0"));
        refresh_GUI();
    }

    //關閉表演標記-----------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void turn_off(){
        if(!UserInfoConfig.getConfig(mappage.this,"GPS_Status","map_id", "0").equals("0")) {
            gps_turn_off.setEnabled(false);
            gps_turn_off.setBackgroundColor(getResources().getColor(R.color.back_ground_black));
            gps_turn_off.setTextColor(getResources().getColor(R.color.white));
            gps_turn_on.setEnabled(true);
            gps_turn_on.setBackgroundColor(getResources().getColor(R.color.orange));
            gps_turn_on.setTextColor(getResources().getColor(R.color.black));
            close_location(ip, UserInfoConfig.getConfig(mappage.this,"GPS_Status","map_id", "0"));
            UserInfoConfig.setConfig(mappage.this,"GPS_Status","map_id", "0");
        }
    }

    //更新介面中-----------------------------------------------------------------------------------
    public void loading(){
        verify_loading_progress.setVisibility(View.VISIBLE);
    }

    //更新完成-----------------------------------------------------------------------------------
    public void finish_loading(){
        verify_loading_progress.setVisibility(View.GONE);
    }

    //藝人點選-----------------------------------------------------------------------------------
    @Override
    public void onInfoWindowClick(Marker marker) { //按下資訊視窗事件
        /*
        Toast.makeText(this, "好耶",
                Toast.LENGTH_SHORT).show();
         */
        int i = Integer.parseInt(marker.getSnippet());

        Intent intent = new Intent(mappage.this, UserSeesArtistPage.class);
        intent.putExtra("post_user_id", performer_id[i]);
        intent.putExtra("post_user_img", performer_img[i]);
        intent.putExtra("post_user_name", performer_name[i]);
        intent.putExtra("poster_fans", performer_fans[i]);
        intent.putExtra("user_follow_status", follow_status[i]);
        intent.putExtra("user_id", user_id);
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(mappage.this, R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
        mappage.this.startActivity(intent, options.toBundle());
    }

    //開啟表演標記-----------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void upload_location(String ip, String longitude, String latitude, String user_id){
        upload_location_BackgroundWorker upload_location_backgroundWorker
                = new upload_location_BackgroundWorker(mappage.this);
        upload_location_backgroundWorker.mappage_upload_map_id = mappage.this;
        upload_location_backgroundWorker.execute(ip, longitude, latitude, user_id);
    }

    //關閉表演標記-----------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void close_location(String ip, String map_id){
        close_location_BackgroundWorker close_location_backgroundWorker
                = new close_location_BackgroundWorker(mappage.this);
        close_location_backgroundWorker.mappage_upload_map_mark = mappage.this;
        close_location_backgroundWorker.execute(ip, map_id);
    }

    //更新完成-----------------------------------------------------------------------------------
    public void refresh_GUI(){
        mMap.clear();
        GPS_SHOW gps_show = new GPS_SHOW();
        gps_show.mappage_upload_map_mark = mappage.this;
        gps_show.execute(ip, type, user_id);    }

    public void set_map_mark(String[] performer_perform, String[] performer_id,
                             String[] performer_name,String[] performer_img,
                             String[] perform_longitude, String[] perform_latitude,
                             String[] performer_fans, String[] follow_status){
       this.performer_perform = performer_perform;
       this.performer_id = performer_id;
       this.performer_name = performer_name;
       this.performer_img = performer_img;
       this.perform_longitude = perform_longitude;
        this.perform_latitude = perform_latitude;
        this.performer_fans = performer_fans;
        this.follow_status = follow_status;

        //標記目前開啟表演中藝人的位址-------------------------------------------------------------
        if(performer_id != null){
            if(performer_id[0] != null){
                for(int i = 0; i < performer_id.length ; i++){
                    //標點樣式設定
                    mark(performer_name[i], performer_img,
                            performer_perform, perform_longitude[i], perform_latitude[i],
                            performer_fans, follow_status, String.valueOf(i));
                }
            }
        }
    }

    //標記所有正在表演的點----------------------------------------------------------------------
    private void mark(String mark_artist_name, String[] performer_img,
                      String[] mark_artist_perform, String mark_longitude, String mark_latitude,
                      String[] mark_artist_fans, String[] mark_follow_status, String i){
        LatLng performer_mark = new LatLng(Double.parseDouble(mark_longitude), Double.parseDouble(mark_latitude));
        markerOpt.position(performer_mark)
                .title(mark_artist_name)
                .snippet(i);
        //設定infowindow
        CustomInfoAdapter adapter = new CustomInfoAdapter(this,
                performer_img, mark_artist_perform, mark_artist_fans, mark_follow_status);
        mMap.setInfoWindowAdapter((GoogleMap.InfoWindowAdapter) adapter);
        mMap.addMarker(markerOpt);
    }

    //抓出所有表演中的點-------------------------------------------------------------------
    private static class GPS_SHOW extends AsyncTask<String, Void, Void>  {
        String[] performer_perform, performer_id, performer_name,
                performer_img, perform_longitude, perform_latitude;
        String[] performer_fans, follow_status;

        mappage mappage_upload_map_mark;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            try {
                String ip = params[0];
                String type = params[1];
                String user_id = params[2];
                String use_url;
                use_url = ip + "/StreetApp_FinalProject2020/performer/map/gps_show.php";
                URL url = new URL(use_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")
                        + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                String result = stringBuilder.toString();

                try {
                    JSONArray json_Array = new JSONArray(result);
                    JSONObject json_Object = null;

                    performer_perform = new String[json_Array.length()];
                    performer_id = new String[json_Array.length()];
                    performer_name = new String[json_Array.length()];
                    performer_img = new String[json_Array.length()];
                    perform_longitude = new String[json_Array.length()];
                    perform_latitude = new String[json_Array.length()];
                    performer_fans = new String[json_Array.length()];
                    follow_status = new String[json_Array.length()];

                    for (int i = 0; i < json_Array.length(); i++) {
                        json_Object = json_Array.getJSONObject(i);

                        //藝人名稱或暱稱--------------------------------------------------------
                        if (json_Object.getString("performer_nickname").trim().equals("null") ||
                                json_Object.getString("performer_nickname").trim().equals(""))
                            performer_name[i] = json_Object.getString("performerName");
                        else
                            performer_name[i] = json_Object.getString("performer_nickname");

                        //藝人ID---------------------------------------------------------------------
                        performer_id[i] = json_Object.getString("id");

                        //藝人大頭貼------------------------------------------------------------------
                        performer_img[i] = json_Object.getString("imageUrl");

                        //藝人經度------------------------------------------------------------------
                        perform_longitude[i] = json_Object.getString("longitude");

                        //藝人緯度------------------------------------------------------------------
                        perform_latitude[i] = json_Object.getString("latitude");

                        //藝人表演內容------------------------------------------------------------------
                        performer_perform[i] = json_Object.getString("performTheme");

                        //藝人粉絲數量------------------------------------------------------------------
                        performer_fans[i] = json_Object.getString("performerFans");

                        //藝人追蹤狀況-----------------------------------------------------------------
                        if (json_Object.getString("follow").trim().equals("null") ||
                                json_Object.getString("follow").trim().equals(""))
                            follow_status[i] = "0";
                        else
                            follow_status[i] = "1";
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    //result = e.getMessage();
                }

                httpURLConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
//                System.out.println(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mappage_upload_map_mark.loading();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Void result) {
            mappage_upload_map_mark.set_map_mark(performer_perform, performer_id, performer_name,
                    performer_img, perform_longitude, perform_latitude, performer_fans, follow_status);
            mappage_upload_map_mark.finish_loading();
        }
    }

    //開啟表演標記-----------------------------------------------------------------------------------
    private static class upload_location_BackgroundWorker extends AsyncTask<String, Void, Void> {
        Context context;
        String upload_map_id;
        mappage mappage_upload_map_id;

        private upload_location_BackgroundWorker(Context ctx) {
            context = ctx;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String ip, longitude, latitude, user_id;
            ip = params[0];
            longitude = params[1];
            latitude = params[2];
            user_id = params[3];
            try {
                String use_url;
                use_url = ip + "/StreetApp_FinalProject2020/performer/map/turn_on.php";
                URL url = new URL(use_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("longitude", "UTF-8") + "=" + URLEncoder.encode(longitude, "UTF-8")
                        + "&" + URLEncoder.encode("latitude", "UTF-8") + "=" + URLEncoder.encode(latitude, "UTF-8")
                        + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                BufferedInputStream bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream());

                StringBuilder stringBuilder = new StringBuilder();
                InputStream inputStream = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                String result = stringBuilder.toString();

                try {
                    JSONArray json_Array = new JSONArray(result);
                    JSONObject json_Object = null;
                    json_Object = json_Array.getJSONObject(0);
                    //後面的名稱丟資料庫的欄位名稱
                    upload_map_id = json_Object.getString("map_id");
                }catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    //result = e.getMessage();
                }

                httpURLConnection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Void result) {
            mappage_upload_map_id.set_map_id(upload_map_id);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

    //關閉表演標記-----------------------------------------------------------------------------------
    private static class close_location_BackgroundWorker extends AsyncTask<String, Void, Void> {
        Context context;
        mappage mappage_upload_map_mark;

        private close_location_BackgroundWorker(Context ctx) {
            context = ctx;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String ip, close_map_id;
            ip = params[0];
            close_map_id = params[1];
            try {
                String use_url;
                use_url = ip + "/StreetApp_FinalProject2020/performer/map/turn_off.php";
                URL url = new URL(use_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("map_id", "UTF-8") + "=" + URLEncoder.encode(close_map_id, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                StringBuilder stringBuilder = new StringBuilder();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

                httpURLConnection.disconnect();

                //System.out.println(close_map_id);
                //System.out.println(ip);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Void result) {
            mappage_upload_map_mark.refresh_GUI();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

}

