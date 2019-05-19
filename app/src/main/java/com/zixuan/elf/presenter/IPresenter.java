package com.zixuan.elf.presenter;

public interface IPresenter {
    void onApplicationStart();
    void netWorkCallback(String msg,int type);
    void onMoodChanged(int num);
    void callDefect();
    void onSongChanged();
}
