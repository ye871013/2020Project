package com.example.tomato.background;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.tomato.UserInfoConfig;
import com.example.tomato.pagefragments.videowallpage;

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

public class SeenRelationBackgroundWorker extends AsyncTask<String[], Void, Void> {

    public String[] save_post_id, save_poster_nickname, save_post_time, save_post_content,
            save_post_files_url, save_post_type, save_post_likes, save_post_username, save_poster_imge,
            user_likes_post_id, save_user_seen;
    public videowallpage videowallpage_row;
    private String use_url, role;
    private String Login_user;
    String ip;
    Context context;
    AlertDialog alertDialog;

    public SeenRelationBackgroundWorker(Context ctx) {
        context = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String[]... params) {
        //params 值為 0.使用者ID 1.民眾/藝人 2.IP
        String user_id = UserInfoConfig.getConfig(context,"UserInfo","ID","");
        String user_type = UserInfoConfig.getConfig(context,"UserInfo","role","");
        String ip =  UserInfoConfig.getConfig(context,"link","url","localhost");
        String[] post_id = params[0];

        //給予網址------------------------------------------------------------------------------------------------
        String login_url = ip + "/StreetApp_FinalProject2020/videowall/seen_videowall.php";

        for (String s : post_id) {
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

                String post_data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                        + "&" + URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(user_type, "UTF-8")
                        + "&" + URLEncoder.encode("post_id", "UTF-8") + "=" + URLEncoder.encode(s, "UTF-8");

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
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Void result) {

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
