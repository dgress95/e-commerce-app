package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.vintage.engine.discovery.IsPotentialJUnit4TestClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JdbcCustomerDao implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCustomerDao(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource); }

    @Override
    public Customer getCustomer(int customerId) {
        Customer customer = null;
        String sql = "select customer_id, name, street_address1, street_address2, " +
                "city, state, zip_code " +
                "from customer " +
                "where customer_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, customerId);
        if (result.next()) {
            customer = mapRowToCustomer(result);
        }
        return customer;
    }

    @Override
    public List<Customer> getCustomers() {
        List<Customer> customerList = new ArrayList<>();
        String sql = "select customer_id, name, street_address1, street_address2, city, state, zip_code from customer " +
                "order by customer_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            customerList.add(mapRowToCustomer(results));
        }
        return customerList;
    }

    @Override
    public Customer createCustomer(Customer newCustomer) {
        String sql = "insert into customer (name, street_address1, street_address2, city, state, zip_code) " +
                "values (?, ?, ?, ?, ?, ?) " +
                "returning customer_id;";
        Integer customerID = jdbcTemplate.queryForObject(sql, Integer.class, newCustomer.getName(), newCustomer.getStreetAddress1(), newCustomer.getStreetAddress2(),
                newCustomer.getCity(), newCustomer.getState(), newCustomer.getZipCode());
        newCustomer.setCustomerId(customerID);
        return newCustomer;
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        String updateCustomerSql = "UPDATE customer " +
                "SET name = ?, street_address1 = ?, street_address2 = ?, " +
                "city = ?, state = ?, zip_code = ? " +
                "WHERE customer_id = ?;";
        jdbcTemplate.update(updateCustomerSql, updatedCustomer.getName(), updatedCustomer.getStreetAddress1(),
                updatedCustomer.getStreetAddress2(), updatedCustomer.getCity(), updatedCustomer.getState(), updatedCustomer.getZipCode(), updatedCustomer.getCustomerId());
    }

    private Customer mapRowToCustomer(SqlRowSet rowSet) {
        Customer customer = new Customer();
        customer.setCustomerId(rowSet.getInt("customer_id"));
        customer.setName(rowSet.getString("name"));
        customer.setStreetAddress1(rowSet.getString("street_address1"));
        if (rowSet.getString("street_address2") != null) {
            customer.setStreetAddress2(rowSet.getString("street_address2"));
        }
        customer.setCity(rowSet.getString("city"));
        customer.setState(rowSet.getString("state"));
        customer.setZipCode(rowSet.getString("zip_code"));
        return customer;
    }
}
