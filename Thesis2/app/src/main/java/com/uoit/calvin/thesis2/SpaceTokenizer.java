package com.uoit.calvin.thesis2;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView;

class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer {
    @Override
    public int findTokenStart(CharSequence charSequence, int i) {
        int cursor = i;

        while (cursor > 0 && charSequence.charAt(cursor - 1) != ' ') {
            cursor--;
        }
        while (cursor < i && charSequence.charAt(cursor) == ' ') {
            cursor++;
        }
        return cursor;
    }

    @Override
    public int findTokenEnd(CharSequence charSequence, int i) {
        int cursor = i;
        int len = charSequence.length();

        while (cursor < len) {
            if (charSequence.charAt(cursor) == ' ') {
                return cursor;
            } else {
                cursor++;
            }
        }

        return len;
    }

    @Override
    public CharSequence terminateToken(CharSequence charSequence) {
        int i = charSequence.length();

        while (i > 0 && charSequence.charAt(i - 1) == ' ') {
            i--;
        }

        if (i > 0 && charSequence.charAt(i - 1) == ' ') {
            return charSequence;
        } else {
            if (charSequence instanceof Spanned) {
                SpannableString sp = new SpannableString(charSequence + " ");
                TextUtils.copySpansFrom((Spanned) charSequence, 0, charSequence.length(), Object.class, sp, 0);
                return sp;
            } else {
                return charSequence + " ";
            }
        }
    }
}
