import { FaceAuthenticatorFilter } from "../FaceAuthenticatorHook/types.d";

export type IdentityErrorType = {
    type: string;
    message: string;
}

export type IdentitySDKResponseType = Partial <IdentityErrorType> & IIdentityResponse;

export type IdentityEvent =
    | "Identity_Success"
    | "Identity_Pending"
    | "Identity_Error"
    | "Identity_Canceled";

export type IdentityHookReturnType = [
    (token: string) => void,
    IdentityResponseType | undefined,
    boolean,
    IdentityErrorType | undefined,
    boolean
];

export enum IdentityCAFStage {
    BETA,
    PROD,
    DEV,
}

export enum IdentityFilter {
    LINE_DRAWING,
    NATURAL
}

export interface IIdentityResponse {
    authorized?: boolean;
    pending?: boolean;
    attestation?: string;
    attemptId?: string;
}

export interface IIdentityConfig {
    livenessToken: string | null;
    setEnableScreenshots: boolean;
    setLoadingScreen: boolean;
    cafStage: CAFStage;
    setEmailUrl: string | null;
    setPhoneUrl: string | null;
    filter: FaceAuthenticatorFilter
}