import { NativeModules, NativeEventEmitter, Platform } from 'react-native';

const IS_ANDROID = Platform.OS === 'android';

const module = NativeModules.CafSmartAuthModule;

const moduleEventEmitter = IS_ANDROID
  ? new NativeEventEmitter()
  : new NativeEventEmitter(module);

export { module, moduleEventEmitter };
