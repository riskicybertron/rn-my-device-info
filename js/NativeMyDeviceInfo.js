// @flow
import type { TurboModule } from 'react-native/Libraries/TurboModule/RCTExport';
import { TurboModuleRegistry } from 'react-native';

// Menentukan fungsi apa saja yang akan diekspos dari native
export interface Spec extends TurboModule {
  // --- Fungsi yang sudah ada ---
  +getDeviceModel: () => Promise<string>;
  +getSystemVersion: () => string;

  // --- Fungsi Baru dari Daftar ---

  // Info Aplikasi
  +getApplicationName: () => string;
  +getVersion: () => string;
  +getBuildNumber: () => string;

  // Info Perangkat Keras & ID
  +getBrand: () => string;
  +getManufacturer: () => Promise<string>;
  +getUniqueId: () => Promise<string>;
  +getAndroidId: () => Promise<string>;
  +isEmulator: () => Promise<boolean>;
  +isTablet: () => boolean;

  // Info OS
  +getApiLevel: () => Promise<number>;

  // Info Baterai
  +getBatteryLevel: () => Promise<number>;
  +isBatteryCharging: () => Promise<boolean>;

  // Info Jaringan & Penyimpanan
  +getIpAddress: () => Promise<string>;
  +getTotalMemory: () => Promise<number>;
  +getUsedMemory: () => Promise<number>;
}

// Mendaftarkan modul agar bisa diakses oleh React Native
export default (TurboModuleRegistry.get<Spec>('RNMyDeviceInfo'): ?Spec);