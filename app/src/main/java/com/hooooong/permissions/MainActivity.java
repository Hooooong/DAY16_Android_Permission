package com.hooooong.permissions;

import android.Manifest;

/**
 * 안드로이드 권한 요청
 *
 *  가. 일반적인 권한 요청 -> Manifest 에 설정
 *      -
 */
public class MainActivity extends BaseActivity {

    public MainActivity() {
        super(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void init() {
        setContentView(R.layout.activity_main);
    }
}
