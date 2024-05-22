package com.example.tomato.background;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.tomato.Activity.SignUp;

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

public class SignUPBackgroundWorker extends AsyncTask<String, Void, Void> {

    Context context;
    AlertDialog alertDialog;
    public SignUp signUp;

    public SignUPBackgroundWorker(Context ctx) {
        context = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... params) {
        //params 值為 0.網址 1.名稱 2.性別 3.帳號 4.密碼 5.信箱 6.生日
        String ip = params[0];
        String User_name = params[1];
        String User_sex = params[2];
        String User_account = params[3];
        String User_password = params[4];
        String User_email = params[5];
        String User_birthday = params[6];
        String upload_file_name = ip + "/StreetApp_FinalProject2020/uploads/images/" + params[7];
        String user_location = params[8];
        String user_phone = params[9];

            //給予網址------------------------------------------------------------------------------------------------
        String login_url = ip + "/StreetApp_FinalProject2020/sign_up.php";

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
                String post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(User_name, "UTF-8")
                        + "&" + URLEncoder.encode("sex", "UTF-8") + "=" + URLEncoder.encode(User_sex, "UTF-8")
                        + "&" + URLEncoder.encode("account", "UTF-8") + "=" + URLEncoder.encode(User_account, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(User_password, "UTF-8")
                        + "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(User_email, "UTF-8")
                        + "&" + URLEncoder.encode("birthday", "UTF-8") + "=" + URLEncoder.encode(User_birthday, "UTF-8")
                        + "&" + URLEncoder.encode("upload_file_name", "UTF-8") + "=" + URLEncoder.encode(upload_file_name, "UTF-8")
                        + "&" + URLEncoder.encode("user_location", "UTF-8") + "=" + URLEncoder.encode(user_location, "UTF-8")
                        + "&" + URLEncoder.encode("user_phone", "UTF-8") + "=" + URLEncoder.encode(user_phone, "UTF-8");
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


        return null;
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(Void result) {
        Toast.makeText(this.signUp, "註冊成功！", Toast.LENGTH_LONG).show();
        this.signUp.sign_up_success();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
