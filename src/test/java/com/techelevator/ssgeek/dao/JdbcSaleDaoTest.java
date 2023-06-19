package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Sale;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

public class JdbcSaleDaoTest extends BaseDaoTests {

    private static final Sale SALE_1 = new Sale(1, 1, LocalDate.parse("2022-01-01"), null);
    private static final Sale SALE_2 = new Sale(2, 1, LocalDate.parse("2022-02-01"), LocalDate.parse("2022-02-02"));
    private static final Sale SALE_3 = new Sale(3, 2, LocalDate.parse("2022-03-01"), null);
    private static final Sale SALE_4 = new Sale(4, 2, LocalDate.parse("2022-01-01"), LocalDate.parse("2022-01-02"));

    private JdbcSaleDao dao;

    @Before
    public void setup() { dao = new JdbcSaleDao(dataSource); }

    @Test
    public void getSale_returns_sale_by_id() {
        Sale sale = dao.getSale(SALE_1.getSaleId());
        Assert.assertNotNull("getSale returned null", sale);
        assertSalesMatch("getSale returned wrong or partial data", SALE_1, sale);

        sale = dao.getSale(SALE_2.getSaleId());
        assertSalesMatch("getSale returned wrong or partial data", SALE_2, sale);
    }

    @Test
    public void getSalesUnshipped_returns_list_of_unshipped_sales() {
        List<Sale> saleList = dao.getSalesUnshipped();
        Assert.assertEquals("getSalesUnshipped failed to return all unshipped sales", 2, saleList.size());
        assertSalesMatch("getSalesUnshipped returned wrong or partial data", SALE_1, saleList.get(0));
        assertSalesMatch("getSalesUnshipped returned wrong or partial data", SALE_3, saleList.get(1));
    }

    @Test
    public void getSalesByCustomerId_returns_list_of_all_sales_for_customer() {
        List<Sale> saleList = dao.getSalesByCustomerId(SALE_1.getCustomerId());
        Assert.assertEquals("getSalesByCustomerId failed to return all customer sales", 2, saleList.size());
        assertSalesMatch("getSalesByCustomerId returned wrong or partial data", SALE_1, saleList.get(0));
        assertSalesMatch("getSalesByCustomerId returned wrong or partial data", SALE_2, saleList.get(1));
    }

    @Test
    public void getSalesByProductId_returns_list_of_all_sales_for_product() {
        List<Sale> saleList = dao.getSalesByProductId(1);
        Assert.assertEquals("getSalesByCustomerId failed to return all customer sales", 3, saleList.size());
        assertSalesMatch("getSalesByCustomerId returned wrong or partial data", SALE_1, saleList.get(0));
        assertSalesMatch("getSalesByCustomerId returned wrong or partial data", SALE_2, saleList.get(1));
        assertSalesMatch("getSalesByCustomerId returned wrong or partial data", SALE_3, saleList.get(2));
    }

    @Test
    public void createSale_created_new_sale_with_expected_values_and_id() {
        Sale newSale = new Sale();
        newSale.setCustomerId(1);
        newSale.setSaleDate(LocalDate.parse("2022-01-01"));
        newSale.setShipDate(null);

        Sale createdSale = dao.createSale(newSale);

        Assert.assertNotEquals("createSale ID not set when created, remained 0", 0, createdSale.getSaleId());
        assertSalesMatch("createSale does not create sale with expected values", createdSale, newSale);
    }

    @Test
    public void updateSale_has_expected_values_when_retrieved() {
        Sale sale = dao.getSale(SALE_1.getSaleId());
        Assert.assertNotNull("Can't test updateSale until getSale is working", sale);
        sale.setCustomerId(2);

        dao.updateSale(sale);

        Sale updatedSale = dao.getSale(SALE_1.getSaleId());
        Assert.assertEquals("updateSale failed to change sale customer id in database", 2, updatedSale.getCustomerId());
    }

    @Test
    public void deleted_sale_cant_be_retrieved() {
        dao.deleteSale(SALE_1.getSaleId());

        Sale sale = dao.getSale(SALE_1.getSaleId());
        Assert.assertNull("deleteSale failed to remove sale from database", sale);

        List<Sale> sales = dao.getSalesByCustomerId(1);
        Assert.assertEquals("deleteSales left too many sales in database", 1, sales.size());
        assertSalesMatch("deleteSales deleted wrong sale", SALE_2, sales.get(0));
    }


    private void assertSalesMatch(String message, Sale expected, Sale actual) {
        Assert.assertEquals(message, expected.getSaleId(), actual.getSaleId());
        Assert.assertEquals(message, expected.getCustomerId(), actual.getCustomerId());
        Assert.assertEquals(message, expected.getSaleDate(), actual.getSaleDate());
        Assert.assertEquals(message, expected.getShipDate(), actual.getShipDate());
    }
}
