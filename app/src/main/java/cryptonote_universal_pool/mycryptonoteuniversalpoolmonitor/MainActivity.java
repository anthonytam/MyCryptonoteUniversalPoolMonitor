package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by tamfire on 22/12/16.
 */
public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private Fragment currentFragment;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadAppSettings();

        Runnable fetchData = new Runnable() {
            @Override
            public void run() {
                if (PoolSettings.getInstance().shouldSync())
                    new DataFetcher().execute();
            }
        };
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(fetchData, 0,
                                            PoolSettings.getInstance().getSyncScalar(),
                                            PoolSettings.getInstance().getSyncUnit());

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
                fragment = currentFragment;
                break;
            default:
                fragment = new PoolStatsFragment();
                break;
        }

        if (currentFragment.toString().equals("APP_SETTINGS")) {
            saveAppSettings();
        }
        ((DissmissableFragment) currentFragment).onDismiss();

        currentFragment = fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        //TODO: Close keyboard
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
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

        editor.apply();
    }
}
