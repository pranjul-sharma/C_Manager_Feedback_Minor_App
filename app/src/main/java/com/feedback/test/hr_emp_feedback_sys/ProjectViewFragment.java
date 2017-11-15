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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectViewFragment extends Fragment {

    ProgressDialog progressDialog;
    public static ProjectViewFragment newInstance() {
        return new ProjectViewFragment();
    }

    public ProjectViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_project_view, container, false);
        final TextView textView = view.findViewById(R.id.placeholder);
        final RecyclerView recyclerView = view.findViewById(R.id.recycler);
        String url="http://cmanager.000webhostapp.com/projects/info";
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
                            Log.v("value project",s);
                            int len = array.length();
                            String[] leaders = new String[len];
                            String[] titles = new String[len];
                            String[] contents = new String[len];
                            List<List<String>> teams = new ArrayList<>();

                            for (int i = 0; i < len ; i++ ){

                                JSONObject object = array.getJSONObject(i);
                                titles[i] = object.getString("title");
                                contents[i] = object.getString("content");
                                leaders[i] = object.getString("leader");
                                JSONArray jsonArray = object.getJSONArray("team");
                                List<String> tempTeam = new ArrayList<>();
                                for(int j=0;j<jsonArray.length();j++){
                                    tempTeam.add(j,jsonArray.getString(j));
                                }
                                teams.add(i,tempTeam);

                            }
                            ProjectAdapter adapter = new ProjectAdapter(getContext(),titles,contents,leaders,teams);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                            recyclerView.setAdapter(adapter);
                            hideDialog();
                            if (adapter.getItemCount() != 0 ){
                                textView.setVisibility(View.GONE);
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
