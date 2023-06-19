package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.LineItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JdbcLineItemDaoTest extends BaseDaoTests {

    private static final LineItem LINE_ITEM_1 = new LineItem(1, 1, 1, 1);
    private static final LineItem LINE_ITEM_2 = new LineItem(2, 1, 2, 1);
    private static final LineItem LINE_ITEM_3 = new LineItem(3, 1, 4, 1);
    private static final LineItem LINE_ITEM_4 = new LineItem(4, 2, 4, 10);
    private static final LineItem LINE_ITEM_5 = new LineItem(5, 2, 1, 10);
    private static final LineItem LINE_ITEM_6 = new LineItem(6, 3, 1, 100);

    private JdbcLineItemDao dao;

    @Before
    public void setup() { dao = new JdbcLineItemDao(dataSource); }

    @Test
    public void getLineItemsBySaleId_returns_expected_list_of_items() {
        List<LineItem> lineItems = dao.getLineItemsBySale(LINE_ITEM_1.getSaleId());
        Assert.assertEquals("getLineItemsBySaleId failed to return all sales", 3, lineItems.size());
        assertLineItemsMatch("getLineItemsBySaleId returned wrong or partial data", LINE_ITEM_1, lineItems.get(0));
        assertLineItemsMatch("getLineItemsBySaleId returned wrong or partial data", LINE_ITEM_2, lineItems.get(1));
        assertLineItemsMatch("getLineItemsBySaleId returned wrong or partial data", LINE_ITEM_3, lineItems.get(2));
    }

    private void assertLineItemsMatch(String message, LineItem expected, LineItem actual) {
        Assert.assertEquals(message, expected.getLineItemId(), actual.getLineItemId());
        Assert.assertEquals(message, expected.getSaleId(), actual.getSaleId());
        Assert.assertEquals(message, expected.getProductId(), actual.getProductId());
        Assert.assertEquals(message, expected.getQuantity(), actual.getQuantity());
    }
}
