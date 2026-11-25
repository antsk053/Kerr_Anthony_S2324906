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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private TextView rawDataDisplay;
    private Button startButton;
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
        // Set up the links to the graphical components
        rawDataDisplay = (TextView) findViewById(R.id.rawDataDisplay);
        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        try {
            Bundle metaData = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            clientId = metaData.getString("org.me.gcu.kerr_anthony_s2324906.TS_ID");
            clientKey = metaData.getString("org.me.gcu.kerr_anthony_s2324906.TS_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        viewModel = new ViewModelProvider(this).get(TrafficViewModel.class);
        viewModel.getIncidents().observe(this, list -> Log.d("Parser", "Incidents received: " + list.size()));
        viewModel.getRoadworks().observe(this, list -> Log.d("Parser", "Roadworks received: " + list.size()));
        viewModel.getShowingSavedCopy().observe(this, saved -> Log.d("Parser", "Showing saved copy: " + saved));
        viewModel.startAutoUpdate(clientId, clientKey);
    }

    @Override
    public void onClick(View aview) {
        viewModel.loadAll(clientId, clientKey);
    }
}