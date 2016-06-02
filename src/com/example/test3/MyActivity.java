package com.example.test3;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import okhttp3.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyActivity extends Activity implements SensorEventListener {
    /**
     * Called when the activity is first created.
     */
    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private Sensor mAccelerometer;

    private float xy_angle;
    private float xz_angle;
    private float zy_angle;

    private TextView xyView;
    private TextView xzView;
    private TextView zyView;
    TextView tvLocation;

    String EnabledGPS;
    String EnabledNet;
    String Location="none";
    String StatusGPS;
    String StatusNet;

    String address = "http://roads.seo-group.com.ua/";

    private LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        tvLocation = (TextView) findViewById(R.id.tvLocation);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
       // makeGetRequest(Location);

       // mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // Получаем менеджер сенсоров
       // mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); // Получаем датчик положения
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        xyView = (TextView) findViewById(R.id.xyValue);  //
        xzView = (TextView) findViewById(R.id.xzValue);  // Наши текстовые поля для вывода показаний
        zyView = (TextView) findViewById(R.id.zyValue);  //
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1, 10,
                locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            try {
                showLocation(location);
                tvLocation.setText(Location);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception e){}
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();

            try {
                showLocation(locationManager.getLastKnownLocation(provider));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (Exception ex){}
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                StatusGPS = ("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                StatusNet = ("Status: " + String.valueOf(status));
            }
        }
    };

    public void showLocation(Location location) throws MalformedURLException, Exception {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            Location = (formatLocation(location));
            tvLocation.setText(Location);
            myRequest();
            /*HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(address);

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("212.115.253.12", Location));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                String dataAsString= EntityUtils.toString(response.getEntity());
                tvLocation.setText(dataAsString);
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }*/
            //getData(1,Location,address);
            //makeGetRequest(Location);
        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            Location = (formatLocation(location));
            tvLocation.setText(Location);
            //getData(1,Location,address);
            //makeGetRequest(Location);
              myRequest();         }


            /*HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(address);

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("212.115.253.12", Location));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                String dataAsString= EntityUtils.toString(response.getEntity());
                tvLocation.setText(dataAsString);
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
*/





    }
 private void myRequest() throws Exception{
     HttpClient Client = new DefaultHttpClient();

     // Create URL string
    if (!Location.equals("none")){
     String URL = address+"?"+Location + "&" + String.valueOf(xy_angle) +
     "&"+String.valueOf(xz_angle)+"&"+String.valueOf(zy_angle);

    Log.i("httpget", URL);

     try
     {
         String SetServerString = "";

         // Create Request to server and get response

         HttpGet httpget = new HttpGet(URL);
         ResponseHandler<String> responseHandler = new BasicResponseHandler();
         SetServerString = Client.execute(httpget, responseHandler);

         // Show response on activity


     }
     catch(Exception ex)
     {

     }
    }
 }

    private void makeGetRequest(String s) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(address);
        // replace with your url

        HttpResponse response;
        try {

                    request.addHeader(null,null);

                    response = client.execute(request);

            Log.d("grebanyj - clientId", request.toString());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

/*
    public String getData(int _timeout, String encoding, String _url) {
        try {
            HttpURLConnection connect = (HttpURLConnection) new URL(_url).openConnection();
            connect.setRequestMethod("GET");
            connect.setRequestProperty("Content-length", "0");
            connect.setUseCaches(false);
            connect.setAllowUserInteraction(false);
            connect.setConnectTimeout(_timeout);
            connect.setReadTimeout(_timeout);
            connect.connect();

            int status = connect.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            connect.getInputStream(), encoding));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }
        } catch (MalformedURLException ex) {
//код обработки ошибки
        } catch (IOException ex) {
//код обработки ошибки
        }
        return null;
    }
*/

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "%1$.9f|%2$.9f|%3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void checkEnabled() {

        EnabledGPS =("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        EnabledNet =("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void onClickLocationSettings(View view) {
        startActivity(new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { //Изменение точности показаний датчика
        tvLocation.setText(Location);
        try {
            myRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void onSensorChanged(SensorEvent event) { //Изменение показаний датчиков
        xy_angle = event.values[0]; //Плоскость XY
        xz_angle = event.values[1]; //Плоскость XZ
        zy_angle = event.values[2]; //Плоскость ZY

        xyView.setText(String.valueOf(xy_angle));
        xzView.setText(String.valueOf(xz_angle));
        zyView.setText(String.valueOf(zy_angle));
        //makeGetRequest(Location + "&" + String.valueOf(xy_angle) +
        //"&"+String.valueOf(xz_angle)+"&"+String.valueOf(zy_angle));

        //showLocation(location);
        onResume();
        tvLocation.setText(Location);
        try {
            myRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
