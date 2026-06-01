package com.example.lab11correction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Lab11";
    private static final int CODE_PERMISSION = 200;

    // Adresse IP du serveur local (modifier selon votre configuration)
    private static final String SERVER_URL = "http://10.0.2.2/localisation/savePosition.php";

    private TextView tvCoordinates;
    private TextView tvStatus;
    private RequestQueue volleyQueue;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCoordinates = findViewById(R.id.tvCoordinates);
        tvStatus = findViewById(R.id.tvStatus);

        volleyQueue = Volley.newRequestQueue(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        verifierPermissions();
    }

    private void verifierPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    CODE_PERMISSION);
        } else {
            demarrerLocalisation();
        }
    }

    @SuppressLint("MissingPermission")
    private void demarrerLocalisation() {
        tvStatus.setText("⏳ GPS actif — mise à jour toutes les 60s ou 150m");
        // Intervalle : 60 secondes, distance minimale : 150 mètres
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                60_000,
                150f,
                gpsListener
        );
    }

    private final LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location loc) {
            afficherEtEnvoyer(loc);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            tvStatus.setText("⚠️ GPS désactivé — veuillez l'activer dans les paramètres");
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            tvStatus.setText("✅ GPS activé");
        }
    };

    private void afficherEtEnvoyer(Location loc) {
        double lat = loc.getLatitude();
        double lon = loc.getLongitude();
        double alt = loc.getAltitude();
        float precision = loc.getAccuracy();

        String affichage = String.format(Locale.FRENCH,
                "📍 Latitude  : %.6f°\n📍 Longitude : %.6f°\n⛰ Altitude  : %.1f m\n🎯 Précision : %.1f m",
                lat, lon, alt, precision);

        tvCoordinates.setText(affichage);
        tvStatus.setText("✅ Position reçue — envoi au serveur...");

        envoyerAuServeur(lat, lon);
    }

    private void envoyerAuServeur(final double lat, final double lon) {
        StringRequest req = new StringRequest(
                Request.Method.POST,
                SERVER_URL,
                response -> {
                    Log.d(TAG, "Serveur : " + response);
                    tvStatus.setText("✅ Données envoyées avec succès");
                },
                error -> {
                    Log.e(TAG, "Erreur réseau : " + error.getMessage());
                    tvStatus.setText("❌ Échec de l'envoi au serveur");
                    Toast.makeText(this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                String horodatage = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new Date());
                String idAppareil = Settings.Secure.getString(
                        getContentResolver(), Settings.Secure.ANDROID_ID);

                data.put("latitude", String.valueOf(lat));
                data.put("longitude", String.valueOf(lon));
                data.put("date_position", horodatage);
                data.put("imei", idAppareil);

                return data;
            }
        };

        volleyQueue.add(req);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                demarrerLocalisation();
            } else {
                tvStatus.setText("❌ Permission GPS refusée");
                Toast.makeText(this, "Permission GPS nécessaire", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(gpsListener);
    }
}
