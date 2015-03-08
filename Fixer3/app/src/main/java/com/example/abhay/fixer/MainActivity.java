package com.example.abhay.fixer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private GoogleMap Mapper;
    Button getnew, done;
    TextView responder;
    String latitude, longitude, Description, alfa, ID;
    Double latp, lonp;
    Marker fixme;
    ImageView pic;
    ProgressDialog pDialog;
    Bitmap bmp;
    GPS_loc locer;
    private static LatLng loc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }


    public void initialize() {
        Mapper = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        getnew = (Button) findViewById(R.id.bmark);
        done = (Button) findViewById(R.id.bDone);
        responder = (TextView) findViewById(R.id.tvinfo);
        pic = (ImageView) findViewById(R.id.ivpic);
        locer=new GPS_loc(MainActivity.this);
        getnew.setOnClickListener(this);
        done.setOnClickListener(this);
        done.setEnabled(false);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bmark:
                Thread posty = new Thread() {
                    public void run() {
                        getTask();
                    }
                };
                posty.start();
                break;

            case R.id.bDone:
                if(Description.equals("NO MORE COMPLAINTS LEFT")){
                    break;
                }
                else {
                    isNear();
                    Thread doner = new Thread() {
                        public void run() {
                            FinishTask(ID);
                        }
                    };
                    doner.start();
                    break;
                }
        }
    }

    public void getTask() {
        HttpPost post = new HttpPost("http://5.175.131.243:8090/");
        HttpClient client = new DefaultHttpClient();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("abhay447"," "));
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            HttpResponse response = client.execute(post);
            alfa = EntityUtils.toString(response.getEntity());
            manipulate(alfa);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    responder.setText("ID=" + ID + "\nDescription=" + Description + "\nLatitude=" + latitude + "\nLongitude" + longitude);
                    mark();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                done.setEnabled(true);
            }
        });

    }

    public void mark() {
        latp = Double.parseDouble(latitude);
        lonp = Double.parseDouble(longitude);
        loc = new LatLng(latp, lonp);
        if (Mapper != null) {
            if (fixme != null) {
                fixme.setVisible(false);
            }
            fixme = Mapper.addMarker(new MarkerOptions().position(loc).title("fix me"));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(loc, 16);
            Mapper.animateCamera(update);
            new LoadImage().execute("http://5.175.131.243:8070/" + ID + ".png");

        }
    }

    public void manipulate(String s) {
        String a[] = s.split(",");
        ID = a[0];
        Description = a[1];
        latitude = a[2];
        longitude = a[3];
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();
        }

        protected Bitmap doInBackground(String... args) {
            try {
                bmp = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                pic.setImageBitmap(image);
                pDialog.dismiss();
            } else {
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void FinishTask(String ID) {
        HttpPost post = new HttpPost("http://5.175.131.243:8080/");
        HttpClient client = new DefaultHttpClient();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("UPDATE REPORTS SET STATUS='COMPLETED' WHERE ID='"+ID+"'"," "));
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            HttpResponse response = client.execute(post);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void isNear(){
        final Location currentLocation=locer.locater();
        final Location problemLocation=new Location("");
        problemLocation.setLatitude(latp);
        problemLocation.setLongitude(lonp);
        double distance=currentLocation.distanceTo(problemLocation);
        Toast.makeText(MainActivity.this,String.valueOf(distance), Toast.LENGTH_SHORT).show();
    }
}