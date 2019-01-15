package net.redcomdata.application.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <pre>
 *     author : leede
 *     time   : 2018/09/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ImageUtil {
    public static File compressImage(String srcPath, int maxSizeKB) {
        /**
         * 分辨率压缩
         */
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 1080f;// 这里设置高度为800f
        float ww = 720f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        /**
         * 质量压缩
         */
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        File file = new File(srcPath);
        int length = (int) (file.length() / 1024);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(srcPath));
            int options = 100;
            if (length > maxSizeKB) {
                options = 100 * maxSizeKB / length;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            MyToast.show("上传图片失败，请打开sd卡权限");
            return null;
        }
        return file;// 压缩好比例大小后再进行质量压缩
    }
}
