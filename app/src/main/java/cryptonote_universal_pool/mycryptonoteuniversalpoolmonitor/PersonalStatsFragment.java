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
 * Fragment for viewing current user stats. Displays information about the user on the
 * pool which is being monitored.
 *
 * @author Anthony Tam
 */
public class PersonalStatsFragment extends Fragment implements DismissibleFragment {
    private ScheduledExecutorService executorService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.personal_stats, container, false);
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
        ((TextView) view.findViewById(R.id.txt_pendbal_value)).setText(String.format(Locale.US,
                "%s %s", new BigDecimal((double)settings.getPendingBalance() / (double)settings.getCoinUnits())
                        .setScale(5, RoundingMode.HALF_UP).toString(), settings.getSymbol()));
        ((TextView) view.findViewById(R.id.txt_totpaid_value)).setText(String.format(Locale.US,
                "%s %s", new BigDecimal((double)settings.getTotalPaid() / (double)settings.getCoinUnits())
                        .setScale(5, RoundingMode.HALF_UP).toString(), settings.getSymbol()));
        ((TextView) view.findViewById(R.id.txt_lastshare_value)).setText(String.format(Locale.US,
                "%s", getTimeBetween(new Date(), new Date(settings.getLastShare()*1000))));
        ((TextView) view.findViewById(R.id.txt_hashrate_value)).setText(String.format(Locale.US,
                "%s", settings.getHashRate()));
        ((TextView) view.findViewById(R.id.txt_totshares_value)).setText(String.format(Locale.US,
                "%d", settings.getTotalShares()));

    }

    private String getTimeBetween(Date date1, Date date2) {
        long timeDiff = (date1.getTime() - date2.getTime())/1000;
        String unit = timeDiff == 1 ? "Second" : "Seconds";
        if (timeDiff >= 60) {
            timeDiff /= 60;
            unit = timeDiff == 1 ? "Minute" : "Minutes";
            if (timeDiff >= 60) {
                timeDiff /= 60;
                if (timeDiff >= 24) {
                    timeDiff /= 24;
                    unit = timeDiff == 1 ? "Day" : "Days";
                    if (timeDiff >= 7) {
                        timeDiff /= 7;
                        unit = timeDiff == 1 ? "Week" : "Weeks";
                    }
                }
            }
        }
        return String.format(Locale.US, "%d %s", timeDiff, unit);
    }

    public void onDismiss() {
        executorService.shutdownNow();
    }

}
