package com.dinpay.trip.testdemo.fragments.dummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {
    public static final String CONTENT_TYPE_1 = "Sub Type1";
    public static final String CONTENT_TYPE_2 = "Sub Type2";
    public static final String CONTENT_TYPE_3 = "Sub Type3";

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    private static final int COUNT = 3;

    static {
        // Add some sample items.
            DummyItem item = new DummyItem();
            item.id = 1 + "";
            item.content = CONTENT_TYPE_1;
            item.details = Arrays.asList("Sub1","Sub2","Sub3","Sub4","Sub5");
            DummyItem item1 = new DummyItem();
            item1.id = 2 + "";
            item1.content = CONTENT_TYPE_2;
            item1.details = Arrays.asList("Sub1","Sub2","Sub3","Sub4","Sub5");
            DummyItem item2 = new DummyItem();
            item2.id = 3 + "";
            item2.content = CONTENT_TYPE_3;
            item2.details = Arrays.asList("Sub1","Sub2","Sub3","Sub4","Sub5");
            ITEMS.add(item);
            ITEMS.add(item1);
            ITEMS.add(item2);
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String id;
        public String content;
        public List<String> details;

        @Override
        public String toString() {
            return content;
        }
    }
}
