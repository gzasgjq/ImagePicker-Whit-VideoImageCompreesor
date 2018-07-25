package com.gjq.demo.activites;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.gjq.demo.R;
import com.gjq.demo.fragments.ItemFragment;

public class RecycleViewFullActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view_full);
        TabLayout tabLayout = findViewById(R.id.tab_list_bar);
        ViewPager pager = findViewById(R.id.pager_list_viewpager);
        tabLayout.setupWithViewPager(pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return ItemFragment.newInstance(position);
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position){
                    case 0:
                        return "Title1";
                    case 1:
                        return "Title2";
                    case 2:
                        return "Title3";
                }
                return super.getPageTitle(position);
            }
        });
    }
}
