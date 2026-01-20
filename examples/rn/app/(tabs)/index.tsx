import { Button, Text, TextInput, View } from 'react-native';
import { ConsumerAuthenticationState, OtpAuthenticateRequest, OtpSmsLoginOrCreateRequest, useStytch, useStytchAuthenticationState } from "@stytch/react-native-consumer"
import { useState } from 'react';
import { SafeAreaView } from 'react-native-safe-area-context';

enum Step {
  SUBMIT_PHONE_NUMBER,
  SUBMIT_TOKEN,
}

export default function HomeScreen() {
  const authenticationState = useStytchAuthenticationState()
  if (authenticationState == ConsumerAuthenticationState.Loading) {
    return (
      <SafeAreaView>
          <Text>Loading...</Text>
      </SafeAreaView>
    )
  }
  if (authenticationState == ConsumerAuthenticationState.Authenticated) {
    return <AuthenticatedView />
  }
  return <UnauthenticatedView />
}

const AuthenticatedView = () => {
  const stytch = useStytch()
  const logout = async () => {
    await stytch.session.revoke()
  }
  return (
    <SafeAreaView>
        <Text>Authenticated!</Text>
        <Button onPress={logout} title='Logout'></Button>
    </SafeAreaView>
    )
}

const UnauthenticatedView = () => {
  const [step, setStep] = useState<Step>(Step.SUBMIT_PHONE_NUMBER);
  const [inputValue, setInputValue] = useState("");
  const [methodId, setMethodId] = useState("");
  const stytch = useStytch()
  const handleSubmit = async () => {
    if (step == Step.SUBMIT_PHONE_NUMBER) {
      const request = new OtpSmsLoginOrCreateRequest(inputValue, 5);
      stytch.otp.sms.loginOrCreate(request)
        .then((response) => {
          console.log(response)
          setMethodId(response.methodId)
          setStep(Step.SUBMIT_TOKEN)
        })
        .catch(console.error)
        .finally(() => setInputValue(''))
    } else {
      const request = new OtpAuthenticateRequest(inputValue, methodId, 5);
      stytch.otp.authenticate(request)
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
    <SafeAreaView>
        <Text>Testing SMS OTP...</Text>
        <TextInput onChangeText={setInputValue} value={inputValue} placeholder={placeholderText} style={{ margin: 8, borderWidth: 1 }} placeholderTextColor={'#000'} />
        <Button onPress={handleSubmit} title="Submit"></Button>
    </SafeAreaView>
  );
}
