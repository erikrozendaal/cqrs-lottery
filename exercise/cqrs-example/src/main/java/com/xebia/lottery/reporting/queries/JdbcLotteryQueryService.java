package com.xebia.lottery.reporting.queries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xebia.lottery.queries.CustomerAccountQueryResult;
import com.xebia.lottery.queries.CustomerTicketsQueryResult;
import com.xebia.lottery.queries.LotteryInfoQueryResult;
import com.xebia.lottery.queries.LotteryQueryService;
import com.xebia.lottery.shared.LotteryInfo;

@Service
@Transactional(readOnly=true)
public class JdbcLotteryQueryService implements LotteryQueryService {

    @Autowired
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    @PostConstruct
    public void initQuerySchema() {
        simpleJdbcTemplate.update("drop table ticket if exists");
        simpleJdbcTemplate.update("drop table customer if exists");
        simpleJdbcTemplate.update("drop table lottery if exists");
        simpleJdbcTemplate.update("create table lottery(id char(36) primary key, version bigint not null, name varchar not null, drawing_timestamp timestamp not null, prize_amount decimal(9, 2) not null, ticket_price decimal(9, 2) not null)");
        simpleJdbcTemplate.update("create table customer(id char(36) primary key, version bigint not null, name varchar not null, account_balance decimal(9, 2) not null, email varchar not null, street_name varchar not null, house_number varchar not null, postal_code varchar not null, city varchar not null, country varchar not null)");
        simpleJdbcTemplate.update("create table ticket(number varchar not null, lottery_id char(36) not null, customer_id char(36) not null, primary key (number, lottery_id), foreign key (lottery_id) references lottery (id), foreign key (customer_id) references customer (id))");
    }

    public List<LotteryInfoQueryResult> findUpcomingLotteries() {
        return simpleJdbcTemplate.query("select id, version, name, drawing_timestamp, prize_amount, ticket_price from lottery order by name", new ParameterizedRowMapper<LotteryInfoQueryResult>() {
            public LotteryInfoQueryResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new LotteryInfoQueryResult(
                        UUID.fromString(rs.getString("id")),
                        rs.getLong("version"),
                        new LotteryInfo(
                                rs.getString("name"),
                                new Date(rs.getTimestamp("drawing_timestamp").getTime()),
                                rs.getDouble("prize_amount"),
                                rs.getDouble("ticket_price")));
            }
        });
    }
    
    public List<CustomerAccountQueryResult> findCustomers() {
        return simpleJdbcTemplate.query("select id, version, name, account_balance from customer order by name", new ParameterizedRowMapper<CustomerAccountQueryResult>() {

            public CustomerAccountQueryResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CustomerAccountQueryResult(
                        UUID.fromString(rs.getString("id")),
                        rs.getLong("version"), 
                        rs.getString("name"), 
                        rs.getDouble("account_balance"));
            }
        });
    }
    
    public List<CustomerTicketsQueryResult> findLotteryTicketsForCustomer(UUID customerId) {
        return simpleJdbcTemplate.query("select ticket.number as ticket_number, lottery.name as lottery_name, customer.name as customer_name " +
                "from ticket inner join customer on ticket.customer_id = customer.id inner join lottery on ticket.lottery_id = lottery.id where customer.id = ?",
            new ParameterizedRowMapper<CustomerTicketsQueryResult>() {

            public CustomerTicketsQueryResult mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CustomerTicketsQueryResult(
                        rs.getString("ticket_number"),
                        rs.getString("lottery_name"),
                        rs.getString("customer_name"));
            }
        }, customerId);
    }
    
    public String getCustomerName(UUID customerId) {
        return simpleJdbcTemplate.queryForObject("select name from customer where id = ?", String.class, customerId);
    }
    
}
