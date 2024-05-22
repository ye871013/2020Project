package com.example.tomato.pagefragments;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tomato.R;
import com.example.tomato.UserInfoConfig;
import com.example.tomato.background.SearchListBackgroundWorker;
import com.example.tomato.usersees.UserSeesArtistPage;
import com.squareup.picasso.Picasso;

public class searchpage extends Fragment {
    SearchListBackgroundWorker searchListBackgroundWorker;
    searchpage search_page;

    String[] performer_id, performer_Name, performer_nickname,
            cityName, performTheme, performerActType, imageUrl, performerFans;

    private ListView searchList;
    private SearchView searchView;
    private ImageView btn_select;

    private CheckBox visual_arts, performance_arts, ideas_arts;
    CheckBox Taipei, Taoyuan, Keelung,
            Taichung, Yunlin, Changhua, Nantou,
            Kaohsuing, Chiayi, tainan, Pingtung;


    String visual_arts_check, performance_arts_check, ideas_arts_check,
            Taipei_check, Taoyuan_check, Keelung_check,
            Taichung_check, Yunlin_check, Changhua_check, Nantou_check,
            Kaohsuing_check, Chiayi_check, tainan_check, Pingtung_check;

    String type;
    String Login_user;

    private LinearLayout selection;
    ArrayAdapter<String> arrayAdapter;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.searchpage, container, false);

        search_page = this;

        searchList = view.findViewById(R.id.search_ListView);
        searchView = view.findViewById(R.id.SearchView_content);


        //宣告顯示點選方框的layout
        selection = view.findViewById(R.id.selection);
        btn_select = view.findViewById(R.id.btn_select);
        Login_user = UserInfoConfig.getConfig(requireActivity(),"UserInfo","ID","");

        this.give_loading_animation();

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        searchListBackgroundWorker = new SearchListBackgroundWorker(getActivity());
        searchListBackgroundWorker.Search_Page = search_page;
        type = "first";
        searchListBackgroundWorker.execute(type,
                UserInfoConfig.getConfig(getActivity(),"link","url","localhost"));


        btn_select.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selection.getVisibility() == View.VISIBLE) {
                    selection.setVisibility(View.GONE);
                } else
                    selection.setVisibility(View.VISIBLE);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String query) {
                type = "text";
                searchListBackgroundWorker = new SearchListBackgroundWorker(getActivity());
                searchListBackgroundWorker.Search_Page = search_page;
                searchListBackgroundWorker.execute(type,
                        UserInfoConfig.getConfig(getActivity(),"link","url","localhost"),
                        query
                        );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                type = "text";
                searchListBackgroundWorker = new SearchListBackgroundWorker(getActivity());
                searchListBackgroundWorker.Search_Page = search_page;
                searchListBackgroundWorker.execute(type,
                        UserInfoConfig.getConfig(getActivity(),"link","url","localhost"),
                        newText
                        );
                return false;
            }
        });


        //宣告表演項目的CheckBox
        visual_arts = view.findViewById(R.id.visual_arts);
        performance_arts = view.findViewById(R.id.performance_arts);
        ideas_arts = view.findViewById(R.id.ideas_arts);

        //宣告表演地區的CheckBox
        Taipei = view.findViewById(R.id.Taipei);
        Taoyuan = view.findViewById(R.id.Taoyuan);
        Keelung = view.findViewById(R.id.Keelung);
        Taichung = view.findViewById(R.id.Taichung);
        Yunlin = view.findViewById(R.id.Yunlin);
        Changhua = view.findViewById(R.id.Changhua);
        Nantou = view.findViewById(R.id.Nantou);
        Kaohsuing = view.findViewById(R.id.Kaohsiung);
        Chiayi = view.findViewById(R.id.Chiayi);
        tainan = view.findViewById(R.id.tainan);
        Pingtung = view.findViewById(R.id.Pingtung);

        visual_arts.setOnCheckedChangeListener(checkedChangeListener);
        performance_arts.setOnCheckedChangeListener(checkedChangeListener);
        ideas_arts.setOnCheckedChangeListener(checkedChangeListener);
        Taipei.setOnCheckedChangeListener(checkedChangeListener);
        Taoyuan.setOnCheckedChangeListener(checkedChangeListener);
        Keelung.setOnCheckedChangeListener(checkedChangeListener);
        Taichung.setOnCheckedChangeListener(checkedChangeListener);
        Yunlin.setOnCheckedChangeListener(checkedChangeListener);
        Changhua.setOnCheckedChangeListener(checkedChangeListener);
        Nantou.setOnCheckedChangeListener(checkedChangeListener);
        Kaohsuing.setOnCheckedChangeListener(checkedChangeListener);
        Chiayi.setOnCheckedChangeListener(checkedChangeListener);
        tainan.setOnCheckedChangeListener(checkedChangeListener);
        Pingtung.setOnCheckedChangeListener(checkedChangeListener);


        return view;
    }

    void give_loading_animation() {
        arrayAdapter = new VideoWall_loading_page_ArrayAdapter(getActivity());
        searchList.setAdapter(arrayAdapter);
    }

    private CheckBox.OnCheckedChangeListener checkedChangeListener =
            new CheckBox.OnCheckedChangeListener() {


                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    visual_arts_check = "";
                    performance_arts_check = "";
                    ideas_arts_check = "";
                    Taipei_check = "";
                    Taoyuan_check = "";
                    Keelung_check = "";
                    Taichung_check = "";
                    Yunlin_check = "";
                    Changhua_check = "";
                    Nantou_check = "";
                    Kaohsuing_check = "";
                    Chiayi_check = "";
                    tainan_check = "";
                    Pingtung_check = "";

                    if (visual_arts.isChecked()) {
                        visual_arts_check = "視覺藝術";
                    }
                    if (performance_arts.isChecked()) {
                        performance_arts_check = "表演藝術";
                    }
                    if (ideas_arts.isChecked()) {
                        ideas_arts_check = "創意工藝";
                    }
                    if (Taipei.isChecked()) {
                        Taipei_check = "臺北市";
                    }
                    if (Taoyuan.isChecked()) {
                        Taoyuan_check = "桃園市";
                    }
                    if (Keelung.isChecked()) {
                        Keelung_check = "基隆市";
                    }
                    if (Taichung.isChecked()) {
                        Taichung_check = "臺中市";
                    }
                    if (Yunlin.isChecked()) {
                        Yunlin_check = "雲林縣";
                    }
                    if (Changhua.isChecked()) {
                        Changhua_check = "彰化縣";
                    }
                    if (Nantou.isChecked()) {
                        Nantou_check = "南投縣";
                    }
                    if (Kaohsuing.isChecked()) {
                        Kaohsuing_check = "高雄市";
                    }
                    if (Chiayi.isChecked()) {
                        Chiayi_check = "嘉義縣";
                    }
                    if (tainan.isChecked()) {
                        tainan_check = "臺南市";
                    }
                    if (Pingtung.isChecked()) {
                        Pingtung_check = "屏東縣";
                    }
                    type = "check";

                    searchListBackgroundWorker = new SearchListBackgroundWorker(getActivity());
                    searchListBackgroundWorker.Search_Page = search_page;

                    searchListBackgroundWorker.execute(type,
                            UserInfoConfig.getConfig(getActivity(),"link","url","localhost"),
                            visual_arts_check, performance_arts_check, ideas_arts_check,
                            Taipei_check, Taoyuan_check, Keelung_check,
                            Taichung_check, Yunlin_check, Changhua_check, Nantou_check,
                            Kaohsuing_check, Chiayi_check, tainan_check, Pingtung_check
                            );
                }
            };

    public void Give_Data(String[] performer_id, String[] performer_Name,
                          String[] performer_nickname, String[] cityName,
                          String[] performTheme, String[] performerActType,
                          String[] imageUrl, String[] performerFans) {
        this.performer_id = performer_id;
        this.performer_Name = performer_Name;
        this.performer_nickname = performer_nickname;
        this.cityName = cityName;
        this.performTheme = performTheme;
        this.performerActType = performerActType;
        this.imageUrl = imageUrl;
        this.performerFans = performerFans;
    }

    public void give_adapter() {
        arrayAdapter = new SearchRowAdapter(getActivity(), performer_id, performer_Name, performer_nickname,
                cityName, performTheme, performerActType, imageUrl, Login_user, performerFans);
        searchList.setAdapter(arrayAdapter);

        searchListBackgroundWorker.cancel(true);
    }

    public void give_empty_adapter() {
        arrayAdapter = new EmptyRowAdapter(getActivity());
        searchList.setAdapter(arrayAdapter);

        searchListBackgroundWorker.cancel(true);
    }

}

class SearchWall_loading_page_ArrayAdapter extends ArrayAdapter<String> {
    private Context context;

    SearchWall_loading_page_ArrayAdapter(@NonNull Context c) {
        super(c, R.layout.loading);
        this.context = c;
    }


    public int getCount() {
        return 1;
    }


    public View getView(final int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        Null_ViewHolder null_viewHolder;

        if (convertView == null) {
            null_viewHolder = new Null_ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.loading, null, true);

            null_viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);

            convertView.setTag(null_viewHolder);
        } else {
            null_viewHolder = (Null_ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private static class Null_ViewHolder {
        ProgressBar progressBar;
    }
}

class EmptyRowAdapter extends ArrayAdapter<String> {
    private Context context;

    public int getCount() {
        return 1;
    }

    EmptyRowAdapter(Context c) {
        super(c, R.layout.no_rows);
        this.context = c;
    }

    public View getView(final int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        final Null_ViewHolder null_viewHolder;

        if (convertView == null) {
            null_viewHolder = new Null_ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.no_rows, null, true);

            null_viewHolder.null_text = (TextView) convertView.findViewById(R.id.null_text);

            convertView.setTag(null_viewHolder);
        } else {
            null_viewHolder = (Null_ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private static final class Null_ViewHolder {
        TextView null_text;
    }
}

class SearchRowAdapter extends ArrayAdapter<String> {
    private Context context;
    //總共會傳藝人id、表演類別、名稱、地區、照片
    private String[] performer_id;
    private String[] performerActType;
    private String[] performTheme;
    private String[] performer_Name;
    private String[] performer_nickname;
    private String[] imageUrl;
    private String[] cityName;
    private String Login_user;
    private String[] performerFans;

    SearchRowAdapter(Context c, String[] performer_id, String[] performer_Name,
                     String[] performer_nickname, String[] cityName,
                     String[] performTheme, String[] performerActType, String[] imageUrl,
                     String Login_user, String[] performerFans) {
        super(c, R.layout.viedorow, R.id.poster_name, performer_id);
        this.context = c;

        this.performer_id = performer_id;
        this.performer_Name = performer_Name;
        this.performer_nickname = performer_nickname;
        this.cityName = cityName;
        this.performTheme = performTheme;
        this.performerActType = performerActType;
        this.imageUrl = imageUrl;
        this.Login_user = Login_user;
        this.performerFans = performerFans;
    }

    @Override
    public int getCount() {
  /*      if (performer_id.length == 0 || performer_id[0] == null) {
            return 1;
        } else*/
        return performer_id.length;
    }

    public View getView(final int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;

        final ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.search_row, null, true);
            viewHolder.performer_picture = (ImageView) convertView.findViewById(R.id.performer_picture);
            viewHolder.performance_type = (TextView) convertView.findViewById(R.id.performance_type);
            viewHolder.performer_name = (TextView) convertView.findViewById(R.id.performer_name);
            viewHolder.performer_city = (TextView) convertView.findViewById(R.id.performer_city);
            viewHolder.search_row = (RelativeLayout) convertView.findViewById(R.id.search_row);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.performance_type.setText(performerActType[position]);

        if (performer_nickname[position] == null || performer_nickname[position].equals("")) {
            if (performer_Name[position].length() < 6)
                viewHolder.performer_name.setTextSize(35);
            else if (performer_Name[position].length() < 11)
                viewHolder.performer_name.setTextSize(25);
            else
                viewHolder.performer_name.setTextSize(15);
            viewHolder.performer_name.setText(performer_Name[position]);
        } else {
            if (performer_nickname[position].length() < 6)
                viewHolder.performer_name.setTextSize(35);
            else if (performer_nickname[position].length() < 11)
                viewHolder.performer_name.setTextSize(25);
            else
                viewHolder.performer_name.setTextSize(15);
            viewHolder.performer_name.setText(performer_nickname[position]);
        }


        viewHolder.performer_city.setText(cityName[position]);

        viewHolder.search_row.setOnClickListener(new RelativeLayout.OnClickListener() {
            @Override
            public void onClick(View v) {
                String choose_username = performer_id[position];

                Intent intent = new Intent(context, UserSeesArtistPage.class);
                intent.putExtra("post_user_id", choose_username);
                intent.putExtra("post_user_img", imageUrl[position]);
                if (performer_nickname[position] == null || performer_nickname[position].equals("")) {
                    intent.putExtra("post_user_name", performer_Name[position]);
                }
                else{
                    intent.putExtra("post_user_name", performer_nickname[position]);
                }
                intent.putExtra("poster_fans", performerFans[position]);
                intent.putExtra("user_follow_status", "0");
                intent.putExtra("user_id", Login_user);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(context, R.anim.animate_swipe_left_enter, R.anim.animate_fade_exit);
                context.startActivity(intent, options.toBundle());

            }
        });

        if (this.imageUrl[position] == null || this.imageUrl[position].equals("\r")) {
            viewHolder.performer_picture.setImageResource(R.drawable.person_110935);
            //loadImageFromUrl("https://ncyu-webdesign.000webhostapp.com/StreetArtist/Pictures/download.jpg", viewHolder.performer_picture);
        } else
            loadImageFromUrl(imageUrl[position], viewHolder.performer_picture);


        return convertView;
    }

    private static final class ViewHolder {
        ImageView performer_picture;
        TextView performance_type, performer_name, performer_city;
        RelativeLayout search_row;
    }

    private void loadImageFromUrl(String x, ImageView y) {
        Picasso.with(context).load(x).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(y, new com.squareup.picasso.Callback() {

                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });
    }
}