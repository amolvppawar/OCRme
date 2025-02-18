package com.ashomok.ocrme.my_docs.get_my_docs_task;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by iuliia on 12/19/17.
 */

public interface MyDocsAPI {

    @POST("list_ocr_requests")
    Single<MyDocsResponse> getMyDocs(@Body MyDocsRequestBean myDocsRequestBean);

    @DELETE("list_ocr_requests")
    Completable delete(@Query("ocr_request_ids") String[] ocr_request_ids);
}
