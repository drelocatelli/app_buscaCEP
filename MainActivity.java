package com.example.buscacep;

import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.os.StrictMode;

import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    public TextView cepInput;
    public TextView resultOut;
    public String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cepInput = findViewById(R.id.CEP);
        resultOut = findViewById(R.id.result);

    }

    public void Dialog(View view){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Resultado");
        dialog.setMessage(msg);
        dialog.show();
    }


    public void getCep(View view) {
        String cep = cepInput.getText().toString();

        String webservice = "https://viacep.com.br/ws/" + cep + "/json";
        String method = "GET";

        try {

            // conexao
            URL url = new URL(webservice);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept", "application/json");

            // permissoes
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            if (conn.getResponseCode() != 200)
                throw new RuntimeException("HTTP ERROR CODE " + conn.getResponseCode());

            // Input
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String output;

            while((output = br.readLine()) != null){
                result.append(output);
            }

            JSONObject response = new JSONObject(result.toString());

            msg = String.format("" +
                    "CEP: %s\n" +
                    "Logradouro: %s\n" +
                    "Bairro: %s\n" +
                            "Localidade: %s - %s\n" +
                    "DDD: %s"
            , response.getString("cep"), response.getString("logradouro"), response.getString("bairro"), response.getString("localidade"), response.getString("uf"), response.getString("ddd"));

            if(conn.getResponseCode() == 200) {
                resultOut.setText("Abrindo diálogo...");
                this.Dialog(view);
            }

            conn.disconnect();

        }catch(Exception e){
            System.out.println("Try to connect: "+webservice);
            System.out.println("Exception in NetClientGet:-  "+e);
        }
    }


}
