package com.tranquangduy.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tranquangduy.loadvideo.MainActivity;
import com.tranquangduy.loadvideo.R;
import com.tranquangduy.model.Root;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class CustomAdapter extends BaseAdapter {
    private Activity context;
    private int itemLayout;
    private List<Root.Items> listItems;


    public CustomAdapter(Activity context, int itemLayout, List<Root.Items> listItems) {
        this.context = context;
        this.itemLayout = itemLayout;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewOder viewOder;

        if(convertView == null){
            viewOder = new ViewOder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(itemLayout, null);

            viewOder.imgThumbnails = convertView.findViewById(R.id.imgThumbnails);
            viewOder.txtChannelTitle = convertView.findViewById(R.id.txtChannelTitle);
            viewOder.txtTitle = convertView.findViewById(R.id.txtTitle);

            convertView.setTag(viewOder);

        }else {
            viewOder = (ViewOder) convertView.getTag();
        }

        Root.Items t = listItems.get(position);
        viewOder.txtChannelTitle.setText(t.snippet.channelTitle);
        viewOder.txtTitle.setText(t.snippet.title);
        Glide.with(context).load(t.snippet.thumbnails.high.url).into(viewOder.imgThumbnails);

        return convertView;
    }


    public static class ViewOder{
        ImageView imgThumbnails;
        TextView txtTitle, txtChannelTitle;
    }




}
