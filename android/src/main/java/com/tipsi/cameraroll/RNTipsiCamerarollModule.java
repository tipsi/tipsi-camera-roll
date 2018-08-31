package com.tipsi.cameraroll;

import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;

import static com.tipsi.cameraroll.ArgChecks.requireNonNull;

public class RNTipsiCamerarollModule extends ReactContextBaseJavaModule {

  public static final String TAG = RNTipsiCamerarollModule.class.getSimpleName();

  public static final int MAX_IMAGE_SIDE_PIXELS = 1024;

  private final ReactApplicationContext reactContext;

  private enum ErrorCodes {
    STORAGE_NOT_READY,
    WRITE_BITMAP_FAILED,
  }

  public RNTipsiCamerarollModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    initializeImageLoader();
  }

  @Override
  public String getName() {
    return "CameraRoll";
  }

  @ReactMethod
  public void saveToCameraRoll(final String uri, final String album, final Promise promise) {
    requireNonNull(uri, "uri");
    requireNonNull(album, "title");

    if (!isDataUri(uri)) {
      ImageLoader.getInstance().loadImage(uri, new SimpleImageLoadingListener() {
        public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
          writeBitmapToFolder(
            album,
            getFileNameByUri(uri),
            bitmap,
            getCompressFormatByUri(uri),
            promise
          );
        }

        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
          promise.reject(
            failReason.getType().toString(),
            failReason.getCause().getMessage()
          );
        }
      });
    } else {
      Bitmap bitmap = readBase64Bitmap(uri);
      writeBitmapToFolder(
        album,
        getFileNameByUri(uri),
        bitmap,
        getCompressFormatByUri(uri),
        promise
      );
    }
  }

  private static String getFileNameByUri(String uri) {
    return null;
  }

  private static Bitmap.CompressFormat getCompressFormatByUri(String uri) {
    return null;
  }

  public static boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    }
    return false;
  }

  public static File getPublicAlbumStorageDir(String albumName) {
    // Get the directory for the user's public pictures directory.
    File file = new File(Environment.getExternalStoragePublicDirectory(
      Environment.DIRECTORY_PICTURES), albumName);

    return file;
  }

  private static boolean isDataUri(String uri) {
    return false;
  }

  private static Bitmap readBase64Bitmap(String uri) {
    return null;
  }

  private static void writeBitmapToFolder(
    String albumName,
    String fileName,
    Bitmap bitmap,
    Bitmap.CompressFormat format,
    Promise promise
  ) {
    if (!isExternalStorageWritable()) {
      promise.reject(
        ErrorCodes.STORAGE_NOT_READY.toString(),
        "External storage is not writable"
      );
    }

    File album = getPublicAlbumStorageDir(albumName);
    album.mkdirs();

    FileOutputStream out = null;
    try {
      out = new FileOutputStream(new File(album, fileName));
      bitmap.compress(format, 100, out);
      out.close();
    } catch (Exception e) {
      promise.reject(
        ErrorCodes.WRITE_BITMAP_FAILED.toString(),
        e.getMessage()
      );
    }
  }

  void initializeImageLoader() {
    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this.reactContext)
      .defaultDisplayImageOptions(new DisplayImageOptions.Builder().cacheOnDisk(true).build())
      .diskCacheExtraOptions(MAX_IMAGE_SIDE_PIXELS, MAX_IMAGE_SIDE_PIXELS, null)
      .diskCacheSize(8 * 1024 * 1024)
      .diskCacheFileCount(20)
      //.writeDebugLogs()
      .build();

    ImageLoader.getInstance().init(config);
  }
}