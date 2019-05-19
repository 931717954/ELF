package com.zixuan.elf.view;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zixuan.elf.Music;
import com.zixuan.elf.MusicList;
import com.zixuan.elf.R;
import com.zixuan.elf.customizeView.GramophoneView;

import java.util.ArrayList;

public class RecordFragment extends Fragment implements View.OnClickListener {
    FrameLayout frameLayout;
    GramophoneView gramophoneView;
    ImageView img_mood;
    ImageView img_play;
    private Context context;
    ArrayList<ImageView> imageViews;
    static IMainActivity mainActivity;
    TextView songName;
    TextView author;
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_fragment,container,false);
        frameLayout = view.findViewById(R.id.frm_mood);
        img_play = ((ViewGroup)frameLayout.getParent()).findViewById(R.id.img_play);
        img_play.setOnClickListener(this);
        songName = view.findViewById(R.id.tv_song_name);
        author = view.findViewById(R.id.tv_singer_name);
        initPic();
        gramophoneView = view.findViewById(R.id.grp_record);
        if(MusicList.getIsPlaying()){
            musicPlay();
        }
        else {
            musicPause();
        }
        this.view = view;
        return view;
    }

    @Override
    public void onStart() {
        if(MusicList.getIsPlaying()){
            musicPlay();
        }
        else {
            musicPause();
        }
        Music music = MusicList.getMusicList().getMusic(MusicList.getNowPlaying());

        if(music!=null){
            Log.d("lzx", "onStart: "+music.getAuther());
        setAuthorName(music.getAuther());
        setSongName(music.getName());
        setGramophonePic(music.getPicUrl());
        }
        super.onStart();
    }

    public static RecordFragment newRecordFragment(Context context, IMainActivity mainActivity){
        RecordFragment recordFragment = new RecordFragment();
        recordFragment.context = context;
        RecordFragment.mainActivity = mainActivity;
        return recordFragment;
    }
    void initPic(){
        imageViews = new ArrayList<>();
        imageViews.add((ImageView) frameLayout.findViewById(R.id.img_mood_0));
        imageViews.add((ImageView) frameLayout.findViewById(R.id.img_mood_1));
        imageViews.add((ImageView) frameLayout.findViewById(R.id.img_mood_2));
        imageViews.add((ImageView) frameLayout.findViewById(R.id.img_mood_3));
        imageViews.add((ImageView)frameLayout.findViewById(R.id.bg_frame_short));
        imageViews.get(0).setTag(0);
        for (int index = 1; index < imageViews.size(); index++) {
            imageViews.get(index).setVisibility(View.GONE);
            imageViews.get(index).setTag(index);
            imageViews.get(index).setOnClickListener(this);
        }
        imageViews.get(0).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_mood_0:
                if(imageViews.get(2).getVisibility() == View.VISIBLE) {
                    for (int index = 1; index < imageViews.size(); index++) {
                        imageViews.get(index).setVisibility(View.GONE);
                    }
                }
                else {
                    for (int index = 1; index < imageViews.size(); index++) {
                        imageViews.get(index).setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.img_mood_1:
                changeFirstPic(imageViews.get(1));
                break;
            case R.id.img_mood_2:
                changeFirstPic(imageViews.get(2));
                break;
            case R.id.img_mood_3:
                changeFirstPic(imageViews.get(3));
                break;
            case R.id.img_play:
                Intent intent = new Intent(context,MusicActivity.class);
                startActivity(intent);
                break;
        }
    mainActivity.onMoodChanged((int)imageViews.get(0).getTag());
    }
    void changeFirstPic(ImageView imageView){
        int tag = (int)imageView.getTag();
        int rawTag = (int)imageViews.get(0).getTag();
        imageView.setTag(rawTag);
        imageViews.get(0).setTag(tag);
        switch (tag){
            case 0:
                imageViews.get(0).setImageResource(R.mipmap.ic_mood_happy);
                imageView.setImageResource(getAnotherPic(rawTag));
                break;

            case 1:

                imageViews.get(0).setImageResource(R.mipmap.ic_mood_unhappy);
                imageView.setImageResource(getAnotherPic(rawTag));

                break;
            case 2:

                imageViews.get(0).setImageResource(R.mipmap.ic_mood_clam);
                imageView.setImageResource(getAnotherPic(rawTag));

                break;
            case 3:

                imageViews.get(0).setImageResource(R.mipmap.ic_mood_exciting);
                imageView.setImageResource(getAnotherPic(rawTag));
                break;
        }
    }
    @DrawableRes int getAnotherPic(int rawTag){
        switch (rawTag){
            case 0:
                return R.mipmap.ic_mood_happy;
            case 1:
                return R.mipmap.ic_mood_unhappy;
            case 2:
                return R.mipmap.ic_mood_clam;
            case 3:
                return R.mipmap.ic_mood_exciting;
            default:
                return 0;
        }
    }
    public void gramophonePauseOrStart(){
        gramophoneView.pauseOrstart();
    }
    public void setGramophonePic(String url){
        gramophoneView.setPictureUrl(url);
    }
    public void setSongName(String name){
        Log.d("MyTag", "setSongName: "+name);
        songName.setText(name);
    }
    public void setAuthorName(String authorName){
        author.setText(authorName);
    }
    public void musicPause(){
        gramophoneView.pause();
    }
    public void musicPlay(){
        gramophoneView.start();
    }

}
