package com.example.kanet.instagramphotoviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class PhotosActivity extends AppCompatActivity {
    public static final String CLIENT_ID="e05c462ebd86446ea48a5af73769b602";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotoAdapter instagramPhotoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        photos=new ArrayList<>();
        // Create adapter
        instagramPhotoAdapter=new InstagramPhotoAdapter(this,photos);
       //1. Found list view
        ListView lvPhotos=(ListView)findViewById(R.id.lvPhotos);
        //2. Binding
        lvPhotos.setAdapter(instagramPhotoAdapter);

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
        client.get(url,null,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                /*- Response
                Type: {"data" =>[x] => "type"}{"image" or "video"}
                */
                JSONArray photosJSON=null;
                try {
                    photosJSON=response.getJSONArray("data");
                    for (int i=0;i<photosJSON.length();i++){
                        //get line json object at that position
                        JSONObject photoJSON=photosJSON.getJSONObject(i);
                        //decode the attributes of the json into a data model
                        InstagramPhoto photo=new InstagramPhoto();
                        // Author Name: {"data" =>[x] => "user" => "username"}
                        photo.username=photoJSON.getJSONObject("user").getString("username");
                        //  Caption: {"data" =>[x] => "caption" => "text"}
                        photo.caption=photoJSON.getJSONObject("caption").getString("text");
                        //URL: {"data" =>[x] => "images" => "standard_resolution" => "url"}
                        photo.imageUrl=photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        //Height
                        photo.imageHeight=photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        //Like count
                        photo.likesCount=photoJSON.getJSONObject("likes").getInt("count");
                        photos.add(photo);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
                instagramPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,String response, Throwable throwable) {

            }
        });
    }
}
