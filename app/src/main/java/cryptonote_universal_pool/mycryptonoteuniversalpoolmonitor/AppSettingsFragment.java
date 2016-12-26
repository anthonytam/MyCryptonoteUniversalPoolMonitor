package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Locale;

/**
 * Created by tamfire on 22/12/16.
 */
public class AppSettingsFragment extends Fragment implements DissmissableFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PoolSettings settings = PoolSettings.getInstance();
        ((EditText)view.findViewById(R.id.edit_pooladdr)).setText(settings.getPoolAddr());
        ((EditText)view.findViewById(R.id.edit_poolport)).setText(String.format(Locale.US, "%d",
                                                                        settings.getPoolPort()));
    }

    @Override
    public String toString() { return "APP_SETTINGS"; }

    public void onDismiss () {
        View currentView = getView();
        //Prevent a NPE
        if (currentView == null)
            return;
        PoolSettings settings = PoolSettings.getInstance();
        settings.setPoolAddr(((EditText)currentView.findViewById(R.id.edit_pooladdr)).getText()
                                                                                      .toString());
        settings.setPoolPort(Integer.parseInt(((EditText)currentView.findViewById(
                                                        R.id.edit_poolport)).getText().toString()));
    }

}
