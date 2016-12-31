package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import java.util.concurrent.TimeUnit;

/**
 * A singleton object containing all the information required in the app. Uses
 * a synchronized getter since the object is used in multiple threads.
 */
class PoolSettings {
    private static PoolSettings classInstance;

    private final String[] UNITS = {"H/s", "KH/s", "MH/s", "GH/s", "TH/s", "PH/s", "EH/s"};

    private String poolAddress;
    private String walletAddress;
    private int poolPort;
    private boolean syncData;
    private TimeUnit syncUnit;
    private int syncScalar;
    private boolean isInitalLaunch;

    private double fee;
    private long coinUnits;
    private int coinDifficultyTarget;
    private double donationAmount;
    private long minPayment;
    private String symbol;

    private int totalBlocks;
    private boolean newBlockFound;
    private int currMiners;
    private long poolHashRate;
    private long poolLastBlockFound;

    private long difficulty;
    private long blockHeight;
    private long networkLastBlockFound;
    private long lastBlockReward;

    private long pendingBalance;
    private long totalPaid;
    private long lastShare;
    private String hashRate;
    private long totalShares;

    long getTotalShares() { return totalShares; }

    void setTotalShares(long totalShares) { this.totalShares = totalShares; }

    long getPendingBalance() { return pendingBalance; }

    void setPendingBalance(long pendingBalance) { this.pendingBalance = pendingBalance; }

    long getTotalPaid() { return totalPaid; }

    void setTotalPaid(long totalPaid) { this.totalPaid = totalPaid; }

    long getLastShare() { return lastShare; }

    void setLastShare(long lastShare) { this.lastShare = lastShare; }

    String getHashRate() { return hashRate; }

    void setHashRate(String hashRate) { this.hashRate = hashRate; }

    long getNetworkHashRate() {
        try {
            return difficulty / coinDifficultyTarget;
        } catch (NumberFormatException e) {
            return 0;
        } catch (ArithmeticException e) {
            return  0;
        }
    }

    long getDifficulty() { return difficulty; }

    void setDifficulty(long difficulty) { this.difficulty = difficulty; }

    long getBlockHeight() { return blockHeight; }

    void setBlockHeight(long blockHeight) { this.blockHeight = blockHeight; }

    long getNetworkLastBlockFound() { return networkLastBlockFound; }

    void setNetworkLastBlockFound(long networkLastBlockFound) {
        this.networkLastBlockFound = networkLastBlockFound;
    }

    long getLastBlockReward() { return lastBlockReward; }

    void setLastBlockReward(long lastBlockReward) {
        this.lastBlockReward = lastBlockReward;
    }

    long getPoolLastBlockFound() { return poolLastBlockFound; }

    void setPoolLastBlockFound(long poolLastBlockFound) {
        this.poolLastBlockFound = poolLastBlockFound; }

    long getPoolHashRate() { return poolHashRate; }

    void setPoolHashRate(long poolHashRate) { this.poolHashRate = poolHashRate; }

    int getCurrMiners() { return currMiners; }

    void setCurrMiners(int currMiners) { this.currMiners = currMiners; }

    int getTotalBlocks() { return totalBlocks; }

    void setTotalBlocks(int totalBlocks) { this.totalBlocks = totalBlocks; }

    String getPoolAddr() { return poolAddress; }

    void setPoolAddr(String poolAddress) { this.poolAddress = poolAddress; }

    String getWalletAddress() { return  walletAddress; }

    void setWalletAddress(String walletAddress) { this.walletAddress = walletAddress; }

    int getPoolPort() { return poolPort; }

    void setPoolPort(int poolPort) { this.poolPort = poolPort; }

    boolean shouldSync() { return syncData; }

    void setSyncState(boolean syncData) { this.syncData = syncData; }

    void setNewBlockFound (boolean newBlockFound) { this.newBlockFound = newBlockFound; }

    boolean getNewBlockFound () { return newBlockFound; }

    double getFee() { return fee; }

    void setFee(double fee) { this.fee = fee; }

    long getCoinUnits() {
        if (coinUnits == 0)
            return 1;
        return coinUnits;
    }

    void setCoinUnits(long coinUnits) { this.coinUnits = coinUnits; }

    int getCoinDifficultyTarget() { return coinDifficultyTarget; }

    void setCoinDifficultyTarget(int coinDifficultyTarget) {
        this.coinDifficultyTarget = coinDifficultyTarget;
    }

    double getDonationAmount() { return donationAmount; }

    void setDonationAmount(double donationAmount) { this.donationAmount = donationAmount; }

    long getMinPayment() { return minPayment; }

    void setMinPayment(long minPayment) { this.minPayment = minPayment; }

    String getSymbol() { return symbol; }

    void setSymbol(String symbol) { this.symbol = symbol; }

    TimeUnit getSyncUnit() { return syncUnit; }

    void setSyncUnit(TimeUnit syncUnit) { this.syncUnit = syncUnit; }

    int getSyncScalar() { return syncScalar; }

    void setSyncScalar(int syncScalar) { this.syncScalar = syncScalar; }

    String[] getUnits () { return UNITS; }

    void setLaunchState (boolean state) { isInitalLaunch = state; }

    boolean isInitalLaunch () { return  isInitalLaunch; }

    synchronized static PoolSettings getInstance() {
        if (classInstance == null)
            classInstance = new PoolSettings();
        return classInstance;
    }

    private PoolSettings() { }

}
