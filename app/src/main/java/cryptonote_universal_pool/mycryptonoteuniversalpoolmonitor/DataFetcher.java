package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * Fetches data from the pool API. Will convert the data to a JSON objects and add the data
 * to the singleton instance variables.
 *
 * @author Anthony Tam
 */
class DataFetcher extends AsyncTask<String, String, Boolean> {

    protected Boolean doInBackground(String... params) {
        PoolSettings settings = PoolSettings.getInstance();
        if (settings.getPoolAddr() == null || settings.getPoolAddr().equals("")) {
            return false;
        }
        String generalStats = requestStats("/stats");
        if (generalStats.equals("")) {
            return false;
        }
        try {
            JSONObject json = new JSONObject(generalStats);

            JSONObject config = json.getJSONObject("config");
            String poolVer = config.getString("version");
            if(poolVer.equals("v0.99.3.3")) {
                //This version found coin calculation differently
                settings.setCoinUnits(config.getLong("denominationUnit")*10);
                settings.setCoinDifficultyTarget(config.getInt("weight"));

            } else {
                settings.setCoinUnits(config.getLong("coinUnits"));
                settings.setCoinDifficultyTarget(config.getInt("coinDifficultyTarget"));
            }
            settings.setFee(config.getDouble("fee"));
            settings.setSymbol(config.getString("symbol"));
            settings.setMinPayment(config.getLong("minPaymentThreshold"));
            settings.setDonationAmount(0.0);
            try {
                JSONObject donationAddrs = config.getJSONObject("donation");
                for (Iterator it = donationAddrs.keys(); it.hasNext(); )
                    settings.setDonationAmount(settings.getDonationAmount() + donationAddrs.getDouble(
                            (String) it.next()));
            } catch (JSONException e) {
                // The pool has no donations
            }

            JSONObject network = json.getJSONObject("network");
            settings.setDifficulty(network.getLong("difficulty"));
            settings.setBlockHeight(network.getLong("height"));
            settings.setNetworkLastBlockFound(network.getLong("timestamp"));
            settings.setLastBlockReward(network.getLong("reward"));

            JSONObject pool = json.getJSONObject("pool");
            if (pool.getInt("totalBlocks")-1 == settings.getTotalBlocks() && !settings.isInitalLaunch())
                settings.setNewBlockFound(true);
            else settings.setNewBlockFound(false);
            settings.setTotalBlocks(pool.getInt("totalBlocks"));
            settings.setCurrMiners(pool.getInt("miners"));
            settings.setPoolHashRate(pool.getLong("hashrate"));
            settings.setPoolLastBlockFound(pool.getLong("lastBlockFound"));


        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        if (settings.getWalletAddress() != null || !settings.getWalletAddress().equals("")) {
            String walletStats = requestStats(String.format("/stats_address?address=%s",
                    settings.getWalletAddress()));
            if (walletStats.equals(""))
                return false;
            try {
                JSONObject json = new JSONObject(walletStats);
                if (json.has("error"))
                    return false;

                JSONObject config = json.getJSONObject("stats");

                settings.setTotalShares(Long.parseLong(config.getString("hashes")));
                settings.setLastShare(Long.parseLong(config.getString("lastShare")));
                settings.setPendingBalance(Long.parseLong(config.getString("balance")));
                settings.setTotalPaid(Long.parseLong(config.getString("paid")));
                settings.setHashRate(config.getString("hashrate"));
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        PoolSettings.getInstance().setLaunchState(false);
    }

    private String requestStats (String path) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        PoolSettings settings = PoolSettings.getInstance();
        try {
            URL url = new URL("http", settings.getPoolAddr(), settings.getPoolPort(), path);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = wrapStream(connection.getContentEncoding(), connection.getInputStream());
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(String.format(Locale.US, "%s%s", line, "\n"));
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
        return "";
    }

    private static InputStream wrapStream(String contentEncoding, InputStream inputStream)
            throws IOException {
        if (contentEncoding == null || "identity".equalsIgnoreCase(contentEncoding)) {
            return inputStream;
        }
        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            return new GZIPInputStream(inputStream);
        }
        if ("deflate".equalsIgnoreCase(contentEncoding)) {
            return new InflaterInputStream(inputStream, new Inflater(true));
        }
        throw new RuntimeException("unsupported content-encoding: " + contentEncoding);
    }
}
