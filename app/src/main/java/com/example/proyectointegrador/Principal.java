package com.example.proyectointegrador;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;

public class Principal extends AppCompatActivity {
    private DrawerLayout drawer;
    private String codigo; //Variable global para reutilizar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        //  Barra superior
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Menu de hamburgesa
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        SharedPreferences preferences = getSharedPreferences("sesion", MODE_PRIVATE);
        codigo = preferences.getString("codigo", null);

        // Se obtienen los datos del usuario que ingreso para mostrarlos
        if (codigo != null) {
            DBHelper dbHelper = new DBHelper(this);
            DBHelper.UsuarioPuntuacion usuario = dbHelper.obtenerUsuarioPorCodigo(codigo);

            if (usuario != null) {
                TextView txtNombre = headerView.findViewById(R.id.txtNombreUsuario);
                TextView txtPuntos = headerView.findViewById(R.id.nav_header_subtitle);

                txtNombre.setText(usuario.nombreCompleto);
                txtPuntos.setText("Puntos: " + usuario.puntos);
            } else {
                Log.e("Principal", "No se encontro el usuario en la base de datos.");
            }
        } else {
            Log.e("Principal", "No se encontro el codigo de sesion.");
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.black));

        // Muestra las diferentes pantallas a las que se puede acceder
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_scan) {
                selectedFragment = ScanFragment.newInstance(codigo); // Pasamos el codigo para consulta de puntos
            } else if (id == R.id.nav_puntuaciones) {
                selectedFragment = new PuntuacionesFragment();
            } else if (id == R.id.nav_rewards) {
                selectedFragment = new RecompensaFragment();
            } else if (id == R.id.nav_next) {
                selectedFragment = new QuePasaDespuesFragment();
            } else if (id == R.id.nav_contact) {
                selectedFragment = new ContactFragment();
            } else if (id == R.id.nav_logout) { // Cerrado de sesion, se borra
                SharedPreferences.Editor editor = getSharedPreferences("sesion", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();

                // Cambia la pantalla a la de Inicio de Sesion
                Intent intent = new Intent(Principal.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            // Cambia el contenido de la pantalla
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        // Carga la pantalla de inicio al abrir la app
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    // Se cierra el menu lateral, si esta abierto, con el boton back
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Si el usuario gana puntos, se actualizan y muestra el cambio en pantalla
    public void actualizarHeaderUsuario() {
        if (codigo != null) {
            DBHelper dbHelper = new DBHelper(this);
            DBHelper.UsuarioPuntuacion usuario = dbHelper.obtenerUsuarioPorCodigo(codigo);

            if (usuario != null) {
                NavigationView navigationView = findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView txtNombre = headerView.findViewById(R.id.txtNombreUsuario);
                TextView txtPuntos = headerView.findViewById(R.id.nav_header_subtitle);

                txtNombre.setText(usuario.nombreCompleto);
                txtPuntos.setText("Puntos: " + usuario.puntos);
            }
        }
    }
}