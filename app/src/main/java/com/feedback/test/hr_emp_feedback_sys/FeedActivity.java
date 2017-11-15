package com.feedback.test.hr_emp_feedback_sys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FeedActivity extends AppCompatActivity {

    NavigationView navigationView;
    View headerView;
    ExpListAdapter listAdapter;
    ExpandableListView expListView;
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> listDataHeader;
    double[] arrCur;
    String[] labels;
    RadioGroup toogleChart;
    DrawerLayout drawer;
    String[] numbers={"50","6.52","32","32"};
    HashMap<String, List<String>> listDataChild;
    BarChart barChart;
    private int numOfReq = 3;
    private boolean isRequestFailed = false;
    double[] arrLast;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dashboard");
        drawer =  findViewById(R.id.drawer_layout);
        expListView = findViewById(R.id.menu_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        barChart = findViewById(R.id.chart_dashboard);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("loading chart data..please wait...");
        prepareListData();
        populateData();
        String[] xvlaues = {"Mon","Tue","Wed","Thur","Fri","Sat","Sun"};
        double[] cur = {.4,.4,.4,.4,.4,.4,.4};
        double[] last = {.7,.7,.7,.7,.7,.7,.7};
        populateChartData(xvlaues,cur,last);
        numbers = new String[4];

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        barChart = (BarChart)findViewById(R.id.chart_dashboard);
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        toggle.syncState();


        Log.v("CALL_ORDER","first");
        listAdapter = new ExpListAdapter(this, listDataHeader,listDataChild);
        expListView.setAdapter(listAdapter);



        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                if (groupPosition == 1) {
                    if (childPosition == 0) {
                        Intent intent = new Intent(FeedActivity.this, FeedProjActivity.class);
                        intent.putExtra("EXTRA_TAG", "view feedback");
                        startActivity(intent);
                    } else if (childPosition == 1) {
                        Intent intent = new Intent(FeedActivity.this, WriteFeedbackActivity.class);
                        startActivity(intent);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

                if (i == 0 ){
                    drawer.closeDrawer(Gravity.START);

                    return true;
                }
                if (i == 3){
                    Intent intent= new Intent(FeedActivity.this,FeedProjActivity.class);
                    intent.putExtra("EXTRA_TAG","view project");
                    startActivity(intent);
                    return true;
                }
                if (i == 2){
                    Intent intent = new Intent(FeedActivity.this,RatingActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;



            }});

//        loadAndRefreshData();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.v("CALL_ORDER","refresh");
                numOfReq=3;
                loadAndRefreshData();

            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                Log.v("CALL_ORDER","second");
                loadAndRefreshData();

            }
        });


        toogleChart  = findViewById(R.id.toggle_chart);
//        RadioButton weekBtn = findViewById(R.id.week_chart);
//        RadioButton monthBtn = findViewById(R.id.month_chart);
        toogleChart.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                StringRequest request=null;
                showDialog();
                numOfReq = 1;
                if (id == R.id.week_chart){
                    Log.v("CHNAGE","selection week");
                    request = chartRequest(1);
                }
                if (id == R.id.month_chart){
                    Log.v("CHNAGE","selection month");
                    request = chartRequest(2);
                }
                request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                AppController.getInstance().addToRequestQueue(request);
            }
        });

    }

    public void showDialog(){
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    public void hideDialog(){
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
    private void loadAndRefreshData(){
        Log.v("CALL_ORDER","third");
        swipeRefreshLayout.setRefreshing(true);

        String[] urls = {"http://cmanager.000webhostapp.com/projects/report-json?option=1",
                "http://cmanager.000webhostapp.com/users/report-json?option=1",
                "http://cmanager.000webhostapp.com/feedbacks/report-json?option=1",
                "http://cmanager.000webhostapp.com/ratings/report-json?option=1",
                "http://cmanager.000webhostapp.com/ratings/report-json?option=2"};
        StringRequest stringRequest[] = new StringRequest[3];
        stringRequest[0] = new StringRequest(Request.Method.GET, urls[0],
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        numOfReq--;
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            numbers[0] = String.valueOf( jsonArray.get(0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(checkForResponses()){
                            Log.v("success 1","response");
                            populateData();
                            populateChartData(labels,arrCur,arrLast);
                        }
                        Log.v("response received","1");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                numOfReq--;
                    numbers[0]="0";
                isRequestFailed=true;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
//        stringRequest[1] = new StringRequest(Request.Method.GET, urls[1],
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                    //    numbers[2]=Integer.parseInt(s);
//                        numOfReq--;
//                        numbers[2]="23";
//                        Log.v("response received","2");
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                numbers[2]="0";
//                numOfReq--;
//                isRequestFailed=true;
//            }
//        });
        stringRequest[1] = new StringRequest(Request.Method.GET, urls[2],
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        numOfReq--;
                        numbers[2]="3";
//                        numbers[3]=String.valueOf(Integer.parseInt(s));
                        numbers[3]=s.substring(1,s.length()-1);
                        if(checkForResponses()){
                            Log.v("success 2","response");
                            populateData();
                            populateChartData(labels,arrCur,arrLast);
                        }

                        Log.v("response received","3");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                numbers[3]="0";
                numOfReq--;
                isRequestFailed=true;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        int option = 1;
        if(toogleChart.getCheckedRadioButtonId()==R.id.month_chart){
            option = 2;
        }
        stringRequest[2]=chartRequest(option);
        Log.v("CALL_ORDER","back in third");
        AppController.getInstance().cancelPendingRequests(AppController.TAG);
        for (StringRequest request : stringRequest) {
            request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppController.getInstance().addToRequestQueue(request);
        }


    }

    private StringRequest chartRequest(int option){
        Log.v("CALL_ORDER","fourth");
        String url = "http://cmanager.000webhostapp.com/ratings/report-json?option=";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + option,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        numOfReq--;
                        try {
                            Log.v("response received","4");
                            JSONArray array = new JSONArray(s);
                            JSONArray arrayCur = array.getJSONArray(0);
                            JSONArray arrayLast = array.getJSONArray(1);
                            JSONArray arrayLabels = array.getJSONArray(2);
                            numbers[1] =String.valueOf(array.getDouble(5));
                            int len = arrayCur.length();
                            arrCur=new double[len];
                            arrLast = new double[len];
                            for(int i=0;i<arrayCur.length();i++){
                                arrCur[i]=arrayCur.getDouble(i);
                                arrLast[i]=arrayLast.getDouble(i);
                            }
                            labels = new String[arrayLabels.length()];
                            for (int i=0;i<arrayLabels.length();i++)
                                labels[i]=arrayLabels.getString(i);
                            Log.v("numbers ",numbers.toString());
                            if(checkForResponses()){
                                Log.v("success 3","response");
                                populateData();
                                populateChartData(labels,arrCur,arrLast);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                numOfReq--;
                isRequestFailed=true;
                Toast.makeText(getApplicationContext(),"Some error occurred",Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        return stringRequest;
    }

    public boolean checkForResponses(){
        boolean val=false;
        if (numOfReq == 0 && !isRequestFailed)
            val=true;
        return val;

    }
    final void populateData(){
        Log.v("CALL_ORDER","from fourth fifth");
        swipeRefreshLayout.setRefreshing(false);
        //expListView.setIndicatorBounds(expListView.getRight()-40,expListView.getWidth());

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_dashboard);
        String[] desc = {"Projects at hand","Average Rating","Total Employees","Todays's Feedbacks"};
        Log.v("NUMBERS",numbers[0]+" "+numbers[1]+" "+numbers[2]+" "+numbers[3]);
        MyCardAdpater adapter = new MyCardAdpater(this,numbers,desc);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(adapter);
        // floatingActionButton = findViewById(R.id.fab_feed);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        listDataHeader.add("Home");
        listDataHeader.add("Feedbacks");
        listDataHeader.add("Ratings");
        listDataHeader.add("Project Details");

        List<String> feedbacks = new ArrayList<>();
        feedbacks.add("View Feedbacks");
        feedbacks.add("Write Feedbacks");

        listDataChild.put(listDataHeader.get(1),feedbacks);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent intent = new Intent(FeedActivity.this,LogInActivity.class);
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(FeedbackContract.PrefsConst.USER_ID, null);
            editor.putString(FeedbackContract.PrefsConst.USER_NAME, null);
            editor.putString(FeedbackContract.PrefsConst.FIRST_NAME, null);
            editor.putString(FeedbackContract.PrefsConst.LAST_NAME, null);
            editor.putString(FeedbackContract.PrefsConst.TYPE_ID, null);
            editor.putString(FeedbackContract.PrefsConst.USER_TYPE_NAME, null);
            editor.putString(FeedbackContract.PrefsConst.REGISTRATION_NUMBER, null);
            editor.putString(FeedbackContract.PrefsConst.CONTACT, null);
            editor.putString(FeedbackContract.PrefsConst.ADDRESS, null);
            editor.apply();

            startActivity(intent);
            Toast.makeText(getApplicationContext(),"User logged out",Toast.LENGTH_LONG).show();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void populateChartData(String[] xvalues,double[] cur,double[] last){
        Log.v("CALL_ORDER","from fourth sixth");
        barChart.setDescription(null);
        barChart.setScaleEnabled(false);

        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setPinchZoom(false);
//        barChart.setDrawValueAboveBar(true);
        XAxis xAxis = barChart.getXAxis();
        if (xvalues[0].contains("Mon")) {
            for (int i = 0; i < xvalues.length; i++) {
                xvalues[i] = xvalues[i].substring(0, 3);
            }
        }


        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(6);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xvalues));

        ArrayList<BarEntry> barGrp1=new ArrayList<>();
        ArrayList<BarEntry> barGrp2=new ArrayList<>();
        for (int i=0;i<xvalues.length;i++){
            barGrp1.add(new BarEntry(i,(float)cur[i]));
            barGrp2.add(new BarEntry(i,(float)last[i]));
        }
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);

        barChart.getAxisRight().setEnabled(false);

        float groupSpace = 0.06f;
        float barSpace = 0.02f;
        float barWidth = 0.45f;

        BarDataSet set1,set2;

        set1 = new BarDataSet(barGrp1, "Current Month");
        set1.setColor(Color.rgb(255, 171, 0));
        set2 = new BarDataSet(barGrp2, "Last Month");
        set2.setColor(Color.rgb(213, 0, 0));
        BarData data = new BarData(set1,set2);

        data.setValueFormatter(new LargeValueFormatter());
        barChart.setData(data);
        set1.setDrawValues(false);
        set2.setDrawValues(false);
        hideDialog();
        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMaximum(0);
        barChart.getXAxis().setAxisMaximum(0+barChart.getBarData().getGroupWidth(groupSpace,barSpace)*xvalues.length);
        barChart.groupBars(0, groupSpace, barSpace);
        barChart.getData().setHighlightEnabled(false);
        barChart.animateXY(500,500);
        barChart.invalidate();

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setYOffset(20f);
        l.setXOffset(0f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);
    }
}
