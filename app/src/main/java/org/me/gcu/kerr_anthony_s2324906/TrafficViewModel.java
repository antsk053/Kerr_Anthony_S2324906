package org.me.gcu.kerr_anthony_s2324906;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class TrafficViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<Situation>> incidents = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Situation>> roadworks = new MutableLiveData<>();

    public LiveData<ArrayList<Situation>> getIncidents() {
        return incidents;
    }

    public LiveData<ArrayList<Situation>> getRoadworks() {
        return roadworks;
    }
}
