package com.example.tomato.background;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

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

public class UserLikesPostBackgroundWorker extends AsyncTask<String, Void, Void> {

    private String[] save_post_id;
    String[] user_likes_post_id;

    private String result;
    private String Login_user;
    private String Type;
    private String choose_post_id;
    private String role;
    String login_url;
    String Status = "";
    String test;
    String help;

    Context context;
    AlertDialog alertDialog;


    public UserLikesPostBackgroundWorker(Context ctx) {
        context = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... params) {
        //params 值為 0.類別 1.登入的會員ID 2.登入的使用者分類為民眾或藝人 3.按讚的貼文ID
        Type = params[0];
        Login_user = params[1];
        role = params[2];
        choose_post_id = params[3];
        help = params[4];

        if (Type.equals("like")) {
            //民眾用或藝人用
            if (role.equals("User"))
                login_url = "http://172.20.10.5/StreetApp_FinalProject2020/user/like.php";
            else
                login_url = "http://172.20.10.5/StreetApp_FinalProject2020/performer/like.php";

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
                String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Login_user, "UTF-8")
                        + "&" + URLEncoder.encode("post_id", "UTF-8") + "=" + URLEncoder.encode(choose_post_id, "UTF-8");
//                test = "設定POST DATA";
                bufferedWriter.write(post_data);
//                test = "WRITE POST DATA";

                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

//                InputStream inputStream = httpURLConnection.getInputStream();
//
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
//
//                bufferedReader.close();
//                inputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();

                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                test = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                test = e.getMessage();
            }

        } else if (Type.equals("dislike")) {

            if (role.equals("User"))
                login_url = "http://172.20.10.5/StreetApp_FinalProject2020/user/dislike.php";
            else
                login_url = "http://172.20.10.5/StreetApp_FinalProject2020/performer/dislike.php";

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
                String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Login_user, "UTF-8")
                        + "&" + URLEncoder.encode("post_id", "UTF-8") + "=" + URLEncoder.encode(choose_post_id, "UTF-8");
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
                System.out.println(e.getMessage());
//                test = e.getMessage();
            }
        }

        return null;
    }


    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(Type + login_url + role + help);
        alertDialog.show();
        //       alertDialog.setOnCancelListener((DialogInterface.OnCancelListener) this);
    }

    @Override
    protected void onPostExecute(Void result) {
        //this.cancel(true);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
