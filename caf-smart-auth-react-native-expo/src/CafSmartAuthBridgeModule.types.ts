export interface CafSmartAuthSuccess {
  isAuthorized: boolean;
  attemptId?: string | null;
  attestation?: string | null;
}

export interface CafSmartAuthPending {
  isAuthorized: boolean;
  attestation: string | null;
}

export interface CafSmartAuthError {
  message: string;
}

export interface CafSmartAuthCancel {
  isCancelled: boolean;
}

export interface CafSmartAuthLoading {
  isLoading: boolean;
}

export interface CafSmartAuthLoaded {
  isLoaded: boolean;
}

export interface CafSmartAuthTheme {
  backgroundColor?: string;
  textColor?: string;
  progressColor?: string;
  linkColor?: string;
  boxBackgroundColor?: string;
  boxFilledBackgroundColor?: string;
  boxBorderColor?: string;
  boxFilledBorderColor?: string;
  boxTextColor?: string;
}

export interface CafSmartAuthThemeConfigurator {
  lightTheme?: CafSmartAuthTheme;
  darkTheme?: CafSmartAuthTheme;
}

export interface CafFaceAuthenticationSettings {
  loadingScreen?: boolean;
  enableScreenCapture?: boolean;
  filter?: CafFilter;
}

export interface CafSmartAuthSettings {
  faceAuthenticationSettings?: CafFaceAuthenticationSettings;
  stage?: CafStage;
  emailUrl?: string;
  phoneUrl?: string;
  theme?: CafSmartAuthThemeConfigurator;
}

export interface CafSmartAuthResponse {
  success: CafSmartAuthSuccess;
  error: CafSmartAuthError | null;
  cancelled: boolean;
  pending: CafSmartAuthPending | null;
  isLoading: boolean;
}

export type CafSmartAuthBridgeModuleEvents = {
  CafSmartAuth_Success: (payload: CafSmartAuthSuccess) => void;
  CafSmartAuth_Pending: (payload: CafSmartAuthPending) => void;
  CafSmartAuth_Error: (payload: CafSmartAuthError) => void;
  CafSmartAuth_Cancel: (payload: CafSmartAuthCancel) => void;
  CafSmartAuth_Loading: (payload: CafSmartAuthLoading) => void;
  CafSmartAuth_Loaded: (payload: CafSmartAuthLoaded) => void;
};

export enum CafStage {
  "DEV",
  "BETA",
  "PROD",
}

export enum CafFilter {
  "NATURAL",
  "LINE_DRAWING",
}
