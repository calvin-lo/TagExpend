package com.uoit.calvin.thesis_2016;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

public interface TweetsListener {
        void tweetsCompleted(List<Tweet> tweets);
    }
