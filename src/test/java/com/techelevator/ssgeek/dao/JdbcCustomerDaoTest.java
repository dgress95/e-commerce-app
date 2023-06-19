package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class JdbcCustomerDaoTest extends BaseDaoTests {

    private static final Customer CUSTOMER_1 = new Customer(1, "Customer 1", "Addr 1-1", "Addr 1-2", "City 1", "S1", "11111");
    private static final Customer CUSTOMER_2 = new Customer(2, "Customer 2", "Addr 2-1", "Addr 2-2", "City 2", "S2", "22222");
    private static final Customer CUSTOMER_3 = new Customer(3, "Customer 3", "Addr 3-1", null, "City 3", "S3", "33333");

    private JdbcCustomerDao dao;

    @Before
    public void setup() { dao = new JdbcCustomerDao(dataSource); }

    @Test
    public void getCustomer_returns_customer_by_id() {
        Customer customer = dao.getCustomer(CUSTOMER_1.getCustomerId());
        Assert.assertNotNull("getCustomer returned null", customer);
        assertCustomersMatch("getCustomer returned wrong or partial data", CUSTOMER_1, customer);

        customer = dao.getCustomer(CUSTOMER_2.getCustomerId());
        assertCustomersMatch("getCustomer returned wrong or partial data", CUSTOMER_2, customer);
    }

    @Test
    public void getCustomers_returns_list_of_all_customers() {
        List<Customer> customerList = dao.getCustomers();
        Assert.assertEquals("getCustomers failed to return all employees", 4, customerList.size());
        assertCustomersMatch("getCustomers returned wrong or partial data", CUSTOMER_1, customerList.get(0));
        assertCustomersMatch("getCustomers returned wrong or partial data", CUSTOMER_2, customerList.get(1));
        assertCustomersMatch("getCustomers returned wrong or partial data", CUSTOMER_3, customerList.get(2));
    }

    @Test
    public void createCustomer_returns_customer_with_id_and_expected_values() {
        Customer newCustomer = new Customer();
        newCustomer.setName("Customer 1");
        newCustomer.setStreetAddress1("Addr 1-1");
        newCustomer.setCity("City 1");
        newCustomer.setState("S1");
        newCustomer.setZipCode("11111");

        Customer createdCustomer = dao.createCustomer(newCustomer);

        Assert.assertNotEquals("createId not set when created, remained 0", 0, createdCustomer.getCustomerId());
        assertCustomersMatch("createCustomer does not create customer with expected values", createdCustomer, newCustomer);
    }

    @Test
    public void updated_customer_has_expected_name_when_retrieved() {
        Customer customer = dao.getCustomer(CUSTOMER_1.getCustomerId());
        Assert.assertNotNull("Can't test updateCustomer until getCustomer is working", customer);
        customer.setName("Test");

        dao.updateCustomer(customer);

        Customer updatedCustomer = dao.getCustomer(CUSTOMER_1.getCustomerId());
        Assert.assertEquals("updateCustomer failed to change customer name in database", "Test", updatedCustomer.getName());
    }

    private void assertCustomersMatch(String message, Customer expected, Customer actual) {
        Assert.assertEquals(message, expected.getCustomerId(), actual.getCustomerId());
        Assert.assertEquals(message, expected.getName(), actual.getName());
        Assert.assertEquals(message, expected.getStreetAddress1(), actual.getStreetAddress1());
        Assert.assertEquals(message, expected.getStreetAddress2(), actual.getStreetAddress2());
        Assert.assertEquals(message, expected.getCity(), actual.getCity());
        Assert.assertEquals(message, expected.getState(), actual.getState());
        Assert.assertEquals(message, expected.getZipCode(), actual.getZipCode());
    }
}
