# DashDazz Hyper-Glow (Redmi Turbo 4 Pro / Poco F7)

An advanced Kotlin / Jetpack Compose application controlling the rear camera-ring RGB decorative LED on Xiaomi HyperOS 3 devices (Redmi Turbo 4 Pro / Poco F7, codenames: rodin, diablo) via Root & Shizuku ILightsManager bridge.

Based on [DashDazz](https://github.com/olivia-hye/DashDazz) with full GPL-3.0 compliance.

---

## Key Features & Architecture

### 1. Non-Blocking Priority Matrix
- **Priority 1 (Highest):** Notification Alerts (Temporary Overrides with auto-revert)
- **Priority 2:** Lock / Unlock / Screen Flash Effects
- **Priority 3:** Adaptive Charging Animations & Battery Full Alerts
- **Priority 4:** Special System Events (Pocket / Proximity Mute, Low Battery Warning)
- **Priority 5:** React to Audio (Real-time dynamic visualizer)
- **Priority 6 (Lowest):** Ambient / Always-On Baseline (with "Notif Only" baseline fix)

### 2. Advanced Charging & Battery Engine
- Native `ACTION_POWER_CONNECTED` / `ACTION_POWER_DISCONNECTED` & `BatteryManager` receiver
- Pulse / Breathing speed dynamically scaled with charging wattage (Fast Charge vs Standard)
- Segmented Battery Fill Gauge (0% to 100% circular LED level)
- Smooth Dynamic Gradient from Low (Red) -> Mid (Amber) -> Full (Cyan / Green)
- Zero CPU tight loops: Hardware state updates strictly on battery level changes.

### 3. System Automations
- **DND / Silent Mode Sync:** Automatically dims to 25% or completely mutes ring lighting
- **Pocket & Proximity Guard:** Ultrasonic/Infrared proximity sensor mutes lighting when phone is face down or in pocket
- **Night Schedule Mode:** Configurable quiet hours (e.g., 23:00 to 07:00)

### 4. Modernized "Hyper-Glow" Jetpack Compose UI
- Real-time animated Canvas camera ring header previewing active effects
- Pure AMOLED True Black toggle and 5 neon accents
- 6 Extended Effects alongside original presets:
  - *Spectrum Wave*, *Strobe / Pulse Strobe*, *Candle Flicker*, *Neon Cyberpunk*, *Comet / Chaser*, *Breathe & Fade*
- Custom Sequence Timeline Editor

---

## Automated GitHub Actions CI (No PC Needed)

This repository includes `.github/workflows/android.yml`. Every push to `main` automatically compiles and packages `app-debug.apk` and `app-release.apk` artifacts directly in GitHub Actions.
