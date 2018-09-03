package com.tipsi.cameraroll;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.tipsi.cameraroll.ArgChecks.requireNonNull;

public class RNTipsiCamerarollModule extends ReactContextBaseJavaModule {

  public static final String TAG = RNTipsiCamerarollModule.class.getSimpleName();

  static final String JPEG = "jpeg";
  static final String JPG = "jpg";
  static final String PNG = "png";

  private static final int MAX_IMAGE_SIDE_PIXELS = 1024;

  private final ReactApplicationContext reactContext;

  private static final Map<String, Bitmap.CompressFormat> typeToCompressFormat = new HashMap(){{
    put(JPEG, Bitmap.CompressFormat.JPEG);
    put(JPG, Bitmap.CompressFormat.JPEG);
    put(PNG, Bitmap.CompressFormat.PNG);
  }};

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
  public void saveToCameraRoll(final String url, final String album, final Promise promise) {
    new Thread(new Runnable() {
      public void run() {
        saveToCameraRollImpl(url, album, promise);
      }
    }).start();
  }

  void saveToCameraRollImpl(final String url, final String album, final Promise promise) {
    requireNonNull(url, "url");
    requireNonNull(album, "album");

    ParsedDataUrl dataUrl = null;
    try {
      dataUrl = new ParsedDataUrl(url);
    } catch (Throwable t) {
      // ignore
    }

    boolean isDataUrl = dataUrl != null;

    try {
      if (!isDataUrl) {
        writeBitmapToFolder(
          album,
          getFileNameByUrl(url),
          ImageLoader.getInstance().loadImageSync(url),
          getCompressFormatByUri(url)
        );
      } else {
        writeBitmapToFolder(
          album,
          getFileNameByUrl(dataUrl),
          readBase64Bitmap(dataUrl.data),
          typeToCompressFormat.get(dataUrl.getFileExtension())
        );
      }

      promise.resolve(true);
    } catch (Throwable t) {
      promise.reject(t);
    }
  }

  private static String getFileExtensionByUrl(String url) {
    int dotPosition = url.lastIndexOf('.');
    ArgChecks.require(dotPosition != -1, "dot should exist");

    return url.substring(dotPosition + 1);
  }

  private static String getFileNameByUrl(String url) {
    String fileName = url.substring(url.lastIndexOf('/') + 1);
    int dotPosition = fileName.lastIndexOf('.');
    ArgChecks.require(dotPosition != -1, "dot should exist");

    return fileName.substring(0, dotPosition);
  }

  private static String getFileNameByUrl(ParsedDataUrl url) {
    return String.format(
      "%s.%s",
      getUnixTimestampString(),
      url.getFileExtension()
     );
  }

  private static String getUnixTimestampString() {
    return String.format("%l", System.currentTimeMillis());
  }

  private static Bitmap.CompressFormat getCompressFormatByUri(String url) {
    String extension = getFileExtensionByUrl(url);

    Bitmap.CompressFormat compressFormat = typeToCompressFormat.get(extension);
    ArgChecks.requireNonNull(compressFormat, "format should be known");

    return compressFormat;
  }

  private static Bitmap readBase64Bitmap(String base64encodedImage) {
    // can produce OOM (because passing base64 image on mobile is bad idea initially)

    byte[] decodedString = Base64.decode(base64encodedImage, Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
  }

  private static void writeBitmapToFolder(
    String albumName,
    String fileName,
    Bitmap bitmap,
    Bitmap.CompressFormat format
  ) throws Throwable {
    boolean isExternalStorageWritable = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());

    ArgChecks.require(isExternalStorageWritable, "External storage should be writable");

    File album = new File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
      albumName
    );

    album.mkdirs();

    FileOutputStream out = new FileOutputStream(new File(album, fileName));
    bitmap.compress(format, 100, out);
    out.close();
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