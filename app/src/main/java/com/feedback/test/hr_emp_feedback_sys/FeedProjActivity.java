package com.feedback.test.hr_emp_feedback_sys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedProjActivity extends AppCompatActivity {



    private ViewPager mViewPager;
    private TabLayout tabLayout;
    FloatingActionButton floatingActionButton;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_feed);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mViewPager = (ViewPager)findViewById(R.id.container);

        tabLayout = (TabLayout)findViewById(R.id.tabs);

        floatingActionButton = (FloatingActionButton)findViewById(R.id.fab_feed);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FeedProjActivity.this,WriteFeedbackActivity.class);
                startActivity(intent);
            }
        });

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void setUpViewPager(ViewPager mViewPager){
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        sectionsPagerAdapter.addFragment(FeedbackViewFragment.newInstance(),"Feedbacks");
        sectionsPagerAdapter.addFragment(ProjectViewFragment.newInstance(),"Projects");

        mViewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpViewPager(mViewPager);

        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_view_feeds);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_project_box);
        Intent intent = getIntent();
        String tag = intent.getStringExtra("EXTRA_TAG");
        Log.v("TAG_FOUND",tag);
        if (tag.equals("view feedback")){
            mViewPager.setCurrentItem(0);
            Log.v("TAG_FOUND 1",tag+" setting current item to feedback");
        }

        if (tag.equals("view project")){
            mViewPager.setCurrentItem(1);
            Log.v("TAG_FOUND 2",tag+" setting current item to project");
        }

        String[] strings={"Feedbacks","Projects"};
        getSupportActionBar().setTitle(strings[tabLayout.getSelectedTabPosition()]);

    }



}
