package com.example.proyectomorse;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectomorse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private Button buttonSend;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Configurar el evento de clic del botón
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensaje();
            }
        });
    }

    private void enviarMensaje() {
        // Obtener el mensaje del EditText y eliminar espacios al inicio y al final
        String mensaje = editTextMessage.getText().toString().trim().toUpperCase();

        // 1. Validar que el mensaje no esté vacío
        if (mensaje.isEmpty()) {
            // Mostrar mensaje de error si el mensaje está vacío
            Toast.makeText(MainActivity.this, "Por favor, ingresa un mensaje antes de enviarlo", Toast.LENGTH_LONG).show();
            return; // Salir del método porque el mensaje está vacío
        }

        // 2. Validar que el mensaje no tenga espacios al inicio ni al final
        if (mensaje.startsWith(" ") || mensaje.endsWith(" ")) {
            // Mostrar mensaje de error si el mensaje tiene espacios al inicio o al final
            Toast.makeText(MainActivity.this, "Evita espacios al inicio o al final del mensaje", Toast.LENGTH_LONG).show();
            return; // Salir del método porque el mensaje tiene espacios al inicio o al final
        }

        // 3. Validar que el mensaje solo contenga letras del alfabeto y espacios
        if (!mensaje.matches("[A-Z ]+")) {
            // Mostrar mensaje de error si el mensaje no cumple con los requisitos
            Toast.makeText(MainActivity.this, "Por favor, ingresa un mensaje válido (letras del alfabeto A-Z y espacios)", Toast.LENGTH_LONG).show();
            return; // Salir del método porque el mensaje no cumple con los requisitos
        }

        // 4. Validar que el mensaje no contenga más de un espacio consecutivo entre letras
        if (mensaje.contains("  ")) {
            // Mostrar mensaje de error si el mensaje tiene más de un espacio consecutivo
            Toast.makeText(MainActivity.this, "Evita espacios consecutivos entre letras", Toast.LENGTH_LONG).show();
            return; // Salir del método porque el mensaje tiene más de un espacio consecutivo
        }

        // 5. Enviar el mensaje a Firestore
        Map<String, Object> data = new HashMap<>();
        data.put("mensaje", mensaje);

        db.collection("mensajes")
                .add(data)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Limpiar el campo de texto después de enviar el mensaje
                            editTextMessage.setText("");
                            // 6. Mostrar mensaje de éxito
                            Toast.makeText(MainActivity.this, "Mensaje enviado con éxito", Toast.LENGTH_SHORT).show();
                        } else {
                            // 7. Manejar errores aquí
                            Toast.makeText(MainActivity.this, "Hubo un problema al enviar el mensaje", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
