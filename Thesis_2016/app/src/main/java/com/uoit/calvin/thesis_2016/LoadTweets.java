package com.uoit.calvin.thesis_2016;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class LoadTweets extends AsyncTask<String, Void, List<Tweet>> {

    private final TweetsListener listener;
    List<Tweet> tweets = new ArrayList<>();
    Context context;

    LoadTweets(TweetsListener listener, Context context) {
        this.listener = listener;
        this.context = context;
    }
    @Override
    protected List<Tweet> doInBackground(String... params) {
        Call<List<Tweet>> tweetsCall;
        if (params[1].equals(context.getString(R.string.explore_activity_title))) {
            tweetsCall = TwitterCore.getInstance().getApiClient().getStatusesService()
                    .userTimeline(null, params[0], null, null, null, null, null, null, null);
        } else if (params[1].equals(context.getString(R.string.helper_pull_all))){
            tweetsCall = TwitterCore.getInstance().getApiClient().getStatusesService()
                    .userTimeline(null, params[0], null, null, null, null, null, null, null);
        } else {
            UserDBHelper userDBHelper = new UserDBHelper(context);
            User user = userDBHelper.getUserByUsername(params[0]);
            userDBHelper.close();
            tweetsCall = TwitterCore.getInstance().getApiClient().getStatusesService()
                    .userTimeline(null, params[0], null, user.getSinceID(), null, null, null, null, null);
        }

        Response<List<Tweet>> responses = null;
        try {
            responses = tweetsCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (responses != null) {
            tweets = responses.body();
        }

        return tweets;
    }

    @Override
    protected void onPostExecute(List<Tweet> tweets) {
        if (listener != null) {
            listener.tweetsCompleted(tweets);
        }
    }

}
