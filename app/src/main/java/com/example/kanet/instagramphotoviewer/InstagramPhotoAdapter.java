package com.example.kanet.instagramphotoviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import Model.InstagramComment;
import Model.InstagramPhoto;
import Utils.TimeStamp;

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
            viewHolder.tvCountLike=(TextView) convertView.findViewById(R.id.tvCountLikes);
            viewHolder.ivPhoto=(ImageView)convertView.findViewById(R.id.ivPhoto);
            //viewHolder.tvCountComment=(TextView)convertView.findViewById(R.id.tvCountComment);
            viewHolder.ivUserPicture=(ImageView)convertView.findViewById(R.id.ivUserPicture);
            viewHolder.llComments=(LinearLayout)convertView.findViewById(R.id.llCommets);
            viewHolder.tvTimeStamp=(TextView)convertView.findViewById(R.id.tvTimeStamp);

            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolderPhoto)convertView.getTag();
        }
        final InstagramPhoto photo=getItem(position);
        if (photo!=null){
            SpannableString ssUsername=new SpannableString(photo.user.username);
            callUserAcitivty(ssUsername,0,photo.user.username.length(),photo.user.user_id);
            viewHolder.tvUsername.setText(ssUsername);

            viewHolder.tvCaption.setText(photo.caption);

            Picasso.with(getContext()).load(photo.imageUrl).placeholder(R.drawable.ic_launcher).into(viewHolder.ivPhoto);
            Picasso.with(getContext()).load(photo.imageUser).placeholder(R.drawable.ic_launcher).into(viewHolder.ivUserPicture);
            String time= TimeStamp.getDistanceTime(photo.createTime,TimeStamp.CHARACTER_TIME);
            String sCountLike;
            if (photo.countLikes==0){
                sCountLike="0 like";
            }else{
                if (photo.countLikes==1)
                    sCountLike="1 like";
                else
                    sCountLike=String.valueOf(photo.countLikes)+" likes";
            }
            SpannableString ssLike=new SpannableString(sCountLike);
            ssLike.setSpan(new ForegroundColorSpan(Color.BLUE),0,sCountLike.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.tvCountLike.setText(ssLike);
            viewHolder.tvTimeStamp.setText(time);
            if (photo.comments!=null && photo.comments.size()>0){
                viewHolder.llComments.removeAllViews();
                //viewHolder.tvCountComment.setText(String.valueOf(photo.comments.size())+" comments");
                TextView tvViewComment=new TextView(getContext());
                tvViewComment.setText("View all "+String.valueOf(photo.countComments)+" comments");
                tvViewComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), CommentActivity.class);
                        intent.putExtra("media_id", photo.mediaId);
                        getContext().startActivity(intent);
                    }
                });
                viewHolder.llComments.addView(tvViewComment);
                int nMaxComment=2;
                if (photo.comments.size()<nMaxComment)
                    nMaxComment=photo.comments.size();
                for (int i=0;i<nMaxComment;i++){
                    final InstagramComment comment=photo.comments.get(i);
                    TextView tvCommet=new TextView(getContext());
                    final String commentText=comment.fromUser.username + " " +comment.text;
                    SpannableString ss=new SpannableString(commentText);
                    callUserAcitivty(ss, 0, comment.fromUser.username.length(), comment.fromUser.user_id);
                    int start=comment.fromUser.username.length();
                    for (;start<commentText.length();){
                        int startTemp=commentText.indexOf("@",start);
                        if (startTemp!=-1){
                            int end=commentText.indexOf(" ",startTemp);
                            if (end ==-1)
                                end=commentText.length();
                            callUserAcitivty(ss, startTemp, end, comment.fromUser.user_id);
                            start=end;
                        } else {
                            break;
                        }
                    }
                    tvCommet.setText(ss);
                    tvCommet.setMovementMethod(LinkMovementMethod.getInstance());
                    viewHolder.llComments.addView(tvCommet);
                }
            }
        }

        return convertView;
    }


    private void callUserAcitivty(SpannableString ss,int start,int end, final long userid){
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
