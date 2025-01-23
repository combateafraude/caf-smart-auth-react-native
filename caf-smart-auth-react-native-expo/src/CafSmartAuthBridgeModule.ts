import { NativeModule, requireNativeModule } from "expo";

import { CafSmartAuthBridgeModuleEvents } from "./CafSmartAuthBridgeModule.types";

declare class CafSmartAuthBridgeModule extends NativeModule<CafSmartAuthBridgeModuleEvents> {
  startSmartAuth(
    mfaToken: string,
    faceAuthToken: string,
    personId: string,
    policyId: string,
    jsonString: string
  ): Promise<void>;
  requestLocationPermissions(): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<CafSmartAuthBridgeModule>(
  "CafSmartAuthBridgeModule"
);
