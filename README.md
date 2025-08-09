# App Locker

A comprehensive Android application for locking and protecting selected apps on your device using modern Android development practices.

## 🌟 Features

### 🔐 Security Features
- **Device Security Check**: Verifies device lock screen security (PIN, pattern, password, or biometric)
- **Biometric Authentication**: Uses BiometricPrompt for device-level authentication
- **App Password Protection**: Secure password-based app locking
- **Emergency Password**: 24-hour temporary unlock for all apps
- **Failed Attempt Handling**: Photo capture after 3 failed attempts

### 📱 App Management
- **Comprehensive App List**: Shows all installed applications including system apps
- **Selective Locking**: Choose which apps to protect
- **Real-time Monitoring**: Background service for app usage monitoring
- **Visual Indicators**: Clear lock/unlock status for each app

### 🎨 Modern UI/UX
- **Material Design 3**: Latest Material Design implementation
- **Dark/Light Theme**: Automatic theme switching
- **Jetpack Compose**: Modern declarative UI framework
- **Responsive Design**: Optimized for different screen sizes

## 🏗️ Architecture

### MVVM Pattern
- **Model**: Data models and repository layer
- **View**: Jetpack Compose UI components
- **ViewModel**: Business logic and state management

### Key Components
- **Hilt**: Dependency injection
- **Room**: Local database for locked apps
- **DataStore**: Secure preferences storage
- **Navigation**: Jetpack Compose Navigation
- **WorkManager**: Background tasks for emergency unlock
- **CameraX**: Photo capture functionality
- **BiometricPrompt**: Biometric authentication

## 📋 Requirements

- Android API 24+ (Android 7.0)
- Kotlin 1.9+
- Jetpack Compose
- Hilt for dependency injection

## 🚀 Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd AppLocker
```

### 2. Build and Run
```bash
./gradlew build
./gradlew installDebug
```

### 3. Required Permissions
The app requires the following permissions:
- `PACKAGE_USAGE_STATS`: Monitor app usage
- `CAMERA`: Capture photos on failed attempts
- `READ/WRITE_EXTERNAL_STORAGE`: Save photos to gallery
- `FOREGROUND_SERVICE`: Background monitoring
- `SYSTEM_ALERT_WINDOW`: Overlay for password prompts

## 📁 Project Structure

```
app/src/main/java/com/dungz/applocker/
├── data/
│   ├── database/          # Room database components
│   ├── model/            # Data models
│   └── repository/       # Repository layer
├── di/                   # Hilt dependency injection
├── service/              # Background services
├── ui/
│   ├── navigation/       # Navigation components
│   ├── screens/          # UI screens
│   ├── theme/           # Theme and styling
│   └── viewmodel/       # ViewModels
├── util/                # Utility classes
├── worker/              # WorkManager workers
└── MainActivity.kt      # Main activity
```

## 🔧 Key Features Implementation

### Security Check Flow
1. App launches and checks device security
2. Prompts for biometric authentication if available
3. Checks if app password is set
4. Navigates to password setup or main screen

### App Locking Process
1. User selects apps to lock from comprehensive list
2. Apps are stored in encrypted Room database
3. Background service monitors app usage
4. Password prompt appears when locked app is accessed

### Emergency Password System
1. Optional emergency password setup
2. 24-hour unlock period using WorkManager
3. Automatic reset after time expires

### Failed Attempt Handling
1. Tracks failed password attempts
2. Takes photo after 3 failed attempts
3. Saves photo to gallery with timestamp

## 🎨 Theme System

### Color Scheme
- **Light Theme**: Professional blue-based color scheme
- **Dark Theme**: Dark mode with proper contrast
- **Custom Colors**: Success, warning, danger, and info states

### Dimensions
- **Centralized Dimen class**: Consistent spacing and sizing
- **Responsive design**: Adapts to different screen sizes
- **Material Design**: Follows Material Design guidelines

## 🔒 Security Considerations

- **Encrypted Storage**: Passwords stored securely using DataStore
- **Biometric Integration**: Uses Android's BiometricPrompt API
- **Permission Handling**: Proper runtime permission management
- **Background Monitoring**: Secure foreground service implementation

## 🧪 Testing

The application includes:
- Unit tests for ViewModels
- Integration tests for repository layer
- UI tests for critical user flows

## 📱 Usage

1. **First Launch**: Complete security check and password setup
2. **App Selection**: Choose which apps to protect
3. **Daily Use**: Enter password when accessing locked apps
4. **Emergency**: Use emergency password for 24-hour unlock
5. **Settings**: Manage passwords and app protection

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the code comments

---

**Note**: This is a demonstration application. For production use, additional security measures and thorough testing would be required. 