package Model;

import java.util.ArrayList;

/**
 * Created by Kanet on 3/11/2016.
 */
public class InstagramPhoto{
    public InstagramUser user;
    public String caption;
    public String imageUrl;
    public String imageUser;
    public int imageHeight;
    public ArrayList<InstagramUser> likes;
    public long createTime;
    public String mediaId;
    public int countComments;
    public int countLikes;
    public ArrayList<InstagramComment> comments;
}
