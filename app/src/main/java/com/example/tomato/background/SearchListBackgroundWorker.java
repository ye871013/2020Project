package com.example.tomato.background;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.tomato.pagefragments.searchpage;
import com.example.tomato.pagefragments.videowallpage;

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

public class SearchListBackgroundWorker extends AsyncTask<String, Void, Void> {
    String search_word, type;
    String[] performer_id, performer_Name, performer_nickname, cityName,
            performTheme, performerActType, imageUrl, performerFans;
    String visual_arts_check, performance_arts_check, ideas_arts_check,
            Taipei_check, Taoyuan_check, Keelung_check,
            Taichung_check, Yunlin_check, Changhua_check, Nantou_check,
            Kaohsuing_check, Chiayi_check, tainan_check, Pingtung_check;

    String result;
    String ip;
    public searchpage Search_Page;
    String login_url;
    Context context;

    public SearchListBackgroundWorker(Context ctx) {
        context = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... params) {
        type = params[0];
        this.ip = params[1];
        switch (type) {
            case "first":
                login_url = ip + "/StreetApp_FinalProject2020/search/search_list.php";
                try {
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");

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

                break;
            case "text":
                login_url = ip + "/StreetApp_FinalProject2020/search/search_list_word.php";

                search_word = params[2];

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

                    String post_data = URLEncoder.encode("search_word", "UTF-8") + "=" + URLEncoder.encode(search_word, "UTF-8");
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case "check":
                login_url = ip + "/StreetApp_FinalProject2020/search/search_list_check.php";

                visual_arts_check = params[2];
                performance_arts_check = params[3];
                ideas_arts_check = params[4];
                Taipei_check = params[5];
                Taoyuan_check = params[6];
                Keelung_check = params[7];
                Taichung_check = params[8];
                Yunlin_check = params[9];
                Changhua_check = params[10];
                Nantou_check = params[11];
                Kaohsuing_check = params[12];
                Chiayi_check = params[13];
                tainan_check = params[14];
                Pingtung_check = params[15];

                try {
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");

                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                    String post_data =
                            URLEncoder.encode("visual_arts_check", "UTF-8") + "=" + URLEncoder.encode(visual_arts_check, "UTF-8") + "&" +
                                    URLEncoder.encode("performance_arts_check", "UTF-8") + "=" + URLEncoder.encode(performance_arts_check, "UTF-8") + "&" +
                                    URLEncoder.encode("ideas_arts_check", "UTF-8") + "=" + URLEncoder.encode(ideas_arts_check, "UTF-8") + "&" +
                                    URLEncoder.encode("Taipei_check", "UTF-8") + "=" + URLEncoder.encode(Taipei_check, "UTF-8") + "&" +
                                    URLEncoder.encode("Taoyuan_check", "UTF-8") + "=" + URLEncoder.encode(Taoyuan_check, "UTF-8") + "&" +
                                    URLEncoder.encode("Keelung_check", "UTF-8") + "=" + URLEncoder.encode(Keelung_check, "UTF-8") + "&" +
                                    URLEncoder.encode("Taichung_check", "UTF-8") + "=" + URLEncoder.encode(Taichung_check, "UTF-8") + "&" +
                                    URLEncoder.encode("Yunlin_check", "UTF-8") + "=" + URLEncoder.encode(Yunlin_check, "UTF-8") + "&" +
                                    URLEncoder.encode("Changhua_check", "UTF-8") + "=" + URLEncoder.encode(Changhua_check, "UTF-8") + "&" +
                                    URLEncoder.encode("Nantou_check", "UTF-8") + "=" + URLEncoder.encode(Nantou_check, "UTF-8") + "&" +
                                    URLEncoder.encode("Kaohsuing_check", "UTF-8") + "=" + URLEncoder.encode(Kaohsuing_check, "UTF-8") + "&" +
                                    URLEncoder.encode("Chiayi_check", "UTF-8") + "=" + URLEncoder.encode(Chiayi_check, "UTF-8") + "&" +
                                    URLEncoder.encode("tainan_check", "UTF-8") + "=" + URLEncoder.encode(tainan_check, "UTF-8") + "&" +
                                    URLEncoder.encode("Pingtung_check", "UTF-8") + "=" + URLEncoder.encode(Pingtung_check, "UTF-8");

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
                break;
        }


        try {
            JSONArray json_search_Array = new JSONArray(result);
            JSONObject json_search_wall_Object = null;

            performer_id = new String[json_search_Array.length()];
            performer_Name = new String[json_search_Array.length()];
            performer_nickname = new String[json_search_Array.length()];
            cityName = new String[json_search_Array.length()];
            performTheme = new String[json_search_Array.length()];
            performerActType = new String[json_search_Array.length()];
            imageUrl = new String[json_search_Array.length()];
            performerFans = new String[json_search_Array.length()];

            for (int i = 0; i < json_search_Array.length(); i++) {
                json_search_wall_Object = json_search_Array.getJSONObject(i);
                //後面的名稱丟資料庫的欄位名稱
                performer_id[i] = json_search_wall_Object.getString("id");
                performer_Name[i] = json_search_wall_Object.getString("performerName");
                performer_nickname[i] = json_search_wall_Object.getString("performer_nickname");
                cityName[i] = json_search_wall_Object.getString("cityName");
                performTheme[i] = json_search_wall_Object.getString("performTheme");
                performerActType[i] = json_search_wall_Object.getString("performerActType");
                imageUrl[i] = json_search_wall_Object.getString("imageUrl");
                performerFans[i] = json_search_wall_Object.getString("performerFans");
            }

        } catch (Exception e) {
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        /*alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");*/
        //       alertDialog.setOnCancelListener((DialogInterface.OnCancelListener) this);
    }

    @Override
    protected void onPostExecute(Void result) {
        if (performer_id != null && performer_id.length > 0 && performer_id[0] != null) {
            Search_Page.Give_Data(performer_id, performer_Name, performer_nickname,
                    cityName, performTheme, performerActType, imageUrl, performerFans);
            Search_Page.give_adapter();

        } else {
            Search_Page.give_empty_adapter();
        }
        this.cancel(true);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
