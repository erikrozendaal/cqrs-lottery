package com.xebia.lottery.queries;

import com.xebia.cqrs.domain.ValueObject;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.shared.LotteryInfo;

public class LotteryInfoQueryResult extends ValueObject {

    private static final long serialVersionUID = 1L;
    
    private final VersionedId lotteryId;
    private final LotteryInfo lotteryInfo;

    public LotteryInfoQueryResult(VersionedId lotteryId, LotteryInfo lotteryInfo) {
        this.lotteryId = lotteryId;
        this.lotteryInfo = lotteryInfo;
    }
    
    public VersionedId getLotteryId() {
        return lotteryId;
    }
    
    public LotteryInfo getLotteryInfo() {
        return lotteryInfo;
    }

}
