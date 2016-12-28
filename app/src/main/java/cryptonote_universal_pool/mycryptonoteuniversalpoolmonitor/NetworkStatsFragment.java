package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
                "%d", settings.getNetworkHashRate()));
        ((TextView) view.findViewById(R.id.txt_blockfound_value)).setText(String.format(Locale.US,
                "%d", settings.getNetworkLastBlockFound()));
        ((TextView) view.findViewById(R.id.txt_diff_value)).setText(String.format(Locale.US,
                "%d", settings.getDifficulty()));
        ((TextView) view.findViewById(R.id.txt_blockheight_value)).setText(String.format(Locale.US,
                "%d", settings.getBlockHeight()));
        ((TextView) view.findViewById(R.id.txt_lastreward_value)).setText(String.format(Locale.US,
                "%d", settings.getLastBlockReward()));

    }

    public void onDismiss() {
        executorService.shutdownNow();
    }

}
