package com.example.proyectointegrador;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class PuntuacionesFragment extends Fragment {

    private DBHelper dbHelper;
    private TextView nombre1, puntos1;
    private TextView nombre2, puntos2;
    private TextView nombre3, puntos3;
    private TextView textoPosicion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_puntuacion, container, false);

        dbHelper = new DBHelper(getContext());

        nombre1 = view.findViewById(R.id.nombre1);
        puntos1 = view.findViewById(R.id.puntos1);
        nombre2 = view.findViewById(R.id.nombre2);
        puntos2 = view.findViewById(R.id.puntos2);
        nombre3 = view.findViewById(R.id.nombre3);
        puntos3 = view.findViewById(R.id.puntos3);

        textoPosicion = view.findViewById(R.id.texto_posicion);

        mostrarTop3();
        mostrarPosicionUsuarioActivo();

        return view;
    }

    // Muestra nombre y puntos de los 3 usuarios con mas puntos
    private void mostrarTop3() {
        ArrayList<DBHelper.UsuarioPuntuacion> top3 = dbHelper.obtenerTop3Usuarios();

        if (top3.size() > 0) {
            nombre1.setText(top3.get(0).nombreCompleto);
            puntos1.setText("Puntos: " + top3.get(0).puntos);
        }
        if (top3.size() > 1) {
            nombre2.setText(top3.get(1).nombreCompleto);
            puntos2.setText("Puntos: " + top3.get(1).puntos);
        }
        if (top3.size() > 2) {
            nombre3.setText(top3.get(2).nombreCompleto);
            puntos3.setText("Puntos: " + top3.get(2).puntos);
        }
    }

    //  Comparacion de puntos para asignar puesto en el ranking al usuario activo
    private void mostrarPosicionUsuarioActivo() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String codigoUsuario = preferences.getString("codigo", null);

        if (codigoUsuario != null) {
            int posicion = dbHelper.obtenerPosicionUsuarioPorCodigo(codigoUsuario);
            textoPosicion.setText("Estas en la posicion numero " + posicion);
        } else {
            textoPosicion.setText("No se encontro sesión activa.");
        }
    }
}