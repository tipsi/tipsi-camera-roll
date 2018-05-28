package com.tipsi.cameraroll;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;

public class RNTipsiCamerarollModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public RNTipsiCamerarollModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "CameraRoll";
    }

    @ReactMethod
    public void saveToCameraRoll(final String uri, String title, final Promise promise) {
        promise.reject("Not implemented yest");
        // Log.i("saveToCameraRoll", "Start");
        // final Activity activity = getCurrentActivity();
        //         if (activity == null) return;
        // activity.runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {
        //         Log.i("saveToCameraRoll", "runOnUiThread");
        //         Glide.
        //                 with(activity.getApplicationContext()).
        //                 asBitmap().
        //                 load(uri).
        //                 listener(new RequestListener<Bitmap>() {
        //                     @Override
        //                     public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
        //                         Log.i("Filed", "=(");
        //                         return false;
        //                     }

        //                     @Override
        //                     public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
        //                         // resource is your loaded Bitmap
        //                         Log.i("Done", resource.toString());
        //                         return true;
        //                     }
        //                 });
        //         }
        //     });
        }
}