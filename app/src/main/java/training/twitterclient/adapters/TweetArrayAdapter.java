package training.twitterclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import training.twitterclient.R;
import training.twitterclient.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        ViewHolder vH;

        if (null == convertView){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            vH = new ViewHolder();
            vH.ivProfileImg = (ImageView) convertView.findViewById(R.id.ivProfileImg);
            vH.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            vH.tvTweet = (TextView) convertView.findViewById(R.id.tvTweet);
            vH.tvRelativeTime = (TextView) convertView.findViewById(R.id.tvRelativeTime);
            convertView.setTag(vH);
        } else {
            vH = (ViewHolder) convertView.getTag();
        }

        vH.tvUserName.setText(tweet.user.name);
        vH.tvTweet.setText(tweet.body);
        vH.tvRelativeTime.setText(tweet.getCreatedAt());
        Picasso.with(getContext()).load(tweet.user.profileImage).into(vH.ivProfileImg);

        return convertView;
    }

    private static class ViewHolder {
        private ImageView ivProfileImg;
        private TextView tvUserName;
        private TextView tvTweet;
        private TextView tvRelativeTime;
    }
}
