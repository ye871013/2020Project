package com.example.tomato.usersees;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tomato.Activity.LoginOrSingin;
import com.example.tomato.R;
import com.example.tomato.UserInfoConfig;
import com.example.tomato.pagefragments.mappage;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
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


public class UserSeesEditPage extends AppCompatActivity {
    private SlidrInterface slidr;
    ImageView btn_log_out, Top_artist_pictures;
    String user_id, user_type, ip;
    LinearLayout performer_info, user_phone_bar, user_location_bar, user_birthday_bar;
    private View verify_loading_progress;
    EditText user_name, phone_number, user_account, user_email, user_password,
            user_birthday, performer_perform, performer_social, performer_introduce;
    Spinner user_address, performer_location;
    String temp_user_name, temp_phone_number, temp_user_account, temp_user_email, temp_user_password,
            temp_user_birthday, temp_user_address, temp_performer_perform, temp_performer_location,
            temp_performer_social, temp_performer_introduce;
    Button edit_confirm;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_edit_page);

        //設定隱藏標題
        getSupportActionBar().hide();
        slidr = Slidr.attach(this);

        //匯入基本判斷用變數---------------------------------------------------------------
        user_id = UserInfoConfig.getConfig(this,"UserInfo","ID","");
        user_type = UserInfoConfig.getConfig(this,"UserInfo","role","User");
        ip = UserInfoConfig.getConfig(this,"link","url","localhost");

        //初始化GUI---------------------------------------------------------------
        performer_info = findViewById(R.id.performer_info);
        user_phone_bar = findViewById(R.id.user_phone_bar);
        user_location_bar = findViewById(R.id.user_location_bar);
        user_birthday_bar = findViewById(R.id.user_birthday_bar);
        btn_log_out = findViewById(R.id.btn_log_out);
        user_name = findViewById(R.id.user_name);
        phone_number = findViewById(R.id.phone_number);
        user_account = findViewById(R.id.user_account);
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        user_birthday = findViewById(R.id.user_birthday);
        user_address = findViewById(R.id.user_address);
        performer_perform = findViewById(R.id.performer_perform);
        performer_location = findViewById(R.id.performer_location);
        performer_social = findViewById(R.id.performer_social);
        performer_introduce = findViewById(R.id.performer_introduce);
        edit_confirm = findViewById(R.id.edit_confirm);
        verify_loading_progress = findViewById(R.id.verify_loading_progress);
        Top_artist_pictures = findViewById(R.id.Top_artist_pictures);

        final String[] Location = {"北區", "中區", "南區", "東區"};
        final String[] Performer_Location = {"臺北市", "桃園市", "基隆市", "臺中市",
        "雲林縣", "彰化縣", "南投縣", "高雄市", "嘉義縣", "臺南市", "屏東縣"};


        //設定性別、區域三個下拉式選單---------------------------------------------------------------
        ArrayAdapter<String> LocationList = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Location);
        ArrayAdapter<String> Performer_LocationList = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Performer_Location);

        performer_location.setAdapter(Performer_LocationList);
        user_address.setAdapter(LocationList);

        //判斷是否為藝人---------------------------------------------------------
        if(user_type.equals("Performer")) {
            performer_info.setVisibility(View.VISIBLE);
            user_phone_bar.setVisibility(View.GONE);
            user_location_bar.setVisibility(View.GONE);
            user_birthday_bar.setVisibility(View.GONE);
        }else {
            performer_info.setVisibility(View.GONE);
            user_phone_bar.setVisibility(View.VISIBLE);
            user_location_bar.setVisibility(View.VISIBLE);
            user_birthday_bar.setVisibility(View.VISIBLE);
        }

        //登出功能--------------------------------------------------------------
        btn_log_out.setOnClickListener(new ImageButton.OnClickListener(){

            @Override
            public void onClick(View v) {
                UserInfoConfig.setConfig(getApplicationContext(),"UserInfo","username","");
                UserInfoConfig.setConfig(getApplicationContext(),"UserInfo","ID","");
                UserInfoConfig.setConfig(getApplicationContext(),"UserInfo","role","User");
                UserInfoConfig.setConfig(getApplicationContext(), "sign_up", "status", "false");
                Intent intent = new Intent(getApplicationContext(), LoginOrSingin.class);
                startActivity(intent);
                finish();
            }
        });

        //確認更改--------------------------------------------------------------
        edit_confirm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user_type.equals("User"))
                    user_update_info(
                            user_name.getText().toString(), phone_number.getText().toString(),
                            user_account.getText().toString(), user_email.getText().toString(),
                            user_password.getText().toString(),user_birthday.getText().toString(),
                            user_address.getSelectedItem().toString(), user_id
                            );
                else
                    performer_update_info(
                            user_name.getText().toString(),
                            user_account.getText().toString(),
                            user_email.getText().toString(),
                            user_password.getText().toString(),
                            performer_perform.getText().toString(),
                            performer_location.getSelectedItem().toString(),
                            performer_social.getText().toString(),
                            performer_introduce.getText().toString(), user_id);
            }
        });

        loadImageFromUrl(
                UserInfoConfig.getConfig(this,"UserInfo","Img",""),
                Top_artist_pictures
        );

        load_info(user_type, user_id, ip);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //匯入頭貼(使用網址匯入的方式)------------------------------------------------------------------------------------------------
    private void loadImageFromUrl(String x, ImageView y) {
        Picasso.with(UserSeesEditPage.this).load(x).placeholder(R.drawable.person_110935)
                .error(R.drawable.person_110935)
                .into(y, new com.squareup.picasso.Callback() {

                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    //將資料放進去欄位內
    private void user_info_insert(String  temp_user_name, String temp_phone_number,
                                  String temp_user_account, String temp_user_email,
                                  String temp_user_password, String temp_user_birthday,
                                  String temp_user_address){
        user_name.setText(temp_user_name);
        phone_number.setText(temp_phone_number);
        user_account.setText(temp_user_account);
        user_email.setText(temp_user_email);
        user_password.setText(temp_user_password);
        user_birthday.setText(temp_user_birthday);
        switch (temp_user_address) {
            case "北區":
                user_address.setSelection(0);
                break;
            case "中區":
                user_address.setSelection(1);
                break;
            case "南區":
                user_address.setSelection(2);
                break;
            case "東區":
                user_address.setSelection(3);
                break;
        }
    }

    //將資料放進去欄位內
    private void performer_info_insert(String  temp_user_name, String temp_user_account,
                                       String temp_user_email, String temp_user_password,
                                       String temp_performer_perform, String temp_performer_location,
                                       String temp_performer_social, String temp_performer_introduce){
        user_name.setText(temp_user_name);
        user_account.setText(temp_user_account);
        user_email.setText(temp_user_email);
        user_password.setText(temp_user_password);
        performer_perform.setText(temp_performer_perform);
        performer_social.setText(temp_performer_social);
        performer_introduce.setText(temp_performer_introduce);

        switch (temp_performer_location) {
            case "臺北市":
                performer_location.setSelection(0);
                break;
            case "桃園市":
                performer_location.setSelection(1);
                break;
            case "基隆市":
                performer_location.setSelection(2);
                break;
            case "臺中市":
                performer_location.setSelection(3);
                break;
            case "雲林縣":
                performer_location.setSelection(4);
                break;
            case "彰化縣":
                performer_location.setSelection(5);
                break;
            case "南投縣":
                performer_location.setSelection(6);
                break;
            case "高雄市":
                performer_location.setSelection(7);
                break;
            case "嘉義縣":
                performer_location.setSelection(8);
                break;
            case "臺南市":
                performer_location.setSelection(9);
                break;
            case "屏東縣":
                performer_location.setSelection(10);
                break;
        }

    }

    //讀取個人資料------------------------------------------------------------------
    private void load_info(String user_type, String user_id, String ip){
        Personal_info_load personal_info_load = new Personal_info_load();
        personal_info_load.userSeesEditPage = UserSeesEditPage.this;
        personal_info_load.execute(
                ip, user_type, user_id
        );

    }

    //抓出個人資料------------------------------------------------------------------
    private static class Personal_info_load extends AsyncTask<String, Void, Void> {
        String temp_user_name, temp_phone_number, temp_user_account, temp_user_email, temp_user_password,
                temp_user_birthday, temp_user_address, temp_performer_perform, temp_performer_location,
                temp_performer_social, temp_performer_introduce;
        UserSeesEditPage userSeesEditPage;
        String type;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            try {
                String ip = params[0];
                type = params[1];
                String user_id = params[2];
                String use_url;
                use_url = ip + "/StreetApp_FinalProject2020/edit.php";
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

                if(type.equals("User")){
                    try {
                        JSONObject load_info_result_Array = new JSONObject(result);
                        temp_user_name = load_info_result_Array.getString("name");
                        temp_phone_number = load_info_result_Array.getString("User_phone");
                        temp_user_account = load_info_result_Array.getString("User_Account");
                        temp_user_email = load_info_result_Array.getString("User_email");
                        temp_user_password = load_info_result_Array.getString("User_password");
                        temp_user_birthday = load_info_result_Array.getString("User_birthday");
                        temp_user_address = load_info_result_Array.getString("User_location");
                    }catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        //result = e.getMessage();
                    }
                }else{
                    try {
                        JSONObject load_info_result_Array = new JSONObject(result);
                        temp_user_name = load_info_result_Array.getString("name");
                        temp_user_account = load_info_result_Array.getString("performerAccount");
                        temp_user_email = load_info_result_Array.getString("performerEmail");
                        temp_user_password = load_info_result_Array.getString("performerPassword");
                        temp_performer_perform = load_info_result_Array.getString("performTheme");
                        temp_performer_location = load_info_result_Array.getString("cityName");
                        temp_performer_social = load_info_result_Array.getString("social_link");
                        temp_performer_introduce = load_info_result_Array.getString("introduce");
                    }catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                        //result = e.getMessage();
                    }
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
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Void result) {
            if(type.equals("User")) {
                userSeesEditPage.user_info_insert(temp_user_name, temp_phone_number, temp_user_account,
                        temp_user_email, temp_user_password, temp_user_birthday,
                        temp_user_address);
            }else {
                userSeesEditPage.performer_info_insert(temp_user_name, temp_user_account,
                        temp_user_email, temp_user_password, temp_performer_perform,
                        temp_performer_location, temp_performer_social, temp_performer_introduce);
            }
        }
    }

    //上傳使用者個人資料------------------------------------------------------------------
    private void user_update_info(String  temp_user_name, String temp_phone_number,
                                  String temp_user_account, String temp_user_email,
                                  String temp_user_password, String temp_user_birthday,
                                  String temp_user_address, String user_id){
        Personal_info_update personal_info_update = new Personal_info_update();
        personal_info_update.userSeesEditPage = UserSeesEditPage.this;
        personal_info_update.execute(ip, user_type, temp_user_name,temp_phone_number,
                temp_user_account, temp_user_email,
                temp_user_password, temp_user_birthday,
                temp_user_address, user_id);
        verify_loading_progress.setVisibility(View.VISIBLE);
    }

    //上傳藝人個人資料------------------------------------------------------------------
    private void performer_update_info(String  temp_user_name, String temp_user_account,
                                       String temp_user_email, String temp_user_password,
                                       String temp_performer_perform, String temp_performer_location,
                                       String temp_performer_social, String temp_performer_introduce,
                                       String user_id){
        Personal_info_update personal_info_update = new Personal_info_update();
        personal_info_update.userSeesEditPage = UserSeesEditPage.this;
        personal_info_update.execute(ip, user_type, temp_user_name,temp_user_account,
                temp_user_email,temp_user_password,
                temp_performer_perform,temp_performer_location,
                temp_performer_social,temp_performer_introduce,
                user_id);
        verify_loading_progress.setVisibility(View.VISIBLE);
    }

    //上傳個人資料------------------------------------------------------------------
    private static class Personal_info_update extends AsyncTask<String, Void, Void> {
        String temp_user_name, temp_phone_number, temp_user_account, temp_user_email, temp_user_password,
                temp_user_birthday, temp_user_address, temp_performer_perform, temp_performer_location,
                temp_performer_social, temp_performer_introduce;
        String type, user_id;
        UserSeesEditPage userSeesEditPage;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            try {
                String ip = params[0];
                type = params[1];
                String post_data = "";

                if(type.equals("User")){
                    temp_user_name = params[2];
                    temp_phone_number = params[3];
                    temp_user_account = params[4];
                    temp_user_email = params[5];
                    temp_user_password = params[6];
                    temp_user_birthday = params[7];
                    temp_user_address = params[8];
                    user_id = params[9];

                    post_data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")
                            + "&" + URLEncoder.encode("temp_user_name", "UTF-8") + "=" + URLEncoder.encode(temp_user_name, "UTF-8")
                            + "&" + URLEncoder.encode("temp_phone_number", "UTF-8") + "=" + URLEncoder.encode(temp_phone_number, "UTF-8")
                            + "&" + URLEncoder.encode("temp_user_account", "UTF-8") + "=" + URLEncoder.encode(temp_user_account, "UTF-8")
                            + "&" + URLEncoder.encode("temp_user_email", "UTF-8") + "=" + URLEncoder.encode(temp_user_email, "UTF-8")
                            + "&" + URLEncoder.encode("temp_user_password", "UTF-8") + "=" + URLEncoder.encode(temp_user_password, "UTF-8")
                            + "&" + URLEncoder.encode("temp_user_birthday", "UTF-8") + "=" + URLEncoder.encode(temp_user_birthday, "UTF-8")
                            + "&" + URLEncoder.encode("temp_user_address", "UTF-8") + "=" + URLEncoder.encode(temp_user_address, "UTF-8")
                            + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");

                }else{
                    temp_user_name = params[2];
                    temp_user_account = params[3];
                    temp_user_email = params[4];
                    temp_user_password = params[5];
                    temp_performer_perform = params[6];
                    temp_performer_location = params[7];
                    temp_performer_social = params[8];
                    temp_performer_introduce = params[9];
                    user_id = params[10];

                    post_data = URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")
                            + "&" + URLEncoder.encode("temp_user_name", "UTF-8") + "=" + URLEncoder.encode(temp_user_name, "UTF-8")
                            + "&" + URLEncoder.encode("temp_user_account", "UTF-8") + "=" + URLEncoder.encode(temp_user_account, "UTF-8")
                            + "&" + URLEncoder.encode("temp_user_email", "UTF-8") + "=" + URLEncoder.encode(temp_user_email, "UTF-8")
                            + "&" + URLEncoder.encode("temp_user_password", "UTF-8") + "=" + URLEncoder.encode(temp_user_password, "UTF-8")
                            + "&" + URLEncoder.encode("temp_performer_perform", "UTF-8") + "=" + URLEncoder.encode(temp_performer_perform, "UTF-8")
                            + "&" + URLEncoder.encode("temp_performer_location", "UTF-8") + "=" + URLEncoder.encode(temp_performer_location, "UTF-8")
                            + "&" + URLEncoder.encode("temp_performer_social", "UTF-8") + "=" + URLEncoder.encode(temp_performer_social, "UTF-8")
                            + "&" + URLEncoder.encode("temp_performer_introduce", "UTF-8") + "=" + URLEncoder.encode(temp_performer_introduce, "UTF-8")
                            + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
                }
                String use_url;
                use_url = ip + "/StreetApp_FinalProject2020/edit_confirm.php";
                URL url = new URL(use_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();


                StringBuilder stringBuilder = new StringBuilder();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));


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
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Void result) {
            userSeesEditPage.update_finish();
        }
    }

    //上傳完成後--------------------------------------------------------------------
    public void update_finish(){
        verify_loading_progress.setVisibility(View.GONE);
        UserInfoConfig.setConfig(this,"UserInfo","Name",user_name.getText().toString());
        this.finish();
    }
}



