package cryptonote_universal_pool.mycryptonoteuniversalpoolmonitor;

import java.util.concurrent.TimeUnit;

/**
 * Created by tamfire on 25/12/16.
 */
public class PoolSettings {
    private static PoolSettings classInstance;

    private String poolAddress;
    private String walletAddress;
    private int poolPort;
    private boolean syncData;
    private TimeUnit syncUnit;
    private int syncScalar;

    private double fee;
    private long coinUnits;
    private int coinDifficultyTarget;
    private double donationAmount;
    private long minPayment;
    private String symbol;

    private int totalBlocks;
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

    public long getTotalShares() { return totalShares; }

    public void setTotalShares(long totalShares) { this.totalShares = totalShares; }

    public long getPendingBalance() { return pendingBalance; }

    public void setPendingBalance(long pendingBalance) { this.pendingBalance = pendingBalance; }

    public long getTotalPaid() { return totalPaid; }

    public void setTotalPaid(long totalPaid) { this.totalPaid = totalPaid; }

    public long getLastShare() { return lastShare; }

    public void setLastShare(long lastShare) { this.lastShare = lastShare; }

    public String getHashRate() { return hashRate; }

    public void setHashRate(String hashRate) { this.hashRate = hashRate; }

    public long getNetworkHashRate() { return difficulty/coinDifficultyTarget; }

    public long getDifficulty() { return difficulty; }

    public void setDifficulty(long difficulty) { this.difficulty = difficulty; }

    public long getBlockHeight() { return blockHeight; }

    public void setBlockHeight(long blockHeight) { this.blockHeight = blockHeight; }

    public long getNetworkLastBlockFound() { return networkLastBlockFound; }

    public void setNetworkLastBlockFound(long networkLastBlockFound) {
        this.networkLastBlockFound = networkLastBlockFound;
    }

    public long getLastBlockReward() { return lastBlockReward; }

    public void setLastBlockReward(long lastBlockReward) {
        this.lastBlockReward = lastBlockReward;
    }

    public long getPoolLastBlockFound() { return poolLastBlockFound; }

    public void setPoolLastBlockFound(long poolLastBlockFound) {
        this.poolLastBlockFound = poolLastBlockFound; }

    public long getPoolHashRate() { return poolHashRate; }

    public void setPoolHashRate(long poolHashRate) { this.poolHashRate = poolHashRate; }

    public int getCurrMiners() { return currMiners; }

    public void setCurrMiners(int currMiners) { this.currMiners = currMiners; }

    public int getTotalBlocks() { return totalBlocks; }

    public void setTotalBlocks(int totalBlocks) { this.totalBlocks = totalBlocks; }

    public String getPoolAddr() { return poolAddress; }

    public void setPoolAddr(String poolAddress) { this.poolAddress = poolAddress; }

    public String getWalletAddress() { return  walletAddress; }

    public void setWalletAddress(String walletAddress) { this.walletAddress = walletAddress; }

    public int getPoolPort() { return poolPort; }

    public void setPoolPort(int poolPort) { this.poolPort = poolPort; }

    public boolean shouldSync() { return syncData; }

    public void setSyncState(boolean syncData) { this.syncData = syncData; }

    public double getFee() { return fee; }

    public void setFee(double fee) { this.fee = fee; }

    public long getCoinUnits() { return coinUnits; }

    public void setCoinUnits(long coinUnits) { this.coinUnits = coinUnits; }

    public int getCoinDifficultyTarget() { return coinDifficultyTarget; }

    public void setCoinDifficultyTarget(int coinDifficultyTarget) {
        this.coinDifficultyTarget = coinDifficultyTarget;
    }

    public double getDonationAmount() { return donationAmount; }

    public void setDonationAmount(double donationAmount) { this.donationAmount = donationAmount; }

    public long getMinPayment() { return minPayment; }

    public void setMinPayment(long minPayment) { this.minPayment = minPayment; }

    public String getSymbol() { return symbol; }

    public void setSymbol(String symbol) { this.symbol = symbol; }

    public TimeUnit getSyncUnit() { return syncUnit; }

    public void setSyncUnit(TimeUnit syncUnit) { this.syncUnit = syncUnit; }

    public int getSyncScalar() { return syncScalar; }

    public void setSyncScalar(int syncScalar) { this.syncScalar = syncScalar; }

    public synchronized static PoolSettings getInstance() {
        if (classInstance == null)
            classInstance = new PoolSettings();
        return classInstance;
    }

    private PoolSettings() { }

}
