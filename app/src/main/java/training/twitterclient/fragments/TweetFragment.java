package training.twitterclient.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import training.twitterclient.R;
import training.twitterclient.models.User;
import com.squareup.picasso.Picasso;

public class TweetFragment extends DialogFragment {
    private EditText etTweet;
    private ImageView ivProfileImg;
    private TextView tvName;
    private TextView tvUserName;
    private static final String TWEET = " TWEET";
    private static User currentUser;

    public static TweetFragment newInstance(User currentUser) {
        TweetFragment.currentUser = currentUser;
        TweetFragment frag = new TweetFragment();
        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet, container);
        ivProfileImg = (ImageView) view.findViewById(R.id.ivProfileImg);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        etTweet = (EditText) view.findViewById(R.id.etTweet);
        Picasso.with(view.getContext()).load(currentUser.profileImage).into(ivProfileImg);
        tvName.setText(currentUser.name);
        tvUserName.setText("@" + currentUser.screenName);
        setUpEventHandlers();
        getDialog().setTitle("140 " + TWEET);
        return view;
    }

    private void setUpEventHandlers(){
        etTweet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                getDialog().setTitle(140 - etTweet.getText().length() + TWEET);
                return false;
            }
        });
        etTweet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                final String tweet = etTweet.getText().toString();
                TweetSendListener timelineActivity = (TweetSendListener) getActivity();
                timelineActivity.onTweetSend(tweet);
                dismiss();
                return false;
            }
        });
    }

    public interface TweetSendListener{
        void onTweetSend(String tweet);
    }
}
