import React, { useEffect } from "react";
import {
  Button,
  SafeAreaView,
  Text,
  Platform,
  StyleSheet,
  View,
} from "react-native";

import { CafFilter, CafStage } from "../src/CafSmartAuthBridgeModule.types";
import {
  startSmartAuth,
  requestLocationPermissions,
  useSmartAuth,
} from "../src/hooks/useSmartAuth";

const IS_ANDROID = Platform.OS === "android";

export default function App() {
  const { success, cancelled, error, isLoading, pending } = useSmartAuth({
    faceAuthenticationSettings: {
      loadingScreen: false,
      enableScreenCapture: false,
      filter: CafFilter.NATURAL,
    },
    stage: CafStage.PROD,
  });

  function jsonStringify<T>(obj: T): string {
    return JSON.stringify(obj);
  }

  useEffect(() => {
    IS_ANDROID && requestLocationPermissions();
  }, []);

  return (
    <SafeAreaView style={{ flex: 1 }}>
      <View style={styles.container}>
        <Button
          title="Start CafSmartAuth"
          onPress={() => startSmartAuth("", "", "", "")}
        />

        <View>
          <Text>Success {jsonStringify(success)}</Text>
          <Text>Cancelled {jsonStringify(cancelled)}</Text>
          <Text>Error {jsonStringify(error)}</Text>
          <Text>Is Loading {jsonStringify(isLoading)}</Text>
          <Text>Pending {jsonStringify(pending)}</Text>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    gap: 20,
    width: "100%",
    paddingHorizontal: 20,
  },
});
