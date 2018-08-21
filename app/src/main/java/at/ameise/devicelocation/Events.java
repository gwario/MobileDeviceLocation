package at.ameise.devicelocation;

/**
 * Contains global events and their extra keys.
 *
 * Created by mariogastegger on 21.01.17.
 */
public interface Events {

    /**
     * This event is broad casted on every single dataset change.
     */
    String ACTION_DATASET_CHANGED = "action_dataset_changed";

    /**
     * This is an optional long extra.
     * When the data set change is only affecting one report, the value contains the report id.
     */
    String KEY_REPORT_ID = "key_report_id";
}
