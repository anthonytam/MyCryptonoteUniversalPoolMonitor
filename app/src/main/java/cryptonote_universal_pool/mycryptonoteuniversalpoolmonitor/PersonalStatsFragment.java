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
 * Created by tamfire on 22/12/16.
 */
public class PersonalStatsFragment extends Fragment implements DissmissableFragment {
    private ScheduledExecutorService executorService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.personal_stats, container, false);
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
        ((TextView) view.findViewById(R.id.txt_pendbal_value)).setText(String.format(Locale.US,
                "%d", settings.getPendingBalance()));
        ((TextView) view.findViewById(R.id.txt_totpaid_value)).setText(String.format(Locale.US,
                "%d", settings.getTotalPaid()));
        ((TextView) view.findViewById(R.id.txt_lastshare_value)).setText(String.format(Locale.US,
                "%d", settings.getLastShare()));
        ((TextView) view.findViewById(R.id.txt_hashrate_value)).setText(String.format(Locale.US,
                "%s", settings.getHashRate()));
        ((TextView) view.findViewById(R.id.txt_totshares_value)).setText(String.format(Locale.US,
                "%d", settings.getTotalShares()));

    }

    public void onDismiss() {
        executorService.shutdownNow();
    }

}
