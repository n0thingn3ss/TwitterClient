package training.twitterclient.models;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Table(name = "Tweets")
public class Tweet extends Model {
    @Column(name = "body")
    public String body;

    @Column(name = "creation_time")
    public long creationTime;

    @Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE)
    public User user;

    @Column(name = "remote_id",
            onUpdate = ForeignKeyAction.CASCADE,
            onDelete = ForeignKeyAction.CASCADE)
    public long uid;

    public String createdAt;

    private static final int ONE_MINUTE = 60;
    private static final int ONE_HOUR = 60 * 60;
    private static final int ONE_DAY = 60 * 60 * 24;
    private static final int ONE_WEEK = 60 * 60 * 24 * 7;

    public Tweet(){
    }

    public Tweet(String body, long creationTime, long uid, User user) {
        this.body = body;
        this.creationTime = creationTime;
        this.createdAt = getFormattedTime(creationTime);
        this.uid = uid;
        this.user = user;
    }

    public static Tweet fromJson(JSONObject json){
        try {
            String body = json.getString("text");
            long uid = json.getLong("id");
            Tweet t = getById(uid);
            if (null != t) return t;
            long creationTime = getCreationTime(json.getString("created_at"));
            User user = User.fromJson(json.getJSONObject("user"));
            Tweet tweet = new Tweet(body, creationTime, uid, user);
            tweet.save();
            return tweet;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Tweet getById(long id){
        return new Select().from(Tweet.class).where("remote_id = ?", id).executeSingle();
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray){
        List<Tweet> tweets = new ArrayList<>(jsonArray.length());
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < jsonArray.length(); i++){
                try {
                    Tweet t = fromJson(jsonArray.getJSONObject(i));
                    if (null != t) {
                        tweets.add(t);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
        return tweets;
    }

    public static List<Tweet> recentItems() {
        return new Select().from(Tweet.class).orderBy("creation_time DESC").limit("300").execute();
    }

    private static long getCreationTime(final String input){
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        long longCreatedAt = 0;
        try {
            longCreatedAt = sf.parse(input).getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return longCreatedAt;
    }

    private static String getFormattedTime(final long longCreatedAt){
        final long now = System.currentTimeMillis()/1000;
        final long elapsedTime = now - longCreatedAt;
        if (elapsedTime < 1) {
            return "1s";
        } else if(elapsedTime < ONE_MINUTE){
            return elapsedTime + "s";
        } else if (elapsedTime < ONE_HOUR){
            return Math.round(elapsedTime/ONE_MINUTE) + "m";
        } else if (elapsedTime < ONE_DAY){
            return Math.round(elapsedTime/ONE_HOUR) + "h";
        } else if (elapsedTime < ONE_WEEK){
            return Math.round(elapsedTime/ONE_DAY) + "d";
        } else {
            return Math.round(elapsedTime/ONE_WEEK) + "w";
        }
    }

    public String getCreatedAt() {
        return getFormattedTime(creationTime);
    }
}
