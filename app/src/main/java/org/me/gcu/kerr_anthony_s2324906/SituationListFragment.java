package org.me.gcu.kerr_anthony_s2324906;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class SituationListFragment extends Fragment {

    private TrafficViewModel viewModel;
    private SituationAdapter adapter;
    private boolean showingIncidents = true;
    private ArrayList<Situation> incidents = new ArrayList<>();
    private ArrayList<Situation> roadworks = new ArrayList<>();

    public SituationListFragment() {
        super(R.layout.fragment_situation_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(TrafficViewModel.class);

        ListView listView = view.findViewById(R.id.situationList);
        Button incidentsButton = view.findViewById(R.id.incidentsButton);
        Button roadworksButton = view.findViewById(R.id.roadworksButton);

        adapter = new SituationAdapter(requireContext(), new ArrayList<>());
        listView.setAdapter(adapter);

        viewModel.getIncidents().observe(getViewLifecycleOwner(), list -> {
            incidents = list;
            showList();
        });
        viewModel.getRoadworks().observe(getViewLifecycleOwner(), list -> {
            roadworks = list;
            showList();
        });

        incidentsButton.setOnClickListener(v -> {
            showingIncidents = true;
            showList();
        });
        roadworksButton.setOnClickListener(v -> {
            showingIncidents = false;
            showList();
        });

        listView.setOnItemClickListener((parent, v, position, id) -> {
            viewModel.select(adapter.getItem(position));
        });
    }

    private void showList() {
        adapter.clear();
        adapter.addAll(showingIncidents ? incidents : roadworks);
        adapter.notifyDataSetChanged();
    }
}