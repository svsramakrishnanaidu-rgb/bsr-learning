# Beacon Android companion

This directory contains the companion-app reference implementation. Open it in Android Studio (JDK 17), provide `API_BASE_URL` and the FCM `google-services.json`, and pair from the app while signed into the same verified account. The app never attempts carrier/IMEI location. It uses Android's Fused Location Provider after foreground and background permission are explicitly granted.

Production builds must present a persistent foreground-service notification during frequent lost-mode updates, satisfy Google Play's background-location policy, store the pairing credential in EncryptedSharedPreferences, use certificate pinning where operationally practical, and explain that uninstalling/revoking permissions stops reporting.
