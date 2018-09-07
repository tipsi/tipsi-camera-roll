export default {
  // Default value — []
  filesToCopy: [
    '.appiumhelperrc',
    'package.json',
    'android/build.gradle',
    'android/app/build.gradle',
    'android/gradle/wrapper/gradle-wrapper.properties',
    'android/gradle.properties',
    'ios/example/AppDelegate.m',
    'src',
    'scripts',
    '__tests__',
    'rn-cli.config.js',
    'App.js',
    'ios/Podfile',
  ],

  // Default value — { ios: '', android: '' }
  test: {
    ios: 'npm run test:ios',
    android: 'npm run test:android',
  },
}
