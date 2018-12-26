package com.d6.android.app.models.push;

/**
 * author : jinjiarui
 * time   : 2018/11/27
 * desc   :
 * version:
 */
public class MessageBody {
    private String after_open;
     private String play_lights;
     private String ticker;
     private String play_vibrate;
     private String activity;
     private String text;
     private String title;
     private String play_sound;

    public String getAfter_open() {
        return after_open == null ? "" : after_open;
    }

    public void setAfter_open(String after_open) {
        this.after_open = after_open;
    }

    public String getPlay_lights() {
        return play_lights == null ? "" : play_lights;
    }

    public void setPlay_lights(String play_lights) {
        this.play_lights = play_lights;
    }

    public String getTicker() {
        return ticker == null ? "" : ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getPlay_vibrate() {
        return play_vibrate == null ? "" : play_vibrate;
    }

    public void setPlay_vibrate(String play_vibrate) {
        this.play_vibrate = play_vibrate;
    }

    public String getActivity() {
        return activity == null ? "" : activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getText() {
        return text == null ? "" : text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlay_sound() {
        return play_sound == null ? "" : play_sound;
    }

    public void setPlay_sound(String play_sound) {
        this.play_sound = play_sound;
    }
}
