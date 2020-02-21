package edu.quinnipiac.ser210.ls07_numberapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    YearsHandler yrHandler = new YearsHandler();
    ShareActionProvider provider;
    boolean userSelect = false;
    private String url1 = "https://numbersapi.p.rapidapi.com/";
    private String url2= "/year?fragment=true&json=true";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);

        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,yrHandler.years);

        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(yearsAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (userSelect) {
                    final String item = (String) parent.getItemAtPosition(position);
                    Log.i("onItemSelected :year", item);

                    //TODO : call of async subclass goes here
                    new FetchYearFact().execute(item);

                    userSelect = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userSelect = true;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        provider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        if (provider == null)
            Log.d("MainActivity", "noshare provider");

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Hi there");
            if (provider != null) {
                provider.setShareIntent(intent);
            } else
                Toast.makeText(this, "no provider", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

        private class FetchYearFact extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection =null;
            String yearFact = "";
            try{
                URL url = new URL(url1 + strings[0]
                        + url2);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("X-RapidAPI-Key","UygwA3LnI1mshAPcqbrTdu6rvUkxp1Kd1q6jsnETjeLq2t3LzS");

                urlConnection.connect();


                if (urlConnection.getInputStream() == null) {
                    Log.e("no connection", "no connection");
                    return null;
                }
                yearFact = getStringFromBuffer(
                        new BufferedReader(new InputStreamReader(urlConnection.getInputStream())));
                Log.d("yearFact", yearFact);
            }catch (Exception e){
                Log.e("MainActivity","Error" + e.getMessage());
                return null;
            }finally {
                if(urlConnection !=null)
                    urlConnection.disconnect();
            }

            return yearFact;
        }

        private String getStringFromBuffer(BufferedReader bufferedReader) throws Exception {
            StringBuffer buffer = new StringBuffer();
            String line;

            while((line = bufferedReader.readLine()) != null){
                buffer.append(line + '\n');

            }
            if (bufferedReader !=null){
                try{
                    bufferedReader.close();
                }catch (IOException e){
                    Log.e("MainActivity","Error" + e.getMessage());
                    return null;
                }
            }
            Log.d("year fact", buffer.toString());
            return  yrHandler.getYearFact(buffer.toString());
        }

        @Override
        protected void onPostExecute(String result) {
            if(result != null){
                Log.d("MainActivity",result);
                Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                intent.putExtra("yearFact",result);
                startActivity(intent);
            }

        }
    }
}
