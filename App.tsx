import React, { useEffect } from 'react';
import { SafeAreaView, StyleSheet, Button, Text, Platform } from 'react-native';

import { CafFilter, CafStage } from './src/hooks/types';

import {
  startSmartAuth,
  requestLocationPermissions,
  useSmartAuth,
} from './src/hooks/useSmartAuth';

const IS_ANDROID = Platform.OS === 'android';

const App: React.FC<React.FC> = () => {
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
    <SafeAreaView style={styles.container}>
      <Button
        title="Start CafSmartAuth"
        onPress={() =>
          startSmartAuth(
            'mfaToken',
            'faceAuthenticationToken',
            'todas',
            '43485449806',
          )
        }
      />

      <Text>Success {jsonStringify(success)}</Text>
      <Text>Cancelled {jsonStringify(cancelled)}</Text>
      <Text>Error {jsonStringify(error)}</Text>
      <Text>Is Loading {jsonStringify(isLoading)}</Text>
      <Text>Pending {jsonStringify(pending)}</Text>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 20,
    gap: 8,
  },
});

export default App;
