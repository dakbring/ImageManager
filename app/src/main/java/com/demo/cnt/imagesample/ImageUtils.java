package com.demo.cnt.imagesample;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {

    public static void loadImage(Activity activity, Uri uri, RelativeLayout layout, boolean isCapture) {
        Bitmap bitmap = null;
        String path;
        path = isCapture ? uri.getPath() : getRealPathFromURI(activity, uri);

        try {
            bitmap = scaleBitmapFitScreenWidth(activity, path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bitmap != null) {
            if (layout.getChildCount() > 0) {
                layout.removeAllViews();
            }
            TouchImageView imageView = new TouchImageView(activity);
            imageView.setImageBitmap(bitmap);
            imageView.setMinZoom((float).5);
            imageView.setBackgroundColor(Color.parseColor("#ff0099cc"));
            layout.addView(imageView);
        }
    }

    /**
     * return a bitmap with width = screen's width
     * keep the ratio
     */
    public static Bitmap scaleBitmapFitScreenWidth(Activity activity, String imagePath) throws Exception {
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
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        Matrix matrix = new Matrix();

        /*check the orientation state of loaded bitmap*/
        switch (getBitmapOrientation(imagePath)){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                matrix.postRotate(0);
        }
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }

    public static int getBitmapOrientation(String imagePath)throws Exception{
        File imageFile = new File(imagePath);
        ExifInterface exifInterface = new ExifInterface(imageFile.getAbsolutePath());
        return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
        imageCursor.close();
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
