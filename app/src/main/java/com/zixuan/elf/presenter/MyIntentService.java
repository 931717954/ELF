package com.zixuan.elf.presenter;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.zixuan.elf.MusicList;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyIntentService extends Service {
    public MediaPlayer mMediaPlayer = new MediaPlayer();
    private MyBinder mBinder = new MyBinder();
    MusicList musicList;
    private int i = 0;
    static private Presenter presenter = Presenter.getPresenter();
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            return false;
        }
    });

    public MyBinder getBinder(){
        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        musicList = MusicList.getMusicList();

        Log.d("MyTag", "onHandleIntent: ");
        if (intent != null) {
            Log.d("MyTag", "onHandleIntent: 1");
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mBinder.nextMusic();
                }
            });

        }

        iniMediaPlayerFile(i);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(final Intent intent) {



        mBinder = new MyBinder();
        return mBinder;
    }



        private void iniMediaPlayerFile(int dex){
            //获取文件路径
            try {
                //此处的两个方法需要捕获IO异常
                //设置音频文件到MediaPlayer对象中
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource("http://music.163.com/song/media/outer/url?id=" + musicList.getMusic(dex).getId() + ".mp3");
                //让MediaPlayer对象准备
                Log.d("MyTag", "iniMediaPlayerFile: ");
                mMediaPlayer.prepareAsync();

                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });

            } catch (IOException e) {
                Log.d("MyTag", "设置资源，准备阶段出错");
                e.printStackTrace();
            }
        }

        public class MyBinder extends Binder{


            public void playMusic() {
                if (!mMediaPlayer.isPlaying()) {
                    //如果还没开始播放，就开始
                    mMediaPlayer.start();
                }
            }

            /**
             * 暂停播放
             */
            public void pauseMusic() {
                if (mMediaPlayer.isPlaying()) {
                    //如果还没开始播放，就开始
                    mMediaPlayer.pause();
                }
                else {
                    mMediaPlayer.start();
                }
            }

            /**
             * reset
             */
            public void resetMusic() {
                if (!mMediaPlayer.isPlaying()) {
                    //如果还没开始播放，就开始
                    mMediaPlayer.reset();
                    iniMediaPlayerFile(i);
                }
            }

            /**
             * 关闭播放器
             */
            public void closeMedia() {
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
            }


            public void nextMusic() {
                if (mMediaPlayer != null ) {




                    if (i == musicList.getMusicListSize()) {
                        i = 0;
                    } else {
                        i = i + 1;
                    }
                    iniMediaPlayerFile(i);
                    playMusic();
                }
                MusicList.nowPlayingAdd();
                presenter.onSongChanged();

            }

            /**
             * 上一首
             */
            public void preciousMusic() {
                if (mMediaPlayer != null ) {
                    mMediaPlayer.reset();

                    if (i == 1) {
                        i = musicList.getMusicListSize() ;
                    } else {

                        i = i - 1;
                    }
                    iniMediaPlayerFile(i );
                    playMusic();
                }
                MusicList.nowPlayingMinus();
                presenter.onSongChanged();
            }

            /**
             * 获取歌曲长度
             **/
            public int getProgress() {

                return mMediaPlayer.getDuration();
            }

            /**
             * 获取播放位置
             */
            public int getPlayPosition() {

                return mMediaPlayer.getCurrentPosition();
            }

            /**
             * 播放指定位置
             */
            public void seekToPositon(int msec) {
                mMediaPlayer.seekTo(msec);
            }
            public MediaPlayer getMediaPlayer(){
                return mMediaPlayer;
            }
        }
    }


