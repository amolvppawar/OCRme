/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ashomok.ocrme.get_more_requests.row.free_options;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ashomok.ocrme.R;

/**
 * ViewHolder for quick access to row's views
 */
public final class FreeOptionRowViewHolder extends RecyclerView.ViewHolder {

    public ImageView icon;
    public TextView title;
    public TextView subtitle;
    public TextView requestsCost;
    public ImageView isDone;
    public View layout;

    public FreeOptionRowViewHolder(final View v, final OnButtonClickListener clickListener) {
        super(v);
        icon = v.findViewById(R.id.icon);
        title = v.findViewById(R.id.title);
        subtitle = v.findViewById(R.id.subtitle);
        requestsCost = v.findViewById(R.id.requests_cost);
        isDone = v.findViewById(R.id.is_done);
        layout = v.findViewById(R.id.row_parent);

        if (layout != null) {
            layout.setOnClickListener(view -> clickListener.onButtonClicked(getAdapterPosition()));
        }
    }

    /**
     * Handler for a button click on particular row
     */
    public interface OnButtonClickListener {
        void onButtonClicked(int position);
    }
}
