package com.sixe.idp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sixe.idp.Idp;
import com.sixe.idp.R;
import com.sixe.idp.utils.ImageTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.pqpo.smartcropperlib.view.CropImageView;

/**
 * activity of crop images
 */
public class CropActivity extends AppCompatActivity {

    private CropImageView mCropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        mCropImageView = findViewById(R.id.iv_crop);

        String tempImage = getIntent().getStringExtra(Idp.IMAGE_PATH);
        if (TextUtils.isEmpty(tempImage)) {
            Intent intent = new Intent();
            intent.putExtra(Idp.CROP_ERROR, "Image path is null");
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(tempImage, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options);
        Bitmap bitmap = BitmapFactory.decodeFile(tempImage, options);

        mCropImageView.setImageToCrop(bitmap);

        findViewById(R.id.iv_sure).setOnClickListener(view -> {
            if (mCropImageView.canRightCrop()) {
                Bitmap crop = mCropImageView.crop();
                if (crop != null) {
                    File mCroppedFile = ImageTools.createImageFile(CropActivity.this);
                    saveImage(ImageTools.compressImage(crop), mCroppedFile);

                    Intent intent = new Intent();
                    intent.putExtra(Idp.CROP_IMAGE_PATH, mCroppedFile.getAbsolutePath());
                    setResult(RESULT_OK, intent);

                } else {
                    Intent intent = new Intent();
                    intent.putExtra(Idp.CROP_ERROR, "Crop error");
                    setResult(RESULT_CANCELED, intent);
                }
                finish();
            } else {
                Toast.makeText(CropActivity.this, "Cannot crop correctly", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImage(Bitmap bitmap, File saveFile) {
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int calculateSampleSize(BitmapFactory.Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1080;
        int destWidth = 720;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }

        return sampleSize;
    }
}