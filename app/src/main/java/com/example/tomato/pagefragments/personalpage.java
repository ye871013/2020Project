package com.example.tomato.pagefragments;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.tomato.Activity.Analysis;
import com.example.tomato.Activity.Comment;
import com.example.tomato.Activity.LoginOrSingin;
import com.example.tomato.Activity.MainPage;
import com.example.tomato.Activity.SignUp;
import com.example.tomato.Activity.Upload;
import com.example.tomato.R;
import com.example.tomato.UserInfoConfig;
import com.example.tomato.background.BackgroundWorker;
import com.example.tomato.background.UserLikesPostBackgroundWorker;
import com.example.tomato.background.UserSeesArtistPostBackgroundWorker;
import com.example.tomato.background.UserSeesOwnPageBackgroundWorker;
import com.example.tomato.usersees.UserSeesArtistPage;
import com.example.tomato.usersees.UserSeesEditPage;
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
import java.util.Objects;

import static com.example.tomato.R.layout.viedorow;

public class personalpage extends Fragment {
    personalpage Per = this;
    ImageButton btn_upload;
    private ListView listView;
    private TextView edit, User_name;
    private ImageView User_picture;
    ImageButton btn_my_posted, btn_my_likes, btn_my_follows;
    TextView fans;
    String Login_user;
    View top_view;
    String Login_User_Img;

    UserSeesOwnPageBackgroundWorker userSeesOwnPageBackgroundWorker;
    UserSeesOwnPageBackgroundWorker Likes;
    UserSeesOwnPageBackgroundWorker Follows;
    UserSeesOwnPageBackgroundWorker Posts;

    ArrayAdapter<String> arrayAdapter;

    String[] videoes_url;
    String[] mUsername;
    String[] mContent;
    String[] mVideoCounts;
    String[] post_username;
    String[] imagesurl;
    String[] user_likes_post_id;
    String[] save_post_id;
    String[] save_user_seen;
    String[] post_user_type;
    String[] user_follow_status;
    String[] poster_fans;
    String which_page;

    String thread_name= "";

    String mComment[] = {"謝囉", "謝囉", "", "", ""};

    ArrayAdapter<String> Likes_arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@Nullable final LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        /*ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.personalpage,
                    contaoner,
                        false);
        return rootView;*/


//        View view = inflater.inflate(R.layout.personalpage,container,false);

        Login_user =  UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID","");
        Login_User_Img = Login_User_Img = UserInfoConfig.getConfig(requireActivity(),"UserInfo","Img","");


        //判斷是使用者畫面-------------------------------------------------------------------------------------------------
        if(UserInfoConfig.getConfig(requireActivity(),"UserInfo","role","User").equals("User")){
            top_view = getLayoutInflater().inflate(R.layout.personalpage_top_fragment, null);
            edit = top_view.findViewById(R.id.edit);
            User_name = top_view.findViewById(R.id.user_name);
            User_picture = top_view.findViewById(R.id.Top_artist_pictures);
            btn_my_posted = top_view.findViewById(R.id.my_posts);
            btn_my_likes = top_view.findViewById(R.id.my_likes);
            btn_my_follows = top_view.findViewById(R.id.my_follows);
        }
        //判斷是藝人畫面-------------------------------------------------------------------------------------------------
        else {
            top_view = getLayoutInflater().inflate(R.layout.performer_personalpage_top_fragment, null);
            edit = top_view.findViewById(R.id.edit);
            User_name = top_view.findViewById(R.id.user_name);
            User_picture = top_view.findViewById(R.id.Top_artist_pictures);
            btn_my_posted = top_view.findViewById(R.id.my_posts);
            btn_my_likes = top_view.findViewById(R.id.my_likes);
            btn_my_follows = top_view.findViewById(R.id.my_follows);
            fans = top_view.findViewById(R.id.fans);
            //按下粉絲數量後開啟分系介面的按鈕(藝人專用)----------------------------------------------------------------------------------------------------
            fans.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(requireActivity(), Analysis.class);
                                            ActivityOptions options =
                                                    ActivityOptions.makeCustomAnimation(requireActivity(), R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
                                            requireActivity().startActivity(intent, options.toBundle());
                                        }
                                    }
            );
        }

        View view = inflater.inflate(R.layout.personalpage, container, false);

        listView = view.findViewById(R.id.personal_list_view);
        btn_upload = view.findViewById(R.id.open_upload_page);
        btn_upload.setVisibility(View.VISIBLE);


        btn_upload.setOnClickListener(new ImageButton.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Upload.class);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(getContext(), R.anim.animate_slide_up_enter, R.anim.animate_fade_exit);
                requireContext().startActivity(intent, options.toBundle());

            }
        });

        which_page = "first";

        this.give_loading_animation();

        if(UserInfoConfig.getConfig(requireActivity(),"UserInfo","role","User").equals("User")){
            StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
            userSeesOwnPageBackgroundWorker = new UserSeesOwnPageBackgroundWorker(requireActivity());
            userSeesOwnPageBackgroundWorker.User_post = this;
            userSeesOwnPageBackgroundWorker.execute(
                    UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID",""),
                    which_page,
                    UserInfoConfig.getConfig(requireActivity(),"UserInfo","Img",""),
                    UserInfoConfig.getConfig(requireActivity(),"link","url","localhost"),
                    UserInfoConfig.getConfig(requireActivity(),"UserInfo","role","User"));
        }else{
            Total_fans total_fans = new Total_fans(requireActivity());
            total_fans.execute(
                    UserInfoConfig.getConfig(requireActivity(),"link","url","localhost"),
                    UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID",""));
        }



        btn_my_posted.setEnabled(false);
        btn_my_follows.setEnabled(true);
        btn_my_likes.setEnabled(true);


        TextView.OnClickListener edit_onClickListener = new TextView.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                Intent edit_page = new Intent(requireActivity(), UserSeesEditPage.class);

                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(requireActivity(), R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
                startActivity(edit_page, options.toBundle());

            }
        };

        ImageButton.OnClickListener MyFollows_onClickListener = new ImageButton.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
               give_loading_animation();

                btn_my_likes.setImageResource(R.drawable.unclick_fly);
                btn_my_follows.setImageResource(R.drawable.clicked_my_follows);
                btn_my_posted.setImageResource(R.drawable.my_posts);

                which_page = "follows";


                //arrayAdapter.notifyDataSetChanged();

                Follows = new UserSeesOwnPageBackgroundWorker(requireActivity());
                Follows.User_post = Per;
                Follows.execute(
                        UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID",""),
                        which_page,
                        UserInfoConfig.getConfig(requireActivity(),"UserInfo","Img",""),
                        UserInfoConfig.getConfig(requireActivity(),"link","url","localhost"),
                        UserInfoConfig.getConfig(requireActivity(),"UserInfo","role","User"));

                btn_upload.setVisibility(View.INVISIBLE);
                btn_my_posted.setEnabled(true);
                btn_my_follows.setEnabled(false);
                btn_my_likes.setEnabled(true);
            }
        };

        ImageButton.OnClickListener MyLikes_onClickListener = new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                give_loading_animation();

                btn_my_likes.setImageResource(R.drawable.clicked_fly);
                btn_my_follows.setImageResource(R.drawable.my_follows);
                btn_my_posted.setImageResource(R.drawable.my_posts);

                which_page = "likes";

                Likes = new UserSeesOwnPageBackgroundWorker(requireActivity());
                Likes.User_post = Per;
                Likes.execute(
                        UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID",""),
                        which_page,
                        UserInfoConfig.getConfig(requireActivity(),"UserInfo","Img",""),
                        UserInfoConfig.getConfig(requireActivity(),"link","url","localhost"),
                        UserInfoConfig.getConfig(requireActivity(),"UserInfo","role","User"));

                btn_upload.setVisibility(View.INVISIBLE);
                btn_my_posted.setEnabled(true);
                btn_my_follows.setEnabled(true);
                btn_my_likes.setEnabled(false);
            }
        };

        ImageButton.OnClickListener MyPosted_onClickListener = new ImageButton.OnClickListener() {

            @Override
            public void onClick(View v) {
                give_loading_animation();

                btn_my_likes.setImageResource(R.drawable.unclick_fly);
                btn_my_follows.setImageResource(R.drawable.my_follows);
                btn_my_posted.setImageResource(R.drawable.clicked_my_posts);

                which_page = "posts";

                Posts = new UserSeesOwnPageBackgroundWorker(requireActivity());
                Posts.User_post = Per;
                Posts.execute(
                        UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID",""),
                        which_page,
                        UserInfoConfig.getConfig(requireActivity(),"UserInfo","Img",""),
                        UserInfoConfig.getConfig(requireActivity(),"link","url","localhost"),
                        UserInfoConfig.getConfig(requireActivity(),"UserInfo","role","User"));

                btn_upload.setVisibility(View.VISIBLE);
                btn_my_posted.setEnabled(false);
                btn_my_follows.setEnabled(true);
                btn_my_likes.setEnabled(true);
            }
        };

        //設定個人頁面頂端-------------------------------------------------------------------------------------------
        initialTopView(UserInfoConfig.getConfig(requireActivity(),"UserInfo","Name",""),
                UserInfoConfig.getConfig(requireActivity(),"UserInfo","Img",""));

        edit.setOnClickListener(edit_onClickListener);
        btn_my_follows.setOnClickListener(MyFollows_onClickListener);
        btn_my_likes.setOnClickListener(MyLikes_onClickListener);
        btn_my_posted.setOnClickListener(MyPosted_onClickListener);

        listView.addHeaderView(top_view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initialTopView(UserInfoConfig.getConfig(requireActivity(),"UserInfo","Name",""),
                UserInfoConfig.getConfig(requireActivity(),"UserInfo","Img",""));

        which_page = "first";

        this.give_loading_animation();

        if(UserInfoConfig.getConfig(requireActivity(),"UserInfo","role","User").equals("User")){
            StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
            userSeesOwnPageBackgroundWorker = new UserSeesOwnPageBackgroundWorker(requireActivity());
            userSeesOwnPageBackgroundWorker.User_post = this;
            userSeesOwnPageBackgroundWorker.execute(
                    UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID",""),
                    which_page,
                    UserInfoConfig.getConfig(requireActivity(),"UserInfo","Img",""),
                    UserInfoConfig.getConfig(requireActivity(),"link","url","localhost"),
                    UserInfoConfig.getConfig(requireActivity(),"UserInfo","role","User"));
        }else{
            Total_fans total_fans = new Total_fans(requireActivity());
            total_fans.execute(
                    UserInfoConfig.getConfig(requireActivity(),"link","url","localhost"),
                    UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID",""));
        }
        btn_my_posted.setEnabled(false);
        btn_my_follows.setEnabled(true);
        btn_my_likes.setEnabled(true);
    }

    //顯示總追蹤數量----------------------------------------------------------------
    private void int_top(String follow){
        fans.setText(follow);
        UserInfoConfig.setConfig(requireActivity(),"UserInfo_follow","fans",follow);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        userSeesOwnPageBackgroundWorker = new UserSeesOwnPageBackgroundWorker(requireActivity());
        userSeesOwnPageBackgroundWorker.User_post = this;
        userSeesOwnPageBackgroundWorker.execute(
                UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID",""),
                which_page,
                UserInfoConfig.getConfig(requireActivity(),"UserInfo","Img",""),
                UserInfoConfig.getConfig(requireActivity(),"link","url","localhost"),
                UserInfoConfig.getConfig(requireActivity(),"UserInfo","role","User")
        );
    }


    //按下編輯後的按鈕(藝人專用)----------------------------------------------------------------------------------------------------



    //匯入下方要顯示用的LISTVIEW資料------------------------------------------------------------------------------------------------
    public void Row_data(String[] save_post_id, String[] save_poster_nickname, String[] save_post_time,
                     String[] save_post_content, String[] save_post_files_url, String[] save_post_type,
                     String[] save_post_likes, String[] save_post_username, String[] save_poster_picture,
                     String[] user_likes_post_id, String[] post_user_type, String[] user_follow_status,
                         String[] save_user_seen, String[] poster_fans) {
        this.save_post_id = save_post_id;
        this.mUsername = save_poster_nickname;
        this.mContent = save_post_content;
        this.videoes_url = save_post_files_url;
        this.imagesurl = save_poster_picture;
        this.mVideoCounts = save_post_likes;
        this.post_username = save_post_username;
        this.user_likes_post_id = user_likes_post_id;
        this.post_user_type = post_user_type;
        this.user_follow_status = user_follow_status;
        this.save_user_seen = save_user_seen;
        this.poster_fans = poster_fans;
    }

    //讀取畫面------------------------------------------------------------------------------------------------
    void give_loading_animation() {
        arrayAdapter = new PersonalWall_loading_page_ArrayAdapter(requireActivity());
        listView.setAdapter(arrayAdapter);
    }

    //把資料匯入到Adapter------------------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void give_UserPage_adapter(String thread_name) {
        arrayAdapter = new User_Post_RowAdapter(requireActivity(), mUsername, mContent, mComment, mVideoCounts,
                videoes_url, imagesurl, post_username, Login_user, user_likes_post_id, save_post_id
                , post_user_type, user_follow_status, save_user_seen, poster_fans, Login_User_Img);

        listView.setAdapter(arrayAdapter);
        if (thread_name.trim().equals("first")) {
            userSeesOwnPageBackgroundWorker.cancel(true);
        } else if (thread_name.trim().equals("likes")) {
            Likes.cancel(true);
        } else if (thread_name.trim().equals("follows")) {
            Follows.cancel(true);
        } else if (thread_name.trim().equals("posts")) {
            Posts.cancel(true);
        }
        this.thread_name = thread_name;

    }

    //沒有資料匯入沒有資料的樣板Adapter------------------------------------------------------------------------------------------------
    public void give_empty_adapter(String thread_name) {

        if (thread_name.trim().equals("first")) {
            arrayAdapter = new Empty_Own_Post_RowAdapter(requireActivity(), "first");
            listView.setAdapter(arrayAdapter);
            userSeesOwnPageBackgroundWorker.cancel(true);

        } else if (thread_name.trim().equals("likes")) {
            arrayAdapter = new Empty_Own_Post_RowAdapter(requireActivity(), "likes");
            listView.setAdapter(arrayAdapter);
            Likes.cancel(true);

        } else if (thread_name.trim().equals("follows")) {
            arrayAdapter = new Empty_Own_Post_RowAdapter(requireActivity(), "follows");
            listView.setAdapter(arrayAdapter);
            Follows.cancel(true);

        } else if (thread_name.trim().equals("posts")) {
            arrayAdapter = new Empty_Own_Post_RowAdapter(requireActivity(), "posts");
            listView.setAdapter(arrayAdapter);
            Posts.cancel(true);
        }
    }

    //匯入上方要顯示的List頭(這裡是上方的頭貼跟名字)------------------------------------------------------------------------------------------------
    public void initialTopView(String user_name, String ImgUrL) {
        User_name.setText(user_name);

        if (ImgUrL == null || ImgUrL.trim().equals("\r") || ImgUrL.trim().equals("")) {
            User_picture.setImageResource(R.drawable.person_110935);
            //loadImageFromUrl("https://ncyu-webdesign.000webhostapp.com/StreetArtist/Pictures/download.jpg", User_picture);
        } else
            loadImageFromUrl(ImgUrL, User_picture);
    }

    //匯入頭貼(使用網址匯入的方式)------------------------------------------------------------------------------------------------
    private void loadImageFromUrl(String x, ImageView y) {
        Picasso.with(requireActivity()).load(x).placeholder(R.drawable.person_110935)
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


    //看藝人自身目前總共追蹤數量----------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    private class Total_fans extends AsyncTask<String, Void, Void> {
        String follow;
        Context ctx;

        private Total_fans(Context context) {ctx = context;}

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(String... params) {
            String ip = params[0];
            String login_user_id = params[1];

            String login_url = ip + "/StreetApp_FinalProject2020/performer/total_fans.php";
            String result = "";
            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("performer_id", "UTF-8") + "=" + URLEncoder.encode(login_user_id, "UTF-8");
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

            try {
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject = null;
                jsonObject = jsonArray.getJSONObject(0);
                //設定貼文ID
                follow = jsonObject.getString("follow");

            }catch(JSONException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());}

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            int_top(follow);
        }
    }
}
class PersonalWall_loading_page_ArrayAdapter extends ArrayAdapter<String> {
    private Context context;

    PersonalWall_loading_page_ArrayAdapter(@NonNull Context c) {
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

class Empty_Own_Post_RowAdapter extends ArrayAdapter<String> {
    private Context context;
    private String page;

    public int getCount() {
        return 1;
    }

    Empty_Own_Post_RowAdapter(@NonNull Context c, String page) {
        super(c, R.layout.no_rows);
        this.context = c;
        this.page = page;
    }

    public View getView(final int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        final Null_ViewHolder null_viewHolder;

        if (convertView == null) {
            null_viewHolder = new Null_ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.no_rows, null, true);

            null_viewHolder.null_text = (TextView) convertView.findViewById(R.id.null_text);
            null_viewHolder.null_sec_text = (TextView) convertView.findViewById(R.id.null_sec_text);

            switch (this.page) {
                case "first":
                case "posts":
                    null_viewHolder.null_text.setText("目前無貼文");
                    null_viewHolder.null_sec_text.setText("分享你的第一篇貼文");
                    break;
                case "likes":
                    null_viewHolder.null_text.setText("目前無起飛影片");
                    null_viewHolder.null_sec_text.setText("趕緊去搜尋你的起飛影片");
                    break;
                case "follows":
                    null_viewHolder.null_text.setText("目前無追蹤的街頭藝人");
                    null_viewHolder.null_sec_text.setText("趕緊去追蹤您的\n第一位街頭藝人");
                    break;
            }

            convertView.setTag(null_viewHolder);
        } else {
            null_viewHolder = (Null_ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private static class Null_ViewHolder {
        TextView null_text;
        TextView null_sec_text;
    }
}

class User_Post_RowAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] username;
    private String[] comment;
    private String[] videocounts;
    private String[] content;
    private String[] url;
    private String[] Iurl;
    private String[] nickname;
    private String[] user_likes_post_id;
    private String[] save_post_id;
    private String[] post_user_type;
    private String[] user_follow_status;
    private String[] save_user_seen ;
    private String[] poster_fans;
    String Login_User_Img;
    String role, ID;
    private boolean volume = true;
    String Login_user;
    UserLikesPostBackgroundWorker userLikesPostBackgroundWorker;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    User_Post_RowAdapter(@NonNull Context c, String[] user_name, String[] video_content, String[] comments, String[] video_counts,
                         String[] urls, String[] imaurls, String[] postuser, String Login_user, String[] user_likes_post_id,
                         String[] save_post_id, String[] post_user_type, String[] user_follow_status,
                         String[] save_user_seen, String[] poster_fans, String Login_User_Img) {
        super(c, viedorow, R.id.textView, save_post_id);

        this.save_post_id = save_post_id;
        this.url = urls;
        this.context = c;
        this.username = postuser;
        this.content = video_content;
        this.comment = comments;
        this.videocounts = video_counts;
        this.Iurl = imaurls;
        this.nickname = user_name;
        this.Login_user = Login_user;
        this.user_likes_post_id = user_likes_post_id;
        this.post_user_type = post_user_type;
        this.user_follow_status = user_follow_status;
        this.save_user_seen = save_user_seen;
        this.poster_fans = poster_fans;
        this.Login_User_Img = Login_User_Img;
    }

    @Override
    public int getCount() {
        return content.length;
    }

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
        viewHolder.Video_fly.setText(videocounts[position]);
        //設定貼文觀看數數---------------------------------------------------------------------------------
        viewHolder.Video_seen.setText(save_user_seen[position]);
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
                    flies_number = Integer.parseInt(videocounts[position]) + 1;
                    flies = String.valueOf(flies_number);
                    viewHolder.Video_fly.setText(flies);
                    //進入後台做資料庫更新---------------------------------------------------------------------------------
                    String Type = "like";
                    like(role, ID, save_post_id[position], context, Type);

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
                    flies_number = Integer.parseInt(videocounts[position]);
                    flies = String.valueOf(flies_number);
                    viewHolder.Video_fly.setText(flies);
                    //進入後台做資料庫更新---------------------------------------------------------------------------------
                    String Type = "dislike";
                    dislike(role, ID, save_post_id[position], context, Type);

                    //   userLikesPostBackgroundWorker = new UserLikesPostBackgroundWorker(context);
                    //   userLikesPostBackgroundWorker.execute(Type, role, ID, post_id[position], "2");
                }
            }


        });

        //設定自己的頭貼---------------------------------------------------------------------------------
        if (this.Login_User_Img == null || this.Login_User_Img.equals("\r") ||
                this.Login_User_Img.equals(""))
            viewHolder.Login_User_Img.setImageResource(R.drawable.person_110935);
        else
            loadImageFromUrl(this.Login_User_Img, viewHolder.Login_User_Img);

        //設定按下留言欄位後---------------------------------------------------------------------------------
        viewHolder.comment_bar.setOnClickListener(new EditText.OnClickListener(){

            @Override
            public void onClick(View v) {
                //username為貼文者的ID
                String chooseusername = username[position];
                String post_content =  content[position];
                String choose_post_id = save_post_id[position];
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

    private static final class ViewHolder {
        ImageView image_btn_poster, Login_User_Img;
        TextView poster_nickname, VideoContent, VideoComment, Video_fly, Video_seen;
        EditText comment_bar;
        ImageButton btn_fly;
        VideoView Vid;
    }

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


    class SendPostReqAsyncTask extends AsyncTask<String, Void, Void> {
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