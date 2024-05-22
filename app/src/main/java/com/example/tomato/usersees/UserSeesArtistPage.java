package com.example.tomato.usersees;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.tomato.Activity.Comment;
import com.example.tomato.R;
import com.example.tomato.UserInfoConfig;
import com.example.tomato.background.UserFollowsArtistBackgroundWorker;
import com.example.tomato.background.UserSeesArtistAboutBackgroundWorker;
import com.example.tomato.background.UserSeesArtistPostBackgroundWorker;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;
import com.squareup.picasso.Picasso;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.example.tomato.R.layout.viedorow;


public class UserSeesArtistPage extends AppCompatActivity {
    private SlidrInterface slidr;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    UserSeesArtistPostBackgroundWorker userSeesArtistPostBackgroundWorker;
    UserFollowsArtistBackgroundWorker userFollowsArtistBackgroundWorker;
    TextView about_me, artist_name, followers;
    TextView follow;
    ImageView artist_picture, wooden;

    String[]  post_id, post_time, post_content, post_fly, post_seen, post_type, post_video,
            save_poster_nickname, save_post_time, save_post_username, user_likes_post_id,
            save_poster_picture,post_user_type;
    String post_user_img, post_user_name, Poster_username, Login_User, poster_fans, user_follow_status;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userseesartisthomepage);

        //設定隱藏標題
        getSupportActionBar().hide();

        Intent intent = getIntent();
        Poster_username = intent.getStringExtra("post_user_id");
        Login_User = intent.getStringExtra("user_id");
        post_user_img = intent.getStringExtra("post_user_img");
        post_user_name = intent.getStringExtra("post_user_name");
        poster_fans = intent.getStringExtra("poster_fans");
        user_follow_status = intent.getStringExtra("user_follow_status");

        View top_view = getLayoutInflater().inflate(R.layout.userseesartisthomepagetopfragment, null);
        artist_name = top_view.findViewById(R.id.artist_name);
        followers = top_view.findViewById(R.id.followers);
        follow = top_view.findViewById(R.id.text_follow);
        artist_picture = top_view.findViewById(R.id.Top_artist_pictures);
        about_me = top_view.findViewById(R.id.about_me);
        listView = findViewById(R.id.user_see_artist_postList);
        listView.addHeaderView(top_view);

        this.give_loading_animation();

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        userSeesArtistPostBackgroundWorker = new UserSeesArtistPostBackgroundWorker(this);
        userSeesArtistPostBackgroundWorker.Artist_post_row = this;
        // 0.貼文者ID 1.使用者ID 2.網址
        userSeesArtistPostBackgroundWorker.execute(Poster_username, Login_User,
                UserInfoConfig.getConfig(UserSeesArtistPage.this,"link","url","localhost"),
                UserInfoConfig.getConfig(UserSeesArtistPage.this,"UserInfo","role",""));

        follow.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Type;
                if (follow.getText().equals("追蹤")) {
                    Type = "follow";
                    follow.setText("已追蹤");
                    int temp_followers = Integer.parseInt(followers.getText().toString());
                    temp_followers += 1;
                    followers.setText(String.valueOf(temp_followers));
                    userFollowsArtistBackgroundWorker = new UserFollowsArtistBackgroundWorker(getApplicationContext());
                    userFollowsArtistBackgroundWorker.execute(Type,
                            Login_User,
                            Poster_username,
                            UserInfoConfig.getConfig(UserSeesArtistPage.this,"link","url","localhost"),
                            UserInfoConfig.getConfig(UserSeesArtistPage.this,"UserInfo","role",""));
                } else if (follow.getText().equals("已追蹤")) {
                    Type = "un_follow";
                    follow.setText("追蹤");
                    int temp_followers = Integer.parseInt(followers.getText().toString());
                    temp_followers -= 1;
                    followers.setText(String.valueOf(temp_followers));
                    userFollowsArtistBackgroundWorker = new UserFollowsArtistBackgroundWorker(getApplicationContext());
                    userFollowsArtistBackgroundWorker.execute(Type,
                            Login_User,
                            Poster_username,
                            UserInfoConfig.getConfig(UserSeesArtistPage.this,"link","url","localhost"),
                            UserInfoConfig.getConfig(UserSeesArtistPage.this,"UserInfo","role",""));
                }
            }
        });

        about_me.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent About_page = new Intent(getApplicationContext(), UserSeesArtistAboutPage.class);
                About_page.putExtra("message", Poster_username);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
                startActivity(About_page, options.toBundle());
            }
        });

//        BottomNavigationView middle_Navigation = top_view.findViewById(R.id.Bottom_Navigation);

             slidr = Slidr.attach(this);
        initialTopView(post_user_name, post_user_img, poster_fans, user_follow_status);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void Row_data(String[] post_id, String[] save_poster_nickname,
                         String[] post_time, String[] post_content, String[] post_video,
                         String[] post_type, String[] post_fly, String[] save_post_username,
                         String[] save_poster_picture, String[] user_likes_post_id,
                         String[] post_seen, String[] post_user_type) {
        this.post_id = post_id;
        this.save_poster_nickname = save_poster_nickname;
        this.post_time = post_time;
        this.post_content = post_content;
        this.post_video = post_video;
        this.post_type = post_type;
        this.post_fly = post_fly;
        this.post_seen = post_seen;
        this.save_post_username = save_post_username;
        this.save_poster_picture = save_poster_picture;
        this.user_likes_post_id = user_likes_post_id;
        this.post_user_type = post_user_type;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void giveadapter() {
        arrayAdapter = new Artist_Post_RowAdapter(this, post_id, post_content, post_fly, post_seen, post_type, post_video,
                save_poster_nickname, save_post_time, save_post_username, user_likes_post_id,
                save_poster_picture,post_user_type);
        listView.setAdapter(arrayAdapter);

        userSeesArtistPostBackgroundWorker.cancel(true);
    }

    public void give_empty_adapter() {
        arrayAdapter = new Empty_Artist_Post_RowAdapter(this);
        listView.setAdapter(arrayAdapter);

        userSeesArtistPostBackgroundWorker.cancel(true);
    }

    public void initialTopView(String ArtistName, String ImgUrL, String FollowersCounts, String follow) {

        if (ImgUrL == null || ImgUrL.trim().equals("\r") || ImgUrL.trim().equals("")) {
            artist_picture.setImageResource(R.drawable.person_110935);
            //loadImageFromUrl("https://ncyu-webdesign.000webhostapp.com/StreetArtist/Pictures/download.jpg", artist_picture);
        } else
            loadImageFromUrl(ImgUrL, artist_picture);

        if (ArtistName == null || ArtistName.length() < 6)
            artist_name.setTextSize(35);
        else if (ArtistName.length() < 11)
            artist_name.setTextSize(25);
        else
            artist_name.setTextSize(15);

        artist_name.setText(ArtistName);

        if (follow.equals("1"))
            this.follow.setText("已追蹤");
        else
            this.follow.setText("追蹤");

        followers.setText(String.format("%s", FollowersCounts));
    }

    private void loadImageFromUrl(String x, ImageView y) {
        Picasso.with(this).load(x).placeholder(R.mipmap.ic_launcher)
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

    public void give_loading_animation() {
        arrayAdapter = new loading_page_ArrayAdapter(this);
        listView.setAdapter(arrayAdapter);
    }

}

class loading_page_ArrayAdapter extends ArrayAdapter<String> {
    private Context context;

    public loading_page_ArrayAdapter(@NonNull Context c) {
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

    private static class Null_ViewHolder {
        ProgressBar progressBar;
    }
}

class Empty_Artist_Post_RowAdapter extends ArrayAdapter<String> {
    private Context context;

    Empty_Artist_Post_RowAdapter(Context c) {
        super(c, R.layout.no_rows);
        this.context = c;
    }

    public int getCount() {
        return 1;
    }


    public View getView(final int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        final Null_ViewHolder null_viewHolder;

        if (convertView == null) {
            null_viewHolder = new Null_ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.no_rows, null, true);

            null_viewHolder.null_text = (TextView) convertView.findViewById(R.id.null_text);

            convertView.setTag(null_viewHolder);
        } else {
            null_viewHolder = (Null_ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private static class Null_ViewHolder {
        TextView null_text;
    }
}

class Artist_Post_RowAdapter extends ArrayAdapter<String> {
    private Context context;

    String[]  post_id, post_time, post_content, post_fly, post_seen, post_type, post_video,
            save_poster_nickname, save_post_time, save_post_username, user_likes_post_id,
            save_poster_picture,post_user_type;
    String user_type, Login_User, Login_user_img;
    private boolean volume = true;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    Artist_Post_RowAdapter(@NonNull Context c, String[] post_id, String[] post_content,
                           String[] post_fly, String[] post_seen, String[] post_user_type,
                           String[] post_video, String[] save_poster_nickname,
                           String[] post_time, String[] save_post_username,
                           String[] user_likes_post_id, String[] save_poster_picture,
                           String[] post_type){
        super(c, R.layout.viedorow, post_type);
        this.context = c;
        this.post_id = post_id;
        this.save_poster_nickname = save_poster_nickname;
        this.post_time = post_time;
        this.post_content = post_content;
        this.post_video = post_video;
        this.post_type = post_type;
        this.post_fly = post_fly;
        this.post_seen = post_seen;
        this.save_post_username = save_post_username;
        this.save_poster_picture = save_poster_picture;
        this.user_likes_post_id = user_likes_post_id;
        this.post_user_type = post_user_type;
        this.Login_user_img = Login_user_img;
    }

    @Override
    public int getCount() {
        return post_id.length;
    }

    @Override
    public View getView(final int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        final ViewHolder viewHolder;
        Login_user_img = UserInfoConfig.getConfig(this.context,"UserInfo","Img","");
        user_type = UserInfoConfig.getConfig(this.context,"UserInfo","role","");
        Login_User = UserInfoConfig.getConfig(this.context,"UserInfo","ID","");

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(viedorow, null, true);
            viewHolder.image_btn_poster = (ImageView) convertView.findViewById(R.id.User_Picture);
            viewHolder.poster_nickname = (TextView) convertView.findViewById(R.id.poster_name);
            viewHolder.VideoContent = (TextView) convertView.findViewById(R.id.videos_content);
            viewHolder.comment_bar = (EditText) convertView.findViewById(R.id.comment_bar);
            viewHolder.Video_fly = (TextView) convertView.findViewById(R.id.total_fly_number_counts);
            viewHolder.Video_seen = (TextView) convertView.findViewById(R.id.watches_counts);
            viewHolder.btn_fly = (ImageButton) convertView.findViewById(R.id.btnFly);
            viewHolder.Vid = (VideoView) convertView.findViewById(R.id.vids);
            viewHolder.Login_User_Img = (ImageView) convertView.findViewById(R.id.Login_User_Img);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        try {
            String urls = post_video[position];
            Uri videoUri = Uri.parse(urls);
            viewHolder.Vid.setVideoURI(videoUri);

            viewHolder.Vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    volume = false;
                    mp.setLooping(true);
                    viewHolder.Vid.start();
                    mp.setVolume(100f, 100f);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        //設定貼文者顯示名稱---------------------------------------------------------------------------------
        viewHolder.poster_nickname.setText(save_poster_nickname[position]);
        //設定貼文起飛數---------------------------------------------------------------------------------
        viewHolder.Video_fly.setText(post_fly[position]);
        //設定貼文觀看數數---------------------------------------------------------------------------------
        viewHolder.Video_seen.setText(post_seen[position]);
//        viewHolder.VideoComment.setText(comment[position]);
        //設定貼文內容---------------------------------------------------------------------------------
        viewHolder.VideoContent.setText(post_content[position]);

        //設定貼文者頭貼功能---------------------------------------------------------------------------------
        if (this.save_poster_picture[position] == null || this.save_poster_picture[position].equals("\r") ||
                this.save_poster_picture[position].equals(""))
            viewHolder.image_btn_poster.setImageResource(R.drawable.person_110935);
        else
            loadImageFromUrl(save_poster_picture[position], viewHolder.image_btn_poster);

        //按起飛按鈕設定---------------------------------------------------------------------------------
        //設定fly圖片為哪張---------------------------------------------------------------------------------
        if (user_likes_post_id[position] != null) {
            if (user_likes_post_id[position].equals("1")) {
                viewHolder.btn_fly.setBackgroundResource(R.drawable.clicked_fly);
                viewHolder.btn_fly.setTag("1");
            }
            else {
                viewHolder.btn_fly.setBackgroundResource(R.drawable.unclick_fly);
                viewHolder.btn_fly.setTag("0");
            }
        }
        else {
            viewHolder.btn_fly.setBackgroundResource(R.drawable.unclick_fly);
            viewHolder.btn_fly.setTag("0");
        }

        //後端功能與回饋功能---------------------------------------------------------------------------------
        viewHolder.btn_fly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果原本是不起飛---------------------------------------------------------------------------------
                if (viewHolder.btn_fly.getTag().equals("0")) {
                    //讓UI改成起飛圖案---------------------------------------------------------------------------------
                    v.setBackgroundResource(R.drawable.clicked_fly);
                    //更新Tag---------------------------------------------------------------------------------
                    viewHolder.btn_fly.setTag("1");
                    //更新起飛數---------------------------------------------------------------------------------
                    int flies_number;
                    String flies;
                    flies_number = Integer.parseInt(post_fly[position]) + 1;
                    flies = String.valueOf(flies_number);
                    viewHolder.Video_fly.setText(flies);
                    //進入後台做資料庫更新---------------------------------------------------------------------------------
                    String Type = "like";
                    like(user_type, Login_User, post_id[position], context, Type);

                    //  userLikesPostBackgroundWorker = new UserLikesPostBackgroundWorker(context);
                    //  userLikesPostBackgroundWorker.execute(Type, role, ID, post_id[position], "2");

                } else {
                    //先重置UI介面的畫面---------------------------------------------------------------------------------
                    v.setBackgroundResource(R.drawable.unclick_fly);
                    //更新Tag---------------------------------------------------------------------------------
                    viewHolder.btn_fly.setTag("0");
                    //更新起飛數---------------------------------------------------------------------------------
                    int flies_number;
                    String flies;
                    flies_number = Integer.parseInt(post_fly[position]);
                    flies = String.valueOf(flies_number);
                    viewHolder.Video_fly.setText(flies);
                    //進入後台做資料庫更新---------------------------------------------------------------------------------
                    String Type = "dislike";
                    dislike(user_type, Login_User, post_id[position], context, Type);

                    //   userLikesPostBackgroundWorker = new UserLikesPostBackgroundWorker(context);
                    //   userLikesPostBackgroundWorker.execute(Type, role, ID, post_id[position], "2");
                }
            }


        });

        //設定自己的頭貼---------------------------------------------------------------------------------
        if (this.Login_user_img == null || this.Login_user_img.equals("\r") ||
                this.Login_user_img.equals(""))
            viewHolder.Login_User_Img.setImageResource(R.drawable.person_110935);
        else
            loadImageFromUrl(this.Login_user_img, viewHolder.Login_User_Img);

        //設定按下留言欄位後---------------------------------------------------------------------------------
        viewHolder.comment_bar.setOnClickListener(new EditText.OnClickListener(){

            @Override
            public void onClick(View v) {
                //username為貼文者的ID
                String chooseusername = save_post_username[position];
                String intent_post_content =  post_content[position];
                String choose_post_id = post_id[position];
                String fly_status;
                if(viewHolder.btn_fly.getTag().equals("0")) {
                    fly_status = "un_click";
                }
                else {
                    fly_status = "clicked";
                }

                Intent intent = new Intent(context, Comment.class);
                intent.putExtra("post_user_id", chooseusername);
                intent.putExtra("user_id", Login_User);
                intent.putExtra("post_content", intent_post_content);
                intent.putExtra("post_id", choose_post_id);
                intent.putExtra("fly_status", fly_status);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(context, R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
                context.startActivity(intent, options.toBundle());
            }
        });

        return convertView;
    }

    private static final class ViewHolder {
        ImageView image_btn_poster, Login_User_Img;
        TextView poster_nickname, VideoContent, VideoComment, Video_fly, Video_seen;
        EditText comment_bar;
        ImageButton btn_fly;
        VideoView Vid;
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

    //起飛功能
    private void like(final String role, final String  ID, final String post_id, final Context context, String Type) {
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask(context);
        sendPostReqAsyncTask.execute(role, ID, post_id, Type,
                UserInfoConfig.getConfig(context,"link","url","localhost"));
    }

    //不起飛功能
    private void dislike(String role, String  ID, String post_id, final Context context, String Type) {
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask(context);
        sendPostReqAsyncTask.execute(role, ID, post_id, Type,
                UserInfoConfig.getConfig(context,"link","url","localhost"));
    }


    static class SendPostReqAsyncTask extends AsyncTask<String, Void, Void> {
        String Type;
        String login_url;
        String ip;

        private SendPostReqAsyncTask(Context context) {            }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String role = params[0];
            String ID = params[1];
            String post_id = params[2];
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
}

