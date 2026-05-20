package com.example.proyectointegrador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Usuarios.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Creacion de la tabala
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuarios (" +
                "codigo TEXT PRIMARY KEY, " +
                "nombre TEXT, " +
                "apellido TEXT, " +
                "contraseña TEXT, " +
                "puntos INTEGER DEFAULT 0)");

        // Usuarios predefinidos
        db.execSQL("INSERT INTO usuarios (codigo, nombre, apellido, contraseña, puntos) VALUES " +
                "('123456789', 'Luis', 'Ramírez', 'abc123', 87), " +
                "('987654321', 'Ana', 'Gómez', 'def456', 45), " +
                "('123123123', 'Carlos', 'López', 'ghi789', 122), " +
                "('321321321', 'María', 'Fernández', 'jkl012', 73), " +
                "('234234234', 'Pedro', 'Martínez', 'mno345', 200)");
    }

    // Borra la tabla y crea una nueva
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }

    // Guarda un usuario en la DB
    public boolean registrarUsuario(String codigo, String nombre, String apellido, String contraseña) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("codigo", codigo);
        valores.put("nombre", nombre);
        valores.put("apellido", apellido);
        valores.put("contraseña", contraseña);
        valores.put("puntos", 0);  // Usuarios nuevos inician con 0 puntos

        long resultado = db.insert("usuarios", null, valores);
        return resultado != -1;
    }

    // Verifica que lo ingresado coidncida con la DB para el login
    public boolean verificarUsuario(String codigo, String contraseña) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE codigo = ? AND contraseña = ?", new String[]{codigo, contraseña});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // Revisa si ya se uso un codigo para registrarse
    public boolean existeCodigo(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE codigo = ?", new String[]{codigo});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // Busca y regresa el nombre de un usuario segun su codigo
    public String[] obtenerNombreApellido(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre, apellido FROM usuarios WHERE codigo = ?", new String[]{codigo});
        String[] resultado = {"", ""};

        if (cursor.moveToFirst()) {
            resultado[0] = cursor.getString(0);
            resultado[1] = cursor.getString(1);
        }
        cursor.close();
        return resultado;
    }

    // Devuelve una lista con los codigos, contraseñas y puntos
    public ArrayList<String> obtenerCodigosYContraseñas() {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT codigo, contraseña, puntos FROM usuarios", null);
        if (cursor.moveToFirst()) {
            do {
                String codigo = cursor.getString(0);
                String contraseña = cursor.getString(1);
                String puntos = cursor.getString(2);
                lista.add("Código: " + codigo + " - Contraseña: " + contraseña + " - Puntos: " + puntos);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return lista;
    }

    // Borra los registros de la DB
    public void eliminarTodosLosUsuarios() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("usuarios", null, null);
        db.close();
    }

    //Lista on los 3 usuarios con mas puntos
    public ArrayList<UsuarioPuntuacion> obtenerTop3Usuarios() {
        ArrayList<UsuarioPuntuacion> topUsuarios = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre, apellido, puntos FROM usuarios ORDER BY puntos DESC LIMIT 3", null);

        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(0);
                String apellido = cursor.getString(1);
                int puntos = cursor.getInt(2);
                topUsuarios.add(new UsuarioPuntuacion(nombre + " " + apellido, puntos));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return topUsuarios;
    }


    public static class UsuarioPuntuacion {
        public String nombreCompleto;
        public int puntos;

        public UsuarioPuntuacion(String nombreCompleto, int puntos) {
            this.nombreCompleto = nombreCompleto;
            this.puntos = puntos;
        }
    }

    // Da la posicion en el ranking de usuario segun su codigo
    public int obtenerPosicionUsuarioPorCodigo(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT codigo, ROW_NUMBER() OVER (ORDER BY puntos DESC) as posicion " +
                        "FROM usuarios", null);

        int posicion = -1;

        if (cursor.moveToFirst()) {
            do {
                String codigoUsuario = cursor.getString(0);
                int pos = cursor.getInt(1);

                if (codigoUsuario.equals(codigo)) {
                    posicion = pos;
                    break;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return posicion;
    }

    // Busqueda de usuario por codigo para tener nombre y puntos
    public UsuarioPuntuacion obtenerUsuarioPorCodigo(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre, apellido, puntos FROM usuarios WHERE codigo = ?", new String[]{codigo});
        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(0);
            String apellido = cursor.getString(1);
            int puntos = cursor.getInt(2);
            cursor.close();
            return new UsuarioPuntuacion(nombre + " " + apellido, puntos);
        } else {
            cursor.close();
            return null;
        }
    }

    // Actualiza los puntos del usuario
    public void sumarPuntosUsuario(String codigo, int puntosASumar) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE usuarios SET puntos = puntos + ? WHERE codigo = ?", new Object[]{puntosASumar, codigo});
    }

    // Verifica si existe el usuario
    public boolean existeUsuario(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE codigo = ?", new String[]{codigo});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

}