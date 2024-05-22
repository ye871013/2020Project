package com.example.tomato.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.example.tomato.background.SeenRelationBackgroundWorker;
import com.example.tomato.usersees.UserSeesArtistPage;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
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
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.tomato.R.layout.comments_rows;
import static com.example.tomato.R.layout.viedorow;


public class Comment extends AppCompatActivity {
    private SlidrInterface slidr;
    ListView comment_list;
    ImageView btn_send_comment, btn_fly, btn_back;
    EditText comment_bar;
    String Poster_username, Login_User, post_content, post_id , User_type, fly_status;
    ArrayAdapter<String> arrayAdapter;
    SeenRelationBackgroundWorker seenRelationBackgroundWorker;
    String[] comment_user_img, comment_content, comment_user_id, comment_user_type,
            comment_user_name, comment_user_fans, comment_user_follow_status;
    private View comment_loading_progress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_page);

        //設定隱藏標題-------------------------------------------------------------------------------------------
        getSupportActionBar().hide();
        slidr = Slidr.attach(Comment.this);
        comment_loading_progress = findViewById(R.id.comment_loading_progress);

        //取得從影片牆傳來的資訊 使用者ID 貼文者ID 以及貼文內容---------------------------------------------------
        Intent intent = getIntent();
        Login_User = intent.getStringExtra("user_id");
        Poster_username = intent.getStringExtra("post_user_id");
        post_content = intent.getStringExtra("post_content");
        post_id = intent.getStringExtra("post_id");
        //un_click 尚未起飛 、 clicked 起飛過
        fly_status = intent.getStringExtra("fly_status");
        User_type = UserInfoConfig.getConfig(Comment.this,"UserInfo","role","");

        //初始化呼叫每個物件--------------------------------------------------------------------------------
        comment_list = findViewById(R.id.comment_list_view);
        btn_send_comment = findViewById(R.id.send_comment);
        comment_bar = findViewById(R.id.comment_bar);
        btn_fly = findViewById(R.id.btn_fly);
        btn_back = findViewById(R.id.btn_back);

        this.give_loading_animation();

        //設定起飛按鈕-------------------------------------------------------------------------
        if(fly_status.equals("un_click")){
            btn_fly.setImageResource(R.drawable.fly);
            btn_fly.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_fly.setImageResource(R.drawable.clicked_fly);
                    like(User_type, Login_User, post_id, Comment .this,"like");
                    }});
        }
        else {
            btn_fly.setImageResource(R.drawable.clicked_fly);
            btn_fly.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_fly.setImageResource(R.drawable.fly);
                    dislike(User_type, Login_User, post_id, Comment .this,"dislike");
                }});
        }

        //設定留言發送按鈕-------------------------------------------------------------------------
        btn_send_comment.setOnClickListener(new ImageView.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if(!comment_bar.getText().toString().trim().equals("")) {
                                                        String comment_content = comment_bar.getText().toString().trim();
                                                        Send_Comment_Backgroud send_comment_backgroud = new Send_Comment_Backgroud(Comment.this);
                                                        send_comment_backgroud.execute(post_id,comment_content,
                                                                UserInfoConfig.getConfig(Comment.this, "link", "url", "localhost"),
                                                                Login_User, User_type);
                                                        comment_bar.setText("");
                                                    }
                                                }
                                            }
        );

        //讀取以往留言紀錄-------------------------------------------------------------------------
        load_history_comment();

    }

    //讀取以往留言紀錄-------------------------------------------------------------------------
    private void load_history_comment(){
        Load_comment_AsyncTask load_comment_asyncTask = new Load_comment_AsyncTask(Comment.this);
        load_comment_asyncTask.commet = Comment.this;
        load_comment_asyncTask.execute(post_id,
                UserInfoConfig.getConfig(Comment.this,"link","url","localhost"),
                Login_User, User_type);
    };

    //讀取過久的話listview顯示的畫面-------------------------------------------------------------------------
    void give_loading_animation() {
        arrayAdapter = new Commment_loading_page_ArrayAdapter(Comment.this);
        comment_list.setAdapter(arrayAdapter);
    }

    //讀取到的資料匯入-------------------------------------------------------------------------
    void comment_Row_data(String[] comment_user_img,
                  String[] comment_content, String[] comment_user_id,
                  String[] comment_user_type, String[] comment_user_name,
                  String[] comment_user_fans, String[] comment_user_follow_status) {
        this.comment_user_img = comment_user_img;
        this.comment_content = comment_content;
        this.comment_user_id = comment_user_id;
        this.comment_user_type = comment_user_type;
        this.comment_user_name = comment_user_name;
        this.comment_user_fans = comment_user_fans;
        this.comment_user_follow_status = comment_user_follow_status;

        if(this.comment_user_id != null || !this.comment_user_name[0].equals("null"))
            comment_give_adapter();
        else {
            Commment_empty_page_ArrayAdapter commment_empty_page_arrayAdapter = new Commment_empty_page_ArrayAdapter(Comment.this);
            comment_list.setAdapter(commment_empty_page_arrayAdapter);
        }
    }

    void comment_give_adapter(){
        arrayAdapter = new Comment_RowAdapter(Comment.this, comment_user_img,
                comment_content, comment_user_id, comment_user_type,
                comment_user_name, comment_user_fans, comment_user_follow_status);
        comment_list.setAdapter(arrayAdapter);
    }

    //白色箭頭按下後返回----------------------------------------------------------------------------------------
    public void Onclick_back(View view){
        finish();
    }

    private void loadImageFromUrl(String x, ImageView y) {
        Picasso.with(Comment.this).load(x).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(y, new com.squareup.picasso.Callback() {

                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });
    }


    static class Commment_loading_page_ArrayAdapter extends ArrayAdapter<String> {
        private Context context;

        Commment_loading_page_ArrayAdapter(@NonNull Context c) {
            super(c, R.layout.loading);
            this.context = c;
        }
        public int getCount() {
            return 1;
        }
        public View getView(final int position,
                            @Nullable View convertView,
                            @NonNull ViewGroup parent) {
            Null_ViewHolder null_viewHolder;

            if (convertView == null) {
                null_viewHolder = new Null_ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.loading, null, true);

                null_viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);

                convertView.setTag(null_viewHolder);
            } else {
                null_viewHolder = (Null_ViewHolder) convertView.getTag();
            }

            return convertView;
        }

        private class Null_ViewHolder {
            ProgressBar progressBar;
        }
    }

    static class Commment_empty_page_ArrayAdapter extends ArrayAdapter<String> {
        private Context context;

        Commment_empty_page_ArrayAdapter(@NonNull Context c) {
            super(c, R.layout.loading);
            this.context = c;
        }
        public int getCount() {
            return 1;
        }
        public View getView(final int position,
                            @Nullable View convertView,
                            @NonNull ViewGroup parent) {
            Null_ViewHolder null_viewHolder;

            if (convertView == null) {
                null_viewHolder = new Null_ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.empty_black, null, true);

                convertView.setTag(null_viewHolder);
            } else {
                null_viewHolder = (Null_ViewHolder) convertView.getTag();
            }

            return convertView;
        }

        private class Null_ViewHolder {
            ProgressBar progressBar;
        }
    }

    class Comment_RowAdapter extends ArrayAdapter<String> {
        Context context;
        private String[] comment_user_img_url, comment_content_text, comment_user_id, comment_user_type,
                comment_user_name, comment_user_fans, comment_user_follow_status;

        Comment_RowAdapter(@NonNull Context c, String[] comment_user_img,
                           String[] comment_content, String[] comment_user_id,
                           String[] comment_user_type, String[] comment_user_name,
                           String[] comment_user_fans, String[] comment_user_follow_status) {
            super(c, R.layout.comments_rows,comment_user_fans);
            this.context = c;
            this.comment_user_img_url = comment_user_img;
            this.comment_content_text = comment_content;
            this.comment_user_id = comment_user_id;
            this.comment_user_type = comment_user_type;
            this.comment_user_name = comment_user_name;
            this.comment_user_fans = comment_user_fans;
            this.comment_user_follow_status = comment_user_follow_status;
        }

        @Override
        public int getCount() {
            return this.comment_user_id.length;
        }

        public View getView(final int position,
                            @Nullable View new_convertView,
                            @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            final ViewHolder viewHolder;

            if (new_convertView == null) {
                viewHolder = new ViewHolder();
                new_convertView = LayoutInflater.from(context).inflate(comments_rows, null, true);
                viewHolder.comment_user_img = (ImageView) new_convertView.findViewById(R.id.comment_user_img);
                viewHolder.comment_content = (TextView) new_convertView.findViewById(R.id.comment_content);

                new_convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) new_convertView.getTag();
            }

            try {
                //設定貼文者頭貼按下功能(是藝人才能按下)---------------------------------------------------------------------------------
                if (comment_user_type[position].equals("performer")) {
                    viewHolder.comment_user_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String choose_username = comment_user_id[position];

                            Intent intent = new Intent(context, UserSeesArtistPage.class);
                            intent.putExtra("post_user_id", choose_username);
                            intent.putExtra("post_user_img", comment_user_img_url[position]);
                            intent.putExtra("post_user_name", comment_user_name[position]);
                            intent.putExtra("poster_fans", comment_user_fans[position]);
                            intent.putExtra("user_follow_status", comment_user_follow_status[position]);
                            intent.putExtra("user_id", Login_User);
                            ActivityOptions options =
                                    ActivityOptions.makeCustomAnimation(context, R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
                            context.startActivity(intent, options.toBundle());
                        }
                    });
                }

                //設定留言者頭貼功能---------------------------------------------------------------------------------
                if (this.comment_user_img_url[position] == null || this.comment_user_img_url[position].equals("\r") ||
                        this.comment_user_img_url[position].equals("")) {
                    viewHolder.comment_user_img.setImageResource(R.drawable.person_110935);
                }
                else {
                    loadImageFromUrl(comment_user_img_url[position], viewHolder.comment_user_img);
                }

                //設定留言內容功能---------------------------------------------------------------------------------
                viewHolder.comment_content.setText(comment_content_text[position]);

            } catch (Exception e) {
                e.printStackTrace();
            }


            return new_convertView;
        }

        private class ViewHolder {
            ImageView comment_user_img;
            TextView comment_content;
        }

        private void loadImageFromUrl(String x, ImageView y) {
            Picasso.with(context).load(x).placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(y, new com.squareup.picasso.Callback() {

                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }


    }

    //起飛功能------------------------------------------------------------------------------------------
    private void like(final String role, final String  ID, final String post_id, final Context context, String Type) {
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask(context);
        sendPostReqAsyncTask.execute(role, ID, post_id, Type,
                UserInfoConfig.getConfig(context,"link","url","localhost"));
    }

    //不起飛功能--------------------------------------------------------------------------------------------
    private void dislike(String role, String  ID, String post_id, final Context context, String Type) {
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask(context);
        sendPostReqAsyncTask.execute(role, ID, post_id, Type,
                UserInfoConfig.getConfig(context,"link","url","localhost"));
    }

    //喜歡後端---------------------------------------------------------------------------------------
    static class SendPostReqAsyncTask extends AsyncTask<String, Void, Void> {
        private String ID;
        private String post_id;
        private String role;
        String Type;
        String login_url;
        String ip;

        private SendPostReqAsyncTask(Context context) {            }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            this.role = params[0];
            this.ID = params[1];
            this.post_id = params[2];
            this.Type = params[3];
            this.ip = params[4];

            if(Type.equals("like")) {
                if (role.equals("User"))
                    login_url = ip + "/StreetApp_FinalProject2020/user/like.php";
                else
                    login_url = ip + "/StreetApp_FinalProject2020/performer/like.php";

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
                    String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8")
                            + "&" + URLEncoder.encode("post_id", "UTF-8") + "=" + URLEncoder.encode(post_id, "UTF-8");
//                test = "設定POST DATA";
                    bufferedWriter.write(post_data);
//                test = "WRITE POST DATA";

                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    inputStream.close();


                    httpURLConnection.disconnect();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
//                test = e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
//                System.out.println(e.getMessage());
//                test = e.getMessage();
                }
            }
            else {
                if (role.equals("User"))
                    login_url = ip + "/StreetApp_FinalProject2020/user/dislike.php";
                else
                    login_url = ip + "/StreetApp_FinalProject2020/performer/dislike.php";

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
                    String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8")
                            + "&" + URLEncoder.encode("post_id", "UTF-8") + "=" + URLEncoder.encode(post_id, "UTF-8");
//                test = "設定POST DATA";
                    bufferedWriter.write(post_data);
//                test = "WRITE POST DATA";

                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    inputStream.close();


                    httpURLConnection.disconnect();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
//                test = e.getMessage();
                } catch (IOException e) {
                    e.printStackTrace();
//                System.out.println(e.getMessage());
//                test = e.getMessage();
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
//            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
//            alertDialog.setTitle(this.role + this.ID + this.role + this.post_id + this.login_url);
//            alertDialog.show();
            //this.cancel(true);
        }

    }

    //喜歡後端---------------------------------------------------------------------------------------
    private class Send_Comment_Backgroud extends AsyncTask<String, Void, Void> {
        Context context;

        private Send_Comment_Backgroud(Context context) {this.context = context;}

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {

            String post_id = params[0];
            String comment_content = params[1];
            String ip = params[2];
            String user_id = params[3];
            String user_type = params[4];
            String login_url = ip + "/StreetApp_FinalProject2020/comment_page/comment_page_send_comment.php";

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
                String post_data = URLEncoder.encode("post_id", "UTF-8") + "=" + URLEncoder.encode(post_id, "UTF-8")
                        + "&" + URLEncoder.encode("comment_content", "UTF-8") + "=" + URLEncoder.encode(comment_content, "UTF-8")
                        + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                        + "&" + URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(user_type, "UTF-8");
//                test = "設定POST DATA";
                bufferedWriter.write(post_data);
//                test = "WRITE POST DATA";

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();


                httpURLConnection.disconnect();


            } catch (MalformedURLException e) {
                e.printStackTrace();
//                test = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
//                System.out.println(e.getMessage());
//                test = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            comment_loading_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void result) {
            load_history_comment();
            comment_loading_progress.setVisibility(View.GONE);
        }

    }

    //第一次載入基礎資料------------------------------------------------------------------------
    static class Load_comment_AsyncTask extends AsyncTask<String, Void, Void> {
        Comment commet;
        String ip;
        Context ctx;
        String result;
        private String[] background_comment_user_img, background_comment_content, background_comment_user_id,
                background_comment_user_type, background_comment_user_name, background_comment_user_fans,
                background_comment_user_follow_status;

        private Load_comment_AsyncTask(Context context) {ctx = context;}

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String post_id = params[0];
            this.ip = params[1];
            String login_user_id = params[2];
            String User_type = params[3];

            String login_url = ip + "/StreetApp_FinalProject2020/comment_page/comment_page_load_data.php";
                try {
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");

                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                    String post_data = URLEncoder.encode("post_id", "UTF-8") + "=" + URLEncoder.encode(post_id, "UTF-8")
                            + "&" + URLEncoder.encode("login_user_id", "UTF-8") + "=" + URLEncoder.encode(login_user_id, "UTF-8")
                            + "&" + URLEncoder.encode("User_type", "UTF-8") + "=" + URLEncoder.encode(User_type, "UTF-8");
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

            //將post_info的所有資料抓近來陣列
            try {
                JSONArray json_video_wall_Array = new JSONArray(result);
                JSONObject json_video_wall_Object = null;

                this.background_comment_user_img = new String[json_video_wall_Array.length()];
                this.background_comment_content = new String[json_video_wall_Array.length()];
                this.background_comment_user_id = new String[json_video_wall_Array.length()];
                this.background_comment_user_type = new String[json_video_wall_Array.length()];
                this.background_comment_user_name = new String[json_video_wall_Array.length()];
                this.background_comment_user_fans = new String[json_video_wall_Array.length()];
                this.background_comment_user_follow_status = new String[json_video_wall_Array.length()];

                for (int i = 0; i < json_video_wall_Array.length(); i++) {
                    json_video_wall_Object = json_video_wall_Array.getJSONObject(i);
                    //後面的名稱丟資料庫的欄位名稱
                    //留言者頭貼-------------------------------------------------------------------
                    if (json_video_wall_Object.getString("User_ImgURL").trim().equals("null"))
                        this.background_comment_user_img[i] = json_video_wall_Object.getString("imageUrl");
                    else
                        this.background_comment_user_img[i] = json_video_wall_Object.getString("User_ImgURL");

                    //留言者ID-------------------------------------------------------------------
                    this.background_comment_user_id[i] = json_video_wall_Object.getString("comment_user_id");

                    //留言者類別-------------------------------------------------------------------
                    this.background_comment_user_type[i] = json_video_wall_Object.getString("comment_user_type");

                    //留言內容-------------------------------------------------------------------
                    this.background_comment_content[i] = json_video_wall_Object.getString("content");

                    //留言者名稱-------------------------------------------------------------------
                    if (json_video_wall_Object.getString("performer_nickname").trim().equals("null") ||
                            json_video_wall_Object.getString("performer_nickname").trim().equals(""))
                        this.background_comment_user_name[i] = json_video_wall_Object.getString("performerName");
                    else
                        this.background_comment_user_name[i] = json_video_wall_Object.getString("performer_nickname");


                    //留言者粉絲數量-------------------------------------------------------------------
                    if (json_video_wall_Object.getString("performerFans").trim().equals("null"))
                        this.background_comment_user_fans[i] = "";
                    else
                        this.background_comment_user_fans[i] = json_video_wall_Object.getString("performerFans");

                    //留言者是否被我追蹤-------------------------------------------------------------------
                    if (json_video_wall_Object.getString("follow").trim().equals("null") ||
                            json_video_wall_Object.getString("follow").trim().equals(""))
                        this.background_comment_user_follow_status[i] = "0";
                    else
                        this.background_comment_user_follow_status[i] = "1";
                }}

                catch(JSONException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            commet.comment_Row_data(this.background_comment_user_img,
                    this.background_comment_content, this.background_comment_user_id,
                    this.background_comment_user_type, this.background_comment_user_name,
                    this.background_comment_user_fans, this.background_comment_user_follow_status);
        }

    }
}



