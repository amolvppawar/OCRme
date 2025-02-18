package com.ashomok.ocrme.ocr.ocr_task;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by iuliia on 1/22/18.
 */
public class OcrResult implements Serializable {

    private String sourceImageUrl; //example gs://imagetotext-149919.appspot.com/ocr_request_images/659d2a80-f1fa-4b93-80fb-a83c534fc289cropped.jpg
    private List<String> languages;
    private String textResult;
    private String pdfResultGsUrl;
    private String pdfResultMediaUrl;
    private String pdfImageResultGsUrl;
    private String pdfImageResultMediaUrl;
    private Long id;
    private String timeStamp;


    private OcrResult(Builder builder) {
        this.sourceImageUrl = builder.inputImageUrl;
        this.languages = builder.languages;
        this.textResult = builder.textResult;
        this.pdfResultGsUrl = builder.pdfResultGsUrl;
        this.pdfResultMediaUrl = builder.pdfResultMediaUrl;
        this.pdfImageResultGsUrl = builder.pdfImageResultGsUrl;
        this.pdfImageResultMediaUrl = builder.pdfImageResultMediaUrl;
        this.id = builder.id;
        String timeStamp = builder.timeStamp;
        if (timeStamp == null) {
            timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date());
        }
        this.timeStamp = timeStamp;
    }

    public String getSourceImageUrl() {
        return sourceImageUrl;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getTextResult() {
        return textResult;
    }

    public String getPdfResultGsUrl() {
        return pdfResultGsUrl;
    }

    public String getPdfResultMediaUrl() {
        return pdfResultMediaUrl;
    }

    public String getPdfImageResultGsUrl() {
        return pdfImageResultGsUrl;
    }

    public String getPdfImageResultMediaUrl() {
        return pdfImageResultMediaUrl;
    }

    public Long getId() {
        return id;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "OcrResult{" +
                "sourceImageUrl='" + sourceImageUrl + '\'' +
                ", languages=" + languages +
                ", textResult='" + textResult + '\'' +
                ", pdfResultGsUrl='" + pdfResultGsUrl + '\'' +
                ", pdfResultMediaUrl='" + pdfResultMediaUrl + '\'' +
                ", pdfImageResultGsUrl='" + pdfImageResultGsUrl + '\'' +
                ", pdfImageResultMediaUrl='" + pdfImageResultMediaUrl + '\'' +
                ", id=" + id +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }


    public static class Builder {
        private String inputImageUrl;
        private List<String> languages;
        private String textResult;
        private String pdfResultGsUrl;
        private String pdfResultMediaUrl;
        private String pdfImageResultGsUrl;
        private String pdfImageResultMediaUrl;
        private Long id;
        private String timeStamp;

        public Builder sourceImageUrl(String inputImageUrl) {
            this.inputImageUrl = inputImageUrl;
            return this;
        }

        public Builder languages(List<String> languages) {
            this.languages = languages;
            return this;
        }

        public Builder textResult(String textResult) {
            this.textResult = textResult;
            return this;
        }

        public  Builder pdfResultGsUrl(String pdfResultGsUrl) {
            this.pdfResultGsUrl = pdfResultGsUrl;
            return this;
        }

        public Builder pdfResultMediaUrl(String pdfResultMediaUrl) {
            this.pdfResultMediaUrl = pdfResultMediaUrl;
            return this;
        }

        public Builder pdfImageResultGsUrl(String pdfImageResultGsUrl) {
            this.pdfImageResultGsUrl = pdfImageResultGsUrl;
            return this;
        }

        public Builder pdfImageResultMediaUrl(String pdfImageResultMediaUrl) {
            this.pdfImageResultMediaUrl = pdfImageResultMediaUrl;
            return this;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder timeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
            return this;
        }

        public OcrResult build() {
            return new OcrResult(this);
        }
    }
}
