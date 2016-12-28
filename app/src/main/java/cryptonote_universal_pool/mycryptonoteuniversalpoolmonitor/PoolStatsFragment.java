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
 * Fragment for viewing current Pool stats. Displays information about the pool which is
 * being monitored.
 *
 * @author Anthony Tam
 */
public class PoolStatsFragment extends Fragment implements DismissibleFragment {
    private ScheduledExecutorService executorService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pool_stats, container, false);
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
        ((TextView) view.findViewById(R.id.txt_poolrate_value)).setText(String.format(Locale.US,
                "%d", settings.getPoolHashRate()));
        ((TextView) view.findViewById(R.id.txt_blockfound_value)).setText(String.format(Locale.US,
                "%d", settings.getPoolLastBlockFound()));
        ((TextView) view.findViewById(R.id.txt_totblocks_value)).setText(String.format(Locale.US,
                "%d", settings.getTotalBlocks()));
        ((TextView) view.findViewById(R.id.txt_numminers_value)).setText(String.format(Locale.US,
                "%d", settings.getCurrMiners()));
        ((TextView) view.findViewById(R.id.txt_donations_value)).setText(String.format(Locale.US,
                "%f", settings.getDonationAmount()));
        ((TextView) view.findViewById(R.id.txt_poolfee_value)).setText(String.format(Locale.US,
                "%f", settings.getFee()));

    }

    public void onDismiss() {
        executorService.shutdownNow();
    }

}
