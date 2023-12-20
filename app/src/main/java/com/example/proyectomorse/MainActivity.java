package com.example.proyectomorse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private Button buttonSend;
    private String apiKey = "QHS9Q3RVFTQ1SD9C"; // Reemplaza con tu API Key de ThingSpeak
    private Map<String, String> palabraClaveANumero = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Inicializar el mapa de palabras clave a números
        inicializarPalabraClaveANumero();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensaje();
            }
        });
    }

    private void inicializarPalabraClaveANumero() {
        // Asignar palabras clave a números (ajusta según tus necesidades)
        palabraClaveANumero.put("AYUDA", "1");
        palabraClaveANumero.put("SALVACION", "2");
        palabraClaveANumero.put("URGENCIA", "3");
        palabraClaveANumero.put("EMERGENCIA", "4");
        palabraClaveANumero.put("SALIR", "5");
        palabraClaveANumero.put("ALERTA", "6");
        palabraClaveANumero.put("SOS", "7");
        palabraClaveANumero.put("NECESITO", "8");
        palabraClaveANumero.put("RESCATE", "9");
    }

    private void enviarMensaje() {
        String mensaje = editTextMessage.getText().toString().trim().toUpperCase();

        // Verificar si el mensaje es una palabra clave y asignar el número correspondiente
        String numero = obtenerNumeroDePalabraClave(mensaje);

        if (!numero.isEmpty()) {
            // Aquí llamamos a la tarea asincrónica para enviar datos a ThingSpeak
            new SendToThingSpeakTask().execute(numero);
        } else {
            Toast.makeText(MainActivity.this, "Palabra no reconocida.", Toast.LENGTH_LONG).show();
        }
    }

    private String obtenerNumeroDePalabraClave(String palabraClave) {
        // Buscar el número asociado a la palabra clave
        return palabraClaveANumero.get(palabraClave);
    }

    private class SendToThingSpeakTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String valor = params[0];
                URL url = new URL("https://api.thingspeak.com/update.json");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                OutputStream os = urlConnection.getOutputStream();
                os.write(("api_key=" + apiKey + "&field1=" + valor).getBytes());
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
                Toast.makeText(MainActivity.this, "Mensaje enviado con éxito a ThingSpeak", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Hubo un problema al enviar el mensaje", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


