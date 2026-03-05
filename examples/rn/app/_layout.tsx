import { DarkTheme, DefaultTheme, ThemeProvider } from '@react-navigation/native';
import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import 'react-native-reanimated';
import { SafeAreaProvider } from 'react-native-safe-area-context'
import { useColorScheme } from '@/hooks/use-color-scheme';

import { createStytchConsumer, StytchClientConfiguration, StytchProvider } from "@stytch/react-native-consumer"

export const unstable_settings = {
  anchor: '(tabs)',
};


const STYTCH_PUBLIC_TOKEN = process.env.EXPO_PUBLIC_STYTCH_PUBLIC_TOKEN as string;
const stytchConfig = new StytchClientConfiguration(STYTCH_PUBLIC_TOKEN, undefined, 5, {
  googleClientId: '',
  autoSelectEnabled: false,
});
const stytchConsumerClient = createStytchConsumer(stytchConfig);

export default function RootLayout() {
  const colorScheme = useColorScheme();
  return (
    <StytchProvider stytch={stytchConsumerClient}>
        <SafeAreaProvider>
          <Stack>
            <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
          </Stack>
          <StatusBar style="auto" />
        </SafeAreaProvider>
    </StytchProvider>
  );
}
