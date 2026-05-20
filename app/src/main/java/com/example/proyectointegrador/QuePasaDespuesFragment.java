package com.example.proyectointegrador;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.InputStream;
import java.io.OutputStream;

public class QuePasaDespuesFragment extends Fragment {

    @Nullable
    @Override  // Al presionar los botones, se llama la funcion copiarPDFADescargas
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_quepasadespues, container, false);

        Button btnEnero = view.findViewById(R.id.btnDescargarPDFenero);
        Button btnFebrero = view.findViewById(R.id.btnDescargarPDFfebrero);
        Button btnMarzo = view.findViewById(R.id.btnDescargarPDFmarzo);
        Button btnAbril = view.findViewById(R.id.btnDescargarPDFabril);

        // Se define el nombre de los archivos que se descargan y donde estan guardados en la app
        btnEnero.setOnClickListener(v -> copiarPdfADescargas("reporte_enero2025.pdf", R.raw.reporte_enero2025));
        btnFebrero.setOnClickListener(v -> copiarPdfADescargas("reporte_febrero2025.pdf", R.raw.reporte_febrero2025));
        btnMarzo.setOnClickListener(v -> copiarPdfADescargas("reporte_marzo2025.pdf", R.raw.reporte_marzo2025));
        btnAbril.setOnClickListener(v -> copiarPdfADescargas("reporte_abril2025.pdf", R.raw.reporte_abril2025));

        return view;
    }

    // Se copia el pdf de la app al celular
    private void copiarPdfADescargas(String nombreArchivo, int resourceId) {
        Context context = getContext();
        if (context == null) return;

        // Se avisa al celular que se descargala un archivo
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri collection = MediaStore.Files.getContentUri("external");
        Uri fileUri = context.getContentResolver().insert(collection, values);

        // Exportacion de datos
        try (InputStream input = context.getResources().openRawResource(resourceId);
             OutputStream output = context.getContentResolver().openOutputStream(fileUri)) {

            if (output != null) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                Toast.makeText(context, "PDF guardado en Descargas", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Error al guardar archivo", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) { // Se detecta error y se muestra mensaje
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}