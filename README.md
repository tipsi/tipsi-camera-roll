# tipsi-camera-roll

## Getting started

`$ npm install tipsi-camera-roll --save`

### Mostly automatic installation

`$ react-native link tipsi-camera-roll`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `tipsi-camera-roll` and add `RNTipsiCameraroll.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNTipsiCameraroll.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android (Under development)

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.tipsi.cameraroll.RNTipsiCamerarollPackage;` to the imports at the top of the file
  - Add `new RNTipsiCamerarollPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':tipsi-camera-roll'
  	project(':tipsi-camera-roll').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-tipsi-cameraroll/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-tipsi-cameraroll')
  	```


## Usage
```javascript
import CameraRoll from 'tipsi-camera-roll';

CameraRoll.saveToCameraRoll('https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png', 'google')
      .then(r => {
				console.log('Saved', r)
      })
      .catch((error) => {
				console.log('Saved error:', error)
      })
```
### saveToCameraRoll

| Params | Desc | Type | |
| -------------- | -------------- | -------------- | -------------- |
| imageSource    | Source to image. Can be url, local file path, base64 string | String | requred |
| albumName   | Album name. If it nil image will be saved into Camera roll | String | optional |

## TODO:
- Add Android implementation
- Add Error convector in native module
- Clean up code
