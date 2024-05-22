package com.example.tomato.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tomato.R;
import com.example.tomato.UserInfoConfig;
import com.example.tomato.background.CheckUsernameBackgroundWorker;
import com.example.tomato.background.SignUPBackgroundWorker;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Performer_Verify_Page extends AppCompatActivity {
    private View verify_loading_progress;
    private SlidrInterface slidr;
    EditText performer_account, email;
    TextView textview2;
    Button btn_verify;
    ImageView imageView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_page);

        //設定隱藏標題-------------------------------------------------------------------------------------------
        getSupportActionBar().hide();
        slidr = Slidr.attach(this);

        textview2 = findViewById(R.id.textview2);
        performer_account = findViewById(R.id.performer_account);
        email = findViewById(R.id.email);
        btn_verify = findViewById(R.id.btn_verify);
        imageView = findViewById(R.id.imageView);
        verify_loading_progress = findViewById(R.id.verify_loading_progress);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //下方認證按下後觸發事件-----------------------------------------------------------------
    public void btn_verify_onclick(View view){
        String email_input, performer_id_input;
        email_input = email.getText().toString();
        performer_id_input = performer_account.getText().toString();

            Verify_backgroundWorker verify_backgroundWorker = new Verify_backgroundWorker(Performer_Verify_Page.this);
            verify_backgroundWorker.execute(
                    UserInfoConfig.getConfig(this, "link", "url", "localhost"),
                    email_input,
                    performer_id_input
            );

    }

    //上方白色箭頭自動返回鍵-----------------------------------------------------------------
    public void back(View view){
        finish();
    }

    //跑完後端後執行
    public void verify_status(String result){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(Performer_Verify_Page.this).create();
        alertDialog.setTitle("認證狀況");
        String Status = "";

        switch (result) {
            case "Success":
                Status = "請至信箱收信，謝謝";
                alertDialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
                alertDialog.setOnCancelListener(new AlertDialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                });
                break;
            case "Fail":
                Status = "寄信失敗 請再試一次";
                break;
            case "Wrong":
                Status = "錯誤的藝人編號或信箱";
                break;
            case "empty":
                Status = "請輸入完整資訊";
                break;
        }

        alertDialog.setMessage(Status);
        alertDialog.show();

    }

    //按下認證後觸發的後端執行續-----------------------------------------------------------------
    private class Verify_backgroundWorker extends AsyncTask<String,Void,String> {
        String ip;
        String output = "empty";
        String line;
        public Verify_backgroundWorker(Context ctx){
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {
            //params 0.IP  1.email  2.id
            String email = params[1], performer_id = params[2];
            this.ip = params[0];
            String login_url = ip + "/StreetApp_FinalProject2020/performer/verify/verify.php";
            if(!email.equals("") && !performer_id.equals(""))
                try {
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    String post_data = URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                            + "&" + URLEncoder.encode("performer_id", "UTF-8") + "=" + URLEncoder.encode(performer_id, "UTF-8")
                            + "&" + URLEncoder.encode("ip", "UTF-8") + "=" + URLEncoder.encode(ip, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    StringBuilder stringBuilder = new StringBuilder();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));

                    if ((line = bufferedReader.readLine()) != null) {
                        return line;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                output = "empty";
            return output;
        }

        @Override
        protected void onPreExecute() {
            verify_loading_progress.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(String result) {
            verify_loading_progress.setVisibility(View.GONE);
            verify_status(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }
}



