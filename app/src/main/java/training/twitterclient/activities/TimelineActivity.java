package training.twitterclient.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import training.twitterclient.R;
import training.twitterclient.TwitterApplication;
import training.twitterclient.networking.TwitterClient;
import training.twitterclient.adapters.TweetArrayAdapter;
import training.twitterclient.fragments.TweetFragment;
import training.twitterclient.util.EndlessScrollListener;
import training.twitterclient.models.Tweet;
import training.twitterclient.models.User;
import training.twitterclient.util.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements TweetFragment.TweetSendListener {
    private static final String TAG = "TimelineActivity";
    private TwitterClient client;
    private List<Tweet> mTweets = new ArrayList<>();
    private ListView lvTweets;
    private TweetArrayAdapter adapter;
    private User mCurUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        fetchCurrentUser();

        lvTweets = (ListView) findViewById(R.id.lvTweets);
        adapter = new TweetArrayAdapter(TimelineActivity.this, mTweets);
        lvTweets.setAdapter(adapter);

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadMoreTweets(adapter.getItem(adapter.getCount() - 1).uid - 26);
                // or loadMoreTweets(totalItemsCount);
            }
        });
        populateTimeline();
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "response code:" + statusCode + "::" + response);
                adapter.addAll(Tweet.fromJsonArray(response));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.e("ERROR", throwable.getMessage());
                populateTimelineFromDb();
            }
        });
    }

    private void populateTimelineFromDb(){
        adapter.clear();
        adapter.addAll(Tweet.recentItems());
    }

    private void loadMoreTweets(long offset) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "response code:" + statusCode + "::" + response);
                adapter.addAll(Tweet.fromJsonArray(response));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ERROR", throwable.getMessage());
                populateTimelineFromDb();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose_tweet) {
            if(mCurUser != null) {
                TweetFragment tweetFragment = TweetFragment.newInstance(mCurUser);
                tweetFragment.show(getSupportFragmentManager(), "fragment_tweet");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTweetSend(String tweet) {
        if (!Utils.checkForNetwork(this)) {
            if (null != tweet && tweet.trim().length() > 0) {
                client.sendTweet(tweet, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d(TAG, "response code:" + statusCode + "::" + response);
                        mTweets.add(0, Tweet.fromJson(response));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                        Log.e("ERROR", throwable.getMessage());
                        populateTimelineFromDb();
                    }
                });
            }
        }
    }

    private void fetchCurrentUser(){
        Utils.checkForNetwork(this);
        client.getCurrentUserInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "response code:" + statusCode + "::" + response);
                mCurUser = User.fromJson(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.e(TAG, throwable.getMessage());
            }
        });
    }
}
