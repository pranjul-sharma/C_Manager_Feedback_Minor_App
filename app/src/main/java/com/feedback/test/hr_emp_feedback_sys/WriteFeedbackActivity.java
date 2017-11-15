package com.feedback.test.hr_emp_feedback_sys;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WriteFeedbackActivity extends AppCompatActivity {

    List<String> receiver_ids;
    List<String> receivers;
    Spinner spinner;
    ProgressDialog progressDialog;
    EditText ed_tite;
    EditText ed_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_feedback);
        Toolbar toolbar = findViewById(R.id.toolbar_write_feed);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Write Feedback");

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
        spinner = findViewById(R.id.spinner_feedback);
        ed_tite = findViewById(R.id.ed_title_feedback);
        ed_content = findViewById(R.id.ed_content_feedback);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showDialog();
        final SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        StringRequest request = new StringRequest(Request.Method.POST, "http://cmanager.000webhostapp.com/feedbacks/getRecievers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        receiver_ids = new ArrayList<>();
                        receivers = new ArrayList<>();
                        try {
                            Log.v("feedback receiver",s);
                            JSONObject jsonObject = new JSONObject(s);
                            Iterator<String> keys = jsonObject.keys();
                            while (keys.hasNext()){
                                String key = keys.next();
                                receiver_ids.add(key);
                                receivers.add(jsonObject.getString(key));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.spinner_item,receivers);
                            adapter.setDropDownViewResource(R.layout.simple_dropdown_item);
                            spinner.setAdapter(adapter);
                            hideDialog();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideDialog();
                Toast.makeText(getApplicationContext(),"Some error occurred. Try reopening this page.",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("user_id",sharedPreferences.getString(FeedbackContract.PrefsConst.USER_ID,null));
                map.put("type_id",sharedPreferences.getString(FeedbackContract.PrefsConst.TYPE_ID,null));
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                ((TextView)view).setTextColor(getResources().getColor(R.color.colorPrimary));
                ((TextView)view).setTextColor(Color.BLACK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    public void submitFeedback(View view) {
        showDialog();
        final SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
        StringRequest request = new StringRequest(Request.Method.POST, "http://cmanager.000webhostapp.com/feedbacks/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hideDialog();
                        if (s.contains("success")){
                            Snackbar.make(getCurrentFocus(),"Feedback saved successfully.",Snackbar.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Error occurred",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideDialog();
                Toast.makeText(getApplicationContext(),"Some error occurred. Try reopening this page.",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("user_id",sharedPreferences.getString(FeedbackContract.PrefsConst.USER_ID,null));
                map.put("reciever_id",receiver_ids.get(spinner.getSelectedItemPosition()));
                map.put("title",ed_tite.getText().toString());
                map.put("content",ed_content.getText().toString());
                return map;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);

    }
    public void showDialog(){
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    public void hideDialog(){
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
