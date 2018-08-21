package at.ameise.devicelocation.fragment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.ameise.devicelocation.R;
import at.ameise.devicelocation.model.Report;
import at.ameise.devicelocation.util.StringUtil;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * {@link RecyclerView.Adapter} that can display a {@link at.ameise.devicelocation.model.Report} and makes a call to the
 * specified {@link ReportListFragment.OnFragmentInteractionListener}.
 */
public class ReportRecyclerViewAdapter extends RecyclerView.Adapter<ReportRecyclerViewAdapter.ViewHolder> {

    private final List<Report> mValues;
    private final OnReportClickListener mListener;

    public ReportRecyclerViewAdapter(List<Report> items, OnReportClickListener listener) {
        mValues = new ArrayList<>(items);
        mListener = listener;
    }

    public void setData(List<Report> reportList) {
        mValues.clear();
        if(reportList != null)
            mValues.addAll(reportList);
    }

    public List<Report> getCellInfo() {
        return new ArrayList<>(mValues);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_report_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setCell(mValues.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface OnReportClickListener {
        /**
         * Executed when the report list item was clicked.
         * @param report
         */
        void onReportClicked(Report report);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final Context mCtx;
        private Report mItem;

        //@BindView(R.id.card_cell)
        //CardView mCard;
        @BindView(R.id.fragment_report_list_item_root)
        View rootView;

        @BindView(R.id.fragment_report_list_item_title)
        TextView tvTitle;

        @BindView(R.id.fragment_report_list_item_wificell_finished)
        ImageView tvWifiCell;
        @BindView(R.id.fragment_report_list_item_gps_finished)
        ImageView tvGps;
        @BindView(R.id.fragment_report_list_item_mls_finished)
        ImageView tvMls;
        @BindView(R.id.fragment_report_list_item_loading)
        ProgressBar pbLoading;

        @BindView(R.id.fragment_report_list_item_deviation)
        TextView tvDeviation;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mCtx = view.getContext();
        }

        /**
         * Populates the views.
         */
        private void setupView() {

            tvTitle.setText(String.valueOf(android.text.format.DateFormat.format(mCtx.getText(R.string.datetime_format), mItem.getTimestamp())));

            boolean cellsLoaded = mItem.getParamCellTowers() != null;
            boolean wifisLoaded = mItem.getParamWifiAccessPoints() != null;
            boolean gpsLoaded = mItem.getGpsAccuracyRaw() != null;//TODO is this enough?
            boolean mlsLoaded = mItem.getMlsAccuracyRaw() != null;//TODO is this enough?

            boolean loading = !(cellsLoaded && wifisLoaded) || !gpsLoaded || !mlsLoaded;

            if(cellsLoaded || wifisLoaded)
                tvWifiCell.setVisibility(View.VISIBLE);
            else
                tvWifiCell.setVisibility(View.GONE);

            if(gpsLoaded)
                tvGps.setVisibility(View.VISIBLE);
            else
                tvGps.setVisibility(View.GONE);

            if(mlsLoaded)
                tvMls.setVisibility(View.VISIBLE);
            else
                tvMls.setVisibility(View.GONE);

            if(gpsLoaded && mlsLoaded) {

                Float distanceInMeters = null;
                if(mItem.getGpsLatitude() != null && mItem.getMlsLatitude() != null) {
                    distanceInMeters = StringUtil.getDeviationGpsMls(mItem);
                }
                if(distanceInMeters == null) {

                    tvDeviation.setText("");
                } else {

                    tvDeviation.setText(Math.round(distanceInMeters)+"m");
                }
                tvDeviation.setVisibility(View.VISIBLE);

            } else {

                tvDeviation.setVisibility(View.GONE);
            }

            if(loading)
                pbLoading.setVisibility(View.VISIBLE);
            else
                pbLoading.setVisibility(View.GONE);
        }

        public Report getCell() {
            return mItem;
        }

        public void setCell(final Report mItem, final OnReportClickListener onClickListener) {
            this.mItem = mItem;
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onClickListener != null)
                        onClickListener.onReportClicked(mItem);
                }
            });

            setupView();
        }
    }
}

