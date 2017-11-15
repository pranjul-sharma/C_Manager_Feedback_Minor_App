package com.feedback.test.hr_emp_feedback_sys;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RatingActivity extends AppCompatActivity {

    TextView[] textViews;
    EditText ed_note;
    SeekBar[] seekBars;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ratings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textViews = new TextView[5];
        textViews[0] = (TextView)findViewById(R.id.tt_seek_1);
        textViews[1] = (TextView)findViewById(R.id.tt_seek_2);
        textViews[2] = (TextView)findViewById(R.id.tt_seek_3);
        textViews[3] = (TextView)findViewById(R.id.tt_seek_4);
        textViews[4] = (TextView)findViewById(R.id.tt_seek_5);

        ed_note = (EditText)findViewById(R.id.edit_note);
        seekBars = new SeekBar[5];
        seekBars[0] = (SeekBar)findViewById(R.id.seek_bar_1);
        seekBars[1] = (SeekBar)findViewById(R.id.seek_bar_2);
        seekBars[2] = (SeekBar)findViewById(R.id.seek_bar_3);
        seekBars[3] = (SeekBar)findViewById(R.id.seek_bar_4);
        seekBars[4] = (SeekBar)findViewById(R.id.seek_bar_5);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("How it works!");
        dialog.setMessage("Rate the Company based upon the criteria that have been mentioned below.\n" +
                "After you submit it, the overall average of the ratings will be calculated and submitted to the Management Team.\n" +
                "\nNote of Advice is optional and maybe you need to scroll vertically if submit button is not visible");

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog  =  dialog.create();
        alertDialog.show();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        for (int i=0;i<5;i++){
            textViews[i].setText(String.format(Locale.getDefault(),"%.1f",seekBars[i].getProgress()/10.0));
            final int tempI = i;
            seekBars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    textViews[tempI].setText(String.format(Locale.getDefault(),"%.1f",seekBars[tempI].getProgress()/10.0));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    textViews[tempI].setText(String.format(Locale.getDefault(),"%.1f",seekBars[tempI].getProgress()/10.0));
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    textViews[tempI].setText(String.format(Locale.getDefault(),"%.1f",seekBars[tempI].getProgress()/10.0));
                }
            });
        }
    }

    public void submitRating(View view) {
        float sum = 0;
        for(TextView textView :textViews){
            sum += Float.valueOf(textView.getText().toString());
        }
        final float rating=sum/5;
        showDialog();
        StringRequest request  = new StringRequest(Request.Method.POST, "http://cmanager.000webhostapp.com/ratings/add",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        hideDialog();
                        if (s.contains("success"))
                            Snackbar.make(getCurrentFocus(),"Your rating is successfully saved.",Snackbar.LENGTH_LONG).show();
                        if (s.contains("Already Rated"))
                            Snackbar.make(getCurrentFocus(),"You have already rated your day. Please try next day.",Snackbar.LENGTH_LONG).show();
                        else
                            Snackbar.make(getCurrentFocus(),s,Snackbar.LENGTH_LONG).show();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                hideDialog();
                Snackbar.make(getCurrentFocus(),"Some error occurred. Please try later.",Snackbar.LENGTH_SHORT).show();
            }
        }){
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs",MODE_PRIVATE);
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("user_id",sharedPreferences.getString(FeedbackContract.PrefsConst.USER_ID,null));
                map.put("rating",String.valueOf(rating));
                map.put("note",ed_note.getText().toString());
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
