package com.example.proyectointegrador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;

public class Login extends AppCompatActivity {
    EditText txtCodigo, txtContraseña;
    Button btnIniciar, btnRegistrar;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtCodigo = findViewById(R.id.TextV_codigo);
        txtContraseña = findViewById(R.id.TextV_contraseña);
        btnIniciar = findViewById(R.id.Btn_iniciar);
        btnRegistrar = findViewById(R.id.btn_registrar);

        dbHelper = new DBHelper(this);


        CheckBox checkboxMostrar = findViewById(R.id.checkbox_mostrar_login);
        checkboxMostrar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                txtContraseña.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                txtContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        btnIniciar.setOnClickListener(v -> { // Boton para entrar a la app
            String codigo = txtCodigo.getText().toString().trim();
            String contraseña = txtContraseña.getText().toString();


            if (codigo.length() != 9) {  // Codigo de 9 digitos
                Toast.makeText(this, "El codigo debe ser de 9 digitos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (contraseña.length() < 4 || contraseña.length() > 9) { // Contraseña de 4 a 9 digitos
                Toast.makeText(this, "La contraseña debe tener entre 4 y 9 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.verificarUsuario(codigo, contraseña)) {  // Se compara lo igresado con la DB
                SharedPreferences preferences = getSharedPreferences("sesion", MODE_PRIVATE);  // Guarda los datos en la memoria local
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("codigo", codigo);
                editor.apply();

                Toast.makeText(this, "Inicio de sesion exitoso.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, Principal.class);
                startActivity(intent);
                finish();
            } else {  // Usuario no registrado
                if (!dbHelper.existeUsuario(codigo)) {
                    Toast.makeText(this, "Tus datos no estan registrados.", Toast.LENGTH_SHORT).show();
                } else { // Contraseña incorrecta
                    Toast.makeText(this, "Contraseña incorrecta.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cambio a pantalla de registro
        btnRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Registro.class);
            startActivity(intent);
        });

    }
}