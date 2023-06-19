package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.LineItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcLineItemDao implements LineItemDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLineItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<LineItem> getLineItemsBySale(int saleId) {
        List<LineItem> lineItems = new ArrayList<>();
        String sql = "select l.line_item_id, l.sale_id, l.product_id, l.quantity, p.name, p.price " +
                "from line_item as l " +
                "join product as p on l.product_id = p.product_id " +
                "where l.sale_id = ? " +
                "order by l.line_item_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, saleId);
        while (results.next()) {
            lineItems.add(mapRowToLineItem(results));
        }
        return lineItems;
    }

    public LineItem mapRowToLineItem(SqlRowSet rowSet) {
        LineItem lineItem = new LineItem();
        lineItem.setLineItemId(rowSet.getInt("line_item_id"));
        lineItem.setSaleId(rowSet.getInt("sale_id"));
        lineItem.setProductId(rowSet.getInt("product_id"));
        lineItem.setQuantity(rowSet.getInt("quantity"));
        lineItem.setProductName(rowSet.getString("name"));
        lineItem.setPrice(rowSet.getBigDecimal("price"));
        return lineItem;
    }

}
