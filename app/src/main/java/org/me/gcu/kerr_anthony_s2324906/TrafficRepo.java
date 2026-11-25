package org.me.gcu.kerr_anthony_s2324906;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrafficRepo {


    public interface Callback {
        void onLoaded(ArrayList<Situation> situations, boolean fromSavedCopy);
        void onFailed();
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Context context;

    public TrafficRepo(Context context) {
        this.context = context.getApplicationContext();
    }

    // Downloads (or, if offline, reads the saved copy) and parses on a background thread
    public void load(String urlText, String category, String clientId, String clientKey, Callback callback) {
        executor.execute(() -> {
            boolean fromSavedCopy = false;
            String xml = download(urlText, clientId, clientKey);
            if (xml != null) {
                saveCopy(category, xml);
            } else {
                xml = readSavedCopy(category);
                fromSavedCopy = true;
            }
            ArrayList<Situation> list = null;
            if (xml != null) {
                list = new FeedParser().parse(xml, category);
            }
            ArrayList<Situation> finalList = list;
            boolean finalFromSavedCopy = fromSavedCopy;
            mainHandler.post(() -> {
                if (finalList == null) {
                    callback.onFailed();
                } else {
                    callback.onLoaded(finalList, finalFromSavedCopy);
                }
            });
        });
    }

    private String download(String urlText, String clientId, String clientKey) {
        StringBuilder text = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlText);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization",
                    Base64.getEncoder().encodeToString((clientId + ":" + clientKey).getBytes()));
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                text.append(line);
            }
            in.close();
        } catch (IOException e) {
            Log.e("TrafficRepository", "Download failed", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        String result = text.toString();
        int start = result.indexOf("<?");
        if (start > 0) {
            result = result.substring(start);
        }
        return result;
    }

    // Keeps the last good download in the app's private storage
    private void saveCopy(String category, String xml) {
        try (FileOutputStream out = context.openFileOutput("saved_" + category + ".xml", Context.MODE_PRIVATE)) {
            out.write(xml.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Log.e("TrafficRepository", "Could not save a copy", e);
        }
    }

    // Returns the saved copy, or null if there isn't one
    private String readSavedCopy(String category) {
        File file = new File(context.getFilesDir(), "saved_" + category + ".xml");
        if (!file.exists()) {
            return null;
        }
        StringBuilder text = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            Log.e("TrafficRepository", "Could not read the saved copy", e);
            return null;
        }
        return text.toString();
    }
}