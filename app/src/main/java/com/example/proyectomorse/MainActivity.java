package com.example.proyectomorse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private Button buttonSend;
    private String apiKey = "UDN65FSGH7Y06YL8"; // Reemplaza con tu API Key de ThingSpeak

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensaje();
            }
        });
    }

    private void enviarMensaje() {
        String mensaje = editTextMessage.getText().toString().trim().toUpperCase();
        if (mensaje.isEmpty()) {
            Toast.makeText(MainActivity.this, "Por favor, ingresa un mensaje antes de enviarlo", Toast.LENGTH_LONG).show();
            return;
        }

        // Aquí llamamos a la tarea asincrónica para enviar datos a ThingSpeak
        new SendToThingSpeakTask().execute(mensaje);
    }

    private class SendToThingSpeakTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String mensaje = params[0];
                URL url = new URL("https://api.thingspeak.com/update.json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                os.write(("api_key=" + apiKey + "&field1=" + mensaje).getBytes());
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                editTextMessage.setText("");
                Toast.makeText(MainActivity.this, "Mensaje enviado con éxito a ThingSpeak", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Hubo un problema al enviar el mensaje", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

