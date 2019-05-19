package com.zixuan.elf.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public interface IMainActivity {
    void replaceFragment(Fragment fragment);
    void gramophonePauseOrStart();
    void setGramophonePic(String url);
    void onMoodChanged(int num);
    void startService();
    void setSongName(String name);
    void setAuthorName(String author);
    void musicPause();
}
