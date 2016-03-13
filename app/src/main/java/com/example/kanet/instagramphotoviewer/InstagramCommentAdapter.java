package com.example.kanet.instagramphotoviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import Model.InstagramComment;
import Model.InstagramPhoto;
import Utils.TimeStamp;

/**
 * Created by Kanet on 3/13/2016.
 */
public class InstagramCommentAdapter extends ArrayAdapter<InstagramComment> {

    public InstagramCommentAdapter(Context context, List<InstagramComment> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderComment viewHolder;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_comment, null);

            viewHolder=new ViewHolderComment();
            viewHolder.ivUserPicture=(ImageView)convertView.findViewById(R.id.ivUserPicture);
            viewHolder.tvContent=(TextView)convertView.findViewById(R.id.tvContent);
            viewHolder.tvTimeStamp=(TextView)convertView.findViewById(R.id.tvTime);

            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolderComment)convertView.getTag();
        }
        final InstagramComment comment=getItem(position);
        if (comment!=null){
            Picasso.with(getContext()).load(comment.fromUser.imageAvar).placeholder(R.drawable.ic_launcher).into(viewHolder.ivUserPicture);
            String sTime=TimeStamp.getDistanceTime(comment.created_time, TimeStamp.FULL_TIME);
            viewHolder.tvTimeStamp.setText(sTime);
            final String commentText=comment.fromUser.username + " " +comment.text;
            SpannableString ss=new SpannableString(commentText);
            callCommentAcitivty(ss,0,comment.fromUser.username.length(),comment.fromUser.user_id);
            viewHolder.tvContent.setText(ss);
        }
        return convertView;
    }

    private void callCommentAcitivty(SpannableString ss,int start,int end, final long userid){
        ClickableSpan clickableSpan=new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent=new Intent(getContext(),UserActivity.class);
                intent.putExtra("userid",userid);
                getContext().startActivity(intent);
            }
        };
        ss.setSpan(clickableSpan,start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
