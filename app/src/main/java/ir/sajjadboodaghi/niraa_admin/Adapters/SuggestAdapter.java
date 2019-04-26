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
import ir.sajjadboodaghi.niraa_admin.Models.Suggest;

/**
 * Created by Sajjad on 05/07/2018.
 */

public class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.ViewHolder> {

    List<Suggest> suggests;
    Context context;
    Suggest suggest;

    public SuggestAdapter(List<Suggest> suggests, Context context) {
        this.suggests = suggests;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_suggest_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        suggest = suggests.get(position);
        holder.descriptionTextView.setText(suggest.getDescription());
        holder.detailsTextView.setText(
                "شماره تلفن: " + suggest.getPhoneNumber() + "\n" +
                "نسخه نیرا: " + suggest.getNiraaVersion() + "\n" +
                "نسخه اندروید: " + suggest.getAndroidVersion()
        );
        holder.deleteButton.setTag(position);
    }

    @Override
    public int getItemCount() {
        return suggests.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView descriptionTextView;
        TextView detailsTextView;
        Button deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.descriptionTextView);
            detailsTextView = (TextView) itemView.findViewById(R.id.detailsTextView);
            deleteButton = (Button) itemView.findViewById(R.id.deleteButton);
        }
    }


}
