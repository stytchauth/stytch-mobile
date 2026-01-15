import { TurboModule, TurboModuleRegistry } from 'react-native';
import { DeviceInfo } from '../lib/@stytch/react-native-consumer.mjs'

export interface Spec extends TurboModule {
  getDeviceInfo(): Promise<DeviceInfo>;
  saveData(key: string, data: string): Promise<void>;
  getData(key: string): Promise<string|undefined>;
  removeData(key: string): Promise<void>;
};

// create an instance of the module
const module = TurboModuleRegistry.get<Spec>('StytchBridge');

// export it for use within the RN/JS side
export default module;

// This is where the magic happens. Expose a global var with a name/shape that we told Kotlin about, and it "just works"
declare global {
  var StytchBridge: Spec;
}
global.StytchBridge = module!;