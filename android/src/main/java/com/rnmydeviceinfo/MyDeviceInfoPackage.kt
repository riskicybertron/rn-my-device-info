package com.rnmydeviceinfo

import com.facebook.react.TurboReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider

class MyDeviceInfoPackage : TurboReactPackage() {

  // Fungsi ini dipanggil oleh React Native untuk mendapatkan instance dari modul kita
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    return if (name == MyDeviceInfoModule.NAME) {
      MyDeviceInfoModule(reactContext)
    } else {
      null
    }
  }

  // Fungsi ini menyediakan metadata tentang modul ke arsitektur baru (Turbo Modules)
  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
    return ReactModuleInfoProvider {
      val moduleInfos = mutableMapOf<String, ReactModuleInfo>()
      val isTurboModule = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
      
      if (isTurboModule) {
        moduleInfos[MyDeviceInfoModule.NAME] = ReactModuleInfo(
          MyDeviceInfoModule.NAME,
          MyDeviceInfoModule.NAME,
          false, // canOverrideExistingModule
          false, // needsEagerInit
          true,  // hasConstants
          false, // isCxxModule
          true   // isTurboModule
        )
      }
      moduleInfos
    }
  }
}