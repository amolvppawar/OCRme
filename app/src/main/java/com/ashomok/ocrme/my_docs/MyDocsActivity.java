package com.ashomok.ocrme.my_docs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.firebaseUiAuth.AuthUiActivity;
import com.ashomok.ocrme.ocr.ocr_task.OcrResponse;
import com.ashomok.ocrme.ocr.ocr_task.OcrResult;
import com.ashomok.ocrme.ocr_result.OcrResultActivity;
import com.ashomok.ocrme.utils.AlertDialogHelper;
import com.ashomok.ocrme.utils.AutoFitGridLayoutManager;
import com.ashomok.ocrme.utils.InfoSnackbarUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static com.ashomok.ocrme.ocr_result.OcrResultActivity.EXTRA_OCR_RESPONSE;
import com.ashomok.ocrme.utils.LogHelper;

/**
 * Created by iuliia on 8/18/17.
 */

//todo don't reload list when onback clicked or screen rotated  - use some cache solution
//todo stranje scroll animation when load more - looks like ылетает влево и появляется снова

public class MyDocsActivity extends AuthUiActivity implements View.OnClickListener, MyDocsContract.View {

    private static final int DELETE_TAG = 1;
    private static final String TAG = LogHelper.makeLogTag(MyDocsActivity.class);
    boolean isMultiSelect = false;
    AlertDialogHelper alertDialogHelper;

    @Inject
    MyDocsContract.Presenter mPresenter;

    private List<Object> dataList;
    private List<OcrResult> multiSelectDataList;
    private RecyclerViewAdapter adapter;
    private ActionMode mActionMode;
    private ProgressBar progress;

    //"Select" docs action mode
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.my_docs_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_select_all:
                    selectAll(mode);
                    return true;
                case R.id.action_unselect_all:
                    unselectAll(mode);
                    return true;
                case R.id.action_delete:
                    showAlertDialog();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiSelectDataList.clear();
            adapter.notifyDataSetChanged();
        }
    };

    private RecyclerViewCallback callback = new RecyclerViewCallback() {
        @Override
        public void onItemClick(int position) {
            if (isMultiSelect) {
                onMultiSelect(position);
            } else {
                if (dataList.get(position) instanceof OcrResult) {
                    OcrResult doc = (OcrResult) dataList.get(position);
                    startOcrResultActivity(new OcrResponse(doc, OcrResponse.Status.OK));
                }
            }
        }

        @Override
        public void onItemLongClick(int position) {
            onSelectMode();
            onMultiSelect(position);
        }

        @Override
        public void onItemDelete(int position) {
            if (dataList.get(position) instanceof OcrResult) {
                OcrResult doc = (OcrResult) dataList.get(position);
                multiSelectDataList.add(doc);
                mPresenter.deleteDocs(multiSelectDataList);
                multiSelectDataList.clear();
            }
        }

        @Override
        public void onItemShareText(int position) {
            if (dataList.get(position) instanceof OcrResult) {
                OcrResult doc = (OcrResult) dataList.get(position);
                String textResult = doc.getTextResult();
                mPresenter.onShareTextClicked(textResult);
            }
        }

        @Override
        public void onItemSharePdf(int position) {
            if (dataList.get(position) instanceof OcrResult) {
                OcrResult doc = (OcrResult) dataList.get(position);
                String mDownloadURL = doc.getPdfResultMediaUrl();
                mPresenter.onSharePdfClicked(mDownloadURL);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_docs);
        initToolbar();
        initSignInView();

        initRecyclerView();

        progress = findViewById(R.id.progress);

        mPresenter.takeView(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_docs_common, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.select).setVisible(dataList.size() > 0);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.select:
                onSelectMode();
                return true;
            //add more menu items - see my_docs_common
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.dropView();  //prevent leaking activity in
        // case presenter is orchestrating a long running task
    }

    private void initSignInView() {
        Button signInBtn = findViewById(R.id.sign_in_btn);
        signInBtn.setOnClickListener(this);
    }

    private void onSelectMode() {
        if (!isMultiSelect) {
            multiSelectDataList.clear();
            isMultiSelect = true;

            if (mActionMode == null) {
                mActionMode = startActionMode(mActionModeCallback);
            }

            mActionMode.setTitle(multiSelectDataList.size() + getString(R.string.selected));
        }
    }

    private void showAlertDialog() {
        alertDialogHelper = new AlertDialogHelper(this, new AlertDialogHelper.AlertDialogListener() {
            @Override
            public void onPositiveClick(int from) {
                if (from == DELETE_TAG) {
                    if (multiSelectDataList.size() > 0) {

                        mPresenter.deleteDocs(multiSelectDataList);
                        if (mActionMode != null) {
                            mActionMode.finish();
                        }
                    }
                }
            }

            @Override
            public void onNegativeClick(int from) {
            }

            @Override
            public void onNeutralClick(int from) {
            }
        });

        alertDialogHelper.showAlertDialog(
                "",
                getString(R.string.delete_docs, String.valueOf(multiSelectDataList.size())),
                getString(R.string.delete),
                getString(R.string.cancel),
                DELETE_TAG,
                false);
    }

    public void onMultiSelect(int position) {
        if (mActionMode != null) {
            if (multiSelectDataList.contains(dataList.get(position))) {
                multiSelectDataList.remove(dataList.get(position));
            } else {
                if (dataList.get(position) instanceof OcrResult) {
                    OcrResult doc = (OcrResult) dataList.get(position);
                    multiSelectDataList.add(doc);
                }
            }
            mActionMode.setTitle(multiSelectDataList.size() + getString(R.string.selected));

            adapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        LogHelper.d(TAG, "initRecyclerView called");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        AutoFitGridLayoutManager layoutManager =
                new AutoFitGridLayoutManager(this, 500);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        dataList = new ArrayList<>();
        multiSelectDataList = new ArrayList<>();

        adapter = new RecyclerViewAdapter(dataList, multiSelectDataList, callback);
        recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (! recyclerView.canScrollVertically(1)){ //1 for down
                     LogHelper.d(TAG, "onLoadMore");
                    mPresenter.loadMoreDocs();
                }
            }
        });
    }

    private void startOcrResultActivity(OcrResponse data) {
        Intent intent = new Intent(this, OcrResultActivity.class);
        intent.putExtra(EXTRA_OCR_RESPONSE, data);
        startActivity(intent);
    }

    private void unselectAll(ActionMode mode) {
        multiSelectDataList.clear();
        mode.setTitle(multiSelectDataList.size() + getString(R.string.selected));

        //update menu buttons
        Menu menu = mode.getMenu();
        menu.findItem(R.id.action_select_all).setVisible(true);
        menu.findItem(R.id.action_unselect_all).setVisible(false);

        adapter.notifyItemRangeChanged(0, dataList.size());
    }

    private void selectAll(ActionMode mode) {
        multiSelectDataList.clear();
        multiSelectDataList.addAll(Stream.of(dataList)
                .filter(l -> l instanceof OcrResult)
                .map(l -> (OcrResult) l).toList());

        mode.setTitle(multiSelectDataList.size() + getString(R.string.selected));

        //update menu buttons
        Menu menu = mode.getMenu();
        menu.findItem(R.id.action_select_all).setVisible(false);
        menu.findItem(R.id.action_unselect_all).setVisible(true);

        adapter.notifyItemRangeChanged(0, dataList.size());
    }

    /**
     * update UI if signed in/out
     */
    @Override
    public void updateUi(boolean isUserSignedIn) {
        LogHelper.d(TAG, "Update UI. Is user signed in " + isUserSignedIn);
        View askLoginView = findViewById(R.id.ask_login);
        View myDocsView = findViewById(R.id.my_docs);

        askLoginView.setVisibility(isUserSignedIn ? View.GONE : View.VISIBLE);
        myDocsView.setVisibility(isUserSignedIn ? View.VISIBLE : View.GONE);

        if (isUserSignedIn) {
            mPresenter.initWithDocs();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_btn:
                signIn();
                break;
            default:
                break;
        }
    }

    @Override
    public void showError(int errorMessageRes) {
        InfoSnackbarUtil.showError(errorMessageRes, mRootView);
    }

    @Override
    public void showInfo(int infoMessageRes) {
        InfoSnackbarUtil.showInfo(infoMessageRes, mRootView);
    }

    @Override
    public void addNewLoadedDocs(List<OcrResult> newLoadedDocs) {

        LogHelper.d(TAG, "addNewLoadedDocs called with size = " + newLoadedDocs.size());
        int positionStart = dataList.size() - 1;
        dataList.addAll(newLoadedDocs);
        adapter.notifyItemRangeInserted(positionStart, newLoadedDocs.size());

        if (dataList.size() == 0) {
            //show empty view
            View emptyView = findViewById(R.id.empty_result_layout);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            mPresenter.showAdsIfNeeded(dataList, adapter);
        }
        invalidateOptionsMenu();
    }

    @Override
    public void clearDocsList() {
        LogHelper.d(TAG, "clearDocsList called");
        int size = dataList.size();
        dataList.clear();
        adapter.notifyItemRangeRemoved(0, size);

        invalidateOptionsMenu();
    }

    /**
     * Shows the progress UI
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        progress.setVisibility(show ? View.VISIBLE : View.GONE);
        progress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    interface RecyclerViewCallback {
        void onItemClick(int position);

        void onItemLongClick(int position);

        void onItemDelete(int position);

        void onItemShareText(int position);

        void onItemSharePdf(int position);
    }
}
