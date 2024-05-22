package com.example.tomato.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
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
import com.example.tomato.background.UserSeesArtistAboutBackgroundWorker;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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


public class SignUp extends AppCompatActivity {
    private SlidrInterface slidr;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_GALLERY = 200;
    Spinner sex, location;
    Button birthday, btn_sign_up;
    String birthday_datetime, user_sex, user_location;
    ImageView btn_back, user_image;
    EditText real_name, Account, Password, confirm_Password, phone, email;
    CheckUsernameBackgroundWorker checkUsernameBackgroundWorker;
    SignUPBackgroundWorker signUPBackgroundWorker;
    Boolean Status;
    SignUp signUp;
    String file_path = null;
    String url;
    String upload_file_name = "";
    Context ctx = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        //設定隱藏標題-------------------------------------------------------------------------------------------
        getSupportActionBar().hide();
        slidr = Slidr.attach(this);

        url = UserInfoConfig.getConfig(this,"link","url","http://172.20.10.3");
        signUp = this;
        birthday = findViewById(R.id.birthday);
        sex = findViewById(R.id.sex);
        location = findViewById(R.id.locate);
        btn_back = findViewById(R.id.btn_back);
        real_name = findViewById(R.id.real_name);
        Account = findViewById(R.id.Account);
        Password = findViewById(R.id.Password);
        confirm_Password = findViewById(R.id.confirm_Password);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        user_image = findViewById(R.id.user_image);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

        //設定性別、區域三個下拉式選單內容-------------------------------------------------------------------------
        final   String Sex[] = {"請選擇性別", "男生", "女生", "不選擇"};
        final   String Location[] = {"請選擇居住區域", "北區", "中區", "南區", "東區"};

        //設定性別、區域三個下拉式選單-------------------------------------------------------------------------
        ArrayAdapter<String> SexList = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Sex);
        ArrayAdapter<String> LocationList = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                Location);

        //點選照片選擇圖檔上傳
        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        filePicker();
                    } else {
                        requestPermission();
                    }
                } else {
                    filePicker();
                }
            }
        });

        //設定性別、區域下拉式選單選取值的監聽器-------------------------------------------------------------------------
        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user_sex = sex.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                user_location = location.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //設定性別、區域三個下拉式選單-------------------------------------------------------------------------
        sex.setAdapter(SexList);
        location.setAdapter(LocationList);

        //設定出生日期按下後選擇畫面-------------------------------------------------------------------------
        birthday.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        birthday_datetime = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day);
                        birthday.setText(birthday_datetime);
                        birthday.setTextColor(getResources().getColor(R.color.black));
                    }

                }, year , month, day).show();
            }
        });

        //設定這次按鈕監聽事件-------------------------------------------------------------------------
        btn_sign_up.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(signUp).create();
                String Message = "";
                Boolean InCorrect = Boolean.TRUE;
                alertDialog.setTitle("輸入資料有誤");

                if(real_name.getText().toString().equals("")){
                    Message += "請輸入姓名欄位";
                }
                if(Account.getText().toString().equals("")){
                    if (Message.equals(""))
                        Message += "請輸入帳號";
                    else
                        Message += "\n" +"請輸入帳號";
                }
                if(Password.getText().toString().equals("")){
                    if (Message.equals(""))
                        Message += "請輸入密碼";
                    else
                        Message += "\n" +"請輸入密碼";
                }
                if(confirm_Password.getText().toString().equals("")){
                    if (Message.equals(""))
                        Message += "請輸入驗證密碼";
                    else
                        Message += "\n" +"請輸入驗證密碼";
                }
                else if(!confirm_Password.getText().toString().equals(Password.getText().toString())){
                    if (Message.equals(""))
                        Message += "密碼兩次輸入不一致";
                    else
                        Message += "\n" +"密碼兩次輸入不一致";
                }
                if(phone.getText().toString().equals("")){
                    if (Message.equals(""))
                        Message += "請輸入手機號碼";
                    else
                        Message += "\n" +"請輸入手機號碼";
                }
                if(email.getText().toString().equals("")){
                    if (Message.equals(""))
                        Message += "請輸入信箱";
                    else
                        Message += "\n" +"請輸入信箱";
                }
                if(birthday.getText().toString().equals("請選擇生日")){
                    if (Message.equals(""))
                        Message += "請選擇生日";
                    else
                        Message += "\n" +"請選擇生日";
                }
                if(sex.getSelectedItem().toString().equals("請選擇性別")){
                    if (Message.equals(""))
                        Message += "請選擇性別";
                    else
                        Message += "\n" +"請選擇性別";
                }
                if(location.getSelectedItem().toString().equals("請選擇居住區域")){
                    if (Message.equals(""))
                        Message += "請選擇居住區域";
                    else
                        Message += "\n" +"請選擇居住區域";
                }

                if (Message.equals(""))
                    InCorrect = Boolean.FALSE;

                //註冊資料有缺或填寫錯誤事件-------------------------------------------------------------------------
                if(InCorrect){
                    alertDialog.setMessage(Message);
                    alertDialog.show();
                }
                //設定註冊資料皆正確事件-------------------------------------------------------------------------
                else {
                    //檢查是否有重複帳號-------------------------------------------------------------------------
                    //最後檢查帳號是否存在---------------------------------------------------------------------
                    checkUsernameBackgroundWorker = new CheckUsernameBackgroundWorker(signUp);
                    checkUsernameBackgroundWorker.signUp = signUp;
                    checkUsernameBackgroundWorker.execute(
                            UserInfoConfig.getConfig(signUp,"link","url","localhost"),
                            Account.getText().toString());
                }

                //finish();
            }
        });

        //按下返回圖案後關閉此視窗-----------------------------------------------------------------------------------
        btn_back.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //選擇檔案
    private void filePicker() {
        Toast.makeText(getApplicationContext(), "請選取照片", Toast.LENGTH_SHORT).show();
        Intent opengallery = new Intent(Intent.ACTION_PICK);
        opengallery.setType("image/*");
        startActivityForResult(opengallery, REQUEST_GALLERY);
    }

    //權限確認
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SignUp.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(SignUp.this, "Please give permission to upload file", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(SignUp.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(SignUp.this, "Permission successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUp.this, "Permission failed", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            String filePath = getRealPathFromUri(data.getData(), SignUp.this);
            Log.d("File Path：", " " + filePath);
            this.file_path = filePath;

            Bitmap mybitmap = BitmapFactory.decodeFile(filePath);
            user_image.setImageBitmap(mybitmap);

//            File file = new File(filePath);
//            file_name.setText(file.getName());
        }
    }

    public String getRealPathFromUri(Uri uri, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int id = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(id);
        }
    }

    //確認帳號是否存在之後的事件-----------------------------------------------------------------------------------
    public void Check_Status(Boolean status){

        //設定註冊資料皆正確且成功註冊事件-------------------------------------------------------------------------
        if(status){
            if (file_path != null && !file_path.equals("")) {
                UploadFile();
            } else {
                Insert_Into_Database();
            }
        }
        //重複性帳號反應事件-------------------------------------------------------------------------
        else
            Toast.makeText(signUp, "此帳號已有人註冊！", Toast.LENGTH_LONG).show();

    }

    public void Insert_Into_Database(){
        signUPBackgroundWorker = new SignUPBackgroundWorker(signUp);
        signUPBackgroundWorker.signUp = signUp;
        signUPBackgroundWorker.execute(
                UserInfoConfig.getConfig(signUp,"link","url","localhost"),
                real_name.getText().toString(),
                user_sex,
                Account.getText().toString(),
                Password.getText().toString(),
                email.getText().toString(),
                birthday_datetime,
                upload_file_name,
                location.getSelectedItem().toString(),
                phone.getText().toString()
                );
    }

    public void sign_up_success(){
        UserInfoConfig.setConfig(this,"sign_up","status","true");
        UserInfoConfig.setConfig(this,"sign_up_account","temp",Account.getText().toString());
        UserInfoConfig.setConfig(this,"sign_up_password","temp",Password.getText().toString());
        finish();
    }

    private void UploadFile() {

        SignUp_UploadTask uploadTask = new SignUp_UploadTask(ctx);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            uploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{file_path});
        else
            uploadTask.execute(new String[]{file_path});

    }

    //上傳圖檔------------------------------------------------------------------------
    public class SignUp_UploadTask extends AsyncTask<String, String, String> {

        Context task_ctx;

        public SignUp_UploadTask(Context context){
            task_ctx = context;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("true")){
                //Toast.makeText(SignUp.this, "上傳成功", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(SignUp.this, "上傳失敗", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            if(uploadFile(strings[0])){
                return "true";
            }
            else{
                return "failed";
            }
        }



        private boolean uploadFile(String path) {
            final File file = new File(path);
            try {
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("files", file.getName(), RequestBody.create(MediaType.parse("video/*"), file))
                        .addFormDataPart("some_key", "some_value")
                        .addFormDataPart("submit", "submit")
                        .build();

                Request request = new Request.Builder()
                        .url(url + "/StreetApp_FinalProject2020/user/upload_user_image.php")
                        .post(requestBody)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        upload_file_name = response.body().string();
                        Insert_Into_Database();
                    }
                });
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}



