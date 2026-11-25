/*  Starter project for Mobile Platform Development - 1st diet 26/27
    You should use this project as the starting point for your assignment.
*/

//
// Name                 Anthony Kerr
// Student ID           s2324906
// Programme of Study   Software development
//

// UPDATE THE PACKAGE NAME to include your Student Identifier
package org.me.gcu.kerr_anthony_s2324906;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {
    private TrafficViewModel viewModel;

    // Traffic Scotland id and key, read from the manifest (the values come from local.properties)
    private String clientId = "";
    private String clientKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        try {
            Bundle metaData = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            clientId = metaData.getString("org.me.gcu.kerr_anthony_s2324906.TS_ID");
            clientKey = metaData.getString("org.me.gcu.kerr_anthony_s2324906.TS_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        viewModel = new ViewModelProvider(this).get(TrafficViewModel.class);

        TextView statusText = findViewById(R.id.statusText);
        viewModel.getShowingSavedCopy().observe(this, saved ->
                statusText.setText(saved ? "Offline: showing the last saved data" : ""));

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> viewModel.loadAll(clientId, clientKey));

        viewModel.startAutoUpdate(clientId, clientKey);


        Button mapButton = findViewById(R.id.mapButton);
        View mapContainer = findViewById(R.id.map_container);
        FragmentManager fm = getSupportFragmentManager();

// Tapping Map puts the map fragment in the overlay; the Back button removes it again
        mapButton.setOnClickListener(v -> fm.beginTransaction()
                .replace(R.id.map_container, new SituationMapFragment())
                .addToBackStack("map")
                .commit());

// The overlay is visible only while the map is on the back stack
        fm.addOnBackStackChangedListener(() ->
                mapContainer.setVisibility(fm.getBackStackEntryCount() > 0 ? View.VISIBLE : View.GONE));
        mapContainer.setVisibility(fm.getBackStackEntryCount() > 0 ? View.VISIBLE : View.GONE);


    }
}