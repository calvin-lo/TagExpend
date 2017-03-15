package com.uoit.calvin.thesis_2016;

import android.graphics.Bitmap;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

public interface ImageListener {
        void imageCompleted(Bitmap b, User user);
    }
