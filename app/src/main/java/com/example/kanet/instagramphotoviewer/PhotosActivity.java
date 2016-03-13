package com.example.kanet.instagramphotoviewer;

import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.InstagramComment;
import Model.InstagramPhoto;
import Model.InstagramUser;
import Utils.Define;
import cz.msebera.android.httpclient.Header;

public class PhotosActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    public static final String CLIENT_ID="e05c462ebd86446ea48a5af73769b602";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotoAdapter instagramPhotoAdapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        swipeContainer=(SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        photos=new ArrayList<>();
        // Create adapter
        instagramPhotoAdapter=new InstagramPhotoAdapter(this,photos);
        //1. Found list view
        ListView lvPhotos=(ListView)findViewById(R.id.lvPhotos);
        //2. Binding
        lvPhotos.setAdapter(instagramPhotoAdapter);
        //
        fetchPopularPhotos();
        swipeContainer.setOnRefreshListener(this);

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    @Override
    public void onRefresh() {
        fetchPopularPhotos();
    }

    public void fetchPopularPhotos(){
        /*- Popurlar: https://api.instagram.com/v1/media/search?lat=48.858844&lng=2.294351&access_token=ACCESS-TOKEN
            - Response
              - Type: {"data" =>[x] => "type"}{"image" or "video"}
              - URL: {"data" =>[x] => "images" => "standard_resolution" => "url"}
              - Caption: {"data" =>[x] => "caption" => "text"}
              - Author Name: {"data" =>[x] => "user" => "username"}
        */
        String url="https://api.instagram.com/v1/media/popular?client_id="+CLIENT_ID;
        //Create network client
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                /*- Response
                Type: {"data" =>[x] => "type"}{"image" or "video"}
                */
                JSONArray photosJSON = null;
                try {
                    photosJSON = response.getJSONArray("data");
                    instagramPhotoAdapter.clear();
                    for (int i = 0; i < photosJSON.length(); i++) {
                        //get line json object at that position
                        JSONObject photoJSON = photosJSON.getJSONObject(i);
                        //decode the attributes of the json into a data model
                        InstagramPhoto photo = new InstagramPhoto();
                        //Get profile user
                        // Author Name: {"data" =>[x] => "user" => "username"}
                        photo.user=new InstagramUser();
                        photo.user.username = photoJSON.getJSONObject("user").getString("username");
                        // Profile picture: {"data" =>[x] => "user" => "profile_picture"}
                        photo.user.imageAvar = photoJSON.getJSONObject("user").getString("profile_picture");
                        photo.user.user_id = photoJSON.getJSONObject("user").getLong("id");
                        //Create time: {"data" =>[x] => "caption" => "created_time"}
                        photo.createTime = photoJSON.getLong("created_time");
                        //Count comment:
                        fetchLike(photo);
                        //  Caption: {"data" =>[x] => "caption" => "text"}
                        if(photoJSON.optJSONObject("caption") != null){
                            photo.caption =  photoJSON.getJSONObject("caption").getString("text");
                        }
                        //URL: {"data" =>[x] => "images" => "standard_resolution" => "url"}
                        photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        //Height
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        //Like count
                        //photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                        //Get media_id
                        photo.mediaId = photoJSON.getString("id");
                        //Get comment by media_id
                        fetchComment(photo);
                        fetchLike(photo);
                        photos.add(photo);
                    }
                    instagramPhotoAdapter.addAll(photos);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);
                instagramPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {

            }
        });
    }
    public void fetchComment(final InstagramPhoto photo){
        String url="https://api.instagram.com/v1/media/"+photo.mediaId+"/comments?client_id="+CLIENT_ID;
        //Create network client
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray commentsJSON = null;
                try{
                    commentsJSON = response.getJSONArray("data");
                    photo.countComments=commentsJSON.length();
                    int maxLenght= Define.MAX_COMMENT_PHOTOS;
                    if (commentsJSON.length()<maxLenght)
                        maxLenght=commentsJSON.length();
                    if (commentsJSON!=null){
                        photo.comments=new ArrayList<>();
                        photo.comments.clear();
                        for (int i=0;i<maxLenght;i++){
                            //get line json object at that position
                            JSONObject photoJSON = commentsJSON.getJSONObject(i);
                            InstagramComment comment=new InstagramComment();
                            // Commet : {"data" =>[x] => "text"}
                            comment.text= photoJSON.getString("text");
                            // Created_time : {"data" =>[x] => "created_time"}
                            comment.created_time= photoJSON.getLong("created_time");
                            //create object user name
                            comment.fromUser=new InstagramUser();
                            // username : {"data" =>[x] => "from" =>"username"}
                            comment.fromUser.username= photoJSON.getJSONObject("from").getString("username");
                            // id: {"data" =>[x] => "from" =>"id"}
                            comment.fromUser.user_id= photoJSON.getJSONObject("from").getLong("id");
                            photo.comments.add(comment);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
    public void fetchLike(final InstagramPhoto photo){
        String url="https://api.instagram.com/v1/media/"+photo.mediaId+"/likes?client_id="+CLIENT_ID;
        //Create network client
        AsyncHttpClient client=new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray likesJSON = null;
                try{
                    likesJSON = response.getJSONArray("data");
                    photo.countLikes=likesJSON.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
