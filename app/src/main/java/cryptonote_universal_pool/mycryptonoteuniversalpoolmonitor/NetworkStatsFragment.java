package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Fragment for viewing current Network stats. Displays information about the Network of the pool
 * which is being monitored.
 *
 * @author Anthony Tam
 */
public class NetworkStatsFragment extends Fragment implements DismissibleFragment {
    private ScheduledExecutorService executorService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.network_stats, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView)view.findViewById(R.id.txt_pool_title)).setText(
                PoolSettings.getInstance().getPoolAddr());
        final Runnable updateValues = new Runnable() {
            @Override
            public void run() {
                updateValues(view);
            }
        };
        Runnable viewPost = new Runnable() {
            @Override
            public void run() {
                view.post(updateValues);
            }
        };
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(viewPost, 0, 20, TimeUnit.SECONDS);
    }

    private void updateValues(View view) {
        PoolSettings settings = PoolSettings.getInstance();
        ((TextView) view.findViewById(R.id.txt_hashrate_value)).setText(String.format(Locale.US,
                "%s", getFormattedUnits(settings.getNetworkHashRate())));
        ((TextView) view.findViewById(R.id.txt_blockfound_value)).setText(String.format(Locale.US,
                "%s", getTimeBetween(new Date(), new Date(settings.getNetworkLastBlockFound()*1000))));
        ((TextView) view.findViewById(R.id.txt_diff_value)).setText(String.format(Locale.US,
                "%d", settings.getDifficulty()));
        ((TextView) view.findViewById(R.id.txt_blockheight_value)).setText(String.format(Locale.US,
                "%d", settings.getBlockHeight()));
        ((TextView) view.findViewById(R.id.txt_lastreward_value)).setText(String.format(Locale.US,
                "%s %s", new BigDecimal((double)settings.getLastBlockReward() / (double)settings.getCoinUnits())
                        .setScale(5, RoundingMode.HALF_UP).toString(), settings.getSymbol()));

    }

    private String getTimeBetween(Date date1, Date date2) {
        long timeDiff = (date1.getTime() - date2.getTime())/1000;
        String unit = timeDiff == 1 ? "Second Ago" : "Seconds Ago";
        if (timeDiff >= 60) {
            timeDiff /= 60;
            unit = timeDiff == 1 ? "Minute Ago" : "Minutes Ago";
            if (timeDiff >= 60) {
                timeDiff /= 60;
                unit = timeDiff == 1 ? "Hour Ago" : "Hours Ago";
                if (timeDiff >= 24) {
                    timeDiff /= 24;
                    unit = timeDiff == 1 ? "Day Ago" : "Days Ago";
                    if (timeDiff >= 7) {
                        timeDiff /= 7;
                        unit = timeDiff == 1 ? "Week Ago" : "Weeks Ago";
                    }
                }
            }
        }
        return String.format(Locale.US, "%d %s", timeDiff, unit);
    }

    private String getFormattedUnits(double toFormat) {
        int depth = 0;
        while (toFormat >= 1000) {
            if (depth == 6) //We have up to 7 denominations of units
                break;
            toFormat /= 1000;
            depth += 1;
        }
        return String.format(Locale.US, "%s %s", new BigDecimal(toFormat).setScale(2,
                RoundingMode.HALF_UP).toString(), PoolSettings.getInstance().getUnits()[depth]);
    }

    public void onDismiss() {
        executorService.shutdownNow();
    }

}
