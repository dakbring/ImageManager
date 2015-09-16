package com.demo.cnt.imagesample;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {

    private static int rawX = 0;
    private static int rawY = 0;
    private static boolean isZooming = false;

    public static void loadImage(Activity activity, Uri uri, RelativeLayout layout, boolean isCapture) {
        Bitmap bitmap = null;
        String path;
        path = isCapture ? uri.getPath() : getRealPathFromURI(activity, uri);

        try {
            bitmap = scaleBitmap(activity, path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            if (layout.getChildCount() > 0) {
                layout.removeAllViews();
            }
            ImageView imageView = new ImageView(activity);
            imageView.setImageBitmap(bitmap);
            imageView.setOnTouchListener(moveListener);
            layout.addView(imageView);
        }
    }

    //TODO: Optimize listener to move image
    public static View.OnTouchListener moveListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    rawX = 0;
                    rawY = 0;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    isZooming = true;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    isZooming = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(!isZooming) {
                        int x = (int) motionEvent.getRawX();
                        int y = (int) motionEvent.getRawY();
                        if (rawX != 0) {
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                            params.leftMargin = params.leftMargin + (x - rawX);
                            params.topMargin = params.topMargin + (y - rawY);
                            view.setLayoutParams(params);
                        }
                        rawX = x;
                        rawY = y;
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    /**
     * return a bitmap with width = screen's width
     * keep the ratio
     */
    public static Bitmap scaleBitmap(Activity activity, String imagePath) throws Exception {
        Bitmap srcBitmap = loadBitmap(imagePath);
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int screenWidth = size.x;
        double ratio = (double) srcBitmap.getWidth() / (double) srcBitmap.getHeight();
        int scaledHeight = (int) ((double) screenWidth / ratio);

        return Bitmap.createScaledBitmap(srcBitmap, screenWidth, scaledHeight, false);
    }

    /**
     * load bitmap from file path
     */
    public static Bitmap loadBitmap(String imagePath) throws Exception {
        return BitmapFactory.decodeStream(new FileInputStream(imagePath));
    }

    private static String getRealPathFromURI(Activity activity, Uri contentUri) {
        String arrayData[] = contentUri.getLastPathSegment().split(":");
        String id;
        if (arrayData.length > 1) {
            id = arrayData[1];
        } else {
            id = arrayData[0];
        }

        final String[] imageColumns = {MediaStore.Images.Media.DATA};
        final String imageOrderBy = null;

        Uri uri = getUri();
        String selectedImagePath = "path";

        Cursor imageCursor = activity.getContentResolver().query(uri, imageColumns,
                MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

        if (imageCursor.moveToFirst()) {
            selectedImagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        return selectedImagePath;
    }

    private static Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    public static Uri createUriFromPath(String path) {
        File file = new File(path);
        return Uri.fromFile(file);
    }

    public static void galleryAddPic(Activity activity, Uri uri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        activity.sendBroadcast(mediaScanIntent);
    }

    public static File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale(Locale.ENGLISH.getLanguage())).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }
}
