package com.xebia.lottery.queries;

import java.util.UUID;

import com.xebia.cqrs.domain.ValueObject;
import com.xebia.lottery.shared.LotteryInfo;

public class LotteryInfoQueryResult extends ValueObject {

    private static final long serialVersionUID = 1L;
    
    private final UUID lotteryId;
    private final long lotteryVersion;
    private final LotteryInfo lotteryInfo;

    public LotteryInfoQueryResult(UUID lotteryId, long lotteryVersion, LotteryInfo lotteryInfo) {
        this.lotteryId = lotteryId;
        this.lotteryVersion = lotteryVersion;
        this.lotteryInfo = lotteryInfo;
    }
    
    public UUID getId() {
        return lotteryId;
    }
    
    public long getVersion() {
        return lotteryVersion;
    }
    
    public LotteryInfo getInfo() {
        return lotteryInfo;
    }

}
