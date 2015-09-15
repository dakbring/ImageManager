package com.demo.cnt.imagesample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final int SELECT_PICTURE = 1;

    /**
     * Standard activity result: operation succeeded.
     */
    public static final int RESULT_OK = -1;

    private String mImagePath = "";

    private RelativeLayout vImageContainerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vImageContainerLayout = (RelativeLayout)findViewById(R.id.image_container);
        findViewById(R.id.get_gallery_btn).setOnClickListener(this);
        findViewById(R.id.take_photo_btn).setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                ImageUtils.loadImage(this, selectedImageUri, vImageContainerLayout, false);
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                if (!TextUtils.isEmpty(mImagePath)) {
                    Uri contentUri = ImageUtils.createUriFromPath(mImagePath);
                    ImageUtils.galleryAddPic(this, contentUri);
                    ImageUtils.loadImage(this, contentUri, vImageContainerLayout, true);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.get_gallery_btn:
                getImageFromGallery();
                break;
            case R.id.take_photo_btn:
                takePicture();
                break;
        }
    }

    protected void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, ""), SELECT_PICTURE);
    }

    protected void takePicture() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = ImageUtils.createImageFile();
                // Save a file: mImagePath for use with ACTION_VIEW intents
                mImagePath = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
}
