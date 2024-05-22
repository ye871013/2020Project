package com.example.tomato;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomato.usersees.UserSeesArtistPage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity context;
    String[] performer_perform, performer_img, performer_fans, performer_status;
    int i;

    public CustomInfoAdapter(Activity context, String[] performer_img, String[] performer_perform,
                        String[] performer_fans, String[] performer_status){
        this.context = context;
        this.performer_perform = performer_perform;
        this.performer_img = performer_img;
        this.performer_fans = performer_fans;
        this.performer_status = performer_status;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        View view = context.getLayoutInflater().inflate(R.layout.map_open_custom, null);

        final TextView artist_name = (TextView) view.findViewById(R.id.artist_name);
        TextView artist_perform = (TextView) view.findViewById(R.id.artist_perform);
        ImageView map_artist_pictures = (ImageView) view.findViewById(R.id.map_artist_pictures);

        i = Integer.parseInt(marker.getSnippet());
        artist_name.setText(marker.getTitle());
        artist_perform.setText(performer_perform[i]);

        //設定貼文者頭貼功能---------------------------------------------------------------------------------
        if (this.performer_img[i] == null || this.performer_img[i].equals("\r") ||
                this.performer_img.equals(""))
            map_artist_pictures.setImageResource(R.drawable.person_110935);
        else
            loadImageFromUrl(performer_img[i], map_artist_pictures);

        return view;
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
