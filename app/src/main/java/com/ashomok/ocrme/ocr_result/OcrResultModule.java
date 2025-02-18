package com.ashomok.ocrme.ocr_result;

import android.content.Context;

import androidx.annotation.StringRes;

import com.ashomok.ocrme.BuildConfig;
import com.ashomok.ocrme.R;
import com.ashomok.ocrme.ad.AdMobProvider;
import com.ashomok.ocrme.ocr_result.tab_fragments.image_pdf.ImagePdfContract;
import com.ashomok.ocrme.ocr_result.tab_fragments.image_pdf.ImagePdfFragment;
import com.ashomok.ocrme.ocr_result.tab_fragments.image_pdf.ImagePdfPresenter;
import com.ashomok.ocrme.ocr_result.tab_fragments.searchable_pdf.SearchablePdfContract;
import com.ashomok.ocrme.ocr_result.tab_fragments.searchable_pdf.SearchablePdfFragment;
import com.ashomok.ocrme.ocr_result.tab_fragments.searchable_pdf.SearchablePdfPresenter;
import com.ashomok.ocrme.ocr_result.tab_fragments.text.TextContract;
import com.ashomok.ocrme.ocr_result.tab_fragments.text.TextFragment;
import com.ashomok.ocrme.ocr_result.tab_fragments.text.TextPresenter;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class OcrResultModule {

    @Binds
    abstract OcrResultContract.Presenter ocrResultPresenter(OcrResultPresenter presenter);

    @Binds
    abstract SearchablePdfContract.Presenter searchablePdfPresenter(SearchablePdfPresenter presenter);

    @Binds
    abstract ImagePdfContract.Presenter imagePdfPresenter(ImagePdfPresenter presenter);

    @Binds
    abstract TextContract.Presenter textPresenter(TextPresenter presenter);

    @ContributesAndroidInjector
    abstract TextFragment textFragment();

    @ContributesAndroidInjector
    abstract SearchablePdfFragment searchablePdfFragment();

    @ContributesAndroidInjector
    abstract ImagePdfFragment imagePdfFragment();

    @Provides
    static AdMobProvider provideAdMobProviderImpl(Context context, @StringRes int adMobId) {
        return new AdMobProvider(context, adMobId);
    }

    @Provides
    static @StringRes
    int provideBannerAdId() {
        if (BuildConfig.DEBUG) {
            return R.string.test_banner_ad_id;
        } else {
            return R.string.result_banner_ad_id;
        }
    }
}
