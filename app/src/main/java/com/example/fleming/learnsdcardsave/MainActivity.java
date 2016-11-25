package com.example.fleming.learnsdcardsave;

import android.support.v4.app.Fragment;

public class MainActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        return new SDCardSaveFragment();
    }

    @Override
    protected void onActivityCreate() {
        setContentView(R.layout.activity_base);
    }
}
