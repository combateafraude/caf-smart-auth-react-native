import { NativeModules, NativeEventEmitter } from "react-native";
import { useEffect, useState } from "react";
import * as T from './types.d';
import { Platform } from "react-native";

const isAndroid = Platform.OS === "android";
export const CAF_IDENTITY_MODULE =  NativeModules.CafIdentity;
export const CAF_IDENTITY_MODULE_EMITTER = new NativeEventEmitter(CAF_IDENTITY_MODULE);

const defaultConfig: T.IIdentityConfig = {
  cafStage: T.IdentityCAFStage.PROD,
  setEmailUrl: null,
  setPhoneUrl: null,
  livenessToken: null,
  setEnableScreenshots: false,
  setLoadingScreen: false,
  filter: T.IdentityFilter.LINE_DRAWING
}

function formatedConfig(config?: T.IIdentityConfig): string {
  const responseConfig = config || defaultConfig;

  return JSON.stringify({
    ...responseConfig,
    cafStage: isAndroid ? T.IdentityCAFStage[responseConfig.cafStage] : responseConfig.cafStage,
    filter: isAndroid ? T.IdentityFilter[responseConfig.filter] : responseConfig.filter,
  })
}

function IdentityHook(token: string, policyId: string, config?: T.IIdentityConfig): T.IdentityHookReturnType {
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<T.IdentityErrorType | undefined>();
  const [data, setData] = useState<T.IIdentityResponse | undefined>();
  const [canceled, setCanceled] = useState<String | undefined>();

  const handleEvent = (event: string, res?: T.IdentitySDKResponseType) => {
    switch (event) {
      case "Identity_Success":
        setData({
          authorized: res?.authorized,
          attemptId: res?.attemptId,
          attestation: res?.attestation
        });
        setLoading(false);
        break;
      case "Identity_Pending":
        setData({
          pending: res?.pending,
          attestation: res?.attestation
        })
        setLoading(false);
        break;
      case "Identity_Error":
        setError({...res} as T.IdentityErrorType);
        setLoading(false);
        break;
      case "Identity_Canceled":
        setCanceled(res?.message);
        setLoading(false);
      default:
        break;
    }
  };

  useEffect(() => {
    const eventListener = (event: T.IdentityEvent, res?: T.IdentitySDKResponseType) =>
      handleEvent(event, res);

    CAF_IDENTITY_MODULE_EMITTER.addListener("Identity_Success", (data) => eventListener("Identity_Success", data));
    CAF_IDENTITY_MODULE_EMITTER.addListener("Identity_Pending", (data) => eventListener("Identity_Pending", data));
    CAF_IDENTITY_MODULE_EMITTER.addListener("Identity_Error", (data) => eventListener("Identity_Error", data));
    CAF_IDENTITY_MODULE_EMITTER.addListener("Identity_Canceled", (data) => eventListener("Identity_Canceled", data));

    return () => {
      CAF_IDENTITY_MODULE_EMITTER.removeAllListeners("Identity_Success");
      CAF_IDENTITY_MODULE_EMITTER.removeAllListeners("Identity_Pending");
      CAF_IDENTITY_MODULE_EMITTER.removeAllListeners("Identity_Error");
      CAF_IDENTITY_MODULE_EMITTER.removeAllListeners("Identity_Canceled");
    };
  }, [token]);

  const send = (personId: string): void => {
    setLoading(true);
    setData(undefined);
    setError(undefined);
    setCanceled(undefined);
    CAF_IDENTITY_MODULE.identity(token, personId, policyId, formatedConfig(config));
  };

  return [send, data, loading, error, canceled];
}

export default IdentityHook;
