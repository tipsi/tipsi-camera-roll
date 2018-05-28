#import "RNTipsiCameraroll.h"
#import <React/RCTImageLoader.h>
#import <React/RCTBridge.h>
@import Photos;

@implementation CameraRoll

RCT_EXPORT_MODULE()

@synthesize bridge = _bridge;

static NSString *const kErrorUnableToLoad = @"ERROR_UNABLE_TO_LOAD";
static NSString *const kErrorUnableToSave = @"ERROR_UNABLE_TO_SAVE";
static NSString *const kErrorCreateAlbum = @"ERROR_CREATE_ALBUM";

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_METHOD(saveToCameraRoll:(NSURLRequest *)request
                  albumTitle:(NSString*)title
                  resolve:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject) {
    void (^complete)(BOOL, NSError *, NSString *) = ^(BOOL success, NSError *__nullable error, NSString *__nullable localIdentifier){
        if (!error) {
            resolve(@YES);
        } else {
            reject(@"1", @"Can save image to album", error);
        }
    };
        [_bridge.imageLoader loadImageWithURLRequest:request
                                            callback:^(NSError *loadError, UIImage *loadedImage) {
                                                if (loadError) {
                                                    reject(kErrorUnableToLoad, nil, loadError);
                                                    return;
                                                }
                                                [PHPhotoLibrary requestAuthorization:^(PHAuthorizationStatus status) {
                                                    if (status == PHAuthorizationStatusAuthorized) {
                                                        if (title) {
                                                            PHAssetCollection *assetCollection = [self albumWithTitle:title];
                                                            if(assetCollection){
                                                                [self saveImage:loadedImage toAlbum:assetCollection andCompleteBLock:complete];
                                                            } else {
                                                                [[PHPhotoLibrary sharedPhotoLibrary] performChanges:^{
                                                                    [PHAssetCollectionChangeRequest creationRequestForAssetCollectionWithTitle:title];
                                                                } completionHandler:^(BOOL success, NSError *error) {
                                                                    if (!success) {
                                                                        reject(kErrorCreateAlbum, error.localizedDescription, error);
                                                                    } else {
                                                                        PHAssetCollection *assetCollection = [self albumWithTitle:title];
                                                                        [self saveImage:loadedImage toAlbum:assetCollection andCompleteBLock:complete];
                                                                    }
                                                                }];
                                                            }
                                                        } else {
                                                            [self saveImage:loadedImage toAlbum:nil andCompleteBLock:complete];
                                                        }
                                                    } else {
                                                        reject(@"1", @"This application is not authorized to access photo data", nil);
                                                    }
                                                }];
                                            }];
}

-(PHAssetCollection*)albumWithTitle:(NSString*)title {
    // Check if album exists. If not, create it.
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"localizedTitle = %@", title];
    PHFetchOptions *options = [[PHFetchOptions alloc]init];
    options.predicate = predicate;
    PHFetchResult *result = [PHAssetCollection fetchAssetCollectionsWithType:PHAssetCollectionTypeAlbum subtype:PHAssetCollectionSubtypeAny options:options];
    if (result.count) {
        return result.firstObject;
    }
    return nil;

}

-(void)saveImage:(UIImage *)image
         toAlbum:(PHAssetCollection *)album
andCompleteBLock:(nullable void(^)(BOOL success, NSError *__nullable error, NSString *__nullable localIdentifier))completeBlock {
    __block PHObjectPlaceholder *placeholder;
    [[PHPhotoLibrary sharedPhotoLibrary] performChanges:^{

        PHAssetChangeRequest *assetRequest = [PHAssetChangeRequest creationRequestForAssetFromImage:image];
        placeholder = [assetRequest placeholderForCreatedAsset];

        if(album) {
            PHAssetCollectionChangeRequest *albumChangeRequest = [PHAssetCollectionChangeRequest changeRequestForAssetCollection:album];
            [albumChangeRequest addAssets:@[placeholder]];
        }
    } completionHandler:^(BOOL success, NSError *error) {
        completeBlock(success, error, placeholder.localIdentifier);
    }];
}

@end

