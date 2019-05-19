package com.zixuan.elf.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.pictureloadlibrary.Flow;
import com.zixuan.elf.Music;
import com.zixuan.elf.MusicList;
import com.zixuan.elf.R;
import com.zixuan.elf.presenter.IPresenter;
import com.zixuan.elf.presenter.MyIntentService;
import com.zixuan.elf.presenter.Presenter;

import org.sang.lrcview.LrcView;

import java.util.Timer;
import java.util.TimerTask;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener,IMusicActivity {
    Toolbar toolbar;
    ImageView pause;
    ImageView next;
    ImageView last;
    IPresenter presenter;
    Timer timer;
    MyIntentService.MyBinder myBinder;
    ImageView pic;
    TextView authorName;
    TextView songName;
    LrcView lrcView;
    AppCompatSeekBar mediaSeekBar;
    static private String lyric = null;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MyIntentService.MyBinder) service;
            if(MusicList.getLyric()!=null && myBinder!= null &&myBinder.getMediaPlayer()!=null){
                int time = myBinder.getMediaPlayer().getDuration();
                mediaSeekBar.setMax(time);
                setLyric(MusicList.getLyric());
                getProgress();
                mediaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                        //获取进度条的进度
                        int p = seekBar.getProgress();
                        //将进度条的进度赋值给歌曲
                        myBinder.getMediaPlayer().seekTo(p);
                        //开始音乐继续获取歌曲的进度
                        getProgress();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                        //取消timer任务
                        stopTimer();
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {


                    }
                });

            }
        };
    public void stopTimer() {
        if(timer!=null)
        {
            timer.cancel();
        }
    }


    private void getProgress() {

            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {

                    //获取歌曲的进度
                    int p = myBinder.getMediaPlayer().getCurrentPosition();

                    //将获取歌曲的进度赋值给seekbar
                    Log.d("MyTag", "run: "+p);
                    mediaSeekBar.setProgress(p);
                }
            }, 0, 2000);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myBinder = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        mediaSeekBar = findViewById(R.id.sb_music_main);
        presenter = Presenter.getPresenter(this,this);
        initToolbar();
        initImageView();
        bindService();
        if(!MusicList.getIsPlaying()){
            pause.setImageResource(R.mipmap.ic_play_pause);
        }
        lrcView = findViewById(R.id.lrc_view);
        initInfo();

    }
    void initInfo(){
        pic = findViewById(R.id.img_music_background);

        Music music = MusicList.getMusicList().getMusic(MusicList.getNowPlaying());
        Flow.with(this).load(music.getPicUrl()).into(pic);
        authorName = findViewById(R.id.tv_music_singer);
        authorName.setText(music.getAuther());
        songName = findViewById(R.id.tv_music_song);
        songName.setText(music.getName());
    }
    private void initToolbar(){
        toolbar = findViewById(R.id.tb_music);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    private void initImageView(){
        this.pause = findViewById(R.id.img_pause_run);
        pause.setTag(false);
        pause.setOnClickListener(this);
        this.last = findViewById(R.id.img_move_back);
        last.setOnClickListener(this);
        this.next = findViewById(R.id.img_move_forward);
        next.setOnClickListener(this);
    }
    private void bindService(){
        Intent intent = new Intent(this, MyIntentService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_pause_run:
                myBinder.pauseMusic();
                if(MusicList.getIsPlaying()){
                    pause.setImageResource(R.mipmap.ic_play_pause);

                    MusicList.changeIsPlaying();
                }
                else {
                    pause.setImageResource(R.mipmap.ic_play_running);

                    MusicList.changeIsPlaying();
                }
                break;
            case R.id.img_move_back:
                myBinder.preciousMusic();

                if(!MusicList.getIsPlaying()){
                    pause.setImageResource(R.mipmap.ic_play_running);
                    MusicList.changeIsPlaying();
                }
                break;
            case R.id.img_move_forward:
                myBinder.nextMusic();
                if(!MusicList.getIsPlaying()){
                    pause.setImageResource(R.mipmap.ic_play_running);
                    MusicList.changeIsPlaying();
                }
                break;
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public void setSongName(String name) {
        songName.setText(name);
    }

    @Override
    public void setAuthorName(String author) {
        authorName.setText(author);
    }

    @Override
    public void setPicture(String url) {
        Flow.with(this).load(url).into(pic);
    }

    @Override
    public void setLyric(String lyric) {
        lrcView.setLrc(lyric);
        lrcView.setPlayer(myBinder.getMediaPlayer());
        lrcView.init();
    }
}
