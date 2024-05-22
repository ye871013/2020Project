package com.example.tomato.pagefragments;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import androidx.fragment.app.Fragment;

import com.example.tomato.Activity.Comment;
import com.example.tomato.R;
import com.example.tomato.UserInfoConfig;
import com.example.tomato.background.LoadingVideoWallBackgroundWorker;
import com.example.tomato.background.SeenRelationBackgroundWorker;
import com.example.tomato.background.UserFollowsArtistBackgroundWorker;
import com.example.tomato.background.UserLikesPostBackgroundWorker;
import com.example.tomato.usersees.UserSeesArtistPage;
import com.squareup.picasso.Picasso;

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
import java.util.Objects;

import static com.example.tomato.R.layout.user_edit_page;
import static com.example.tomato.R.layout.viedorow;


public class videowallpage extends Fragment {
    ListView listView;
    ArrayAdapter<String> arrayAdapter;

    LoadingVideoWallBackgroundWorker loadingVideoWallBackgroundWorker;

    String[] videoes_url;
    String[] mUsername;
    String[] mContent;
    String[] mVideoCounts;
    String[] post_username;
    String[] post_id;
    String[] video_seen_count;
    String Login_User_Img;
    String[] post_user_type;
    String[] poster_fans;
    String[] user_follow_status;

    String[] imagesurl;

    String[] mComment;

    String[] user_likes_post_id;
    String Login_user;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.videowallpage, container, false);

        listView = view.findViewById(R.id.VideoListView);
        Login_User_Img = UserInfoConfig.getConfig(requireActivity(),"UserInfo","Img","");
        Login_user = UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID","");

        this.give_loading_animation();

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        loadingVideoWallBackgroundWorker = new LoadingVideoWallBackgroundWorker(getActivity());
        loadingVideoWallBackgroundWorker.videowallpage_row = this;

        loadingVideoWallBackgroundWorker.execute(
                UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID",""),
                UserInfoConfig.getConfig(requireActivity(),"UserInfo","role",""),
                UserInfoConfig.getConfig(requireActivity(),"link","url","localhost"));




        return view;
    }

    void give_loading_animation() {
        arrayAdapter = new VideoWall_loading_page_ArrayAdapter(requireActivity());
        listView.setAdapter(arrayAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void Row_data(String[] save_post_id, String[] save_poster_nickname, String[] save_post_time,
                         String[] save_post_content, String[] save_post_files_url, String[] save_post_type,
                         String[] save_post_likes, String[] save_post_username, String[] save_poster_imge,
                         String[] user_likes_post_id , String[]video_seen_count, String[] post_user_type,
                         String[] poster_fans, String[] user_follow_status) {
        this.mUsername = save_poster_nickname;
        this.mContent = save_post_content;
        this.videoes_url = save_post_files_url;
        this.mVideoCounts = save_post_likes;
        this.post_username = save_post_username;
        this.imagesurl = save_poster_imge;
        this.post_id = save_post_id;
        this.user_likes_post_id = user_likes_post_id;
        this.video_seen_count = video_seen_count;
        this.post_user_type = post_user_type;
        this.poster_fans = poster_fans;
        this.user_follow_status = user_follow_status;

        giveadapter();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void giveadapter() {

        arrayAdapter = new VideoRowAdapter(requireActivity(), mUsername, mContent, mComment, mVideoCounts,
                videoes_url, imagesurl, post_username, post_id, Login_user, user_likes_post_id,
                Login_User_Img, video_seen_count, post_user_type, poster_fans, user_follow_status);
        listView.setAdapter(arrayAdapter);

        //將載入的影片放置到觀看關係的TABLE中，並且同時增加觀看次數----------------------------------------------------------------------------------
        if (UserInfoConfig.getConfig(requireActivity(),"UserInfo","role","User").equals("User")){
            SeenRelationBackgroundWorker seenRelationBackgroundWorker;
            seenRelationBackgroundWorker = new SeenRelationBackgroundWorker(getActivity());
            //載入曾載入過的影片ID--------------------------------------------------------------------------------------
            seenRelationBackgroundWorker.execute(
                    post_id
            );
        }

        loadingVideoWallBackgroundWorker.cancel(true);
    }

    public void testforStrinf(String x) {
        Toast.makeText(getActivity(), x, Toast.LENGTH_LONG).show();
    }

    public void test(int x) {
        Toast.makeText(getActivity(), String.valueOf(x), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
       /* StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        loadingVideoWallBackgroundWorker = new LoadingVideoWallBackgroundWorker(getActivity());
        loadingVideoWallBackgroundWorker.videowallpage_row = this;
        loadingVideoWallBackgroundWorker.execute(Login_user);  */  }


}

class VideoWall_loading_page_ArrayAdapter extends ArrayAdapter<String> {
    private Context context;

    VideoWall_loading_page_ArrayAdapter(@NonNull Context c) {
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

class VideoRowAdapter extends ArrayAdapter<String> {
    Context context;
    private String[] username;
    private String[] fly_video_counts;
    private String[] content;
    String[] url;
    private String[] Iurl;
    private boolean volume = true;
    String[] nickname;
    String[] post_id;
    String Login_user;
    String[] user_likes_post_id;
    String[] video_seen_count;
    String[] post_user_type;
    String[] poster_fans;
    String[] user_follow_status;
    String login_url;
    String Login_User_Img_url;

    private String role;
    private String ID;

    UserFollowsArtistBackgroundWorker userFollowsArtistBackgroundWorker;
    UserLikesPostBackgroundWorker userLikesPostBackgroundWorker;

    VideoRowAdapter(@NonNull Context c, String[] user_name, String[] video_content, String[] comments, String[] fly_video_counts,
                    String[] urls, String[] imaurls, String[] postuser, String[] post_id, String Login_user,
                    String[] user_likes_post_id, String Login_User_Img, String[] video_seen_count, String[] post_user_type,
                    String[] poster_fans, String[] user_follow_status) {
        super(c, R.layout.viedorow, R.id.video_row_content, user_likes_post_id );
        this.url = urls;
        this.context = c;
        this.username = postuser;
        this.content = video_content;
        this.fly_video_counts = fly_video_counts;
        this.Iurl = imaurls;
        this.nickname = user_name;
        this.post_id = post_id;
        this.Login_user = Login_user;
        this.user_likes_post_id = user_likes_post_id;
        this.Login_User_Img_url = Login_User_Img;
        this.video_seen_count = video_seen_count;
        this.post_user_type = post_user_type;
        this.poster_fans = poster_fans;
        this.user_follow_status = user_follow_status;
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
        role = UserInfoConfig.getConfig(this.context,"UserInfo","role","");
        ID = UserInfoConfig.getConfig(this.context,"UserInfo","ID","");

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(viedorow, null, true);
            viewHolder.image_btn_poster = (ImageView) convertView.findViewById(R.id.User_Picture);
            viewHolder.poster_nickname = (TextView) convertView.findViewById(R.id.poster_name);
            viewHolder.VideoContent = (TextView) convertView.findViewById(R.id.videos_content);
            viewHolder.comment_bar = (EditText) convertView.findViewById(R.id.comment_bar);
//            viewHolder.VideoComment = (TextView) convertView.findViewById(R.id.top_comment);
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
            String urls = url[position];
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
        viewHolder.poster_nickname.setText(nickname[position]);

        //設定貼文者名稱按下後功能(是藝人才能按下)---------------------------------------------------------------------------------
        if(post_user_type[position].equals("performer")) {
            viewHolder.poster_nickname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String chooseusername = username[position];
                    //      UserSeesArtistPostBackgroundWorker userSeesArtistPostBackgroundWorker = new UserSeesArtistPostBackgroundWorker(getContext());
                    //      userSeesArtistPostBackgroundWorker.execute(chooseusername);

                    Intent intent = new Intent(context, UserSeesArtistPage.class);
                    intent.putExtra("post_user_id", chooseusername);
                    intent.putExtra("post_user_img", Iurl[position]);
                    intent.putExtra("post_user_name", nickname[position]);
                    intent.putExtra("poster_fans", poster_fans[position]);
                    intent.putExtra("user_follow_status", user_follow_status[position]);
                    intent.putExtra("user_id", Login_user);
                    ActivityOptions options =
                            ActivityOptions.makeCustomAnimation(context, R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
                    context.startActivity(intent, options.toBundle());
                }
            });
        }

        //設定貼文者頭貼按下功能(是藝人才能按下)---------------------------------------------------------------------------------
        if(post_user_type[position].equals("performer")) {
            viewHolder.image_btn_poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String chooseusername = username[position];

                    Intent intent = new Intent(context, UserSeesArtistPage.class);
                    intent.putExtra("post_user_id", chooseusername);
                    intent.putExtra("post_user_img", Iurl[position]);
                    intent.putExtra("post_user_name", nickname[position]);
                    intent.putExtra("poster_fans", poster_fans[position]);
                    intent.putExtra("user_follow_status", user_follow_status[position]);
                    intent.putExtra("user_id", Login_user);
                    ActivityOptions options =
                            ActivityOptions.makeCustomAnimation(context, R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
                    context.startActivity(intent, options.toBundle());
                }
            });
        }

        //放帳號的位置
//        Vusername.setTag(username[position]);

        //設定貼文起飛數---------------------------------------------------------------------------------
        viewHolder.Video_fly.setText(fly_video_counts[position]);
        //設定貼文觀看數數---------------------------------------------------------------------------------
        viewHolder.Video_seen.setText(video_seen_count[position]);
//        viewHolder.VideoComment.setText(comment[position]);
        //設定貼文內容---------------------------------------------------------------------------------
        viewHolder.VideoContent.setText(content[position]);

        //設定貼文者頭貼功能---------------------------------------------------------------------------------
        if (this.Iurl[position] == null || this.Iurl[position].equals("\r") ||
                this.Iurl[position].equals(""))
            viewHolder.image_btn_poster.setImageResource(R.drawable.person_110935);
        else
            loadImageFromUrl(Iurl[position], viewHolder.image_btn_poster);

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
                    flies_number = Integer.parseInt(fly_video_counts[position]) + 1;
                    flies = String.valueOf(flies_number);
                    viewHolder.Video_fly.setText(flies);
                    //進入後台做資料庫更新---------------------------------------------------------------------------------
                    String Type = "like";
                    like(role, ID, post_id[position], context, Type);

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
                    flies_number = Integer.parseInt(fly_video_counts[position]);
                    flies = String.valueOf(flies_number);
                    viewHolder.Video_fly.setText(flies);
                    //進入後台做資料庫更新---------------------------------------------------------------------------------
                    String Type = "dislike";
                    dislike(role, ID, post_id[position], context, Type);

                 //   userLikesPostBackgroundWorker = new UserLikesPostBackgroundWorker(context);
                 //   userLikesPostBackgroundWorker.execute(Type, role, ID, post_id[position], "2");
                }
            }


        });

        //設定自己的頭貼---------------------------------------------------------------------------------
        if (this.Login_User_Img_url == null || this.Login_User_Img_url.equals("\r") ||
                this.Login_User_Img_url.equals(""))
            viewHolder.Login_User_Img.setImageResource(R.drawable.person_110935);
        else
            loadImageFromUrl(this.Login_User_Img_url, viewHolder.Login_User_Img);

        //設定按下留言欄位後---------------------------------------------------------------------------------
        viewHolder.comment_bar.setOnClickListener(new EditText.OnClickListener(){

            @Override
            public void onClick(View v) {
                //username為貼文者的ID
                String chooseusername = username[position];
                String post_content =  content[position];
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
                intent.putExtra("user_id", Login_user);
                intent.putExtra("post_content", post_content);
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
}