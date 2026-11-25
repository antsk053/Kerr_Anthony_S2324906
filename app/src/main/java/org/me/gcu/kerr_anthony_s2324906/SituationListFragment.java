package org.me.gcu.kerr_anthony_s2324906;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class SituationListFragment extends Fragment {

    private TrafficViewModel viewModel;
    private SituationAdapter adapter;
    private EditText searchBox;


    private ArrayList<Situation> incidents = new ArrayList<>();
    private ArrayList<Situation> roadworks = new ArrayList<>();
    private boolean showingIncidents = true;

    public SituationListFragment() {
        super(R.layout.fragment_situation_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(TrafficViewModel.class);

        ListView situationList = view.findViewById(R.id.situationList);
        Button incidentsButton = view.findViewById(R.id.incidentsButton);
        Button roadworksButton = view.findViewById(R.id.roadworksButton);
        searchBox = view.findViewById(R.id.searchBox);

        adapter = new SituationAdapter(requireContext(), new ArrayList<>());
        situationList.setAdapter(adapter);


        situationList.setOnItemClickListener((parent, v, position, id) ->
                viewModel.select(adapter.getItem(position)));

        viewModel.getIncidents().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                incidents = list;
                showList();
            }
        });

        viewModel.getRoadworks().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                roadworks = list;
                showList();
            }
        });

        incidentsButton.setOnClickListener(v -> {
            showingIncidents = true;
            showList();
        });

        roadworksButton.setOnClickListener(v -> {
            showingIncidents = false;
            showList();
        });


        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showList();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }


    private void showList() {
        String query = searchBox.getText().toString().trim().toLowerCase();
        ArrayList<Situation> source = showingIncidents ? incidents : roadworks;

        adapter.clear();
        for (Situation s : source) {
            if (query.isEmpty() || matches(s, query)) {
                adapter.add(s);
            }
        }
        adapter.notifyDataSetChanged();
    }


    private boolean matches(Situation s, String query) {
        return s.getRoad() != null && s.getRoad().toLowerCase().contains(query);
    }
}