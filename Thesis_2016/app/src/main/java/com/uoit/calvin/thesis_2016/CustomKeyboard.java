package com.uoit.calvin.thesis_2016;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class CustomKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private android.inputmethodservice.Keyboard keyboard;

    private boolean caps = false;

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new android.inputmethodservice.Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        switch(primaryCode){
            case android.inputmethodservice.Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                break;
            case android.inputmethodservice.Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case android.inputmethodservice.Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && caps){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
        }
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }
}