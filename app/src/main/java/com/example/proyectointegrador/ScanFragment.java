package com.example.proyectointegrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Random;

public class ScanFragment extends Fragment {

    private DBHelper dbHelper;
    private String codigo;

    public ScanFragment() {

    }

    // Recibir codigo desde principal y se guarda
    public static ScanFragment newInstance(String codigo) {
        ScanFragment fragment = new ScanFragment();
        Bundle args = new Bundle();
        args.putString("codigo", codigo);
        fragment.setArguments(args);
        return fragment;
    }

    // Se declara el codigo y se inicializa la DB
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            codigo = getArguments().getString("codigo");
        }
        dbHelper = new DBHelper(getContext());
    }

    // Inicializa la oantalla
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_scan, container, false);

        // Cuando se presiona el boton se ejecuta inicarEscaneo (abrir camara)
        view.findViewById(R.id.btnEscanear).setOnClickListener(v -> iniciarEscaneo());

        return view;
    }

    private final ActivityResultLauncher<Intent> scanLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                IntentResult intentResult = IntentIntegrator.parseActivityResult(
                        IntentIntegrator.REQUEST_CODE, result.getResultCode(), result.getData());

                if (intentResult != null) {
                    String contenido = intentResult.getContents();
                    if (contenido != null && !contenido.isEmpty()) {
                        int puntosGanados = new Random().nextInt(13) + 3; // 3 a 15 puntos
                        dbHelper.sumarPuntosUsuario(codigo, puntosGanados);

                        Toast.makeText(getContext(),
                                "Codigo escaneado\n¡Ganaste " + puntosGanados + " puntos!",
                                Toast.LENGTH_LONG).show();

                        if (getActivity() instanceof Principal) {
                            ((Principal) getActivity()).actualizarHeaderUsuario(); // Actualizacion de puntos
                        }

                    } else {
                        Toast.makeText(getContext(), "Escaneo cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    //
    private void iniciarEscaneo() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);  // Solo lee QR
        integrator.setPrompt("Escanea un codigo QR valido para ganar puntos");
        integrator.setCameraId(0); // 0 = camara trasera, 1 = camara frontal
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setCaptureActivity(Capture.class); // Clase que modifica la apariencia visual
        scanLauncher.launch(integrator.createScanIntent()); // Abrir camara
    }
}