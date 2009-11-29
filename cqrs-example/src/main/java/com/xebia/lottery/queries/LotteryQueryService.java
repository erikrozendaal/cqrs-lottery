package com.xebia.lottery.queries;

import java.util.List;
import java.util.UUID;

public interface LotteryQueryService {

    List<LotteryInfoQueryResult> findUpcomingLotteries();

    List<CustomerAccountQueryResult> findCustomers();

    List<CustomerTicketsQueryResult> findLotteryTicketsForCustomer(UUID customerId);

    String getCustomerName(UUID customerId);

}
