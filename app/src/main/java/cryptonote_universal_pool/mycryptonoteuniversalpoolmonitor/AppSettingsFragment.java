package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.Locale;

import cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor.barcode.BarcodeCaptureActivity;

/**
 * Created by tamfire on 22/12/16.
 */
public class AppSettingsFragment extends Fragment implements DissmissableFragment,
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
        settings.setWalletAddress(((EditText)currentView.findViewById(R.id.edit_walletaddr))
                                                                             .getText().toString());
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
