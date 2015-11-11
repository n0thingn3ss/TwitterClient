package training.twitterclient.networking;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1";
    private static final String REST_CONSUMER_KEY = "jcwiCn9H9oZGx4pd8sXuBcjR1";
    private static final String REST_CONSUMER_SECRET = "YZ0pXs0qHKPMyHCWZb0BQ9QUP78NsRbS41XVKH5pPLpNMcvwuR";
    private static final String REST_CALLBACK_URL = "http://codepath.com";
    public static final String HOME = "statuses/home_timeline.json";
    public static final String TWEET = "statuses/update.json";
    public static final String USER_INFO = "account/verify_credentials.json";

    public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

    public void getHomeTimeline(AsyncHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams();
        params.put("count", 25);
        params.put("since_id", 1);
        client.get(getApiUrl(HOME), params, responseHandler);
    }

    public void sendTweet(String tweet, AsyncHttpResponseHandler responseHandler){
        RequestParams params = new RequestParams();
        params.put("status", tweet);
        client.post(getApiUrl(TWEET), params, responseHandler);
    }

    public void getCurrentUserInfo(AsyncHttpResponseHandler responseHandler){
        client.get(getApiUrl(USER_INFO), responseHandler);
    }


    private static RequestParams getDefaultRequestParams() {
        RequestParams params = new RequestParams();
        params.put("client_id", REST_CONSUMER_KEY);
        return params;
    }
	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, responseHandler);
	 *    i.e client.post(apiUrl, params, responseHandler);
	 */
}