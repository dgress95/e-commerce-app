package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class JdbcProductDaoTest extends BaseDaoTests {

     private static final Product PRODUCT_1 = new Product(1, "Product 1", "Description 1",  new BigDecimal("9.99"), "product-1.png");
     private static final Product PRODUCT_2 = new Product(2, "Product 2", "Description 2",  new BigDecimal("19.00"), "product-2.png");
     private static final Product PRODUCT_3 = new Product(3, "Product 3", "Description 3", new BigDecimal("123.45"), "product-3.png");
     private static final Product PRODUCT_4 = new Product(4, "Product 4", "Description 4",  new BigDecimal("0.99"), "product-4.png");

    private JdbcProductDao dao;

    @Before
    public void setup() { dao = new JdbcProductDao(dataSource); }

    @Test
    public void getProduct_returns_product_by_id() {
        Product product = dao.getProduct(PRODUCT_1.getProductId());
        Assert.assertNotNull("getProduct returned null", product);
        assertProductsMatch("getProduct returned wrong or partial data", PRODUCT_1, product);

        product = dao.getProduct(PRODUCT_2.getProductId());
        assertProductsMatch("getProduct returned wrong or partial data", PRODUCT_2, product);
    }

    @Test
    public void getProducts_returns_list_of_all_products() {
        List<Product> productList = dao.getProducts();
        Assert.assertEquals("getProducts falied to return all products", 4, productList.size());
        assertProductsMatch("getProducts returned wrong or partial data", PRODUCT_1, productList.get(0));
        assertProductsMatch("getProducts returned wrong or partial data", PRODUCT_2, productList.get(1));
        assertProductsMatch("getProducts returned wrong or partial data", PRODUCT_3, productList.get(2));
        assertProductsMatch("getProducts returned wrong or partial data", PRODUCT_4, productList.get(3));
    }

    @Test
    public void getProducts_without_sales_returns_list_of_products_with_no_sales() {
        List<Product> productsWithNoSales = dao.getProductsWithNoSales();
        Assert.assertEquals("getProductsWithNoSales returned products with sales",
                1, productsWithNoSales.size());
        assertProductsMatch("getProductWithNoSales returned wrong product without sales",
                PRODUCT_3, productsWithNoSales.get(0));
    }

    @Test
    public void createProduct_returns_product_with_expected_id_and_values() {
        Product newProduct = new Product();
        newProduct.setName("Product 1");
        newProduct.setDescription("Description 1");
        newProduct.setPrice(new BigDecimal("9.99"));
        newProduct.setImageName("product-1.png");

        Product createdProduct = dao.createProduct(newProduct);

        Assert.assertNotEquals("createProduct ID not set when created, remained 0", 0, createdProduct.getProductId());
        assertProductsMatch("createProduct does not create product with expected values", createdProduct, newProduct);
    }

    @Test
    public void updateProduct_has_expected_name_when_retrieved() {
        Product product = dao.getProduct(PRODUCT_1.getProductId());
        Assert.assertNotNull("Can't test updateProduct until getProduct is working", product);
        product.setName("Test");

        dao.updateProduct(product);

        Product updatedProduct = dao.getProduct(PRODUCT_1.getProductId());
        Assert.assertEquals("updateProduct failed to change product name in database", "Test", updatedProduct.getName());
    }

    @Test
    public void deleted_product_cant_be_retrieved() {
        dao.deleteProduct(PRODUCT_1.getProductId());

        Product product = dao.getProduct(PRODUCT_1.getProductId());
        Assert.assertNull("deleteProduct failed to remove product from database", product);

        List<Product> products = dao.getProducts();
        Assert.assertEquals("deleteProduct left too many products in database", 3, products.size());
        assertProductsMatch("deleteProduct deleted wrong product", PRODUCT_2, products.get(0));
    }

    private void assertProductsMatch(String message, Product expected, Product actual) {
        Assert.assertEquals(message, expected.getProductId(), actual.getProductId());
        Assert.assertEquals(message, expected.getName(), actual.getName());
        Assert.assertEquals(message, expected.getDescription(), actual.getDescription());
        Assert.assertEquals(message, expected.getPrice(), actual.getPrice());
        Assert.assertEquals(message, expected.getImageName(), actual.getImageName());
    }

}
