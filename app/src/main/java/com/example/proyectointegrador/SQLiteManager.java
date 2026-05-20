package com.example.proyectointegrador;

import android.content.Context;

public class SQLiteManager {
    public static void añadirPuntos(Context context, DBHelper dbHelper, String codigo, int puntos) {
        dbHelper.sumarPuntosUsuario(codigo, puntos);
    }
}