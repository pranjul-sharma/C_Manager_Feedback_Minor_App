package com.feedback.test.hr_emp_feedback_sys;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FeedbackViewFragment extends Fragment {

    ProgressDialog progressDialog ;
    public static FeedbackViewFragment newInstance() {
        return new FeedbackViewFragment();
    }
    public FeedbackViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_feedback_view, container, false);
        final TextView textView = view.findViewById(R.id.placeholder);
        final RecyclerView recyclerView = view.findViewById(R.id.recycler);
        String url="http://cmanager.000webhostapp.com/feedbacks/read";
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("please wait...");
        showDialog();
        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs",MODE_PRIVATE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray array = new JSONArray(s);
                            Log.v("vlaue feedback",s);
                            int len = array.length();
                            String[] titles = new String[len];
                            String[] givenBy = new String[len];
                            String[] contents = new String[len];
                            for (int i = 0; i < len; i++) {
                                JSONObject object = array.getJSONObject(i);
                                titles[i] = object.getString("title");
                                contents[i] = object.getString("content");
                                String firstName = object.getJSONObject("user").getString("first_name");
                                String lastName = object.getJSONObject("user").getString("last_name");
                                givenBy[i] = firstName + " " + lastName;
                            }

                            FeedbackAdapter adapter = new FeedbackAdapter(getContext(),titles,contents,givenBy);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                            recyclerView.setAdapter(adapter);
                            hideDialog();
                            if (adapter.getItemCount() != 0 ){
                                textView.setVisibility(View.GONE);
                            }
                            if (adapter.getItemCount() == 0){
                                textView.setText("You  haven't received any feedback yet.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Snackbar.make(view,"Some error occured",Snackbar.LENGTH_LONG).show();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map= new HashMap<>();
                map.put("user_id",sharedPreferences.getString(FeedbackContract.PrefsConst.USER_ID,null));
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(stringRequest);
        return view;
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
