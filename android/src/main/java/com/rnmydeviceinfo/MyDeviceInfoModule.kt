package com.rnmydeviceinfo

// Pastikan semua import ini ada dan benar
import android.os.Build
import android.provider.Settings
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.app.ActivityManager
import java.util.UUID

// File ini akan dibuat otomatis oleh Codegen setelah newArchEnabled=true
import com.rnmydeviceinfo.NativeMyDeviceInfoSpec

class MyDeviceInfoModule(private val context: ReactApplicationContext) : NativeMyDeviceInfoSpec(context) {

  override fun getName() = NAME

  @ReactMethod
  override fun getDeviceModel(promise: Promise) {
    promise.resolve(Build.MODEL)
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  override fun getSystemVersion(): String {
    return Build.VERSION.RELEASE
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  override fun getApplicationName(): String {
    val applicationInfo = context.applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
  }

  // == FUNGSI YANG DIPERBAIKI ==
  @ReactMethod(isBlockingSynchronousMethod = true)
  override fun getVersion(): String {
    return try {
      val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
      packageInfo.versionName ?: "unknown" // Menggunakan Elvis operator untuk handle null
    } catch (e: Exception) {
      "unknown"
    }
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  override fun getBuildNumber(): String {
    return try {
      val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo.longVersionCode.toString()
      } else {
        @Suppress("DEPRECATION")
        packageInfo.versionCode.toString()
      }
    } catch (e: Exception) {
      "unknown"
    }
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  override fun getBrand(): String {
    return Build.BRAND
  }

  @ReactMethod
  override fun getManufacturer(promise: Promise) {
    promise.resolve(Build.MANUFACTURER)
  }

  @ReactMethod
  override fun getUniqueId(promise: Promise) {
    val uniqueId = "ID:" + Build.BOARD + Build.BRAND + Build.DEVICE + Build.HARDWARE + Build.MANUFACTURER + Build.MODEL + Build.PRODUCT
    promise.resolve(UUID.nameUUIDFromBytes(uniqueId.toByteArray()).toString())
  }

  @ReactMethod
  override fun getAndroidId(promise: Promise) {
    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    promise.resolve(androidId ?: "unknown")
  }

  @ReactMethod
  override fun isEmulator(promise: Promise) {
    val isEmulator = (Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || "google_sdk" == Build.PRODUCT)
    promise.resolve(isEmulator)
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  override fun isTablet(): Boolean {
    val metrics = context.resources.displayMetrics
    val widthDp = metrics.widthPixels / metrics.density
    val heightDp = metrics.heightPixels / metrics.density
    val screenDiagonal = Math.sqrt((widthDp * widthDp) + (heightDp * heightDp).toDouble())
    return screenDiagonal >= 7.0
  }

  @ReactMethod
  override fun getApiLevel(promise: Promise) {
    promise.resolve(Build.VERSION.SDK_INT)
  }

  @ReactMethod
  override fun getBatteryLevel(promise: Promise) {
    val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    if (level == -1 || scale == -1) {
      promise.resolve(-1.0)
      return
    }
    promise.resolve((level.toFloat() / scale.toFloat()).toDouble())
  }

  @ReactMethod
  override fun isBatteryCharging(promise: Promise) {
    val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    promise.resolve(isCharging)
  }
  
  @ReactMethod
  override fun getIpAddress(promise: Promise) {
    try {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        promise.resolve(ipAddress)
    } catch (e: Exception) {
        promise.reject("E_IP_ADDRESS", "Gagal mendapatkan alamat IP", e)
    }
  }

  @ReactMethod
  override fun getTotalMemory(promise: Promise) {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    promise.resolve(memoryInfo.totalMem.toDouble())
  }
  
  @ReactMethod
  override fun getUsedMemory(promise: Promise) {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)
    val usedMemory = memoryInfo.totalMem - memoryInfo.availMem
    promise.resolve(usedMemory.toDouble())
  }

  companion object {
    const val NAME = "RNMyDeviceInfo"
  }
}