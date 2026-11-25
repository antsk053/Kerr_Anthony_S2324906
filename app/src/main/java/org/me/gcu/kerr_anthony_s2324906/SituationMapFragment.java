package org.me.gcu.kerr_anthony_s2324906;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class SituationMapFragment extends Fragment implements OnMapReadyCallback {

    private TrafficViewModel viewModel;
    private GoogleMap map;
    private CheckBox showIncidents;
    private CheckBox showRoadworks;

    private ArrayList<Situation> incidents = new ArrayList<>();
    private ArrayList<Situation> roadworks = new ArrayList<>();

    public SituationMapFragment() {
        super(R.layout.fragment_map);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(TrafficViewModel.class);

        showIncidents = view.findViewById(R.id.showIncidents);
        showRoadworks = view.findViewById(R.id.showRoadworks);

        viewModel.getIncidents().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                incidents = list;
                drawMarkers();
            }
        });

        viewModel.getRoadworks().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                roadworks = list;
                drawMarkers();
            }
        });

        // Ticking or unticking a box redraws the map with only the chosen groups
        showIncidents.setOnCheckedChangeListener((button, checked) -> drawMarkers());
        showRoadworks.setOnCheckedChangeListener((button, checked) -> drawMarkers());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        // Start the camera over Scotland
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(56.5, -4.2), 6));

        // Tapping a marker also selects that situation for the detail panel
        map.setOnMarkerClickListener(marker -> {
            Object tag = marker.getTag();
            if (tag instanceof Situation) {
                viewModel.select((Situation) tag);
            }
            return false;   // false = still show the pop-up
        });

        drawMarkers();
    }

    // Clears the map and draws whichever groups are ticked
    private void drawMarkers() {
        if (map == null) {
            return;
        }
        map.clear();

        if (showIncidents.isChecked()) {
            for (Situation s : incidents) {
                addToMap(s, BitmapDescriptorFactory.HUE_RED, Color.RED);
            }
        }
        if (showRoadworks.isChecked()) {
            for (Situation s : roadworks) {
                addToMap(s, BitmapDescriptorFactory.HUE_AZURE, Color.BLUE);
            }
        }
    }

    // One marker at the start point, plus a line if there is also an end point
    private void addToMap(Situation s, float markerHue, int lineColour) {
        RoadLocation loc = s.getLocation();
        if (loc == null) {
            return;
        }

        boolean hasFrom = loc.getFromLat() != 0 || loc.getFromLong() != 0;
        boolean hasTo = loc.getToLat() != 0 || loc.getToLong() != 0;
        if (!hasFrom && !hasTo) {
            return;   // no coordinates in the feed for this one
        }

        LatLng from = new LatLng(loc.getFromLat(), loc.getFromLong());
        LatLng to = new LatLng(loc.getToLat(), loc.getToLong());
        LatLng start = hasFrom ? from : to;

        Marker marker = map.addMarker(new MarkerOptions()
                .position(start)
                .title(s.getCategory() + ": " + s.getRoad())
                .snippet(shorten(s.getComment()))
                .icon(BitmapDescriptorFactory.defaultMarker(markerHue)));
        if (marker != null) {
            marker.setTag(s);
        }

        if (hasFrom && hasTo && !from.equals(to)) {
            map.addPolyline(new PolylineOptions()
                    .add(from, to)
                    .color(lineColour)
                    .width(8));
        }
    }

    private String shorten(String text) {
        if (text == null) {
            return "";
        }
        return text.length() > 70 ? text.substring(0, 70) + "..." : text;
    }
}