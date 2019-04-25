package br.com.bossini.usjt_ccp3anmca_weather_forecast;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView weatherRecyclerView;
    private WeatherAdapter weatherAdapter;
    private List<Weather> previsoes;
    private EditText locationEditText;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(this);
        locationEditText = findViewById(R.id.locationEditText);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        weatherRecyclerView =
                findViewById(R.id.weatherRecyclerView);
        previsoes = new ArrayList<>();
        previsoes.add(new Weather(
            500,
            "Teste 1",
            37,
            38,
            0.7,
            ""
        ));
        previsoes.add(new Weather(
                554345822,
                "Teste 2",
                37,
                38,
                0.7,
                ""
        ));
        weatherAdapter = new WeatherAdapter(this, previsoes);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        weatherRecyclerView.setAdapter(weatherAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeCidade =
                        locationEditText.getEditableText().toString();
                obtemPrevisoesV5(nomeCidade);
            }
        });
    }
    public void obtemPrevisoesV5 (String cidade){
        String endereco = getString(
                R.string.web_service_url,
                cidade,
                getString(R.string.api_key)
        );
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                endereco,
                null,
                (response) ->{
                    lidaComJSON(response);
                },
                (error) -> {
                    Toast.makeText(this,
                            getString(R.string.connect_error) + error.getLocalizedMessage(),
                            Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(req);

    }
    public void obtemPrevisoesV4 (String cidade){
        String endereco = getString(
                R.string.web_service_url,
                cidade,
                getString(R.string.api_key)
        );
        new ObtemPrevisoes().execute(endereco);
    }

    public void obtemPrevisoesV3 (String cidade){
        new Thread(()-> {
            String endereco = getString(
                    R.string.web_service_url,
                    cidade,
                    getString(R.string.api_key)
            );
            try {
                URL url = new URL (endereco);
                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder resultado = new StringBuilder ("");
                String aux = null;
                while ((aux = reader.readLine()) != null)
                    resultado.append(aux);
                reader.close();
                conn.disconnect();
                runOnUiThread(() -> {
                    lidaComJSON(resultado.toString());
                    //Toast.makeText(this, resultado.toString(), Toast.LENGTH_SHORT).show();
                });
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    class ObtemPrevisoes extends AsyncTask <String, Void, String>{

        @Override
        protected String doInBackground(String... enderecos) {
            String endereco = enderecos[0];
            try {
                URL url = new URL (endereco);
                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder resultado = new StringBuilder ("");
                String aux = null;
                while ((aux = reader.readLine()) != null)
                    resultado.append(aux);
                reader.close();
                conn.disconnect();
                return resultado.toString();
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String resultado) {
            lidaComJSON(resultado);
        }
    }

    public void lidaComJSON (JSONObject resultado){
        try {
            previsoes.clear();
            JSONArray list = resultado.getJSONArray("list");
            for (int i = 0; i < list.length(); i++){
                JSONObject caraDaVez = list.getJSONObject(i);
                long dt = caraDaVez.getLong("dt");
                JSONObject main = caraDaVez.getJSONObject("main");
                double temp_min = main.getDouble("temp_min");
                double temp_max = main.getDouble("temp_max");
                double humidity = main.getDouble("humidity");
                String description =
                        caraDaVez.
                                getJSONArray("weather").
                                getJSONObject(0).
                                getString("description");
                String icon =
                        caraDaVez.
                                getJSONArray("weather").
                                getJSONObject(0).
                                getString("icon");
                Weather w =
                        new Weather(dt, description, temp_min, temp_max, humidity, icon);
                previsoes.add(w);
            }
            weatherAdapter.notifyDataSetChanged();
            
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void lidaComJSON (String resultado){
        try {
            lidaComJSON(new JSONObject(resultado));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void obtemPrevisoesV2 (String cidade){
        new Thread(()-> {
            String endereco = getString(
                    R.string.web_service_url,
                    cidade,
                    getString(R.string.api_key)
            );
            try {
                URL url = new URL (endereco);
                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                Toast.makeText(this, "acabou...", Toast.LENGTH_SHORT).show();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void obtemPrevisoesV1 (String cidade){
        String endereco = getString(
                R.string.web_service_url,
                cidade,
                getString(R.string.api_key)
        );
        try {
            URL url = new URL (endereco);
            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();

        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
