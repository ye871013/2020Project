package com.example.tomato.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Upload extends AppCompatActivity {
    private SlidrInterface slidr;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_GALLERY = 200;
    private long downloadId;
    TextView file_name;
    String file_path = null;
    Button upload;
    ProgressBar progressBar;
    VideoView preview;
    EditText upload_video_content;
    String url, User_type, User_id;
    String upload_file_name = "1";
    Context ctx = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload);

        //設定隱藏標題-------------------------------------------------------------------------------------------
        getSupportActionBar().hide();
        slidr = Slidr.attach(Upload.this);

        url = UserInfoConfig.getConfig(this,"link","url","http://172.20.10.3");
        User_id = UserInfoConfig.getConfig(this,"UserInfo","ID","");
        User_type = UserInfoConfig.getConfig(this,"UserInfo","role","Performer");

        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));

        /* Button download = findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginDownload();
            }
        });

         */

        upload_video_content = findViewById(R.id.upload_video_content);

        preview = findViewById(R.id.preview);
        preview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        ImageView select = findViewById(R.id.choose_files);
        select.setOnClickListener(new View.OnClickListener() {
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

        progressBar = findViewById(R.id.progress);
        upload = findViewById(R.id.btn_upload_files);
        //file_name = findViewById(R.id.filename);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (file_path != null) {
                    UploadFile();
                } else {
                    Toast.makeText(Upload.this, "請先選擇檔案", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UploadFile() {

        UploadTask uploadTask = new UploadTask(ctx);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
            uploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{file_path});
        else
            uploadTask.execute(new String[]{file_path});

    }

    private void beginDownload() {
        File file = new File(getExternalFilesDir(null), "Dummy");

        DownloadManager.Request request = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request = new DownloadManager.Request(Uri.parse("https://linpack-for-tableau.com/uploads/actualites/livedemo-1.png"))
                    .setTitle("Hey")
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true);
        } else {
            request = new DownloadManager.Request(Uri.parse("https://linpack-for-tableau.com/uploads/actualites/livedemo-1.png"))
                    .setTitle("Hey")
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationUri(Uri.fromFile(file))
                    .setAllowedOverRoaming(true);
        }

        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);
    }

    private void filePicker() {

        Toast.makeText(getApplicationContext(), "請選取影片", Toast.LENGTH_SHORT).show();
        Intent opengallery = new Intent(Intent.ACTION_PICK);
        opengallery.setType("video/*");
        startActivityForResult(opengallery, REQUEST_GALLERY);
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId == id) {
                Toast.makeText(Upload.this, "Download Complete", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void finish_upload(){
        this.finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Upload.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(Upload.this, "Please give permission to upload file", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(Upload.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Upload.this, Manifest.permission.READ_EXTERNAL_STORAGE);
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
                    Toast.makeText(Upload.this, "Permission successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Upload.this, "Permission failed", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            String filePath = getRealPathFromUri(data.getData(), Upload.this);
            Log.d("File Path：", " " + filePath);
            this.file_path = filePath;

            Uri videoUri;
            videoUri = data.getData();
            preview.setVisibility(View.VISIBLE);
            preview.setVideoURI(videoUri);
            preview.start();

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

    //上傳影片成功後回call--------------------------------------------------------------
    public void Upload_Success(Context context){
        Upload_Succes_Task upload_succes_task = new Upload_Succes_Task(context);
        //params 值為 0.網址 1.檔案名稱 2.影片內容(文字) 3.使用者ID 4.使用者類型
        upload_succes_task.execute(
                url,
                upload_file_name,
                upload_video_content.getText().toString(),
                User_id,
                User_type
        );
    }

    //上傳影片執行緒--------------------------------------------------------------
    public class UploadTask extends AsyncTask<String, String, String> {

        Context task_ctx;

        public UploadTask(Context context){
            task_ctx = context;
        }

            @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            if(s.equalsIgnoreCase("true")){
                Toast.makeText(Upload.this, "上傳成功", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Upload.this, "上傳失敗", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
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
                        .url(url + "/StreetApp_FinalProject2020/upload.php")
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
                        Upload_Success(task_ctx);

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

    //上傳影片詳細資料執行緒--------------------------------------------------------------
    public class Upload_Succes_Task extends AsyncTask<String, Void, Void> {

        Context Upload_Success_Task_context;

        public Upload_Succes_Task(Context ctx) {
            Upload_Success_Task_context = ctx;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            //params 值為 0.網址 1.檔案名稱 2.影片內容(文字) 3.使用者ID 4.使用者類型
            String ip = params[0];
            String File_name = ip + "/StreetApp_FinalProject2020/uploads/videos/" + params[1];
            String Post_Content = params[2];
            String type = "video";
            String User_id = params[3];
            String User_type = params[4];

            //給予網址------------------------------------------------------------------------------------------------
            String login_url = ip + "/StreetApp_FinalProject2020/upload_content.php";

            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
//                test = "帳號：" + params[0]
                OutputStream outputStream = httpURLConnection.getOutputStream();
//                test = "OUT STREAM";
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
//                test = "設定BUFFERED WRITER";
                String post_data = URLEncoder.encode("file_name", "UTF-8") + "=" + URLEncoder.encode(File_name, "UTF-8")
                        + "&" + URLEncoder.encode("post_content", "UTF-8") + "=" + URLEncoder.encode(Post_Content, "UTF-8")
                        + "&" + URLEncoder.encode("post_type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")
                        + "&" + URLEncoder.encode("User_id", "UTF-8") + "=" + URLEncoder.encode(User_id, "UTF-8")
                        + "&" + URLEncoder.encode("User_type", "UTF-8") + "=" + URLEncoder.encode(User_type, "UTF-8")
                        ;
                bufferedWriter.write(post_data);

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();

                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPreExecute() {
            finish_upload();
        }

        @Override
        protected void onPostExecute(Void result) {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

}



