package com.example.tomato.background;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.tomato.pagefragments.personalpage;
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

public class UserSeesOwnPageBackgroundWorker extends AsyncTask<String, Void, String> {

    private String[] save_post_id, save_poster_nickname, save_post_time, save_post_content, save_post_files_url, save_post_type,
            save_post_likes, save_post_username, user_likes_post_id, User_Posted_Posts, User_Follows_id, User_Follows_posts,
            save_poster_picture, User_Follows_post, post_user_type, user_follow_status, save_user_seen, poster_fans;

    private String User_Likes,
            User_Follows, User_posted, User_ID, which_page,
            login_url, UserImg, User_type;

    public personalpage User_post;

    Context context;
    AlertDialog alertDialog;

    public UserSeesOwnPageBackgroundWorker(Context ctx) {
        context = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        User_ID = params[0];
        which_page = params[1];
        UserImg = params[2];
        String ip = params[3];
        User_type = params[4];

        String result = "";
        String post_result = "";
        String like_result = "";



        //第一次打開個人頁面
        if (which_page.trim().equals("first")) {
            //測試用本機連線
            if(User_type.equals("User"))
                login_url = ip + "/StreetApp_FinalProject2020/user/user_postwall.php";
            else
                login_url = ip + "/StreetApp_FinalProject2020/performer/performer_postwall.php";

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
                String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(User_ID, "UTF-8");
//                test = "設定POST DATA";
                bufferedWriter.write(post_data);
//                test = "WRITE POST DATA";


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

                save_post_id = new String[jsonArray.length()];
                save_post_files_url = new String[jsonArray.length()];
                save_poster_nickname = new String[jsonArray.length()];
                save_post_likes = new String[jsonArray.length()];
                save_post_content = new String[jsonArray.length()];
                save_post_type = new String[jsonArray.length()];
                save_post_time = new String[jsonArray.length()];
                save_post_username = new String[jsonArray.length()];
                save_poster_picture = new String[jsonArray.length()];
                user_likes_post_id = new String[jsonArray.length()];
                post_user_type = new String[jsonArray.length()];
                user_follow_status = new String[jsonArray.length()];
                save_user_seen = new String[jsonArray.length()];
                poster_fans = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    //設定貼文ID
                    save_post_id[i] = jsonObject.getString("post_id");
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
                    save_post_content[i] = jsonObject.getString("post_content");
                    //貼文檔案
                    save_post_files_url[i] = jsonObject.getString("videoURL");
                    //貼文格式
                    save_post_type[i] = jsonObject.getString("post_type");
                    //貼文讚數
                    save_post_likes[i] = jsonObject.getString("fly");
                    //貼文觀看數
                    save_user_seen[i] = jsonObject.getString("seen");

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

                    //使用者是否追蹤這個藝人
                    if (jsonObject.getString("follow").trim().equals("null") ||
                            jsonObject.getString("follow").trim().equals(""))
                        user_follow_status[i] = "0";
                    else
                        user_follow_status[i] = "1";

                    //貼文者粉絲數量
                    if (jsonObject.getString("performerFans").trim().equals("null"))
                        poster_fans[i] = "";
                    else
                        poster_fans[i] = jsonObject.getString("performerFans");
                }


            } catch (Exception e) {
      /*      System.out.println(e.getMessage());
            result = e.getMessage();*/
            }
        }

        //按下自己的貼文頁面
        else if (which_page.trim().equals("posts")) {

            if(User_type.equals("User"))
                login_url = ip + "/StreetApp_FinalProject2020/user/user_postwall.php";
            else
                login_url = ip + "/StreetApp_FinalProject2020/performer/performer_postwall.php";

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
                String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(User_ID, "UTF-8");
//                test = "設定POST DATA";
                bufferedWriter.write(post_data);
//                test = "WRITE POST DATA";


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

                save_post_id = new String[jsonArray.length()];
                save_post_files_url = new String[jsonArray.length()];
                save_poster_nickname = new String[jsonArray.length()];
                save_post_likes = new String[jsonArray.length()];
                save_post_content = new String[jsonArray.length()];
                save_post_type = new String[jsonArray.length()];
                save_post_time = new String[jsonArray.length()];
                save_post_username = new String[jsonArray.length()];
                save_poster_picture = new String[jsonArray.length()];
                user_likes_post_id = new String[jsonArray.length()];
                post_user_type = new String[jsonArray.length()];
                user_follow_status = new String[jsonArray.length()];
                save_user_seen = new String[jsonArray.length()];
                poster_fans = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    //設定貼文ID
                    save_post_id[i] = jsonObject.getString("post_id");
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
                    save_post_content[i] = jsonObject.getString("post_content");
                    //貼文檔案
                    save_post_files_url[i] = jsonObject.getString("videoURL");
                    //貼文格式
                    save_post_type[i] = jsonObject.getString("post_type");
                    //貼文讚數
                    save_post_likes[i] = jsonObject.getString("fly");
                    //貼文觀看數
                    save_user_seen[i] = jsonObject.getString("seen");

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

                    //使用者是否追蹤這個藝人
                    if (jsonObject.getString("follow").trim().equals("null") ||
                            jsonObject.getString("follow").trim().equals(""))
                        user_follow_status[i] = "0";
                    else
                        user_follow_status[i] = "1";

                    //貼文者粉絲數量
                    if (jsonObject.getString("performerFans").trim().equals("null"))
                        poster_fans[i] = "";
                    else
                        poster_fans[i] = jsonObject.getString("performerFans");
                }
            } catch (Exception e) {
            System.out.println(e.getMessage());
                /*result = e.getMessage();*/
            }
        }


        //按下喜歡的貼文頁面
        else if (which_page.trim().equals("likes")) {

            if(User_type.equals("User"))
                login_url = ip + "/StreetApp_FinalProject2020/user/user_like_postwall.php";
            else
                login_url = ip + "/StreetApp_FinalProject2020/performer/performer_like_postwall.php";
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
                String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(User_ID, "UTF-8");
//                test = "設定POST DATA";
                bufferedWriter.write(post_data);
//                test = "WRITE POST DATA";

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
                like_result = stringBuilder.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
//                test = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
//                System.out.println(e.getMessage());
//                test = e.getMessage();
            }

            try {
                JSONArray jsonArray = new JSONArray(like_result);
                JSONObject jsonObject = null;

                save_post_id = new String[jsonArray.length()];
                save_post_files_url = new String[jsonArray.length()];
                save_poster_nickname = new String[jsonArray.length()];
                save_post_likes = new String[jsonArray.length()];
                save_post_content = new String[jsonArray.length()];
                save_post_type = new String[jsonArray.length()];
                save_post_time = new String[jsonArray.length()];
                save_post_username = new String[jsonArray.length()];
                save_poster_picture = new String[jsonArray.length()];
                user_likes_post_id = new String[jsonArray.length()];
                post_user_type = new String[jsonArray.length()];
                user_follow_status = new String[jsonArray.length()];
                save_user_seen = new String[jsonArray.length()];
                poster_fans = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    //設定貼文ID
                    save_post_id[i] = jsonObject.getString("post_id");
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
                    save_post_content[i] = jsonObject.getString("post_content");
                    //貼文檔案
                    save_post_files_url[i] = jsonObject.getString("videoURL");
                    //貼文格式
                    save_post_type[i] = jsonObject.getString("post_type");
                    //貼文讚數
                    save_post_likes[i] = jsonObject.getString("fly");
                    //貼文觀看數
                    save_user_seen[i] = jsonObject.getString("seen");

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

                    //使用者是否追蹤這個藝人
                    if (jsonObject.getString("follow").trim().equals("null") ||
                            jsonObject.getString("follow").trim().equals(""))
                        user_follow_status[i] = "0";
                    else
                        user_follow_status[i] = "1";

                    //貼文者粉絲數量
                    if (jsonObject.getString("performerFans").trim().equals("null"))
                        poster_fans[i] = "";
                    else
                        poster_fans[i] = jsonObject.getString("performerFans");
                }
            } catch (Exception e) {
      /*      System.out.println(e.getMessage());
            result = e.getMessage();*/
            }
        }

        //按下追蹤者藝人頁面
        else if (which_page.trim().equals("follows")) {


            if(User_type.equals("User"))
                login_url = ip + "/StreetApp_FinalProject2020/user/user_follow_postwall.php";
            else
                login_url = ip + "/StreetApp_FinalProject2020/performer/performer_follow_postwall.php";
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
                String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(User_ID, "UTF-8");
//                test = "設定POST DATA";
                bufferedWriter.write(post_data);
//                test = "WRITE POST DATA";

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
                like_result = stringBuilder.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
//                test = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
//                System.out.println(e.getMessage());
//                test = e.getMessage();
            }

            try {
                JSONArray jsonArray = new JSONArray(like_result);
                JSONObject jsonObject = null;

                save_post_id = new String[jsonArray.length()];
                save_post_files_url = new String[jsonArray.length()];
                save_poster_nickname = new String[jsonArray.length()];
                save_post_likes = new String[jsonArray.length()];
                save_post_content = new String[jsonArray.length()];
                save_post_type = new String[jsonArray.length()];
                save_post_time = new String[jsonArray.length()];
                save_post_username = new String[jsonArray.length()];
                save_poster_picture = new String[jsonArray.length()];
                user_likes_post_id = new String[jsonArray.length()];
                post_user_type = new String[jsonArray.length()];
                user_follow_status = new String[jsonArray.length()];
                save_user_seen = new String[jsonArray.length()];
                poster_fans = new String[jsonArray.length()];


                //設定自己貼文的資料
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    //設定貼文ID
                    save_post_id[i] = jsonObject.getString("post_id");
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
                    save_post_content[i] = jsonObject.getString("post_content");
                    //貼文檔案
                    save_post_files_url[i] = jsonObject.getString("videoURL");
                    //貼文格式
                    save_post_type[i] = jsonObject.getString("post_type");
                    //貼文讚數
                    save_post_likes[i] = jsonObject.getString("fly");
                    //貼文觀看數
                    save_user_seen[i] = jsonObject.getString("seen");

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

                    //使用者是否追蹤這個藝人
                    if (jsonObject.getString("follow").trim().equals("null") ||
                            jsonObject.getString("follow").trim().equals(""))
                        user_follow_status[i] = "0";
                    else
                        user_follow_status[i] = "1";

                    //貼文者粉絲數量
                    if (jsonObject.getString("performerFans").trim().equals("null"))
                        poster_fans[i] = "";
                    else
                        poster_fans[i] = jsonObject.getString("performerFans");

                }
            } catch (Exception e) {
            System.out.println(e.getMessage());
                /*result = e.getMessage();*/
            }
        }


        return which_page;
    }


    @Override
    protected void onPreExecute() {
        /*alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");*/
        //       alertDialog.setOnCancelListener((DialogInterface.OnCancelListener) this);
    }

    /* protected void onPostExecute(String test) {
         alertDialog.setMessage(test);
         alertDialog.show();
     }
 */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPostExecute(String which_page) {

        if (this.save_post_id != null && this.save_post_id.length > 0 &&
                this.save_post_id[0] != null) {
            //傳回去使用者暱稱、照片值
            if (which_page.trim().equals("first")) {
            //第一次開啟個人頁面
                this.User_post.Row_data(this.save_post_id, this.save_poster_nickname, this.save_post_time,
                        this.save_post_content, this.save_post_files_url, this.save_post_type,
                        this.save_post_likes, this.save_post_username, this.save_poster_picture,
                        this.user_likes_post_id, this.post_user_type, this.user_follow_status,
                        this.save_user_seen , this.poster_fans);

                this.User_post.give_UserPage_adapter(which_page);

            } else if (which_page.trim().equals("posts")) {
            //按下個人貼文頁面
                this.User_post.Row_data(this.save_post_id, this.save_poster_nickname, this.save_post_time,
                        this.save_post_content, this.save_post_files_url, this.save_post_type,
                        this.save_post_likes, this.save_post_username, this.save_poster_picture,
                        this.user_likes_post_id, this.post_user_type, this.user_follow_status,
                        this.save_user_seen , this.poster_fans);

                this.User_post.give_UserPage_adapter(which_page);

            } else if (which_page.trim().equals("likes")) {
            //按下起飛貼文頁面
                this.User_post.Row_data(this.save_post_id, this.save_poster_nickname, this.save_post_time,
                        this.save_post_content, this.save_post_files_url, this.save_post_type,
                        this.save_post_likes, this.save_post_username, this.save_poster_picture,
                        this.user_likes_post_id, this.post_user_type, this.user_follow_status,
                        this.save_user_seen , this.poster_fans);


                this.User_post.give_UserPage_adapter(which_page);
            } else if (which_page.trim().equals("follows")) {

                this.User_post.Row_data(this.save_post_id, this.save_poster_nickname, this.save_post_time,
                        this.save_post_content, this.save_post_files_url, this.save_post_type,
                        this.save_post_likes, this.save_post_username, this.save_poster_picture,
                        this.user_likes_post_id, this.post_user_type, this.user_follow_status,
                        this.save_user_seen , this.poster_fans);

                this.User_post.give_UserPage_adapter(which_page);
            }

        } else {
            this.User_post.give_empty_adapter(which_page);
        }
        this.cancel(true);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
