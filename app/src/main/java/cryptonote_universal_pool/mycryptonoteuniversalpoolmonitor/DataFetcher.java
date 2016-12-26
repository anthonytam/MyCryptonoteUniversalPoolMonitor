package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import android.os.AsyncTask;
import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tamfire on 26/12/16.
 */
public class DataFetcher extends AsyncTask<String, String, String> {

    protected String doInBackground(String... params) {
        String generalStats = getGeneralStats();
        if (generalStats.equals(""))
            return null;
        try {
            JSONObject json = new JSONObject(generalStats);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private String getGeneralStats () {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        PoolSettings settings = PoolSettings.getInstance();
        try {
            URL url = new URL("http", settings.getPoolAddr(), settings.getPoolPort(), "/stats");
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }
            return buffer.toString();
            // Includes MalformedURLException (Is a subclass)
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
