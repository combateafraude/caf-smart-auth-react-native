// Reexport the native module. On web, it will be resolved to CafSmartAuthBridgeModule.web.ts
// and on native platforms to CafSmartAuthBridgeModule.ts
export { default } from "./CafSmartAuthBridgeModule";
export * from "./CafSmartAuthBridgeModule.types";
