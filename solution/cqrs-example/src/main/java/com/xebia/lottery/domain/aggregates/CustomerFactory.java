package com.xebia.lottery.domain.aggregates;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xebia.cqrs.bus.Bus;
import com.xebia.cqrs.domain.VersionedId;
import com.xebia.lottery.commands.ValidationError;
import com.xebia.lottery.shared.CustomerInfo;

@Component
public class CustomerFactory {

    @Autowired private Bus bus;
    
    public Customer create(VersionedId customerId, CustomerInfo info, double initialAccountBalance) {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (StringUtils.isBlank(info.getName())) {
            errors.add(new ValidationError("customer name is required"));
        }
        if (initialAccountBalance < 10.0) {
            errors.add(new ValidationError("minimum account balance is 10.00"));
        }
        if (errors.isEmpty()) {
            return new Customer(customerId, info, initialAccountBalance);
        } else {
            bus.reply(errors);
            return null;
        }
    }

}
