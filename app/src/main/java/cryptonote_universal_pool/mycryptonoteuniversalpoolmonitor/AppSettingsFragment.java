package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor.barcode.BarcodeCaptureActivity;

/**
 * Fragment for managing app settings. All options which can be changed my the user are here
 * and will be saved when the user leaves the screen by using the DissmissableFragment interface.
 *
 * @author Anthony Tam
 */
public class AppSettingsFragment extends Fragment implements DismissibleFragment,
                                                             View.OnClickListener {
    private final int BARCODE_READER_REQ = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PoolSettings settings = PoolSettings.getInstance();
        view.findViewById(R.id.btn_scanaddr).setOnClickListener(this);
        ((EditText)view.findViewById(R.id.edit_pooladdr)).setText(settings.getPoolAddr());
        ((EditText)view.findViewById(R.id.edit_poolport)).setText(String.format(Locale.US, "%d",
                                                                        settings.getPoolPort()));
        ((EditText)view.findViewById(R.id.edit_walletaddr)).setText(settings.getWalletAddress());
        ((Switch)view.findViewById(R.id.swc_enablenotifications)).setChecked(settings.shouldShowNotifications());
        if (settings.shouldSync())
            ((Spinner)view.findViewById(R.id.spn_refresh)).setSelection(getIndex((Spinner)view
                .findViewById(R.id.spn_refresh),String.format(Locale.US, "%d %s",
                settings.getSyncScalar(), settings.getSyncUnit().name().charAt(0) +
                        (settings.getSyncScalar() == 1 && settings.getSyncUnit().name().equals("MINUTES") ||
                                settings.getSyncUnit().name().equals("HOURS") ? settings.getSyncUnit().name()
                                .toLowerCase(Locale.US).substring(1, settings.getSyncUnit().name().length() - 1) :
                                settings.getSyncUnit().name().toLowerCase(Locale.US).substring(1)))));
        else
            ((Spinner)view.findViewById(R.id.spn_refresh)).setSelection(getIndex((Spinner)view
                .findViewById(R.id.spn_refresh), "Never"));
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++)
            if (spinner.getItemAtPosition(i).equals(myString))
                return i;
        return -1;
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

        String walletAddr = ((EditText)currentView.findViewById(R.id.edit_walletaddr)).getText().toString();
        Pattern p = Pattern.compile("^4[0-9AB][123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]{93}$");
        if (p.matcher(walletAddr).matches()) {
            settings.setWalletAddress(walletAddr);
        } else {
            Toast.makeText(getActivity().getApplicationContext(), getText(R.string.eng_invalidwallet),
                    Toast.LENGTH_SHORT).show();
            settings.setWalletAddress("");
        }

        settings.setShowNotifications(((Switch)currentView.findViewById(
                R.id.swc_enablenotifications)).isChecked());

        switch (((Spinner)currentView.findViewById(R.id.spn_refresh)).getSelectedItem().toString()) {
            case "30 Seconds":
                settings.setSyncScalar(30);
                settings.setSyncUnit(TimeUnit.SECONDS);
                settings.setSyncState(true);
                break;
            case "1 Minute":
                settings.setSyncScalar(1);
                settings.setSyncUnit(TimeUnit.MINUTES);
                settings.setSyncState(true);
                break;
            case "5 Minutes":
                settings.setSyncScalar(5);
                settings.setSyncUnit(TimeUnit.MINUTES);
                settings.setSyncState(true);
                break;
            case "15 Minutes":
                settings.setSyncScalar(15);
                settings.setSyncUnit(TimeUnit.MINUTES);
                settings.setSyncState(true);
                break;
            case "30 Minutes":
                settings.setSyncScalar(30);
                settings.setSyncUnit(TimeUnit.MINUTES);
                settings.setSyncState(true);
                break;
            case "1 Hour":
                settings.setSyncScalar(1);
                settings.setSyncUnit(TimeUnit.HOURS);
                settings.setSyncState(true);
                break;
            case "Never":
                settings.setSyncState(false);
                break;
        }

    }

    public void onClick(View view) {
        if (view.getId() == R.id.btn_scanaddr) {
            Intent intent = new Intent(getActivity().getApplicationContext(),
                    BarcodeCaptureActivity.class);
            startActivityForResult(intent, BARCODE_READER_REQ);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQ) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    View view = getView();
                    if (view != null) {
                        ((EditText) view.findViewById(R.id.edit_walletaddr))
                                .setText(barcode.displayValue);
                    }
                }
            } else Log.e("BarcodeScanner", CommonStatusCodes.getStatusCodeString(resultCode));
        } else super.onActivityResult(requestCode, resultCode, data);
    }
}
