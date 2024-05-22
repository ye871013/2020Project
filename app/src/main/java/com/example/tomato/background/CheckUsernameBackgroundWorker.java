package com.example.tomato.background;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.tomato.Activity.LoginOrSingin;
import com.example.tomato.Activity.SignUp;

import org.json.JSONException;
import org.json.JSONObject;

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

public class CheckUsernameBackgroundWorker extends AsyncTask<String,Void,Boolean> {
    private String output = "";
    public SignUp signUp;

    private Context context;
    AlertDialog alertDialog;
    public CheckUsernameBackgroundWorker(Context ctx){
        context = ctx;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Boolean doInBackground(String... params) {
        //給網域[0]給輸入帳號[1]------------------------------------------------------------------------------------------------
        String ip = params[0];
        String typing_username = params[1];

        //本機測試用連線網址------------------------------------------------------------------------------------------------
        String login_url = ip + "/StreetApp_FinalProject2020/check_exist.php";

            try {
//                test = "進Try";
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//              test = "打開連線";
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(typing_username, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

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
                output = stringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
//                test = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
//                System.out.println(e.getMessage());
//                test = e.getMessage();
            }

                try {

                    JSONObject login_result_Array = new JSONObject(output);

                    String outcome = login_result_Array.getString("status");

                    switch (outcome) {
                        case "sign up fail":
                            return false;
                        case "sign up success":
                            return true;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                     System.out.println(e.getMessage());
                     //result = e.getMessage();
                }

        return true;
    }

    @Override
    protected void onPreExecute() {
    }


    @Override
    protected void onPostExecute(Boolean result) {
        this.signUp.Check_Status(result);
        this.cancel(true);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
