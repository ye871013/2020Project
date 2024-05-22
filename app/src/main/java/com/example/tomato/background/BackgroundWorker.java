package com.example.tomato.background;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.tomato.Activity.LoginOrSingin;
import com.example.tomato.UserInfoConfig;

import org.json.JSONArray;
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

public class    BackgroundWorker extends AsyncTask<String,Void,String> {
    private String ID;
    private String img;
    private String name;
    String ip;
    public LoginOrSingin LogIn;
    String output = "";

    private Context context;
    AlertDialog alertDialog;
    public BackgroundWorker(Context ctx){
        context = ctx;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
//        String test="預設";
//        Boolean result = false;
//        Boolean ConnectionStatus = false;
        String result = "";
        this.ip = params[3];
        //本機測試用連線網址
        String login_url = ip + "/StreetApp_FinalProject2020/login.php";
//        test = "過URL";
//            test = "進IF";
        if(type.equals("login")) {
            try {
//                test = "進Try";
                String username = params[1];
                String password = params[2];
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//              test = "打開連線";
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8")
                        + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
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
//                    JSONArray login_result_Array = new JSONArray(output);
//                    JSONObject result_Object = null;
//
//                    result_Object = login_result_Array.getJSONObject(0);
//
//                    outcome = result_Object.getString("status");
//                    ID = result_Object.getString("ID");

                    JSONObject login_result_Array = new JSONObject(output);

                    String outcome = login_result_Array.getString("status");
                    ID = login_result_Array.getString("ID");
                    img = login_result_Array.getString("img");
                    name = login_result_Array.getString("name");

//                    JSONObject object = new JSONObject(output);
//                    JSONArray jArray_status = object.getJSONArray("status");
//                    JSONArray jArray_ID = object.getJSONArray("ID");
//
//                    JSONObject result_status_outcome = jArray_status.getJSONObject(0);
//                    JSONObject result_ID_outcome = jArray_ID.getJSONObject(0);
//                    outcome = result_status_outcome.getString("status");
//                    ID = result_ID_outcome.getString("ID");

                    switch (outcome) {
                        case "performer login success":
//                    ConnectionStatus = true;
                            result = "Artist";
                            break;
                        case "Member login success":
//                    ConnectionStatus = true;
                            result = "User";
                            break;
                        case "login not success":
//                    ConnectionStatus = true;
                            result = "fail";
                            break;
                    }

                    return result;

                } catch (JSONException e) {
                    e.printStackTrace();
                     System.out.println(e.getMessage());
                     //result = e.getMessage();
                }

        }
        //        return test;
        return result;
    }

    @Override
    protected void onPreExecute() {
        /*alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");*/
 //       alertDialog.setOnCancelListener((DialogInterface.OnCancelListener) this);
    }

    /*protected void onPostExecute(String test) {
        *//*
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
        alertDialog.setMessage(test);
        alertDialog.show();
        *//*
    }*/

    @Override
    protected void onPostExecute(String result) {
        this.LogIn.TaskFinish(result, ID, img, name);
        this.cancel(true);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
