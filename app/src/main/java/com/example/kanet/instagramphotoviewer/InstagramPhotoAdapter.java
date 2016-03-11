package com.example.kanet.instagramphotoviewer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Kanet on 3/11/2016.
 */
public class InstagramPhotoAdapter extends ArrayAdapter<InstagramPhoto>{
    public InstagramPhotoAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

   @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderPhoto viewHolder;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_photo, null);

            viewHolder=new ViewHolderPhoto();
            viewHolder.tvUsername=(TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvCaption=(TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.tvCountLike=(TextView) convertView.findViewById(R.id.tvCountLike);
            viewHolder.ivPhoto=(ImageView)convertView.findViewById(R.id.ivPhoto);

            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolderPhoto)convertView.getTag();
        }
        InstagramPhoto photo=getItem(position);
        if (photo!=null){
            viewHolder.tvUsername.setText(photo.username);
            viewHolder.tvCaption.setText(photo.caption);
            viewHolder.tvCountLike.setText(String.valueOf(photo.likesCount)+ "likes");
            Picasso.with(getContext()).load(photo.imageUrl).into(viewHolder.ivPhoto);
        }

        return convertView;
    }
}
