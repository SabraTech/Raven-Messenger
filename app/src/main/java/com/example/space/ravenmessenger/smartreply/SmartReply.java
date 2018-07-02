package com.example.space.ravenmessenger.smartreply;

import android.support.annotation.Keep;

/**
 * SmartReplyData contains predicted message, and confidence.
 * <p>
 * NOTE: this class used by JNI, class name and constructor should not be obfuscated.
 */
@Keep
public class SmartReply {

    private final String text;
    private final float score;

    @Keep
    public SmartReply(String text, float score) {
        this.text = text;
        this.score = score;
    }

    public String getText() {
        return text;
    }

    public float getScore() {
        return score;
    }
}
