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

  const mfaToken =
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2NmY1YzU0NjhlMWI3YTAwMDg2OGRhZGEifQ.5pm1Pq3fipLfuWOzxMYCAHirML8nzWWkf4O10u1ov68';
  const faceAuthenticationToken =
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiI2NzA0MWIxODczMzFkZTAwMDgyNjZkMDMifQ.a6BRVT35JLPRtlkGXM9jVsX817PuVKQ2UsMqEXz0GJM';

  useEffect(() => {
    IS_ANDROID && requestLocationPermissions();
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <Button
        title="Start Smart Auth"
        onPress={() =>
          startSmartAuth(
            mfaToken,
            faceAuthenticationToken,
            'todas',
            '43485449806',
          )
        }
      />

      <Text>Success {JSON.stringify(success)}</Text>
      <Text>Cancelled {JSON.stringify(cancelled)}</Text>
      <Text>Error {JSON.stringify(error)}</Text>
      <Text>Is Loading {JSON.stringify(isLoading)}</Text>
      <Text>Pending {JSON.stringify(pending)}</Text>
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
