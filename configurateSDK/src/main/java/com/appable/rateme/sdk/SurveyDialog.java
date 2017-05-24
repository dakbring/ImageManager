package com.appable.rateme.sdk;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by mac on 4/28/17.
 */

public class SurveyDialog extends Dialog {

    private WebView mWebview;
    private ImageButton closeBtn;
    private Context context;
    private Dialog mDialog;
    private String virtualUrl;

    public SurveyDialog(@NonNull Context context, String virtualUrl) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setContentView(R.layout.dialog_survey);
        this.context = context;
        mWebview = (WebView) findViewById(R.id.web_view);
        closeBtn = (ImageButton) findViewById(R.id.btn_close);
        mDialog = this;
        this.virtualUrl = virtualUrl;

        init();
    }

    public SurveyDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    protected SurveyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private void init() {

        mWebview.setWebChromeClient(new WebChromeClient());

        WebSettings settings = mWebview.getSettings();

        settings.setJavaScriptEnabled(true); // enable javascript

        settings.setSaveFormData(true);

        settings.setSavePassword(false);

        settings.setJavaScriptEnabled(true);

        settings.setSupportZoom(false);

        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        settings.setDomStorageEnabled(true);

        settings.setSupportMultipleWindows(false);

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(context, description, Toast.LENGTH_SHORT).show();
            }
        });

        mWebview.loadUrl(this.virtualUrl);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }


}
