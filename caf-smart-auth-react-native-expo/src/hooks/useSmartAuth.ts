import { useState, useEffect } from "react";

import CafSmartAuthBridgeModule, {
  CafSmartAuthSettings,
  CafSmartAuthResponse,
  CafSmartAuthSuccess,
  CafSmartAuthError,
  CafSmartAuthPending,
  CafSmartAuthCancel,
  CafSmartAuthLoading,
  CafSmartAuthLoaded,
} from "../";

let responseFormattedOptions: string = "";

const formattedOptions = (settings: CafSmartAuthSettings): string => {
  const formatToJSON = JSON.stringify({
    ...settings,
  });

  return formatToJSON;
};

const useSmartAuth = (settings?: CafSmartAuthSettings) => {
  const [response, setResponse] = useState<CafSmartAuthResponse>({
    success: {
      isAuthorized: false,
      attemptId: null,
      attestation: null,
    },
    error: null,
    cancelled: false,
    isLoading: false,
    pending: {
      isAuthorized: false,
      attestation: null,
    },
  });

  responseFormattedOptions = formattedOptions(settings!);

  useEffect(() => {
    CafSmartAuthBridgeModule.addListener(
      "CafSmartAuth_Success",
      (event: CafSmartAuthSuccess) => {
        setResponse({
          success: {
            isAuthorized: event.isAuthorized,
            attemptId: event.attemptId,
            attestation: event.attestation,
          },
          error: null,
          cancelled: false,
          isLoading: false,
          pending: {
            isAuthorized: false,
            attestation: null,
          },
        });
      }
    );

    CafSmartAuthBridgeModule.addListener(
      "CafSmartAuth_Error",
      (event: CafSmartAuthError) => {
        setResponse({
          success: {
            isAuthorized: false,
            attemptId: null,
            attestation: null,
          },
          error: event,
          cancelled: false,
          isLoading: false,
          pending: {
            isAuthorized: false,
            attestation: null,
          },
        });
      }
    );

    CafSmartAuthBridgeModule.addListener(
      "CafSmartAuth_Cancel",
      (event: CafSmartAuthCancel) => {
        setResponse({
          success: {
            isAuthorized: false,
            attemptId: null,
            attestation: null,
          },
          error: null,
          cancelled: event.isCancelled,
          isLoading: false,
          pending: {
            isAuthorized: false,
            attestation: null,
          },
        });
      }
    );

    CafSmartAuthBridgeModule.addListener(
      "CafSmartAuth_Pending",
      (event: CafSmartAuthPending) => {
        setResponse({
          success: {
            isAuthorized: false,
            attemptId: null,
            attestation: null,
          },
          error: null,
          cancelled: false,
          isLoading: false,
          pending: {
            isAuthorized: event.isAuthorized,
            attestation: event.attestation,
          },
        });
      }
    );

    CafSmartAuthBridgeModule.addListener(
      "CafSmartAuth_Loading",
      (event: CafSmartAuthLoading) => {
        setResponse({
          success: {
            isAuthorized: false,
            attemptId: null,
            attestation: null,
          },
          error: null,
          cancelled: false,
          isLoading: event.isLoading,
          pending: {
            isAuthorized: false,
            attestation: null,
          },
        });
      }
    );

    CafSmartAuthBridgeModule.addListener(
      "CafSmartAuth_Loaded",
      (event: CafSmartAuthLoaded) => {
        setResponse({
          success: {
            isAuthorized: false,
            attemptId: null,
            attestation: null,
          },
          error: null,
          cancelled: false,
          isLoading: event.isLoaded,
          pending: {
            isAuthorized: false,
            attestation: null,
          },
        });
      }
    );

    return () => {
      CafSmartAuthBridgeModule.removeAllListeners("CafSmartAuth_Success");
      CafSmartAuthBridgeModule.removeAllListeners("CafSmartAuth_Pending");
      CafSmartAuthBridgeModule.removeAllListeners("CafSmartAuth_Error");
      CafSmartAuthBridgeModule.removeAllListeners("CafSmartAuth_Cancel");
      CafSmartAuthBridgeModule.removeAllListeners("CafSmartAuth_Loading");
      CafSmartAuthBridgeModule.removeAllListeners("CafSmartAuth_Loaded");
    };
  }, []);

  return {
    success: response.success,
    error: response.error,
    cancelled: response.cancelled,
    pending: response.pending,
    isLoading: response.isLoading,
  };
};

const startSmartAuth = (
  mfaToken: string,
  faceAuthToken: string,
  policyId: string,
  personId: string
) => {
  CafSmartAuthBridgeModule.startSmartAuth(
    mfaToken,
    faceAuthToken,
    personId,
    policyId,
    responseFormattedOptions
  );
};

const requestLocationPermissions = async () => {
  await CafSmartAuthBridgeModule.requestLocationPermissions();
};

export { startSmartAuth, requestLocationPermissions, useSmartAuth };
