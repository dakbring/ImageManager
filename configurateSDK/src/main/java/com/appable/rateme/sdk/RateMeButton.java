package com.appable.rateme.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RateMeButton extends AppCompatImageButton implements View.OnTouchListener {

    private static final String TAG = RateMeButton.class.getName();
    private String virtualDefaultUrl = "https://dev.configurateapp.com/url/device/";
    private Context context;
    private float dX;
    private float dY;
    private GestureDetector gestureDetector;
    private String virtualID;


    public RateMeButton(Context context) {
        super(context);
        initialize();
    }

    public RateMeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (!isInEditMode()) {
            initialize();
        }
    }

    public RateMeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if (!isInEditMode()) {
            initialize();
        }
    }

    private void initialize() {
        //set the default icon for RateMe button
        setImageResource(R.drawable.ic_launcher);
        //set background transfarent
        setBackground(null);
        //set min size for the button
        setMinimumWidth(getResources().getInteger(R.integer.min_width));
        setMinimumHeight(getResources().getInteger(R.integer.min_height));

        setOnTouchListener(this);

        gestureDetector = new GestureDetector(getContext(), new SingleTapConfirm());

        this.virtualID = getMetaData((Activity)getContext(),"com.appable.rateme.sdk.VirtualDeviceID");

        this.virtualDefaultUrl = this.virtualDefaultUrl + this.virtualID;

        //Adding platform parameter
        this.virtualDefaultUrl += "?platfom=" + GlobalVariables.SDK_ANDROID_PLATFORM;

    }

    private String getMetaData(Activity activity, String name) {
        try {
            ActivityInfo ai = activity.getPackageManager().getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            Bundle metaData = ai.metaData;
            if (metaData == null) {
                throw new Exception("metaData is null. Unable to get meta data for " + name);
            } else {
                return metaData.getString(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        /**
         * detect if the action if the tap action, then open the virtual device dialog
         * .Otherwise, move the rateMebutton position where user wants
         */
        if (gestureDetector.onTouchEvent(event)) {
            SurveyDialog surveyDialog = new SurveyDialog(context, virtualDefaultUrl);
            surveyDialog.show();
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    v.setX(event.getRawX() + dX);
                    v.setY(event.getRawY() + dY);
                    break;
            }
        }

        return true;
    }

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }
}
