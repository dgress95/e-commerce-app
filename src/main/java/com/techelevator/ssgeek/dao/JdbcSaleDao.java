package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Sale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcSaleDao implements SaleDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcSaleDao(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource); }

    @Override
    public Sale getSale(int saleId) {
        Sale sale = null;
        String sql = "select s.sale_id, s.customer_id, s.sale_date, s.ship_date, customer.name " +
                "from sale as s " +
                "join customer on customer.customer_id = s.customer_id " +
                "where s.sale_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, saleId);
        if (result.next()) {
            sale = mapRowToSale(result);
        }
        return sale;
    }

    @Override
    public List<Sale> getSalesUnshipped() {
        List<Sale> unshippedSales = new ArrayList<>();
        String sql = "select s.sale_id, s.customer_id, s.sale_date, s.ship_date, customer.name " +
                "from sale as s " +
                "join customer on customer.customer_id = s.customer_id " +
                "where s.ship_date is null " +
                "order by s.sale_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            unshippedSales.add(mapRowToSale(results));
        }
        return unshippedSales;
    }

    @Override
    public List<Sale> getSalesByCustomerId(int customerId) {
        List<Sale> salesByCustomerId = new ArrayList<>();
        String sql = "select s.sale_id, s.customer_id, s.sale_date, s.ship_date, customer.name " +
                "from sale as s " +
                "join customer on customer.customer_id = s.customer_id " +
                "where s.customer_id = ? " +
                "order by s.sale_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, customerId);
        while (results.next()) {
            salesByCustomerId.add(mapRowToSale(results));
        }
        return salesByCustomerId;
    }

    @Override
    public List<Sale> getSalesByProductId(int productId) {
        List<Sale> salesByProductId = new ArrayList<>();
        String sql = "select s.sale_id, s.customer_id, s.sale_date, s.ship_date, customer.name " +
                "from sale as s " +
                "join line_item as l on l.sale_id = s.sale_id " +
                "join customer on customer.customer_id = s.customer_id " +
                "where l.product_id = ? " +
                "order by s.sale_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, productId);
        while (results.next()) {
            salesByProductId.add(mapRowToSale(results));
        }
        return salesByProductId;
    }

    @Override
    public Sale createSale(Sale newSale) {
        String sql = "insert into sale (customer_id, sale_date, ship_date) " +
                "values (?, ?, ?) " +
                "returning sale_id;";
        Integer saleID = jdbcTemplate.queryForObject(sql, Integer.class, newSale.getCustomerId(), newSale.getSaleDate(), newSale.getShipDate());
        newSale.setSaleId(saleID);
        return newSale;
    }

    @Override
    public void updateSale(Sale updatedSale) {
        String sql = "update sale set customer_id = ?, sale_date = ?, ship_date = ? " +
                "where sale_id = ?;";
        jdbcTemplate.update(sql, updatedSale.getCustomerId(), updatedSale.getSaleDate(), updatedSale.getShipDate(),
                updatedSale.getSaleId());
    }

    @Override
    public void deleteSale(int saleId) {
        String sql = "delete from line_item " +
                "where sale_id = ?;";
        jdbcTemplate.update(sql, saleId);
        String sql1 = "delete from sale " +
                "where sale_id = ?;";
        jdbcTemplate.update(sql1, saleId);
    }

    public Sale mapRowToSale(SqlRowSet rowSet) {
        Sale sale = new Sale();
        sale.setSaleId(rowSet.getInt("sale_id"));
        sale.setCustomerId(rowSet.getInt("customer_id"));
        sale.setCustomerName(rowSet.getString("name"));
        sale.setSaleDate(LocalDate.parse(rowSet.getString("sale_date")));
        if (rowSet.getString("ship_date") != null) {
            sale.setShipDate(LocalDate.parse(rowSet.getString("ship_date")));
        }
        return sale;
    }
}
