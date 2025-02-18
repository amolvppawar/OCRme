package com.ashomok.ocrme.language_choser;

/**
 * Created by iuliia on 11/15/17.
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ashomok.ocrme.language_choser.LanguagesListAdapter.ResponsableList;
import com.ashomok.ocrme.language_choser.di.AllLanguageCodes;
import com.ashomok.ocrme.language_choser.di.RecentlyChosenLanguageCodes;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;

import com.ashomok.ocrme.utils.LogHelper;
import static dagger.internal.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link LanguageOcrActivity}), retrieves the data and updates
 * the UI as required.
 * <p>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */

public class LanguageOcrPresenter implements LanguageOcrContract.Presenter {
    public static final String TAG = LogHelper.makeLogTag(LanguageOcrPresenter.class);
    // This is provided lazily because its value is determined in the Activity's onCreate. By
    // calling it in takeView(), the value is guaranteed to be set.
    private final Lazy<ResponsableList<String>> checkedLanguageCodesLazy;
    private final Lazy<List<String>> recentlyChosenLanguageCodesLazy;
    @Nullable
    private LanguageOcrContract.View view;
    @NonNull
    private List<String> allLanguageCodes;

    /**
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    LanguageOcrPresenter(Lazy<ResponsableList<String>> checkedLanguageCodesLazy,
                         @AllLanguageCodes @NonNull List<String> allLanguageCodes,
                         @RecentlyChosenLanguageCodes Lazy<List<String>> recentlyChosenLanguageCodesLazy) {
        this.checkedLanguageCodesLazy = checkedLanguageCodesLazy;
        this.allLanguageCodes = checkNotNull(allLanguageCodes);
        this.recentlyChosenLanguageCodesLazy = recentlyChosenLanguageCodesLazy;
    }

    private void showLanguages() {
        if (view != null) {
            //init recently chosen language list
            if (recentlyChosenLanguageCodesLazy.get().size() > 0) {
                view.showRecentlyChosenLanguages(
                        recentlyChosenLanguageCodesLazy.get(), checkedLanguageCodesLazy.get());

            }

            //init all languages list
            view.showAllLanguages(allLanguageCodes, checkedLanguageCodesLazy.get());

            //init auto btn
            view.initAutoBtn();
            if (checkedLanguageCodesLazy.get().size() < 1) {
                //check auto btn
                view.updateAutoView(true);
            }
        }
    }

    @Override
    public void takeView(LanguageOcrContract.View languageOcrActivity) {
        view = languageOcrActivity;
        showLanguages();
    }

    @Override
    public void dropView() {
        view = null;
    }
}
