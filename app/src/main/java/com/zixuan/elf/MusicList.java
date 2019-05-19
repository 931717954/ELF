package com.zixuan.elf;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class MusicList {
    private LinkedList<Music> list = new LinkedList<>();
    static private int nowPlaying = 0;
    static private boolean isPlaying = true;
    static private String lyric = null;

    public static String getLyric() {
        return lyric;
    }

    public static void setLyric(String lyric) {
        MusicList.lyric = lyric;
    }

    private MusicList(){}


    static private MusicList musicList;
    static public MusicList getMusicList(){
        if(musicList == null){
            synchronized (MusicList.class){
                if(musicList == null){
                    musicList = new MusicList();
                }
            }
        }
        return musicList;
    }
    public void addMusic(JSONArray array){
        try {
            for (int n = 0; n < array.length(); n++) {
                JSONObject object = array.getJSONObject(n);
                String name = object.getString("name");
                long id = object.getInt("id");
                JSONObject object1 = object.getJSONArray("ar").getJSONObject(0);

                String author = object1.getString("name");
                String picUrl = object.getJSONObject("al").getString("picUrl");
                list.add(new Music(id,name,author,picUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("MyTag", "addMusic: "+list);
    }
    public int getMusicListSize(){
        return list.size();
    }
    public void removeMusic(){
        list.clear();
    }
    public Music getMusic(int index){
        if(index<list.size()){
            return list.get(index);
        }
        else {
            return null;
        }
    }
    static public void setNowPlaying(int num){
        nowPlaying = num;
    }
    static public int nowPlayingAdd(){
        nowPlaying++;
        return nowPlaying;
    }
    static public int nowPlayingMinus(){
        nowPlaying--;
        return nowPlaying;
    }
    static public int getNowPlaying(){
        return nowPlaying;
    }
    static public boolean getIsPlaying(){
        return isPlaying;
    }
    static public boolean changeIsPlaying(){
        isPlaying = !isPlaying;
        return isPlaying;
    }
}
