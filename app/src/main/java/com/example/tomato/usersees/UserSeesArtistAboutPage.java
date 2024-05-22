package com.example.tomato.usersees;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tomato.R;
import com.example.tomato.UserInfoConfig;
import com.example.tomato.background.UserSeesArtistAboutBackgroundWorker;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;


public class UserSeesArtistAboutPage extends AppCompatActivity {
    private SlidrInterface slidr;
    TextView ActType, PerformanceCity, Social_link, Introduce;
    UserSeesArtistAboutBackgroundWorker userSeesArtistAboutBackgroundWorker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sees_artist_about_me);

        //設定隱藏標題
        getSupportActionBar().hide();
        ActType = findViewById(R.id.ActType);
        PerformanceCity = findViewById(R.id.Performance_City);
        Social_link = findViewById(R.id.Social_link);
        Introduce = findViewById(R.id.Introduce);


        Intent intent = getIntent();
        String Poster_username = intent.getStringExtra("message");

        userSeesArtistAboutBackgroundWorker = new UserSeesArtistAboutBackgroundWorker(getApplicationContext());
        userSeesArtistAboutBackgroundWorker.execute(Poster_username,
                UserInfoConfig.getConfig(getApplicationContext(),"link","url","localhost"));
        userSeesArtistAboutBackgroundWorker.About = this;

        slidr = Slidr.attach(this);
    }

    public void change_text(String ActType, String Performance_City, String Social_Link, String Introduce){
        this.ActType.setText(ActType);
        this.PerformanceCity.setText(Performance_City);
        this.Social_link.setText(Social_Link);
        this.Introduce.setText(Introduce);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}



