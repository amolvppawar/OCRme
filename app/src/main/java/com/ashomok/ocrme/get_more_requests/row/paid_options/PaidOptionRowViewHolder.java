package com.ashomok.ocrme.get_more_requests.row.paid_options;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ashomok.ocrme.R;

public class PaidOptionRowViewHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView subtitleTop;
    private TextView subtitleBottom;
    private TextView price;
    private View layout;

    public PaidOptionRowViewHolder(final View v, final OnRowClickListener clickListener) {
        super(v);

        title = v.findViewById(R.id.title);
        subtitleTop = v.findViewById(R.id.subtitle_top);
        subtitleBottom = v.findViewById(R.id.subtitle_bottom);
        price = v.findViewById(R.id.price);
        layout = v.findViewById(R.id.row_parent);

        if (layout != null) {
            layout.setOnClickListener(view -> clickListener.onRowClicked(getAdapterPosition()));
        }
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getSubtitleTop() {
        return subtitleTop;
    }

    public TextView getSubtitleBottom() {
        return subtitleBottom;
    }

    public TextView getPrice() {
        return price;
    }

    public View getLayout() {
        return layout;
    }

    /**
     * Handler for a button click on particular row
     */
    public interface OnRowClickListener {
        void onRowClicked(int position);
    }
}
