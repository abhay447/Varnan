package com.example.abhay.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Button capture,submit,newcom;
    ImageView pic;
    TextView problem,responder;
    Intent i;
    Bitmap bmp;
    GPS_loc locer;
    String Gpsser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize(){
        pic=(ImageView)findViewById(R.id.ivpic);
        capture=(Button)findViewById(R.id.bcapture);
        problem=(TextView)findViewById(R.id.tvproblem);
        submit=(Button)findViewById(R.id.bsubmit);
        newcom=(Button)findViewById(R.id.bNew);
        capture.setOnClickListener(this);
        submit.setOnClickListener(this);
        newcom.setOnClickListener(this);
        locer=new GPS_loc(MainActivity.this);
        responder=(TextView)findViewById(R.id.tv1);

    }

    private void freeze(){
        capture.setEnabled(false);
        submit.setEnabled(false);
        problem.setEnabled(false);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.bcapture:
                i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,REQUEST_IMAGE_CAPTURE);
                break;

            case R.id.bsubmit:
                if(bmp==null){
                    Toast.makeText(getApplicationContext(),"Please take a pic",Toast.LENGTH_LONG).show();
                    break;
                }
                saver(bmp);
                final Location location=locer.locater();
                double latitude =location.getLatitude();
                double longitude=location.getLongitude();
                Gpsser=Double.toString(latitude)+","+Double.toString(longitude);

                Thread posty=new Thread(){
                public void run(){
                    postData(Gpsser);
                }
                };
                if(location!=null) {
                    posty.start();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enable GPS or Network", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.bNew:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                break;
        }
    }



    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Bundle extras = data.getExtras();
            bmp = (Bitmap)extras.get("data");
            pic.setImageBitmap(bmp);
        }
    }


    public void postData(String Gpsser){
            runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responder.setText("Registering Complaint");
            }
        });
            String path = Environment.getExternalStorageDirectory().toString();
            HttpPost post = new HttpPost("http://serverip:8080/");
            HttpClient client = new DefaultHttpClient();
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("file", new FileBody(new File(path,"hola.png")));
            builder.addTextBody("INSERT INTO REPORTS (DESCRIPTION,GPS,STATUS,USERID,FORWARDEDTO) VALUES("
                    +"'"+problem.getText().toString()+"'"+
                    ",'"+Gpsser+"'"+
                    ",'REGISTERED'"+"" +
                    ",'abhay.kchaturvedi.civ13@itbhu.ac.in'"+
                    ",'ORGANISATION')","");
            post.setEntity(builder.build());
            HttpResponse response = null;
            try {
                response = client.execute(post);
            } catch (IOException e) {
                e.printStackTrace();
            }
           HttpEntity entity = response.getEntity();
           runOnUiThread(new Runnable() {
            @Override
            public void run() {
                responder.setText("Complaint registered");freeze();
            }
            });
    }
    public  void saver(Bitmap bmp) {

        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, "hola.png");
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        bmp.compress(Bitmap.CompressFormat.PNG,100, fOut);
        if (fOut != null) {
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fOut != null) {
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
