package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;

public class JdbcProductDao implements ProductDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductDao(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource); }

    @Override
    public Product getProduct(int productId) {
        Product product = null;
        String sql = "select product_id, name, description, price, image_name " +
                "from product " +
                "where product_id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, productId);
        if (result.next()) {
            product = mapRowToProduct(result);
        }
        return product;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> productList = new ArrayList<>();
        String sql = "select product_id, name, description, price, image_name " +
                "from product " +
                "order by product_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            productList.add(mapRowToProduct(results));
        }
        return productList;
    }

    @Override
    public List<Product> getProductsWithNoSales() {
        List<Product> productWithNoSaleList = new ArrayList<>();
        String sql = "select p.product_id, p.name, p.description, p.price, p.image_name, l.sale_id " +
                        "from product as p " +
                        "left outer join line_item as l on l.product_id = p.product_id " +
                        "where l.sale_id IS NULL " +
                        "order by p.product_id;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            productWithNoSaleList.add(mapRowToProduct(results));
        }
        return productWithNoSaleList;
    }

    @Override
    public Product createProduct(Product newProduct) {
        String sql = "insert into product (name, description, price, image_name) " +
                "values (?, ?, ?, ?) " +
                "returning product_id;";
        Integer productID = jdbcTemplate.queryForObject(sql, Integer.class, newProduct.getName(), newProduct.getDescription(), newProduct.getPrice(), newProduct.getImageName());
        newProduct.setProductId(productID);
        return newProduct;
    }

    @Override
    public void updateProduct(Product updatedProduct) {
        String updateProductSql = "update product set name = ?, description = ?, price = ?, image_name = ? " +
                "where product_id = ?;";
        jdbcTemplate.update(updateProductSql, updatedProduct.getName(), updatedProduct.getDescription(),
                updatedProduct.getPrice(), updatedProduct.getImageName(), updatedProduct.getProductId());
    }

    @Override
    public void deleteProduct(int productId) {
        String deleteProductSql1 = "delete from line_item " +
                "where product_id = ?;";
        jdbcTemplate.update(deleteProductSql1, productId);
        String deleteProductSql = "delete from product " +
                "where product_id = ?;";
        jdbcTemplate.update(deleteProductSql, productId);
    }

    private Product mapRowToProduct(SqlRowSet rowSet) {
        Product product = new Product();
        product.setProductId(rowSet.getInt("product_id"));
        product.setName(rowSet.getString("name"));
        product.setDescription(rowSet.getString("description"));
        product.setPrice(rowSet.getBigDecimal("price"));
        if (rowSet.getString("image_name") != null) {
            product.setImageName(rowSet.getString("image_name"));
        }
        return product;
    }
}
