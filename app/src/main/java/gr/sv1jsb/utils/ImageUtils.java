package gr.sv1jsb.utils;

import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by andreas on 15/8/2014.
 */
public class ImageUtils {
    private static ImageCache mImageCache;

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromUri(ContentResolver cr, Uri uri, int size) {
        try {
            InputStream is = cr.openInputStream(uri);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            options.inSampleSize = calculateInSampleSize(options, size, size);
            options.inJustDecodeBounds = false;
            is = cr.openInputStream(uri);
            return BitmapFactory.decodeStream(is, null, options);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static Bitmap decodeSampledBitmapFromFD(FileDescriptor fd, int size) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, size, size);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    public static void addImageCache(FragmentManager fragmentManager, float memCacheSizePercent){
        mImageCache = ImageCache.getInstance(fragmentManager, memCacheSizePercent);
    }

    public static void updateBitmapToCache(String data, Bitmap bitmap){
        mImageCache.updateBitmapToCache(data, bitmap);
    }

    public static void loadBitmap(File path, String playerphoto, ImageView imageView, Resources res, Bitmap bm) {
        Bitmap bitmap = null;

        if (mImageCache != null && playerphoto != null) {
            bitmap = mImageCache.getBitmapFromMemCache(playerphoto);
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else if (cancelPotentialWork(playerphoto, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, path);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(res, bm, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(playerphoto);
        }
    }

    private static boolean cancelPotentialWork(String data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            if(bitmapData == null) return true;
            if (!bitmapData.equals(data)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    static class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String data = "";
        private String mPath = "";

        public BitmapWorkerTask(ImageView imageView, File path) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            if(path != null) {
                mPath = path.getPath();
            }
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            data = params[0];
            final Bitmap bm = BitmapFactory.decodeFile(mPath+"/"+data);
            if (bm != null && mImageCache != null) {
                mImageCache.addBitmapToCache(data, bm);
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    public static boolean saveImage(Bitmap bm, File dir, String fileName){
        try {
            OutputStream os = new FileOutputStream(new File(dir, "/" + fileName));
            bm.compress(Bitmap.CompressFormat.JPEG, 90, os);
            os.flush();
            os.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean saveImage(Bitmap bm, File f){
        try {
            OutputStream os = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, os);
            os.flush();
            os.close();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
