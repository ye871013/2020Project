package com.example.tomato.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tomato.R;
import com.example.tomato.UserInfoConfig;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;


public class Analysis extends AppCompatActivity {
    TextView total_seen_number, total_fly_number;
    TextView to_be_con, no_data;
    PieChart pieChart;
    String ip, performer_id, choose_type, follows;
    private View loading_progress;
    ArrayList<PieEntry> pie_audiences = new ArrayList<>();
    Button btn_comment, btn_location, btn_sex, btn_years;
    ImageView btn_back;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fans_analysis);

        //設定隱藏標題
        getSupportActionBar().hide();
        SlidrInterface slidr = Slidr.attach(this);

        //初始化物件--------------------------------------------------------------------
        btn_comment = findViewById(R.id.btn_comment);
        btn_location = findViewById(R.id.btn_location);
        btn_sex = findViewById(R.id.btn_sex);
        btn_years = findViewById(R.id.btn_years);
        total_fly_number = findViewById(R.id.total_fly_number);
        total_seen_number = findViewById(R.id.total_seen_number);
        pieChart = findViewById(R.id.pie_chart);
        loading_progress = findViewById(R.id.loading_progress);
        btn_back = findViewById(R.id.btn_back);
        to_be_con = findViewById(R.id.to_be_con);
        no_data = findViewById(R.id.no_data);

        btn_back.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_location.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                btn_location.setTextColor(getResources().getColor(R.color.black));
                btn_years.setTextColor(getResources().getColor(R.color.white));
                btn_sex.setTextColor(getResources().getColor(R.color.white));
                btn_comment.setTextColor(getResources().getColor(R.color.white));
                Analysis_location();
            }
        });

        btn_sex.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                btn_sex.setTextColor(getResources().getColor(R.color.black));
                btn_years.setTextColor(getResources().getColor(R.color.white));
                btn_location.setTextColor(getResources().getColor(R.color.white));
                btn_comment.setTextColor(getResources().getColor(R.color.white));
                Analysis_sex();
            }
        });

        btn_comment.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                btn_comment.setTextColor(getResources().getColor(R.color.black));
                btn_years.setTextColor(getResources().getColor(R.color.white));
                btn_sex.setTextColor(getResources().getColor(R.color.white));
                btn_location.setTextColor(getResources().getColor(R.color.white));
                Analysis_comment();
            }
        });

        btn_years.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                btn_years.setTextColor(getResources().getColor(R.color.black));
                btn_location.setTextColor(getResources().getColor(R.color.white));
                btn_sex.setTextColor(getResources().getColor(R.color.white));
                btn_comment.setTextColor(getResources().getColor(R.color.white));
                Analysis_years();
            }
        });

        //初始化變數--------------------------------------------------------------------
        performer_id = UserInfoConfig.getConfig(Analysis.this,"UserInfo","ID","");
        ip = UserInfoConfig.getConfig(Analysis.this,"link","url","localhost");
        follows = UserInfoConfig.getConfig(Analysis.this,"UserInfo_follow","fans","0");
        choose_type = "first";

        Analysis_first();
    }

    //初次載入-----------------------------------------------------------------------------------
    private void Analysis_first(){
        loading_progress.setVisibility(View.VISIBLE);
        choose_type = "first";
        Load_Analysis_AsyncTask load_analysis_asyncTask
                = new Load_Analysis_AsyncTask(Analysis.this);
        load_analysis_asyncTask.execute(
                ip, performer_id, choose_type
        );
    }

    //更改上方觀看與總起飛----------------------------------------------------------------------------
    private void top_ini(String seen, String fly){
        //loading_progress.setVisibility(View.GONE);
        total_fly_number.setText(String.format("%s次", fly));
        total_seen_number.setText(String.format("%s次", seen));
        if(follows.equals("0")){
            no_fans();
        }else {
            Analysis_years();
        }
    }

    //沒有粉絲-------------------------------------------------------------------------
    private void no_fans(){
        pieChart.setVisibility(View.GONE);
        to_be_con.setVisibility(View.GONE);
        loading_progress.setVisibility(View.GONE);
        no_data.setVisibility(View.VISIBLE);
    }

    //分析留言數-----------------------------------------------------------------------------------
    private void Analysis_comment(){
        pieChart.setVisibility(View.GONE);
        to_be_con.setVisibility(View.VISIBLE);
    }

    //分析地區分布-----------------------------------------------------------------------------------
    private void Analysis_location(){
        if(follows.equals("0")){
            no_fans();
        }else {
            loading_progress.setVisibility(View.VISIBLE);
            to_be_con.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
            choose_type = "location";
            Load_Analysis_AsyncTask load_analysis_asyncTask
                    = new Load_Analysis_AsyncTask(Analysis.this);
            load_analysis_asyncTask.execute(
                    ip, performer_id, choose_type
            );
        }
    }

    //分析性別比例-----------------------------------------------------------------------------------
    private void Analysis_sex(){
        if(follows.equals("0")){
            no_fans();
        }else {
            loading_progress.setVisibility(View.VISIBLE);
            to_be_con.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
            choose_type = "sex";
            Load_Analysis_AsyncTask load_analysis_asyncTask
                    = new Load_Analysis_AsyncTask(Analysis.this);
            load_analysis_asyncTask.execute(
                    ip, performer_id, choose_type
            );}
    }

    //分析年齡比例-----------------------------------------------------------------------------------
    private void Analysis_years(){
        if(follows.equals("0")){
            no_fans();
        }else {
            loading_progress.setVisibility(View.VISIBLE);
            to_be_con.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
            choose_type = "years";
            Load_Analysis_AsyncTask load_analysis_asyncTask
                    = new Load_Analysis_AsyncTask(Analysis.this);
            load_analysis_asyncTask.execute(
                    ip, performer_id, choose_type);
        }
    }

    //判別哪種圖形--------------------------------------------------------------
    private void data_insert(String[] count_name, String[] count_number){
        if(count_number != null){
            String center_text = "";
            switch (choose_type){
                case "location":
                    center_text = "地區分布";
                    ini_pie_chart(count_name, count_number, center_text);
                    break;
                case "sex":
                    center_text = "性別分布";
                    ini_pie_chart(count_name, count_number, center_text);
                    break;
                case "years":
                    center_text = "年齡分布";
                    years_calculation(center_text, count_number);
                    break;
            }
        }
    }

    //初始化圓餅圖----------------------------------------------------------
    private void ini_pie_chart(String[] count_name, String[] count_number, String center_text){
        pie_audiences.clear();
        pieChart.clear();
        for (int i = 0; i < count_name.length ; i++) {
            pie_audiences.add(new PieEntry(Integer.parseInt(count_number[i]), count_name[i]));
        }
        PieDataSet pieDataSet = new PieDataSet(pie_audiences, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setDrawValues(false);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(25f);
        pieChart.getLegend().setEnabled(false);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(center_text);
        pieChart.setCenterTextSize(35f);
        pieChart.animate();

        loading_progress.setVisibility(View.GONE);
    }

    //計算年齡層----------------------------------------------------------
    private void years_calculation(String center_text, String[] count_number){
        Integer[] years_distinct = new Integer[]{0, 0, 0, 0, 0};
        Calendar time = Calendar.getInstance();
        int now = time.get(Calendar.YEAR);
        int temp;

        for (String s : count_number) {
            temp = Integer.parseInt(s.substring(0, 4));
            if ((now - temp) <= 18)
                years_distinct[0] += 1;
            else if ((now - temp) > 18)
                years_distinct[1] += 1;
            else if ((now - temp) > 29)
                years_distinct[2] += 1;
            else if ((now - temp) > 49)
                years_distinct[3] += 1;
            else if ((now - temp) > 60)
                years_distinct[4] += 1;
        }
        pie_audiences.clear();
        pieChart.clear();

        if(years_distinct[0] != 0)
            pie_audiences.add(new PieEntry(years_distinct[0], "18以下"));
        if(years_distinct[1] != 0)
            pie_audiences.add(new PieEntry(years_distinct[1], "19~29"));
        if(years_distinct[2] != 0)
            pie_audiences.add(new PieEntry(years_distinct[2], "30~49"));
        if(years_distinct[3] != 0)
            pie_audiences.add(new PieEntry(years_distinct[3], "50~59"));
        if(years_distinct[4] != 0)
            pie_audiences.add(new PieEntry(years_distinct[4], "60以上"));

        PieDataSet pieDataSet = new PieDataSet(pie_audiences, "");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setDrawValues(false);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(25f);
        pieChart.getLegend().setEnabled(false);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(center_text);
        pieChart.setCenterTextSize(35f);
        pieChart.animate();

        loading_progress.setVisibility(View.GONE);

    }

    public void Analysis_total_fly_number(View view){
    }

    public void Analysis_total_seen_number(View view){
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //第一次載入分析資料------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    private class Load_Analysis_AsyncTask extends AsyncTask<String, Void, Void> {
        String[] count_name, count_number;
        String fly, seen;
        Context ctx;

        private Load_Analysis_AsyncTask(Context context) {ctx = context;}

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String ip = params[0];
            String login_user_id = params[1];
            String choose_type = params[2];

            String login_url = ip + "/StreetApp_FinalProject2020/performer/Analysis.php";
            String result = "";
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("performer_id", "UTF-8") + "=" + URLEncoder.encode(login_user_id, "UTF-8")
                        + "&" + URLEncoder.encode("choose_type", "UTF-8") + "=" + URLEncoder.encode(choose_type, "UTF-8");
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
                result = stringBuilder.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //如果是地區和性別用這個---------------------------------------------------------
            if(choose_type.equals("location") || choose_type.equals("sex")){
            //將post_info的所有資料抓近來陣列
            try {
                JSONArray json_video_wall_Array = new JSONArray(result);
                JSONObject json_video_wall_Object = null;

                count_name = new String[json_video_wall_Array.length()];
                count_number = new String[json_video_wall_Array.length()];

                for (int i = 0; i < json_video_wall_Array.length(); i++) {
                    json_video_wall_Object = json_video_wall_Array.getJSONObject(i);
                    //後面的名稱丟資料庫的欄位名稱
                    //統計值得名稱-------------------------------------------------------------------
                    count_name[i] = json_video_wall_Object.getString("count_name");
                    //統計值的數量-------------------------------------------------------------------
                    count_number[i] = json_video_wall_Object.getString("count_number");
                }}
            catch(JSONException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }}else if(choose_type.equals("years")){
                try {
                    JSONArray json_video_wall_Array = new JSONArray(result);
                    JSONObject json_video_wall_Object = null;

                    count_number = new String[json_video_wall_Array.length()];

                    for (int i = 0; i < json_video_wall_Array.length(); i++) {
                        json_video_wall_Object = json_video_wall_Array.getJSONObject(i);
                        //後面的名稱丟資料庫的欄位名稱
                        //統計值的數量-------------------------------------------------------------------
                        count_number[i] = json_video_wall_Object.getString("count_number");
                    }}
                catch(JSONException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }else{
                try {
                    /*
                    JSONArray json_video_wall_Array = new JSONArray(result);
                    JSONObject json_video_wall_Object = null;
                    json_video_wall_Object = json_video_wall_Array.getJSONObject(0);
                    seen = json_video_wall_Object.getString("seen");
                    fly = json_video_wall_Object.getString("fly");
                     */

                    JSONObject jsonObject = new JSONObject(result);
                    seen = jsonObject.getString("seen");
                    fly = jsonObject.getString("fly");

                    }
                catch(JSONException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            if(choose_type.equals("first")){
                top_ini(seen, fly);
            }
            else
                data_insert(count_name, count_number);
        }

    }
}



