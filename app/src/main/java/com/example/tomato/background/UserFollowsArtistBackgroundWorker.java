package com.example.tomato.background;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

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

public class UserFollowsArtistBackgroundWorker extends AsyncTask<String, Void, Void> {

    private String[] save_followed_artists_id;
    String[] user_likes_post_id;

    private String result;
    private String Login_user;
    private String Type;
    private String choose_artist_id;
    String Status = "";


    Boolean follow = false;
    Context context;
    AlertDialog alertDialog;


    public UserFollowsArtistBackgroundWorker(Context ctx) {
        context = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... params) {
        //params 值為 0.類別 1.登入的會員帳號 2.要進行互動的藝人ID
        Type = params[0];
        Login_user = params[1];
        choose_artist_id = params[2];
        String  ip = params[3];
        String user_type = params[4];

        System.out.println(Type);
        System.out.println(Login_user);
        System.out.println(choose_artist_id);
        System.out.println(ip);
        System.out.println(user_type);



        String login_url;
        if (Type.equals("follow")) {
            if(user_type.equals("User"))
                login_url = ip + "/StreetApp_FinalProject2020/user/follow.php";
            else
                login_url = ip + "/StreetApp_FinalProject2020/performer/follow.php";
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
                String post_data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(Login_user, "UTF-8")
                        + "&" + URLEncoder.encode("choose_id", "UTF-8") + "=" + URLEncoder.encode(choose_artist_id, "UTF-8");
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

                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
//                test = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
//                System.out.println(e.getMessage());
//                test = e.getMessage();
            }
        } else if (Type.equals("un_follow")) {
            if(user_type.equals("User"))
                login_url = ip + "/StreetApp_FinalProject2020/user/unfollow.php";
            else
                login_url = ip + "/StreetApp_FinalProject2020/performer/unfollow.php";

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
                String post_data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(Login_user, "UTF-8")
                        + "&" + URLEncoder.encode("choose_id", "UTF-8") + "=" + URLEncoder.encode(choose_artist_id, "UTF-8");
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
        }

        return null;
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Void result) {

/*
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
        alertDialog.setMessage(save_post_id[0] + choose_post_id + Login_user + Status);
        alertDialog.show();


*/
System.out.println("成功追蹤");

        this.cancel(true);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
