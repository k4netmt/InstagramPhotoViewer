package com.example.kanet.instagramphotoviewer;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.InstagramComment;
import android.widget.AbsListView;
import Model.InstagramUser;
import Utils.Define;
import cz.msebera.android.httpclient.Header;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{

    ArrayList<InstagramComment> comments;
    InstagramCommentAdapter commentAdapter;
    ListView lvComments;
    TextView tvLoadMore;
    int max_lenght,limit_lenght,start=0;
    final int count_items=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        String media_id=getIntent().getStringExtra("media_id");
        comments= new ArrayList<>();

        lvComments=(ListView)findViewById(R.id.lvComments);
        //tvLoadMore=(TextView)findViewById(R.id.tvLoadMore);
        //tvLoadMore.setOnClickListener(this);
        //Set event for tvLoadCore
        //setLoadMore();
        commentAdapter=new InstagramCommentAdapter(this,comments);
        lvComments.setAdapter(commentAdapter);
        fetchCommentFirst(media_id);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.tvLoadMore){
            limit_lenght=count_items;
            if (limit_lenght<count_items)
                tvLoadMore.setVisibility(View.VISIBLE);
            else
                limit_lenght=count_items+start;
        }
    }


    public void fetchCommentFirst(final String mediaId){
        String url="https://api.instagram.com/v1/media/"+mediaId+"/comments?client_id="+ Define.CLIENT_ID;
        //Create network client
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray commentsJSON = null;
                try {
                    commentsJSON = response.getJSONArray("data");
                    max_lenght = commentsJSON.length();
                    for (int i = 0; i < commentsJSON.length(); i++) {
                        //get line json object at that position
                        JSONObject commentJSON = commentsJSON.getJSONObject(i);
                        InstagramComment comment = new InstagramComment();
                        // Commet : {"data" =>[x] => "text"}
                        comment.text = commentJSON.getString("text");

                        // Created_time : {"data" =>[x] => "created_time"}
                        comment.created_time = commentJSON.getLong("created_time");
                        //create object user name
                        comment.fromUser = new InstagramUser();
                        // username : {"data" =>[x] => "from" =>"username"}
                        comment.fromUser.username = commentJSON.getJSONObject("from").getString("username");
                        // id: {"data" =>[x] => "from" =>"id"}
                        comment.fromUser.user_id = commentJSON.getJSONObject("from").getLong("id");
                        comment.fromUser.imageAvar = commentJSON.getJSONObject("from").getString("profile_picture");
                        comments.add(comment);
                    }
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
