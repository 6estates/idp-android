package com.sixe.idp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Picture tools
 */
public class ImageTools {

    /**
     * Determine whether the file exists
     *
     * @param filePath file path
     * @return true or false
     */
    public static boolean fileExits(final String filePath) {
        if (null == filePath) {
            return false;
        }

        File file = new File(filePath);
        if (file.isDirectory()) {
            return false;
        }
        return file.exists();
    }

    /**
     * Get bitmap object image
     *
     * @param filePath Picture path
     * @return bitmap object
     */
    public static Bitmap getBitMap(final String filePath) {
        if (null == filePath) {
            return null;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            Logger.output("File Not Exits:" + filePath);
            return null;
        }

        FileInputStream fis = null;
        try {
            assert file != null;
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Logger.output("Exception:" + e.getMessage());
        }

        if (null == fis) {
            return null;
        }

        return BitmapFactory.decodeStream(fis);
    }

    /**
     * Proportional compression picture
     *
     * @param srcPath Picture path
     * @return bitmap object
     */
    public static Bitmap getCompressImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是1080*720分辨率，所以高和宽我们设置为
        float hh = 1080f;//这里设置高度为1080f
        float ww = 720f;//这里设置宽度为720f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap scaleImg(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os); // 将图片信息无压缩保存到os中
        if (os.toByteArray().length / 1024 > 1024) {
            // 图片大于1M进行压缩，避免生成图片时溢出
            os.reset();
            image.compress(Bitmap.CompressFormat.JPEG, 50, os); // 将图片压缩50%存入os中
        }
        // 获取图片尺寸宽高
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
        int width = options.outWidth;
        int height = options.outHeight;
        // 计算缩放比
        int scale = 1;
        if (width >= height && width > pixelW) {
            scale = (int) (width / pixelW);
        } else if (width < height && height > pixelH) {
            scale = (int) (height / pixelH);
        }
        // 进行图片压缩
        is = new ByteArrayInputStream(os.toByteArray()); // 重新设置输入流
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        bitmap = BitmapFactory.decodeStream(is, null, options);

        return compressImage(bitmap);
    }

    /**
     * 质量压缩
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        int byteCount = image.getByteCount();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 500 && options > 0) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 创建文件用于保存拍摄的图片
     * @param context
     * @return
     */
    public static String createImagePath(Context context) {
        // 创建图片文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 保存图片的绝对路径
        return image.getAbsolutePath();
//        return image;
    }

    /**
     * Create a file to save picture
     * @param context context
     * @return file of picture
     */
    public static File createImageFile(Context context) {
        // 创建图片文件名
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Rotate Bitmap
     * @param bm bitmap object
     * @param orientationDegree degree
     * @return bitmap after rotate
     */
    public static Bitmap rotateBitmap(Bitmap bm, int orientationDegree) {
        //方便判断，角度都转换为正值
        int degree = orientationDegree;
        if( degree < 0){
            degree = 360 + orientationDegree;
        }

        int srcW = bm.getWidth();
        int srcH = bm.getHeight();

        Matrix m = new Matrix();
        m.setRotate(degree, (float) srcW / 2, (float) srcH / 2);
        float targetX, targetY;

        int destH = srcH;
        int destW = srcW;

        //根据角度计算偏移量，原理不明
        if (degree == 90 ) {
            targetX = srcH;
            targetY = 0;
            destH = srcW;
            destW = srcH;
        } else if( degree == 270){
            targetX = 0;
            targetY = srcW;
            destH = srcW;
            destW = srcH;
        }else if(degree == 180){
            targetX = srcW;
            targetY = srcH;
        }else {
            return bm;
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        //注意destW 与 destH 不同角度会有不同
        Bitmap bmTemp = Bitmap.createBitmap(destW, destH, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bmTemp);
        canvas.drawBitmap(bm, m, paint);
        return bmTemp;
    }

}
