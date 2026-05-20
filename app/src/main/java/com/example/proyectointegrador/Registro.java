package com.example.proyectointegrador;

import android.annotation.SuppressLint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Registro extends AppCompatActivity {
    EditText txtNombre, txtApellido, txtCodigo, txtContraseña, txtConfirmar;
    Button btnRegistrar;
    DBHelper dbHelper;

    // Vinculacion para la captura de datos
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtNombre = findViewById(R.id.TextV_nombre);
        txtApellido = findViewById(R.id.TextV_apellido);
        txtCodigo = findViewById(R.id.TextV_codigo);
        txtContraseña = findViewById(R.id.TextV_contraseña);
        txtConfirmar = findViewById(R.id.TextV_confirmar);
        btnRegistrar = findViewById(R.id.btn_registrar);

        dbHelper = new DBHelper(this);

        // Se examinan los datos borrando espacios vacios para meterlos a la DB
        btnRegistrar.setOnClickListener(v -> {
            String nombre = txtNombre.getText().toString().trim();
            String apellido = txtApellido.getText().toString().trim();
            String codigo = txtCodigo.getText().toString().trim();
            String contraseña = txtContraseña.getText().toString();
            String confirmar = txtConfirmar.getText().toString();

            if (nombre.isEmpty() || apellido.isEmpty() || codigo.isEmpty() || contraseña.isEmpty() || confirmar.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!codigo.matches("\\d{9}")) {
                Toast.makeText(this, "El codigo debe tener exactamente 9 digitos numericos.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!contraseña.matches(".{4,9}")) {
                Toast.makeText(this, "La contraseña debe tener entre 4 y 9 caracteres.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!contraseña.equals(confirmar)) {
                Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.existeCodigo(codigo)) {  // Compara el codigo con la DB para verificar que no se este usando
                Toast.makeText(this, "El codigo ya está registrado.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si cumple los requisitos, se registran los datos en la DB
            boolean registrado = dbHelper.registrarUsuario(codigo, nombre, apellido, contraseña);
            if (registrado) {
                Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show();
                finish();// regresa al login
            } else {
                Toast.makeText(this, "Error al registrar.", Toast.LENGTH_SHORT).show();
            }
        });

        CheckBox checkboxMostrar = findViewById(R.id.checkbox_mostrar_registro);

        checkboxMostrar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtContraseña.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                txtConfirmar.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                txtContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
                txtConfirmar.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

    }
}