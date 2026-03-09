import { Button, Text, TextInput, View } from 'react-native';
import { ConsumerAuthenticationState, useStytch, useStytchAuthenticationState } from "@stytch/react-native-consumer"
import { useState } from 'react';
import { SafeAreaView } from 'react-native-safe-area-context';

enum Step {
  SUBMIT_PHONE_NUMBER,
  SUBMIT_TOKEN,
}

export default function HomeScreen() {
  const authenticationState = useStytchAuthenticationState()
  
  if (authenticationState instanceof ConsumerAuthenticationState.Loading) {
    return (
      <SafeAreaView>
          <Text>Loading...</Text>
      </SafeAreaView>
    )
  }
  if (authenticationState instanceof ConsumerAuthenticationState.Authenticated) {
    return <AuthenticatedView />
  }
  return <UnauthenticatedView />
}


const biometricsOptions = {
  sessionDurationMinutes: 30,
  androidBiometricOptions: {
    allowDeviceCredentials: false,
    allowFallbackToCleartext: false,
    title: "Hello",
    subTitle: "Use your finger!",
    negativeButtonText: "NO"
  },
  iosBiometricOptions: {
    reason: "Use your face!",
    fallbackTitle: "Or pin?",
    cancelTitle: "Or don't"
  },
};

const passkeysOptions = { domain: "stytch.com", preferImmediatelyAvailableCredentials: true, sessionDurationMinutes: 30};

const AuthenticatedView = () => {
  const stytch = useStytch()
  const logout = async () => {
    await stytch.session.revoke()
  }
  const authenticateWithBiometrics = async () => {
    stytch.biometrics.authenticate(biometricsOptions).then(console.log).catch(console.error)
  };
  const registerBiometrics = async () => {
    stytch.biometrics.register(biometricsOptions).then(console.log).catch(console.error)
  };
  const registerPasskeys = async () => {
    stytch.passkeys.register(passkeysOptions).then(console.log).catch(console.error)
  };
  const authenticatePasskeys = async () => {
    stytch.passkeys.authenticate(passkeysOptions).then(console.log).catch(console.error)
  };
  return (
    <SafeAreaView>
        <Text>Authenticated!</Text>
        <Button onPress={registerBiometrics} title="Register Biometrics"></Button>
        <Button onPress={authenticateWithBiometrics} title="Authenticate Biometrics"></Button>
        <Button onPress={registerPasskeys} title="Register Passkey"></Button>
        <Button onPress={authenticatePasskeys} title="Authenticate Passkey"></Button>
        <Button onPress={logout} title='Logout'></Button>
    </SafeAreaView>
    )
}

const UnauthenticatedView = () => {
  const stytch = useStytch()
  const authenticateWithBiometrics = async () => {
    stytch.biometrics.authenticate(biometricsOptions).then(console.log).catch(console.error)
  };
  const deleteBiometrics = async () => {
    stytch.biometrics.removeRegistration().then(console.log).catch(console.error);
  };
  const authenticatePasskeys = async () => {
    stytch.passkeys.authenticate(passkeysOptions).then(console.log).catch(console.error)
  };
  return (
    <SafeAreaView>
        <SMSOTPVIew />
        <OAuthView />
        <Button onPress={authenticateWithBiometrics} title="Authenticate Biometrics"></Button>
        <Button onPress={deleteBiometrics} title="Delete Biometrics"></Button>
        <Button onPress={authenticatePasskeys} title="Authenticate Passkey"></Button>
    </SafeAreaView>
  );
}

const SMSOTPVIew = () => {
  const [step, setStep] = useState<Step>(Step.SUBMIT_PHONE_NUMBER);
  const [inputValue, setInputValue] = useState("");
  const [methodId, setMethodId] = useState("");
  const stytch = useStytch()
  const handleSubmit = async () => {
    if (step == Step.SUBMIT_PHONE_NUMBER) {
      stytch.otp.sms.loginOrCreate({ phoneNumber: inputValue })
        .then((response) => {
          console.log(response)
          setMethodId(response.methodId)
          setStep(Step.SUBMIT_TOKEN)
        })
        .catch(console.error)
        .finally(() => setInputValue(''))
    } else {
      stytch.otp.authenticate({ token: inputValue, methodId: methodId, sessionDurationMinutes: 5})
        .then(console.log)
        .catch(console.error)
        .finally(() => setInputValue(''))
    }
  };
  let placeholderText = "Phone Number"
  if (step == Step.SUBMIT_TOKEN) {
    placeholderText = "Code"
  }
  return (
    <>
      <Text>Testing SMS OTP...</Text>
      <TextInput onChangeText={setInputValue} value={inputValue} placeholder={placeholderText} style={{ margin: 8, borderWidth: 1 }} placeholderTextColor={'#000'} />
      <Button onPress={handleSubmit} title="Submit"></Button>
    </>
  )
}

const OAuthView = () => {
  const stytch = useStytch()
  const oauthStartParameters = {
    loginRedirectUrl: "com.stytch.mobile.demo://oauth",
    signupRedirectUrl: "com.stytch.mobile.demo://oauth",
    customScopes: null,
    providerParams: null,
    oauthAttachToken: null,
    sessionDurationMinutes: 5
  };
  const triggerGoogleOAuth = async () => stytch.oauth.google.start(oauthStartParameters).then(console.log).catch(console.error);
  const triggerAppleOAuth = async () => stytch.oauth.apple.start(oauthStartParameters).then(console.log).catch(console.error);
  return (
    <>
      <Text>Testing OAuth...</Text>
      <Button onPress={triggerGoogleOAuth} title="Google OAuth"></Button>
      <Button onPress={triggerAppleOAuth} title="Apple OAuth"></Button>
    </>
  )
}