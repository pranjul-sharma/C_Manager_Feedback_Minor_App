package com.feedback.test.hr_emp_feedback_sys;

import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LogInActivity extends AppCompatActivity {

    Button btnlogin;
    EditText username;
    EditText password;
    ProgressDialog progressDialog;
    ProgressBar circle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        circle = new ProgressBar(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging you in");


        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        username = (EditText)findViewById(R.id.et_username);
        password = (EditText)findViewById(R.id.et_passwrd);
        btnlogin = (Button)findViewById(R.id.btn_login);
        final String urlLogin = "http://cmanager.000webhostapp.com/users/login";
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(isNetworkEnabled()) {

                    if (validEntries()) {
                        showDialog();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlLogin
                                , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Log.v("JSON_FROM_VOLLEY", s);
                                hideDialog();
                                if (s.contains("Wrong Credentials"))
                                    Snackbar.make(view, "Username/Password does not match.", Snackbar.LENGTH_LONG).show();
                                else if (s.contains("Please send"))
                                    Snackbar.make(view, s.substring(1, s.length() - 2), Snackbar.LENGTH_LONG).show();
                                else {
                                    parseData(s);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.v("VOLLEY_ERROR", volleyError.toString());
                                hideDialog();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("username", username.getText().toString());
                                map.put("password", password.getText().toString());
                                map.put("rtype", "json");
                                return map;
                            }
                        };
                        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        AppController.getInstance().addToRequestQueue(stringRequest);
                    } else {
                        Snackbar.make(view, "please fill username/password", Snackbar.LENGTH_LONG).show();
                    }
                }else {
                    Snackbar.make(view, "No Internet Connection.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean validEntries(){
        return !(username.getText().toString().equals("") || password.getText().toString().equals(""));
    }

    private void parseData(String s){
        try {
            JSONObject jsonObject = new JSONObject(s);
            String id = jsonObject.getString("id");
            String type_id = jsonObject.getString("type_id");
            String username = jsonObject.getString("username");
            String first_name = jsonObject.getString("first_name");
            String last_name = jsonObject.getString("last_name");
            String registeraton_number = jsonObject.getString("registeraton_number");
            String contact = jsonObject.getString("contact");
            String address = jsonObject.getString("address");
            JSONObject user_type_details = jsonObject.getJSONObject("type");
            String type_name = user_type_details.getString("name");

            JSONArray menu_array = jsonObject.getJSONArray("menus");
            for (int i = 0; i < menu_array.length(); i++) {

            }

            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(FeedbackContract.PrefsConst.USER_ID, id);
            editor.putString(FeedbackContract.PrefsConst.USER_NAME, username);
            editor.putString(FeedbackContract.PrefsConst.FIRST_NAME, first_name);
            editor.putString(FeedbackContract.PrefsConst.LAST_NAME, last_name);
            editor.putString(FeedbackContract.PrefsConst.TYPE_ID, type_id);
            editor.putString(FeedbackContract.PrefsConst.USER_TYPE_NAME, type_name);
            editor.putString(FeedbackContract.PrefsConst.REGISTRATION_NUMBER, registeraton_number);
            editor.putString(FeedbackContract.PrefsConst.CONTACT, contact);
            editor.putString(FeedbackContract.PrefsConst.ADDRESS, address);
            editor.apply();

            Intent intent = new Intent(LogInActivity.this, FeedActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"Error while parsing data. Please try again later.",Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog(){
        if (!progressDialog.isShowing()){
            progressDialog.show();

        }
    }

    private void hideDialog(){
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private boolean isNetworkEnabled(){
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(LogInActivity.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo= null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo!=null && activeNetworkInfo.isConnected();
    }
}
