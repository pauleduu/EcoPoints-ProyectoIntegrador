package com.example.proyectointegrador;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecompensaFragment extends Fragment {

    private TextView textViewContador;
    private Handler handler = new Handler();
    private Runnable runnable;

    public RecompensaFragment() {
    }

    // Se muestra el contador en pantalla
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recompensa, container, false);

        textViewContador = view.findViewById(R.id.textViewContador);
        iniciarContador();

        return view;
    }

    private void iniciarContador() {
        runnable = new Runnable() {
            @Override
            public void run() {
                textViewContador.setText(obtenerTiempoRestante()); // Mostrar tiempo restante
                handler.postDelayed(this, 1000);  // Se repite cada segundo
            }
        };
        handler.post(runnable);
    }

    // Se caclula el tiempo que falta para que acabe el mes actual
    private String obtenerTiempoRestante() {
        Calendar ahora = Calendar.getInstance();
        Calendar finDeMes = Calendar.getInstance();
        finDeMes.set(Calendar.DAY_OF_MONTH, finDeMes.getActualMaximum(Calendar.DAY_OF_MONTH));
        finDeMes.set(Calendar.HOUR_OF_DAY, 23);
        finDeMes.set(Calendar.MINUTE, 59);
        finDeMes.set(Calendar.SECOND, 59);

        long millisRestantes = finDeMes.getTimeInMillis() - ahora.getTimeInMillis();  // Fecha actual y fecha final transformadas y restadas

        // Conversion de milisegundos a tiempo real
        long dias = TimeUnit.MILLISECONDS.toDays(millisRestantes);
        long horas = TimeUnit.MILLISECONDS.toHours(millisRestantes) % 24;
        long minutos = TimeUnit.MILLISECONDS.toMinutes(millisRestantes) % 60;
        long segundos = TimeUnit.MILLISECONDS.toSeconds(millisRestantes) % 60;

        return String.format(Locale.getDefault(),
                "%d dias, %02d horas, %02d minutos, %02d segundos", dias, horas, minutos, segundos);
    }

    // Apagar contador cada que se cambia de pantalla
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }
}