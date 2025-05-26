
# HERSAFEZONE Android Project Structure

## Package: com.laxnar.hersafezone

### Project Tree:
```
android/
├── settings.gradle
├── build.gradle
├── app/
│   ├── build.gradle (with google-services plugin applied)
│   ├── proguard-rules.pro
│   ├── google-services.json (to be placed here before Gradle sync)
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── res/
│           │   ├── values/
│           │   │   ├── strings.xml
│           │   │   └── themes.xml
│           │   └── xml/
│           │       ├── backup_rules.xml
│           │       └── data_extraction_rules.xml
│           └── kotlin/
│               └── com/
│                   └── laxnar/
│                       └── hersafezone/
│                           ├── MainActivity.kt
│                           ├── ui/
│                           │   ├── HerSafeZoneApp.kt
│                           │   ├── theme/
│                           │   │   ├── Theme.kt
│                           │   │   └── Type.kt
│                           │   └── screens/
│                           │       ├── OnboardingScreen.kt
│                           │       ├── HomeScreen.kt
│                           │       └── LiveMapScreen.kt
│                           ├── data/
│                           │   └── FirestoreSchema.kt
│                           └── service/
│                               └── FCMService.kt
```

### Configuration Details:
- **Min SDK**: 26
- **Target SDK**: 34
- **Package Name**: com.laxnar.hersafezone
- **App Name**: HERSAFEZONE

### Dependencies Included:
- Firebase BOM (32.7.0)
- Firebase Auth, Firestore, Messaging
- Kotlin Coroutines Android
- Lifecycle Runtime KTX
- Activity Compose
- Jetpack Compose (1.5.4)
- Navigation Compose
- Maps Compose
- GeoHash library (ch.hsr:geohash)
- Accompanist Permissions

### Navigation Routes:
1. **OnboardingScreen** - Welcome and setup screen
2. **HomeScreen** - Main dashboard
3. **LiveMapScreen** - Real-time location tracking

### Key Features Setup:
- Firebase integration ready (place google-services.json in app/)
- FCM service for push notifications
- Firestore schema with predefined constants
- Location permissions configured
- Material 3 theme applied
- Navigation between screens implemented

### Next Steps:
1. Place your google-services.json file in android/app/
2. Run `npx cap add android`
3. Run `npx cap sync`
4. Open in Android Studio and build
