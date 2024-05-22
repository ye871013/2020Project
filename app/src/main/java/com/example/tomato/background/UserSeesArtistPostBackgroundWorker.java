package com.example.tomato.background;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.tomato.Activity.MainPage;
import com.example.tomato.pagefragments.videowallpage;
import com.example.tomato.usersees.UserSeesArtistPage;

import org.json.JSONArray;
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
import java.util.Map;
import java.util.Set;

public class UserSeesArtistPostBackgroundWorker extends AsyncTask<String, Void, Void> {

    // post_id -> 貼文ID  / post_content -> 貼文內容  /  post_fly -> 貼文起飛數  /
    // post_seen -> 貼文觀看數 / post_type -> 貼文類型(暫不用到) / post_video -> 貼文影片
    private String[]  post_id, post_content, post_fly, post_seen, post_type, post_video,
            save_poster_nickname, save_post_time, save_post_username, user_likes_post_id,
            save_poster_picture,post_user_type;


    String Login_user;
    String result;
    String user_type;

    public UserSeesArtistPage Artist_post_row;

    Context context;
    AlertDialog alertDialog;

    public UserSeesArtistPostBackgroundWorker(Context ctx) {
        context = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... params) {
        // 0.貼文者ID 1.使用者ID 2.網址
        String Post_username = params[0];
        Login_user = params[1];
        String login_url = params[2] + "/StreetApp_FinalProject2020/see_performer_page/see_performer_homepage.php";
        user_type = params[3];
        try {
            URL url = new URL(login_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");


            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String post_data = URLEncoder.encode("Post_username", "UTF-8") + "=" + URLEncoder.encode(Post_username, "UTF-8")
                    + "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(Login_user, "UTF-8")
                    + "&" + URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(user_type, "UTF-8");
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
//                test = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
//                System.out.println(e.getMessage());
//                test = e.getMessage();
        }

        try {
            JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonObject = null;

            post_id = new String[jsonArray.length()];
            post_video = new String[jsonArray.length()];
            save_poster_nickname = new String[jsonArray.length()];
            post_fly = new String[jsonArray.length()];
            post_content = new String[jsonArray.length()];
            post_type = new String[jsonArray.length()];
            save_post_time = new String[jsonArray.length()];
            save_post_username = new String[jsonArray.length()];
            save_poster_picture = new String[jsonArray.length()];
            user_likes_post_id = new String[jsonArray.length()];
            post_user_type = new String[jsonArray.length()];
            post_seen = new String[jsonArray.length()];


            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                //後面的名稱丟資料庫的欄位名稱
                post_id[i] = jsonObject.getString("post_id");
                //貼文者名稱或暱稱
                if (jsonObject.getString("User_Name").trim().equals("null")){
                    if (jsonObject.getString("performer_nickname").trim().equals("null") ||
                            jsonObject.getString("performer_nickname").trim().equals(""))
                        save_poster_nickname[i] = jsonObject.getString("performerName");
                    else
                        save_poster_nickname[i] = jsonObject.getString("performer_nickname");
                }
                else
                    save_poster_nickname[i] = jsonObject.getString("User_Name");
                //貼文時間
                save_post_time[i] = jsonObject.getString("post_time");
                //貼文內容
                post_content[i] = jsonObject.getString("post_content");
                //貼文檔案
                post_video[i] = jsonObject.getString("videoURL");
                //貼文格式
                post_type[i] = jsonObject.getString("post_type");
                //貼文讚數
                post_fly[i] = jsonObject.getString("fly");
                //貼文觀看數
                post_seen[i] = jsonObject.getString("seen");

                //貼文者ID
                if (jsonObject.getString("User_ID").trim().equals("null")) {
                    save_post_username[i] = jsonObject.getString("id");
                    post_user_type[i] = "performer";
                }else {
                    save_post_username[i] = jsonObject.getString("User_ID");
                    post_user_type[i] = "user";
                }
                //貼文者頭貼(同使用者頭貼)
                if (jsonObject.getString("User_ImgURL").trim().equals("null"))
                    save_poster_picture[i] = jsonObject.getString("imageUrl");
                else
                    save_poster_picture[i] = jsonObject.getString("User_ImgURL");
                //使用者是否喜歡貼文
                if (jsonObject.getString("likes").trim().equals("null"))
                    user_likes_post_id[i] = "0";
                else
                    user_likes_post_id[i] = "1";
            }
//            return save_post_files_url;
        } catch (Exception e) {
      /*      System.out.println(e.getMessage());
            result = e.getMessage();*/
        }
            return null;
    }


    @Override
    protected void onPreExecute() {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPostExecute(Void result) {

        if (post_id != null && post_id.length > 0 &&
                post_id[0] != null ) {
            this.Artist_post_row.Row_data(this.post_id, this.save_poster_nickname, this.save_post_time,
                    this.post_content, this.post_video, this.post_type, this.post_fly,
                    this.save_post_username, this.save_poster_picture, this.user_likes_post_id, this.post_seen,
                    this.post_user_type);

            this.Artist_post_row.giveadapter();
        } else {
            this.Artist_post_row.give_empty_adapter();
        }

        this.cancel(true);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
