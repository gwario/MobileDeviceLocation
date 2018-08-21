package at.ameise.devicelocation.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import at.ameise.devicelocation.R;
import at.ameise.devicelocation.loader.ReportsLoader;
import at.ameise.devicelocation.model.Report;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReportListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReportListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Report>>,ReportRecyclerViewAdapter.OnReportClickListener {

    private static final int LOADER_ID = 0;

    @BindView(R.id.report_list)
    RecyclerView reportList;

    private OnFragmentInteractionListener mListener;
    private ReportRecyclerViewAdapter mAdapter;
    private BroadcastReceiver appInitReceiver;

    public ReportListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ReportListFragment.
     */
    public static ReportListFragment newInstance() {
        return new ReportListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_report_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reportList.setLayoutManager(new LinearLayoutManager(getContext()));
        reportList.setAdapter(mAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {

            mListener = (OnFragmentInteractionListener) context;

            mAdapter = new ReportRecyclerViewAdapter(Collections.<Report>emptyList(), this);

        } else {
            throw new RuntimeException(context.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<List<Report>> onCreateLoader(int id, Bundle args) {
        return new ReportsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Report>> loader, List<Report> data) {
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Report>> loader) {
        mAdapter.setData(null);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onReportClicked(Report report) {

        if(mListener != null)
            mListener.onReportClicked(report);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onReportClicked(Report report);
    }
}
