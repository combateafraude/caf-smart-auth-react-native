export enum CafStage {
  'DEV',
  'BETA',
  'PROD',
}

export enum CafFilter {
  'NATURAL',
  'LINE_DRAWING',
}

export interface CafSmartAuthSuccess {
  isAuthorized: boolean;
  attemptId?: string | null;
  attestation?: string | null;
}

export interface CafSmartAuthPending {
  isAuthorized: boolean;
  attestation: string | null;
}

export interface CafSmartAuthResponse {
  success: CafSmartAuthSuccess;
  error: string | null;
  cancelled: boolean;
  pending: CafSmartAuthPending | null;
  isLoading: boolean;
}

export interface CafFaceAuthenticationSettings {
  loadingScreen?: boolean;
  enableScreenCapture?: boolean;
  filter?: CafFilter;
}

export interface CafSmartAuthSettings {
  faceAuthenticationSettings?: CafFaceAuthenticationSettings;
  stage?: CafStage;
}
