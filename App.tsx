import React from 'react';
import { Button, SafeAreaView, ScrollView, StatusBar, StyleSheet, Text, View } from 'react-native';
import { Colors } from 'react-native/Libraries/NewAppScreen';
import { IIdentityConfig, IdentityCAFStage, IdentityFilter } from './src/caf-bridge-sdk/IdentityHook/types.d';
import IdentityHook from './src/caf-bridge-sdk/IdentityHook';

const App: React.FC<any> = () => {

  const backgroundStyle = {
    backgroundColor: Colors.lighter,
  };

  //Insert your generated JWT here. Check documentation here: https://docs.caf.io/sdks/access-token
  const mobileToken = "";
  const identityToken = ""
  
  const CPF = ""

  const IdentityConfig: IIdentityConfig = {
    cafStage: IdentityCAFStage.DEV,
    setEmailUrl: null,
    setPhoneUrl: null,
    livenessToken: mobileToken,
    setEnableScreenshots: false,
    setLoadingScreen: false,
    filter: IdentityFilter.LINE_DRAWING
  }

  // Identity
  const [sendIdentity, identityData, loadingIdentity, identityError] = IdentityHook(identityToken, "policy", IdentityConfig);

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle='dark-content'
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <View
          style={{
            backgroundColor: Colors.white,
          }}>
          <View style={styles.sectionContainer}>
            <Button
              title='Execute Identity'
              onPress={() => sendIdentity(CPF)}
            />
            <Text style={[styles.sectionTitle, {
              color: Colors.black
            },]}>
              {loadingIdentity ? 'Loading ...' : ''}</Text>
            <View style={styles.sectionResultContainer}>
              {!!identityError && <Text style={[styles.sectionResultText, {
                color: Colors.black
              },]}>
                {`${identityError.type}: \n ${identityError.message}`}</Text>}
              {identityData && <Text style={[styles.sectionResultText, {
                color: Colors.black
              },]}>
                {'IdentityResult:\n' + JSON.stringify(identityData)}</Text>}
            </View>
            <Text style={[styles.sectionTitle, {
              color: Colors.black
            },]}></Text>
          </View> 
        </View>
      </ScrollView>
    </SafeAreaView>
  )
}

const styles = StyleSheet.create({
  sectionContainer: {
    flex: 1,
    marginHorizontal: 40,
    marginTop: 20,
    paddingHorizontal: 24,
    backgroundColor: '#42d602',
    borderRadius: 8,
  },
  sectionResultContainer: {
    flex: 1,
    alignItems: 'center',
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    marginTop: 20,
    fontSize: 18,
    fontWeight: '500',
    textAlign: 'center',
  },
  sectionResultText: {
    fontSize: 16,
    fontWeight: '400',
    lineHeight: 25,
    textAlign: 'auto',
  },
});

export default App;