package at.ameise.devicelocation.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import at.ameise.devicelocation.R;
import at.ameise.devicelocation.activity.MainActivity;
import at.ameise.devicelocation.loader.ReportLoader;
import at.ameise.devicelocation.model.Report;
import at.ameise.devicelocation.util.StringUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Report detail screen.
 * This fragment is either contained in a {@link MainActivity}.
 * on handsets.
 */
public class ReportDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Report> {

    private static final String TAG = ReportDetailFragment.class.getSimpleName();

    public static final String ARG_REPORT_ID = "arg_report_id";
    private static final int LOADER_ID = 1;

    @BindView(R.id.fragment_report_detail_loading)
    LinearLayout layoutLoading;
    @BindView(R.id.fragment_report_detail_report)
    LinearLayout layoutReport;


    @BindView(R.id.fragment_report_detail_deviation)
    TextView tvDeviation;

    @BindView(R.id.fragment_report_detail_gps_latitude)
    TextView tvGpsLatitude;
    @BindView(R.id.fragment_report_detail_gps_longitude)
    TextView tvGpsLongitude;
    @BindView(R.id.fragment_report_detail_gps_accuracy)
    TextView tvGpsAccuracy;

    @BindView(R.id.fragment_report_detail_mls_latitude)
    TextView tvMlsLatitude;
    @BindView(R.id.fragment_report_detail_mls_longitude)
    TextView tvMlsLongitude;
    @BindView(R.id.fragment_report_detail_mls_accuracy)
    TextView tvMlsAccuracy;

    @BindView(R.id.fragment_report_detail_mls_number_wifi)
    TextView tvMlsNumberWifi;
    @BindView(R.id.fragment_report_detail_mls_number_cell)
    TextView tvMlsNumberCell;
    @BindView(R.id.fragment_report_detail_mls_request)
    TextView tvMlsRequest;
    @BindView(R.id.fragment_report_detail_mls_response)
    TextView tvMlsResponse;

    /**
     * The dummy content this fragment is presenting.
     */
    private Report mItem;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param itemId the item id.
     * @return A new instance of fragment ReportListFragment.
     */
    public static ReportDetailFragment newInstance(long itemId) {
        ReportDetailFragment fragment = new ReportDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_REPORT_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report_detail, container, false);
        ButterKnife.bind(this, rootView);

        if(getLoaderManager().getLoader(LOADER_ID) != null)
            getLoaderManager().restartLoader(LOADER_ID, getArguments(), this);
        else
            getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, getArguments(), this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView();
    }

    private void setupView() {

        if(mItem == null) {

            layoutReport.setVisibility(View.GONE);
            layoutLoading.setVisibility(View.VISIBLE);

        } else {

            AppCompatActivity activity = (AppCompatActivity) this.getActivity();
            if(activity.getSupportActionBar() != null)
                activity.getSupportActionBar().setTitle(android.text.format.DateFormat.format(getText(R.string.datetime_format), mItem.getTimestamp()));

            layoutLoading.setVisibility(View.GONE);
            layoutReport.setVisibility(View.VISIBLE);

            Float distanceInMeters = null;
            if(mItem.getGpsLatitude() != null && mItem.getMlsLatitude() != null) {
                distanceInMeters = StringUtil.getDeviationGpsMls(mItem);
            }
            tvDeviation.setText(StringUtil.naOrValue(distanceInMeters));

            tvGpsLatitude.setText(StringUtil.naOrValue(mItem.getGpsLatitude()));
            tvGpsLongitude.setText(StringUtil.naOrValue(mItem.getGpsLongitude()));
            tvGpsAccuracy.setText(StringUtil.naOrValue(mItem.getGpsAccuracyRaw()));

            tvMlsLatitude.setText(StringUtil.naOrValue(mItem.getMlsLatitude()));
            tvMlsLongitude.setText(StringUtil.naOrValue(mItem.getMlsLongitude()));
            tvMlsAccuracy.setText(StringUtil.naOrValue(mItem.getMlsAccuracyRaw()));

            tvMlsNumberWifi.setText(mItem.getParamWifiAccessPoints() != null ? String.valueOf(mItem.getParamWifiAccessPoints().size()) : "N/A");
            tvMlsNumberCell.setText(mItem.getParamCellTowers() != null ? String.valueOf(mItem.getParamCellTowers().size()) : "N/A");

            tvMlsRequest.setText(mItem.getMlsRequest() != null ? mItem.getMlsRequest() : "N/A");
            tvMlsResponse.setText(mItem.getMlsResponse() != null ? mItem.getMlsResponse() : "N/A");
        }
    }

    @Override
    public Loader<Report> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "Create ReportLoader for report "+args.getLong(ARG_REPORT_ID));
        return new ReportLoader(getActivity(), args.getLong(ARG_REPORT_ID));
    }

    @Override
    public void onLoadFinished(Loader<Report> loader, Report report) {
        mItem = report;
        setupView();
        Log.d(TAG, "LoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Report> loader) {
        mItem = null;
        setupView();
    }
}
