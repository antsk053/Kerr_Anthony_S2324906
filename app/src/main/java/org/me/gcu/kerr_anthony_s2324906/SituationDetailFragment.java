package org.me.gcu.kerr_anthony_s2324906;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class SituationDetailFragment extends Fragment {

    public SituationDetailFragment() {
        super(R.layout.fragment_situation_detail);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView detailText = view.findViewById(R.id.detailText);
        TrafficViewModel viewModel = new ViewModelProvider(requireActivity()).get(TrafficViewModel.class);
        viewModel.getSelected().observe(getViewLifecycleOwner(), s -> detailText.setText(describe(s)));
    }

    private String describe(Situation s) {
        StringBuilder text = new StringBuilder();
        text.append(s.getCategory()).append(": ").append(s.getRoad()).append("\n\n");
        text.append(s.getComment()).append("\n\n");
        text.append("Start: ").append(s.getStartTime()).append("\n");

        if (s.getEndTime() == null) {
            text.append("End: not given\n");
        } else {
            text.append("End: ").append(s.getEndTime()).append("\n");
        }

        text.append("Direction: ").append(s.getDirection()).append("\n");
        text.append("Type: ").append(s.getDetailType()).append("\n\n");

        Impact impact = s.getImpact();
        if (!impact.getDelaysType().isEmpty()) {
            text.append("Delays: ").append(impact.getDelaysType()).append("\n");
        }
        if (impact.getLanesWorking() >= 0) {
            text.append("Lanes working: ").append(impact.getLanesWorking()).append("\n");
        }
        if (impact.getLanesUnavailable() >= 0) {
            text.append("Lanes restricted: ").append(impact.getLanesUnavailable()).append("\n");
        }

        RoadLocation loc = s.getLocation();
        text.append("\nFrom: ").append(loc.getFromLat()).append(", ").append(loc.getFromLong());
        text.append("\nTo: ").append(loc.getToLat()).append(", ").append(loc.getToLong());
        return text.toString();
    }
}