import { FaceAuthenticatorFilter } from "../FaceAuthenticatorHook/types.d";

export type IdentityErrorType = {
    type: string;
    message: string;
}

export type IdentitySDKResponseType = Partial <IdentityErrorType> & IIdentityResponse;

export type IdentityEvent =
    | "Identity_Success"
    | "Identity_Pending"
    | "Identity_Error";

export type IdentityHookReturnType = [
    (token: string) => void,
    IdentityResponseType | undefined,
    boolean,
    IdentityErrorType | undefined
];

export enum IdentityCAFStage {
    DEV,
    BETA,
    PROD
}

export enum IdentityFilter {
    LINE_DRAWING,
    NATURAL
}

export interface IIdentityResponse {
    authorized?: boolean;
    pending?: boolean;
    attestation?: string;
}

export interface IIdentityConfig {
    faceAuthToken: string | null;
    setEnableScreenshots: boolean;
    setLoadingScreen: boolean;
    cafStage: CAFStage;
    setEmailUrl: string | null;
    setPhoneUrl: string | null;
    filter: FaceAuthenticatorFilter
}