package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

/**
 * Created by tamfire on 25/12/16.
 */
public class PoolSettings {
    private static PoolSettings classInstance;

    private String poolAddress;
    private int poolPort;

    public synchronized static PoolSettings getInstance() {
        if (classInstance == null)
            classInstance = new PoolSettings();
        return classInstance;
    }

    private PoolSettings() { }

    public String getPoolAddr() {
        return poolAddress;
    }

    public void setPoolAddr(String newAddress) {
        poolAddress = newAddress;
    }

    public int getPoolPort() { return poolPort; }

    public void setPoolPort(int poolPort) { this.poolPort = poolPort; }
}
