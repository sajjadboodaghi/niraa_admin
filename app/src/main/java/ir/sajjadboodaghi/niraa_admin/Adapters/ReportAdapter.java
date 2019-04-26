package ir.sajjadboodaghi.niraa_admin.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ir.sajjadboodaghi.niraa_admin.R;
import ir.sajjadboodaghi.niraa_admin.Models.Report;

/**
 * Created by Sajjad on 05/07/2018.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    List<Report> reports;
    Context context;

    public ReportAdapter(List<Report> reports, Context context) {
        this.reports = reports;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_report_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.reporterNumberTV.setText(reports.get(position).getReporterNumber());
        holder.descriptionTV.setText(reports.get(position).getDescription());

        holder.deleteButton.setTag(position);
        holder.showReportedItemButton.setTag(reports.get(position).getItemId());
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView reporterNumberTV;
        TextView descriptionTV;
        Button deleteButton;
        Button showReportedItemButton;

        public ViewHolder(View itemView) {
            super(itemView);
            reporterNumberTV = (TextView) itemView.findViewById(R.id.reporterNumberTV);
            descriptionTV = (TextView) itemView.findViewById(R.id.descriptionTV);
            deleteButton = (Button) itemView.findViewById(R.id.deleteButton);
            showReportedItemButton = (Button) itemView.findViewById(R.id.showReportedItemButton);
        }
    }


}
