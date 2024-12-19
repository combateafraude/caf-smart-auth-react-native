import { useState, useEffect } from 'react';

import { module, moduleEventEmitter } from '../modules/CafSmartAuth';

import {
  CafSmartAuthSettings,
  CafSmartAuthResponse,
  CafSmartAuthSuccess,
  CafSmartAuthPending,
} from '../hooks/types';

let responseFormattedOptions: string = '';

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
    moduleEventEmitter.addListener(
      'CafSmartAuth_Success',
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
      },
    );

    moduleEventEmitter.addListener('CafSmartAuth_Error', (event: string) => {
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
    });

    moduleEventEmitter.addListener('CafSmartAuth_Cancel', (event: boolean) => {
      setResponse({
        success: {
          isAuthorized: false,
          attemptId: null,
          attestation: null,
        },
        error: null,
        cancelled: event,
        isLoading: false,
        pending: {
          isAuthorized: false,
          attestation: null,
        },
      });
    });

    moduleEventEmitter.addListener(
      'CafSmartAuth_Pending',
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
      },
    );

    moduleEventEmitter.addListener('CafSmartAuth_Loading', (event: boolean) => {
      setResponse({
        success: {
          isAuthorized: false,
          attemptId: null,
          attestation: null,
        },
        error: null,
        cancelled: false,
        isLoading: event,
        pending: {
          isAuthorized: false,
          attestation: null,
        },
      });
    });

    moduleEventEmitter.addListener('CafSmartAuth_Loaded', (event: boolean) => {
      setResponse({
        success: {
          isAuthorized: false,
          attemptId: null,
          attestation: null,
        },
        error: null,
        cancelled: false,
        isLoading: event,
        pending: {
          isAuthorized: false,
          attestation: null,
        },
      });
    });

    return () => {
      moduleEventEmitter.removeAllListeners('CafSmartAuth_Success');
      moduleEventEmitter.removeAllListeners('CafSmartAuth_Pending');
      moduleEventEmitter.removeAllListeners('CafSmartAuth_Error');
      moduleEventEmitter.removeAllListeners('CafSmartAuth_Cancel');
      moduleEventEmitter.removeAllListeners('CafSmartAuth_Loading');
      moduleEventEmitter.removeAllListeners('CafSmartAuth_Loaded');
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
  personId: string,
) => {
  module.startSmartAuth(
    mfaToken,
    faceAuthToken,
    personId,
    policyId,
    responseFormattedOptions,
  );
};

const requestLocationPermissions = async () => {
  await module.requestLocationPermissions();
};

export { startSmartAuth, requestLocationPermissions, useSmartAuth };
