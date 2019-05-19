package com.zixuan.elf.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zixuan.elf.Music;
import com.zixuan.elf.MusicList;
import com.zixuan.elf.R;
import com.zixuan.elf.customizeView.GramophoneView;
import com.zixuan.elf.presenter.IPresenter;
import com.zixuan.elf.presenter.MyIntentService;
import com.zixuan.elf.presenter.Presenter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IMainActivity {
    GramophoneView gramophoneView;
    IPresenter presenter;
    private Toolbar toolbar;
    DrawerLayout drawerLayout;
    ArrayList<TextView> textViews;
    ImageView img_mood;
    ImageView img_play;
    FrameLayout frm_not_expanded;
    RecordFragment recordFragment;
    private MyIntentService myIntentService;
    private boolean isBond = false;
    MyIntentService.MyBinder myBinder ;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBond = true;
            myBinder = (MyIntentService.MyBinder) service;
            Log.d("MyTag", "onServiceConnected: 已绑定");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBond = false;
            myBinder = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordFragment = RecordFragment.newRecordFragment(this,this);
        replaceFragment(recordFragment);
        initToolbar();
        presenter = Presenter.getPresenter(this,this);
        presenter.onApplicationStart();

    }

    void inituserPic(){

        gramophoneView = findViewById(R.id.grp_user_pic);
        gramophoneView.setPictureUrl("http://p2.music.126.net/7aPUVZ-jmdzcfvE3xs8Mlw==/18819241023006535.jpg");
    }
    private void initToolbar(){
        drawerLayout = findViewById(R.id.dl_main);
        TextView textView = findViewById(R.id.tv_user_name);
        textView.setText("十二桎梏");
        toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }
    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frm_main,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void gramophonePauseOrStart() {
        recordFragment.gramophonePauseOrStart();

    }

    @Override
    public void setGramophonePic(String url) {

        recordFragment.setGramophonePic(url);

    }

    @Override
    public void onMoodChanged(int num) {
        presenter.onMoodChanged(num);
    }

    @Override
    public void startService() {


        Intent intent = new Intent(this, MyIntentService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    @Override
    public void setSongName(String name) {
        recordFragment.setSongName(name);
    }

    @Override
    public void setAuthorName(String author) {
        recordFragment.setAuthorName(author);
    }

    @Override
    public void musicPause() {
        recordFragment.musicPause();
    }


    private Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            menuItem.getItemId();
            switch (menuItem.getItemId()){
                case R.id.drawer_main:
                    drawerLayout.openDrawer(GravityCompat.END);
            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    private void addDrawerItem(){
        textViews.add((TextView) findViewById(R.id.tv_daily_recommend_cn));
        textViews.add((TextView) findViewById(R.id.tv_daily_recommend_eg));
        textViews.add((TextView) findViewById(R.id.tv_comments_plaza_cn));
        textViews.add((TextView) findViewById(R.id.tv_comments_plaza_eg));
        textViews.add((TextView) findViewById(R.id.tv_my_collection_cn));
        textViews.add((TextView) findViewById(R.id.tv_my_collection_eg));
    }




    @Override
    protected void onDestroy() {
        presenter = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    @Override
    protected void onResume() {
        if(!isBond){
            Intent intent = new Intent(this,MyIntentService.class);
            bindService(intent,connection,BIND_AUTO_CREATE);
        }

        super.onResume();
    }

    @Override
    protected void onStart() {

        super.onStart();
    }
}
