package ru.ra66it.updaterforspotify.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.pixelcan.inkpageindicator.InkPageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ra66it.updaterforspotify.storage.QueryPreferneces;
import ru.ra66it.updaterforspotify.R;
import ru.ra66it.updaterforspotify.adapter.IntroPagerAdapter;

/**
 * Created by 2Rabbit on 17.11.2017.
 */

public class IntroActivity extends AppCompatActivity {

    @BindView(R.id.intro_view_pager)
    ViewPager viewPager;
    @BindView(R.id.pageIndicator)
    InkPageIndicator indicator;
    @BindView(R.id.btn_next)
    Button buttonNext;
    private int localPos;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);


        viewPager.setAdapter(new IntroPagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(viewPager);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                localPos = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buttonNext.setOnClickListener(view -> {
            switch (localPos) {
                case 0:
                    viewPager.setCurrentItem(1);
                    break;
                case 1:
                    viewPager.setCurrentItem(2);
                    break;
                case 2:
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        QueryPreferneces.setFirstLaunch(getApplicationContext(), false);
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        getPermission();
                    }
                    break;
            }

        });

    }

    private void getPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }


    @Override
    public void onBackPressed() {

    }
}
