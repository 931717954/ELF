package com.zixuan.elf.presenter;

import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;

import com.zixuan.elf.Music;
import com.zixuan.elf.MusicList;
import com.zixuan.elf.model.Model;
import com.zixuan.elf.view.IMainActivity;
import com.zixuan.elf.view.IMusicActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class Presenter implements IPresenter {
    Context context;
    MusicList musicList;
    IMainActivity mainActivity;
    IMusicActivity musicActivity;
    static final int MOOD_SONG_LIST = 0;
    static final int COMMONED_SONG_LIST = 1;
    static final int SONG_LIST_DETAILS = 2;
    static final int SONG = 3;
    static final int LYRICS = 4;
    static final String STATE = "state";
    static final String DATA = "data";
    private String moodListId;
    static final String INFORMATION_BASE_URL = "http://elf.egos.hosigus.com";
    static final String MOOD_SONG_LIST_URL ="/getSongListID.php";
    static final String COMMONED_SONG_LIST_URL ="/getRecommendID.php";
    static final String MUSIC_DETAIL_BASE_URL="http://elf.egos.hosigus.com/music";
    static final String SONG_LIST_DETAILS_URL = "/playlist/detail";
    static final String SONG_URL ="http://music.163.com/song/media/outer/url?id=";
    static final String LYRICS_URL = "/lyric";
    Model model;
    static int mood = 0;
    private Presenter(){
        model = Model.getModel(this);
        musicList = MusicList.getMusicList();
    }

    static private Presenter presenter;
    public static Presenter getPresenter(Context context, IMainActivity mainActivity){

        if(presenter == null){
            synchronized (Presenter.class){
                if(presenter == null){
                    presenter = new Presenter();
                }
            }
        }
        presenter.context = context;
        presenter.mainActivity = mainActivity;
        return presenter;
    }
    public static Presenter getPresenter(Context context, IMusicActivity musicActivity){

        if(presenter == null){
            synchronized (Presenter.class){
                if(presenter == null){
                    presenter = new Presenter();
                }
            }
        }
        presenter.context = context;
        presenter.musicActivity = musicActivity;
        return presenter;
    }
    public static Presenter getPresenter(){

        if(presenter == null){
            synchronized (Presenter.class){
                if(presenter == null){
                    presenter = new Presenter();
                }
            }
        }
        return presenter;
    }
    @Override
    public void onApplicationStart() {
        model.getInformation(INFORMATION_BASE_URL+MOOD_SONG_LIST_URL+"?type=HAPPY",MOOD_SONG_LIST);
    }

    @Override
    public void netWorkCallback(String msg,int type) {
        switch (type){
            case MOOD_SONG_LIST:
                jsonAnalysis_0(msg);
                break;
            case COMMONED_SONG_LIST:
                break;
            case SONG_LIST_DETAILS:
                jsonAnalysis_2(msg);

                break;
            case SONG:
                break;
            case LYRICS:
                jsonAnalysis_4(msg);
                break;
        }
    }

    @Override
    public void onMoodChanged(int num) {
        if(mood != num){
            mood = num;
            switch (num){
                case 0:
                    model.getInformation(INFORMATION_BASE_URL+MOOD_SONG_LIST_URL+"?type=HAPPY",MOOD_SONG_LIST);
                    break;
                case 1:
                    model.getInformation(INFORMATION_BASE_URL+MOOD_SONG_LIST_URL+"?type=UNHAPPY",MOOD_SONG_LIST);
                    break;
                case 2:
                    model.getInformation(INFORMATION_BASE_URL+MOOD_SONG_LIST_URL+"?type=CALM",MOOD_SONG_LIST);
                    break;
                case 3:
                    model.getInformation(INFORMATION_BASE_URL+MOOD_SONG_LIST_URL+"?type=EXCITING",MOOD_SONG_LIST);
                    break;
            }
        }
    }

    private void jsonAnalysis_0(String msg){
        JSONObject object = null;
        JSONObject object1 = null;
        try {
            object = new JSONObject(msg);
            object1 = object.getJSONObject(DATA);

            moodListId = object1.getString("id");
            Log.d("MyTag", "jsonAnalysis_0: "+moodListId);
        } catch (JSONException e) {
            e.printStackTrace();
            callDefect();
        }
        model.getInformation(MUSIC_DETAIL_BASE_URL+SONG_LIST_DETAILS_URL+"?id="+moodListId,SONG_LIST_DETAILS);
    }
    private void jsonAnalysis_2(String msg){
        Log.d("MyTag", "jsonAnalysis_2: ");
        JSONObject jsonObject = null;
        JSONObject jsonObject1 = null;
        JSONArray jsonArray = null;

        try {
            jsonObject = new JSONObject(msg);
            jsonObject1 = jsonObject.getJSONObject("playlist");
            jsonArray = jsonObject1.getJSONArray("tracks");
            Log.d("MyTag", "jsonAnalysis_2: ");
            musicList.removeMusic();
            musicList.addMusic(jsonArray);
            onSongChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            callDefect();
        }
    }
    private void jsonAnalysis_4(String msg){
        JSONObject jsonObject = null;
        JSONObject jsonObject1 = null;
        String message = null;
        try {
            jsonObject = new JSONObject(msg);
            jsonObject1 = jsonObject.getJSONObject("lrc");
            message = jsonObject1.getString("lyric");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MusicList.setLyric(message);

        if(musicActivity != null){

            musicActivity.setLyric(message);
        }
    }
    @Override
    public void callDefect(){

    }
    public void callForWords(long id){
        String url = MUSIC_DETAIL_BASE_URL+LYRICS_URL+"?id="+id;
        model.getInformation(url,LYRICS);
    }
    @Override
    public void onSongChanged() {

        Music music = musicList.getMusic(MusicList.getNowPlaying());
        callForWords(music.getId());
        if(mainActivity != null) {
            mainActivity.startService();
            mainActivity.setGramophonePic(music.getPicUrl());
            mainActivity.setSongName(music.getName());
            mainActivity.setAuthorName(music.getAuther());
        }
        if(musicActivity != null){
            musicActivity.setAuthorName(music.getAuther());
            musicActivity.setSongName(music.getName());
            musicActivity.setPicture(music.getPicUrl());
        }
    }
}
