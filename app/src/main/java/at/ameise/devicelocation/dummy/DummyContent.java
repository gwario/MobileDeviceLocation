package at.ameise.devicelocation.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ameise.devicelocation.model.Report;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Report> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Long, Report> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(Report item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }

    private static Report createDummyItem(int position) {

        Report report = new Report();

        report.setId((long) position);
        report.setTimestamp(System.currentTimeMillis()+1000*position);

        return report;
    }
}
