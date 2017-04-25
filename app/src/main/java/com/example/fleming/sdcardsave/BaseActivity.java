package com.example.fleming.sdcardsave;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * BaseActivity
 * Created by Fleming on 2016/11/23.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private FragmentManager mManager;

    protected abstract Fragment createFragment();

    protected abstract void onActivityCreate();

    protected void init() {}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        onActivityCreate();

        mManager = getSupportFragmentManager();
        Fragment fragment = mManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = createFragment();
            if (fragment != null) {
                mManager.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        }
    }

    protected Fragment getFragment() {
        return mManager.findFragmentById(R.id.fragment_container);
    }
}
