package com.example.tomato.background;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.tomato.usersees.UserSeesArtistAboutPage;
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

public class UserSeesArtistAboutBackgroundWorker extends AsyncTask<String, Void, Void> {


    String[] save_performerName, save_performer_nickname, save_CityName, save_performanceTheme,
            save_performerType, save_Social, save_introduce;

    String Top_Artist_Name, save_artist_total_followers, Top_Artist_Picture;

    Boolean follow = false;
    String[] save_followed_artists_id;
    String Login_user;

    String result;

    public UserSeesArtistAboutPage About;

    Context context;
    AlertDialog alertDialog;

    public UserSeesArtistAboutBackgroundWorker(Context ctx) {
        context = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... params) {
        String Post_username = params[0];
        String ip = params[1];
        String use_url = ip + "/StreetApp_FinalProject2020/performer/about.php";
        try {
            URL url = new URL(use_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");

            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
//                test = "帳號：" + params[0]
            OutputStream outputStream = httpURLConnection.getOutputStream();
//                test = "OUT STREAM";
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
//                test = "設定BUFFERED WRITER";
            String post_data = URLEncoder.encode("performer_id", "UTF-8") + "=" + URLEncoder.encode(Post_username, "UTF-8");
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


            save_performerName = new String[jsonArray.length()];
            save_performer_nickname = new String[jsonArray.length()];
            save_CityName = new String[jsonArray.length()];
            save_performanceTheme = new String[jsonArray.length()];
            save_performerType = new String[jsonArray.length()];
            save_Social = new String[jsonArray.length()];
            save_introduce = new String[jsonArray.length()];

            jsonObject = jsonArray.getJSONObject(0);
            //後面的名稱丟資料庫的欄位名稱
            save_performerName[0] = jsonObject.getString("performerName");
            save_performer_nickname[0] = jsonObject.getString("performer_nickname");
            save_CityName[0] = jsonObject.getString("cityName");
            save_performanceTheme[0] = jsonObject.getString("performTheme");
            save_performerType[0] = jsonObject.getString("performerActType");
            save_Social[0] = "尚無資訊";
            save_introduce[0] = "請自定義您的個人介紹哦！";

        } catch (Exception e) {
      /*      System.out.println(e.getMessage());
            result = e.getMessage();*/
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Void result) {
//        this.Top_Artist_Name = this.save_poster_nickname[0];
        About.change_text(save_performanceTheme[0],
                save_CityName[0],
                save_Social[0],
                save_introduce[0]);

        this.cancel(true);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
