package com.ashomok.ocrme.language_choser;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.IntStream;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.Settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ashomok.ocrme.utils.InfoSnackbarUtil.showError;
import com.ashomok.ocrme.utils.LogHelper;

/**
 * Created by iuliia on 12/11/16.
 */

public class LanguagesListAdapter extends RecyclerView.Adapter<LanguagesListAdapter.ViewHolder> {

    private static final String TAG = LogHelper.makeLogTag(LanguagesListAdapter.class);
    private static final int MAX_CHECKED_ALLOWED = 3;
    private final StateChangedNotifier notifier;
    private List<String> allLanguageCodes;
    private ResponsableList<String> checkedLanguageCodes;

    LanguagesListAdapter(List<String> allLanguageCodes,
                         @Nullable ResponsableList<String> checkedLanguageCodes,
                         StateChangedNotifier notifier) {

        this.allLanguageCodes = allLanguageCodes;
        this.notifier = notifier;

        this.checkedLanguageCodes = (checkedLanguageCodes == null) ?
                new ResponsableList<>(new ArrayList<>()) : checkedLanguageCodes;

        this.checkedLanguageCodes.addOnListChangedListener(checkedLanguage -> {
            int changedPos = IntStream.range(0, allLanguageCodes.size())
                    .filter(i -> checkedLanguage.equals(allLanguageCodes.get(i)))
                    .findFirst().orElse(-1);

            notifyItemChanged(changedPos);
        });
    }

    List<String> getCheckedLanguageCodes() {
        return checkedLanguageCodes;
    }

    private void addToChecked(String language) {
        if (checkedLanguageCodes.size() < MAX_CHECKED_ALLOWED) {
            checkedLanguageCodes.add(language);
        } else {
            LogHelper.w(TAG, "attempt to add checked language when max amount reached");
        }

        if (checkedLanguageCodes.size() > 0) {
            notifier.changeAutoState(false);
        }
    }

    void onAutoStateChanged(boolean isAutoChecked) {
        if (isAutoChecked) {
            //uncheck all items
            checkedLanguageCodes.clear();
            notifyDataSetChanged();
        }
    }

    private void removeFromChecked(String language) {
        checkedLanguageCodes.remove(language);
    }


    String getItem(int i) {
        return allLanguageCodes.get(i);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ocr_language_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = getItem(position);
        View parent = holder.languageLayout.getRootView();

        holder.languageName.setText(Settings.getSortedOcrLanguageSupportList(parent.getContext()).get(item));

        holder.languageLayout.setOnClickListener(view -> {
            if (checkedLanguageCodes.contains(item)) {
                //checked - uncheck
                removeFromChecked(item);
                holder.updateUi(false);
            } else {
                //unchecked - check
                if (checkedLanguageCodes.size() < MAX_CHECKED_ALLOWED) {
                    addToChecked(item);
                    holder.updateUi(true);
                } else {
                    String message = String.format(view.getContext().getString(R.string.max_checked_allowed),
                            String.valueOf(MAX_CHECKED_ALLOWED));
                    showError(message, parent);
                }
            }
        });

        holder.updateUi(checkedLanguageCodes.contains(item));
    }

    @Override
    public int getItemCount() {
        return allLanguageCodes.size();
    }

    public interface OnListChangedListener {
        void onListChangedFor(Object o);
    }

    // Provide a reference to the views for each data item
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout languageLayout;
        ImageView checkedIcon;
        TextView languageName;
        ImageView add;
        ImageView remove;

        ViewHolder(View v) {
            super(v);
            checkedIcon = v.findViewById(R.id.checked_icon);
            languageName = v.findViewById(R.id.language_name);
            add = v.findViewById(R.id.add);
            remove = v.findViewById(R.id.remove);
            languageLayout = v.findViewById(R.id.ocr_language_layout);
        }

        void updateUi(boolean checked) {
            checkedIcon.setVisibility(checked ? View.VISIBLE : View.INVISIBLE);
            add.setVisibility(checked ? View.GONE : View.VISIBLE);
            remove.setVisibility(checked ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * list with add / remove element event
     *
     * @param <E>
     */
    static class ResponsableList<E> extends ArrayList<E> {

        private List<OnListChangedListener> listenerList = new ArrayList<>();

        ResponsableList(@NonNull Collection<? extends E> c) {
            super(c);
        }

        void addOnListChangedListener(OnListChangedListener listener) {
            listenerList.add(listener);

        }

        @Override
        public boolean add(E e) {
            boolean res = super.add(e);
            for (OnListChangedListener listener : listenerList) {
                listener.onListChangedFor(e);
            }
            return res;
        }

        @Override
        public boolean remove(Object o) {
            boolean res = super.remove(o);
            for (OnListChangedListener listener : listenerList) {
                listener.onListChangedFor(o);
            }
            return res;
        }
    }
}
