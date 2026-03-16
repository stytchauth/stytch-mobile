import * as SecureStore from 'expo-secure-store';
import { useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Button,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import {
  BiometricsAvailability,
  ConsumerAuthenticationState,
  StytchClientConfiguration,
  StytchProvider,
  createStytchConsumer,
  useStytch,
  useStytchAuthenticationState,
} from '@stytch/react-native-consumer';

const KEY_DEMO_TYPE = 'DEMO_APP_TYPE';
const KEY_PUBLIC_TOKEN = 'STYTCH_PUBLIC_TOKEN';
const KEY_GOOGLE_CLIENT_ID = 'GOOGLE_CLIENT_ID';

type Screen = 'loading' | 'selector' | 'tokenEntry' | 'consumer' | 'b2b';

export default function App() {
  const [screen, setScreen] = useState<Screen>('loading');
  const [stytchClient, setStytchClient] = useState<ReturnType<typeof createStytchConsumer> | null>(null);

  useEffect(() => {
    initScreen();
  }, []);

  const initScreen = async () => {
    const demoType = await SecureStore.getItemAsync(KEY_DEMO_TYPE);
    if (!demoType) {
      setScreen('selector');
      return;
    }
    const token = await SecureStore.getItemAsync(KEY_PUBLIC_TOKEN);
    if (!token) {
      setScreen('tokenEntry');
      return;
    }
    if (demoType === 'CONSUMER') {
      const googleClientId = await SecureStore.getItemAsync(KEY_GOOGLE_CLIENT_ID);
      setStytchClient(buildClient(token, googleClientId));
      setScreen('consumer');
    } else {
      setScreen('b2b');
    }
  };

  const handleSelectDemo = async (demoType: string) => {
    await SecureStore.setItemAsync(KEY_DEMO_TYPE, demoType);
    setScreen('tokenEntry');
  };

  const handleSubmitToken = async (publicToken: string, googleClientId: string | null) => {
    await SecureStore.setItemAsync(KEY_PUBLIC_TOKEN, publicToken);
    if (googleClientId) {
      await SecureStore.setItemAsync(KEY_GOOGLE_CLIENT_ID, googleClientId);
    }
    const demoType = await SecureStore.getItemAsync(KEY_DEMO_TYPE);
    if (demoType === 'CONSUMER') {
      setStytchClient(buildClient(publicToken, googleClientId));
      setScreen('consumer');
    } else {
      setScreen('b2b');
    }
  };

  const handleSwitchDemos = async () => {
    await SecureStore.deleteItemAsync(KEY_PUBLIC_TOKEN);
    await SecureStore.deleteItemAsync(KEY_GOOGLE_CLIENT_ID);
    await SecureStore.deleteItemAsync(KEY_DEMO_TYPE);
    setStytchClient(null);
    setScreen('selector');
  };

  if (screen === 'loading') {
    return (
      <SafeAreaView style={styles.centered}>
        <ActivityIndicator size="large" />
      </SafeAreaView>
    );
  }

  if (screen === 'selector') {
    return <SelectorScreen onSelect={handleSelectDemo} />;
  }

  if (screen === 'tokenEntry') {
    return <TokenEntryScreen onSubmit={handleSubmitToken} />;
  }

  if (screen === 'consumer' && stytchClient) {
    return (
      <StytchProvider stytch={stytchClient}>
        <ConsumerScreen onSwitchDemos={handleSwitchDemos} />
      </StytchProvider>
    );
  }

  if (screen === 'b2b') {
    return <B2BScreen onSwitchDemos={handleSwitchDemos} />;
  }

  return null;
}

function buildClient(publicToken: string, googleClientId: string | null | undefined) {
  return createStytchConsumer(
    new StytchClientConfiguration(
      publicToken,
      undefined,
      5,
      googleClientId ? { googleClientId, autoSelectEnabled: false } : undefined,
    ),
  );
}

// MARK: - Selector

function SelectorScreen({ onSelect }: { onSelect: (demoType: string) => void }) {
  return (
    <SafeAreaView style={styles.centered}>
      <Text style={styles.title}>Stytch Demo</Text>
      <Text style={styles.subtitle}>Select a demo to get started</Text>
      <View style={styles.buttonGroup}>
        <Button title="Consumer" onPress={() => onSelect('CONSUMER')} />
        <View style={styles.spacer} />
        <Button title="B2B" onPress={() => onSelect('B2B')} />
      </View>
    </SafeAreaView>
  );
}

// MARK: - Token Entry

function TokenEntryScreen({
  onSubmit,
}: {
  onSubmit: (publicToken: string, googleClientId: string | null) => void;
}) {
  const [publicToken, setPublicToken] = useState('');
  const [googleClientId, setGoogleClientId] = useState('');

  const handleSubmit = () => {
    const token = publicToken.trim();
    if (!token) return;
    onSubmit(token, googleClientId.trim() || null);
  };

  return (
    <SafeAreaView style={styles.centered}>
      <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
        <Text style={styles.title}>Configure SDK</Text>
        <TextInput
          style={styles.input}
          placeholder="Public Token"
          value={publicToken}
          onChangeText={setPublicToken}
          autoCapitalize="none"
          autoCorrect={false}
        />
        <TextInput
          style={styles.input}
          placeholder="Google Client ID (optional)"
          value={googleClientId}
          onChangeText={setGoogleClientId}
          autoCapitalize="none"
          autoCorrect={false}
        />
        <Button title="Continue" onPress={handleSubmit} disabled={!publicToken.trim()} />
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

// MARK: - Consumer

function ConsumerScreen({ onSwitchDemos }: { onSwitchDemos: () => void }) {
  const stytch = useStytch();
  const authState = useStytchAuthenticationState();
  const [smsStep, setSmsStep] = useState<'phone' | 'code'>('phone');
  const [methodId, setMethodId] = useState('');
  const [inputValue, setInputValue] = useState('');
  const [biometricsAvailability, setBiometricsAvailability] = useState<unknown>(null);
  const [lastResponse, setLastResponse] = useState<string | null>(null);

  const biometricsOptions = {
    sessionDurationMinutes: 30,
    androidBiometricOptions: {
      allowDeviceCredentials: false,
      title: 'Authenticate',
      subTitle: '',
      negativeButtonText: 'Cancel',
    },
    iosBiometricOptions: {
      reason: 'Authenticate',
      fallbackTitle: 'Use Passcode',
      cancelTitle: 'Cancel',
    },
  };

  const refreshBiometrics = async () => {
    try {
      const availability = await stytch.biometrics.getAvailability(biometricsOptions);
      setBiometricsAvailability(availability);
    } catch {
      setBiometricsAvailability(null);
    }
  };

  useEffect(() => {
    refreshBiometrics();
  }, [authState]);

  const statusText = () => {
    if (authState instanceof ConsumerAuthenticationState.Loading) return 'Loading...';
    if (authState instanceof ConsumerAuthenticationState.Authenticated) return 'Welcome Back';
    return 'Please Login';
  };

  const handleSmsSubmit = async () => {
    if (smsStep === 'phone') {
      try {
        const response = await stytch.otp.sms.loginOrCreate({ phoneNumber: inputValue });
        setMethodId(response.methodId);
        setSmsStep('code');
        setLastResponse(JSON.stringify(response, null, 2));
      } catch (e) {
        setLastResponse(String(e));
      } finally {
        setInputValue('');
      }
    } else {
      try {
        const response = await stytch.otp.authenticate({
          token: inputValue,
          methodId,
          sessionDurationMinutes: 5,
        });
        setSmsStep('phone');
        setMethodId('');
        setLastResponse(JSON.stringify(response, null, 2));
      } catch (e) {
        setSmsStep('phone');
        setMethodId('');
        setLastResponse(String(e));
      } finally {
        setInputValue('');
      }
    }
  };

  const handleGoogleOAuth = async () => {
    try {
      const response = await stytch.oauth.google.start({
        loginRedirectUrl: 'my-login-redirect-url',
        signupRedirectUrl: 'my-signup-redirect-url',
        customScopes: null,
        providerParams: null,
        oauthAttachToken: null,
        sessionDurationMinutes: 5,
      });
      setLastResponse(JSON.stringify(response, null, 2));
    } catch (e) {
      setLastResponse(String(e));
    }
  };

  const handleAppleOAuth = async () => {
    try {
      const response = await stytch.oauth.apple.start({
        loginRedirectUrl: 'my-login-redirect-url',
        signupRedirectUrl: 'my-signup-redirect-url',
        customScopes: null,
        providerParams: null,
        oauthAttachToken: null,
        sessionDurationMinutes: 5,
      });
      setLastResponse(JSON.stringify(response, null, 2));
    } catch (e) {
      setLastResponse(String(e));
    }
  };

  const handleBiometrics = async () => {
    try {
      let response;
      if (biometricsAvailability instanceof BiometricsAvailability.Available) {
        response = await stytch.biometrics.register(biometricsOptions);
      } else if (biometricsAvailability instanceof BiometricsAvailability.AlreadyRegistered) {
        response = await stytch.biometrics.authenticate(biometricsOptions);
      } else {
        return;
      }
      setLastResponse(JSON.stringify(response, null, 2));
    } catch (e) {
      setLastResponse(String(e));
    }
    await refreshBiometrics();
  };

  const biometricsLabel = () => {
    if (!biometricsAvailability) return 'Checking Biometrics...';
    if (biometricsAvailability instanceof BiometricsAvailability.Available) return 'Register Biometrics';
    if (biometricsAvailability instanceof BiometricsAvailability.AlreadyRegistered) return 'Authenticate Biometrics';
    return 'Biometrics Unavailable';
  };

  const biometricsEnabled = () =>
    biometricsAvailability instanceof BiometricsAvailability.Available ||
    biometricsAvailability instanceof BiometricsAvailability.AlreadyRegistered;

  const handleSwitchDemos = async () => {
    if (authState instanceof ConsumerAuthenticationState.Authenticated) {
      try {
        await stytch.session.revoke();
      } catch {}
    }
    onSwitchDemos();
  };

  return (
    <SafeAreaView style={styles.flex}>
      <View style={styles.header}>
        <Text style={styles.title}>Stytch Consumer Demo</Text>
        <Text style={styles.subtitle}>{statusText()}</Text>
      </View>

      <ScrollView contentContainerStyle={styles.scrollContent}>
        <Button title="Google Login" onPress={handleGoogleOAuth} />
        <View style={styles.spacer} />
        <Button title="Apple Login" onPress={handleAppleOAuth} />
        <View style={styles.spacer} />

        <View style={styles.row}>
          <TextInput
            style={[styles.input, styles.flex]}
            placeholder={smsStep === 'phone' ? 'Phone Number' : 'Code'}
            value={inputValue}
            onChangeText={setInputValue}
            keyboardType={smsStep === 'phone' ? 'phone-pad' : 'number-pad'}
            autoCorrect={false}
          />
          <Button
            title={smsStep === 'phone' ? 'Send Code' : 'Verify'}
            onPress={handleSmsSubmit}
          />
        </View>
        <View style={styles.spacer} />

        <Button
          title={biometricsLabel()}
          onPress={handleBiometrics}
          disabled={!biometricsEnabled()}
        />
        <View style={styles.spacer} />

        <Button title="SWITCH DEMOS" onPress={handleSwitchDemos} color="red" />
      </ScrollView>

      {lastResponse && (
        <View style={styles.responseContainer}>
          <Text style={styles.responseLabel}>Last response:</Text>
          <ScrollView style={styles.responseScroll}>
            <Text style={styles.responseText}>{lastResponse}</Text>
          </ScrollView>
        </View>
      )}
    </SafeAreaView>
  );
}

// MARK: - B2B

function B2BScreen({ onSwitchDemos }: { onSwitchDemos: () => void }) {
  return (
    <SafeAreaView style={styles.centered}>
      <Text style={styles.title}>B2B — coming soon!</Text>
      <View style={styles.spacer} />
      <Button title="SWITCH DEMOS" onPress={onSwitchDemos} color="red" />
    </SafeAreaView>
  );
}

// MARK: - Styles

const styles = StyleSheet.create({
  flex: { flex: 1 },
  centered: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  header: {
    alignItems: 'center',
    paddingVertical: 12,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#ccc',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  subtitle: {
    fontSize: 15,
    color: '#666',
  },
  scrollContent: {
    padding: 16,
  },
  buttonGroup: {
    width: '100%',
    marginTop: 24,
  },
  spacer: { height: 12 },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 6,
    padding: 10,
    fontSize: 15,
    marginBottom: 12,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  responseContainer: {
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: '#ccc',
    paddingHorizontal: 12,
    paddingTop: 8,
    paddingBottom: 8,
    maxHeight: 160,
  },
  responseLabel: {
    fontSize: 12,
    fontWeight: 'bold',
    marginBottom: 4,
  },
  responseScroll: {
    flex: 1,
  },
  responseText: {
    fontSize: 11,
    fontFamily: Platform.OS === 'ios' ? 'Menlo' : 'monospace',
  },
});
