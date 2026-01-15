import { DarkTheme, DefaultTheme, ThemeProvider } from '@react-navigation/native';
import { Stack } from 'expo-router';
import { StatusBar } from 'expo-status-bar';
import 'react-native-reanimated';

import { useColorScheme } from '@/hooks/use-color-scheme';

import { createStytchConsumer, StytchClientConfiguration, StytchProvider } from "@stytch/react-native-consumer"

export const unstable_settings = {
  anchor: '(tabs)',
};


const STYTCH_PUBLIC_TOKEN = process.env.EXPO_PUBLIC_STYTCH_PUBLIC_TOKEN as string;
const stytchConfig = new StytchClientConfiguration(STYTCH_PUBLIC_TOKEN);
const stytchConsumerClient = createStytchConsumer(stytchConfig);

export default function RootLayout() {
  const colorScheme = useColorScheme();
  return (
    <StytchProvider stytch={stytchConsumerClient}>
      <ThemeProvider value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
        <Stack>
          <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
          <Stack.Screen name="modal" options={{ presentation: 'modal', title: 'Modal' }} />
        </Stack>
        <StatusBar style="auto" />
      </ThemeProvider>
    </StytchProvider>
  );
}
