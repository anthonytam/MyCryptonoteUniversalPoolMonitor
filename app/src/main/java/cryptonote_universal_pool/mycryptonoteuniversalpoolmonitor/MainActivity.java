package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main activity of the program. Creates all event handlers for GUI elements as well as begins
 * timer for data fetching.
 *
 * @author Anthony Tam
 */
public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private Fragment currentFragment;
    private ScheduledExecutorService executorService;

    private ActionBarDrawerToggle drawerToggle;
    private Runnable fetchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAppSettings();

        fetchData = new Runnable() {
            @Override
            public void run() {
                if (PoolSettings.getInstance().shouldSync()) {
                    new DataFetcher().execute();
                    if (PoolSettings.getInstance().getNewBlockFound() && PoolSettings.getInstance().shouldShowNotifications()) {
                        long[] vibratePattern = {0, 200, 200, 200};
                        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(MainActivity.super.getApplicationContext())
                                .setSmallIcon(R.drawable.cryptonotelogo)
                                .setContentTitle("New Block Found!")
                                .setContentText(String.format(Locale.US, "Block value: %s %s",
                                        new BigDecimal((double)PoolSettings.getInstance().getLastBlockReward()/
                                                (double)PoolSettings.getInstance().getCoinUnits())
                                .setScale(2, BigDecimal.ROUND_HALF_UP).toString(), PoolSettings.getInstance().getSymbol()))
                                .setVibrate(vibratePattern);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0, nBuilder.build());
                    }
                }
            }
        };
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(fetchData, 0,
                                            PoolSettings.getInstance().getSyncScalar(),
                                            PoolSettings.getInstance().getSyncUnit());
        // Let data download initially
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        currentFragment = new PoolStatsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent,currentFragment)
                                                                                        .commit();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nvDrawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                }
        );
    }

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment;

        switch (menuItem.getItemId()) {
            case R.id.nav_pool:
                fragment = new PoolStatsFragment();
                break;
            case R.id.nav_personal:
                fragment = new PersonalStatsFragment();
                break;
            case R.id.nav_settings:
                fragment = new AppSettingsFragment();
                break;
            case R.id.nav_network:
                fragment = new NetworkStatsFragment();
                break;
            case R.id.nav_refreshNow:
                new DataFetcher().execute();
                if (PoolSettings.getInstance().getNewBlockFound() && PoolSettings.getInstance().shouldShowNotifications()) {
                    long[] vibratePattern = {0, 200, 200, 200};
                    NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(MainActivity.super.getApplicationContext())
                            .setSmallIcon(R.drawable.cryptonotelogo)
                            .setContentTitle("New Block Found!")
                            .setContentText(String.format(Locale.US, "Block value: %s %s",
                                    new BigDecimal((double)PoolSettings.getInstance().getLastBlockReward()/
                                            (double)PoolSettings.getInstance().getCoinUnits())
                                    .setScale(2, BigDecimal.ROUND_HALF_UP).toString(), PoolSettings.getInstance().getSymbol()))
                            .setVibrate(vibratePattern);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, nBuilder.build());
                }
                try {
                    fragment = currentFragment.getClass().getConstructor().newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    fragment = currentFragment;
                }
                break;
            default:
                fragment = new PoolStatsFragment();
                break;
        }

        if (currentFragment.toString().equals("APP_SETTINGS")) {
            saveAppSettings();
        }
        ((DismissibleFragment) currentFragment).onDismiss();

        currentFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) { }
            @Override
            public void onDrawerStateChanged(int newState) { }
            @Override
            public void onDrawerClosed(View drawerView) { }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (getCurrentFocus() == null)
                    return;
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                getCurrentFocus().clearFocus();
            }
        });
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,
                R.string.drawer_close);
    }

    private void loadAppSettings() {
        SharedPreferences savedSettings = getSharedPreferences(getString(R.string.pool_preferences),
                                                                                                 0);
        PoolSettings settings = PoolSettings.getInstance();
        settings.setPoolAddr(savedSettings.getString("pooladdr", ""));
        settings.setWalletAddress(savedSettings.getString("walletaddr", ""));
        settings.setPoolPort(savedSettings.getInt("poolport", 8117));
        settings.setSyncState(savedSettings.getBoolean("syncstate", true));
        settings.setSyncUnit(TimeUnit.valueOf(savedSettings.getString("syncunit", "MINUTES")));
        settings.setSyncScalar(savedSettings.getInt("syncscalar", 1));
        settings.setShowNotifications(savedSettings.getBoolean("shownotifications", true));
        settings.setLaunchState(true);
    }

    private void saveAppSettings() {
        SharedPreferences savedSettings = getSharedPreferences(getString(R.string.pool_preferences),
                                                                                                 0);
        SharedPreferences.Editor editor = savedSettings.edit();
        PoolSettings settings = PoolSettings.getInstance();
        editor.putString("pooladdr", settings.getPoolAddr());
        editor.putString("walletaddr", settings.getWalletAddress());
        editor.putInt("poolport", settings.getPoolPort());
        editor.putBoolean("syncstate", settings.shouldSync());
        editor.putString("syncunit", settings.getSyncUnit().name());
        editor.putInt("syncscalar", settings.getSyncScalar());
        editor.putBoolean("shownotifications", settings.shouldShowNotifications());
        editor.apply();

        executorService.shutdownNow();
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(fetchData, 0,
                settings.getSyncScalar(),
                settings.getSyncUnit());
    }
}
