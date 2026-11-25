package org.me.gcu.kerr_anthony_s2324906;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SituationAdapter extends ArrayAdapter<Situation> {

    public SituationAdapter(@NonNull Context context, @NonNull ArrayList<Situation> situations) {
        super(context, 0, situations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.item_situation, parent, false);
        }

        Situation s = getItem(position);
        if (s == null) {
            return row;
        }

        View colourBar = row.findViewById(R.id.colourBar);
        ImageView icon = row.findViewById(R.id.icon);
        TextView roadText = row.findViewById(R.id.roadText);
        TextView commentText = row.findViewById(R.id.commentText);

        roadText.setText(s.getRoad());
        commentText.setText(s.getComment());

        colourBar.setBackgroundColor(colourFor(s));
        icon.setImageResource(iconFor(s));

        return row;
    }

    // Worst case first, so the most serious problem decides the colour
    private int colourFor(Situation s) {
        Impact impact = s.getImpact();
        if (impact == null) {
            return Color.parseColor("#388E3C");   // green: nothing given
        }

        String delays = impact.getDelaysType();

        if (impact.getLanesWorking() == 0 || "veryLongDelays".equals(delays)) {
            return Color.parseColor("#D32F2F");   // red: road closed / very long delays
        }
        if ("longDelays".equals(delays) || impact.getLanesUnavailable() > 0) {
            return Color.parseColor("#F57C00");   // orange: long delays / lanes restricted
        }
        if ("delays".equals(delays)) {
            return Color.parseColor("#FBC02D");   // amber: delays
        }
        return Color.parseColor("#388E3C");       // green: nothing given
    }

    // Same idea: first match wins
    private int iconFor(Situation s) {
        Impact impact = s.getImpact();

        if (impact != null) {
            String delays = impact.getDelaysType();

            if (impact.getLanesWorking() == 0) {
                return R.drawable.icon_closed;
            }
            if ("veryLongDelays".equals(delays)) {
                return R.drawable.icon_warning;
            }
            if ("longDelays".equals(delays)) {
                return R.drawable.icon_traffic;
            }
            if (impact.getLanesUnavailable() > 0) {
                return R.drawable.icon_lanes;
            }
            if ("delays".equals(delays)) {
                return R.drawable.icon_delay;
            }
        }

        if ("Roadworks".equals(s.getCategory())) {
            return R.drawable.icon_construction;
        }
        return R.drawable.icon_information;
    }
}