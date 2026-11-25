package org.me.gcu.kerr_anthony_s2324906;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class TrafficViewModel extends AndroidViewModel {

    private static final String UNPLANNED_URL = "https://datex2.trafficscotland.org/rest/2.3/publications/UnplannedEvents/Content.xml";
    private static final String ROADWORKS_URL = "https://datex2.trafficscotland.org/rest/2.3/publications/CurrentRoadworks/Content.xml";

    // 1 hour. Make it shorter (e.g. 60 * 1000) for testing and the demo video.
    private static final long UPDATE_INTERVAL_MS = 60 * 1000;

    private final TrafficRepo repository;
    private final Handler updateHandler = new Handler(Looper.getMainLooper());
    private Runnable updateTask;

    private final MutableLiveData<ArrayList<Situation>> incidents = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<Situation>> roadworks = new MutableLiveData<>();
    private final MutableLiveData<Boolean> showingSavedCopy = new MutableLiveData<>(false);

    public TrafficViewModel(Application application) {
        super(application);
        repository = new TrafficRepo(application);
    }

    public LiveData<ArrayList<Situation>> getIncidents() {
        return incidents;
    }

    public LiveData<ArrayList<Situation>> getRoadworks() {
        return roadworks;
    }

    public LiveData<Boolean> getShowingSavedCopy() {
        return showingSavedCopy;
    }

    public void loadAll(String clientId, String clientKey) {
        repository.load(UNPLANNED_URL, "Incident", clientId, clientKey, new TrafficRepo.Callback() {
            @Override
            public void onLoaded(ArrayList<Situation> situations, boolean fromSavedCopy) {
                incidents.setValue(situations);
                showingSavedCopy.setValue(fromSavedCopy);
            }

            @Override
            public void onFailed() {
                // nothing downloaded and no saved copy
            }
        });

        repository.load(ROADWORKS_URL, "Roadworks", clientId, clientKey, new TrafficRepo.Callback() {
            @Override
            public void onLoaded(ArrayList<Situation> situations, boolean fromSavedCopy) {
                roadworks.setValue(situations);
                showingSavedCopy.setValue(fromSavedCopy);
            }

            @Override
            public void onFailed() {
                // nothing downloaded and no saved copy
            }
        });
    }

    // Loads now (start-up), then again every UPDATE_INTERVAL_MS
    public void startAutoUpdate(String clientId, String clientKey) {
        if (updateTask != null) {
            return; // already running, e.g. after the screen rotated
        }
        updateTask = new Runnable() {
            @Override
            public void run() {
                loadAll(clientId, clientKey);
                updateHandler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        };
        updateHandler.post(updateTask);
    }

    @Override
    protected void onCleared() {
        if (updateTask != null) {
            updateHandler.removeCallbacks(updateTask);
        }
    }
}