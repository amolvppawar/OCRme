package com.ashomok.ocrme.get_more_requests.row.free_options.option_delegates;

import android.content.SharedPreferences;
import android.util.Log;

import com.ashomok.ocrme.OcrRequestsCounter;
import com.ashomok.ocrme.get_more_requests.GetMoreRequestsActivity;
import com.ashomok.ocrme.get_more_requests.row.free_options.UiFreeOptionManagingDelegate;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import com.ashomok.ocrme.utils.LogHelper;

/**
 * Created by iuliia on 3/6/18.
 */

public class LoginToSystemDelegate extends UiFreeOptionManagingDelegate {
    public static final String TAG = LogHelper.makeLogTag(LoginToSystemDelegate.class);
    public static final String ID = "login_to_system";
    private static final String LOGIN_TO_SYSTEM_DONE_TAG = "LOGIN_TO_SYSTEM_DONE";
    private final OcrRequestsCounter ocrRequestsCounter;

    private GetMoreRequestsActivity activity;
    private SharedPreferences sharedPreferences;

    @Inject
    public LoginToSystemDelegate(GetMoreRequestsActivity activity, OcrRequestsCounter ocrRequestsCounter,
                                 SharedPreferences sharedPreferences) {
        super(activity);
        this.ocrRequestsCounter = ocrRequestsCounter;
        this.activity = activity;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    protected void startTask() {
        LogHelper.d(TAG, "onStartTask");
        saveData();
        activity.signIn();

        onTaskDone(ocrRequestsCounter, activity);
    }

    @Override
    public boolean isTaskAvailable() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        boolean loggedIn = auth.getCurrentUser() != null;
        boolean isAlreadyDone = sharedPreferences.getBoolean(LOGIN_TO_SYSTEM_DONE_TAG, false);
        return (!loggedIn) && (!isAlreadyDone);
    }

    private void saveData() {
        sharedPreferences.edit().putBoolean(LOGIN_TO_SYSTEM_DONE_TAG, true).apply();
    }
}
