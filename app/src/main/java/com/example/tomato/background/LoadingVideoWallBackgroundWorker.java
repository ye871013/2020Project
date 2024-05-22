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
import java.util.Map;
import java.util.Set;

public class LoadingVideoWallBackgroundWorker extends AsyncTask<String, Void, Void> {

    public String[] save_post_id, save_poster_nickname, save_post_time, save_post_content,
            save_post_files_url, save_post_type, save_post_likes, save_post_username, save_poster_imge,
            user_likes_post_id, save_user_seen, post_user_type, poster_fans, user_follow_status;
    public videowallpage videowallpage_row;
    private String use_url, role;
    private String Login_user;
    String ip;
    Context context;
    AlertDialog alertDialog;

    public LoadingVideoWallBackgroundWorker(Context ctx) {
        context = ctx;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected Void doInBackground(String... params) {
        Login_user = params[0];
        role = params[1];
        //選擇讀取民眾版本還是藝人版本
        ip = params[2];
        if (role.equals("User"))
            use_url = ip + "/StreetApp_FinalProject2020/videowall/user_videowall.php";
        else
            use_url = ip + "/StreetApp_FinalProject2020/videowall/performer_videowall.php";
        String result = "";
        //本機連線測試用網址
        //讀資料庫

        try {
            URL url = new URL(use_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            String post_data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(Login_user, "UTF-8");
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
        //將post_info的所有資料抓近來陣列
        try {
            JSONArray json_video_wall_Array = new JSONArray(result);
            JSONObject json_video_wall_Object = null;

            save_post_id = new String[json_video_wall_Array.length()];
            save_post_files_url = new String[json_video_wall_Array.length()];
            save_post_likes = new String[json_video_wall_Array.length()];
            save_post_content = new String[json_video_wall_Array.length()];
            save_post_type = new String[json_video_wall_Array.length()];
            save_post_time = new String[json_video_wall_Array.length()];
            save_user_seen = new String[json_video_wall_Array.length()];
            user_likes_post_id = new String[json_video_wall_Array.length()];

            save_poster_nickname = new String[json_video_wall_Array.length()];
            save_post_username = new String[json_video_wall_Array.length()];
            save_poster_imge = new String[json_video_wall_Array.length()];

            post_user_type = new String[json_video_wall_Array.length()];
            poster_fans = new String[json_video_wall_Array.length()];
            user_follow_status = new String[json_video_wall_Array.length()];

            for (int i = 0; i < json_video_wall_Array.length(); i++) {
                json_video_wall_Object = json_video_wall_Array.getJSONObject(i);
                //後面的名稱丟資料庫的欄位名稱
                //貼文ID
                save_post_id[i] = json_video_wall_Object.getString("post_id");

                //貼文時間
                save_post_time[i] = json_video_wall_Object.getString("post_time");

                //貼文內容
                save_post_content[i] = json_video_wall_Object.getString("post_content");

                //貼文影片網址
                save_post_files_url[i] = json_video_wall_Object.getString("videoURL");

                //貼文格式
                save_post_type[i] = json_video_wall_Object.getString("post_type");

                //貼文起飛數
                save_post_likes[i] = json_video_wall_Object.getString("fly");

                //貼文觀看數
                save_user_seen[i] = json_video_wall_Object.getString("seen");

                //貼文者名稱或暱稱
                if (json_video_wall_Object.getString("User_Name").trim().equals("null")){
                    if (json_video_wall_Object.getString("performer_nickname").trim().equals("null") ||
                            json_video_wall_Object.getString("performer_nickname").trim().equals(""))
                        save_poster_nickname[i] = json_video_wall_Object.getString("performerName");
                    else
                        save_poster_nickname[i] = json_video_wall_Object.getString("performer_nickname");
                }
                else
                    save_poster_nickname[i] = json_video_wall_Object.getString("User_Name");

                //貼文者ID
                if (json_video_wall_Object.getString("User_ID").trim().equals("null")) {
                    save_post_username[i] = json_video_wall_Object.getString("id");
                    post_user_type[i] = "performer";
                }
                else {
                    save_post_username[i] = json_video_wall_Object.getString("User_ID");
                    post_user_type[i] = "user";
                }

                //貼文者粉絲數量
                if (json_video_wall_Object.getString("performerFans").trim().equals("null"))
                    poster_fans[i] = "";
                else
                    poster_fans[i] = json_video_wall_Object.getString("performerFans");

                //貼文者大頭貼
                if (json_video_wall_Object.getString("User_ImgURL").trim().equals("null"))
                    save_poster_imge[i] = json_video_wall_Object.getString("imageUrl");
                else
                    save_poster_imge[i] = json_video_wall_Object.getString("User_ImgURL");

                //使用者是否追蹤這個藝人
                if (json_video_wall_Object.getString("follow").trim().equals("null") ||
                        json_video_wall_Object.getString("follow").trim().equals(""))
                    user_follow_status[i] = "0";
                else
                    user_follow_status[i] = "1";

                //登錄的使用者是否喜歡這篇貼文
                if (json_video_wall_Object.getString("likes").trim().equals("null") ||
                        json_video_wall_Object.getString("likes").trim().equals(""))
                    user_likes_post_id[i] = "0";
                else
                    user_likes_post_id[i] = "1";
            }



//            return save_post_files_url;
        }catch (JSONException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            //result = e.getMessage();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        /*alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");*/
        //       alertDialog.setOnCancelListener((DialogInterface.OnCancelListener) this);
    }

    /* protected void onPostExecute(String test) {
    alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
         alertDialog.setMessage(test);
         alertDialog.show();
     }
 */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPostExecute(Void result) {

        /*
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Login Status");
        alertDialog.setMessage(this.save_post_files_url[0]);
        alertDialog.show();

         */

        videowallpage_row.Row_data(this.save_post_id, this.save_poster_nickname, this.save_post_time,
                this.save_post_content, this.save_post_files_url, this.save_post_type, this.save_post_likes,
                this.save_post_username, this.save_poster_imge, this.user_likes_post_id, this.save_user_seen,
                this.post_user_type, this.poster_fans, this.user_follow_status);


//        this.videowallpage_row.testforStrinf(save_post_files_url[4]);
//        this.videowallpage_row.test(save_poster_nickname.length);

    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

}
