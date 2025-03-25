# 📷 Android Gallery App

An Android Gallery app that allows users to browse and manage images on their device. Users can view all phone images, capture new photos using the camera, and set images as wallpaper.

## ✨ Features

- 📂 **Display All Phone Images**: Automatically loads and displays all images from the device.
- 📸 **Capture Images**: Opens the camera, captures images, and saves them to the device gallery.
- 🖼 **Set Image as Wallpaper**: Allows users to set an image as their device wallpaper.
- 📥 **Persistent Image Storage**: Stores image paths and reloads them on app restart.
- 🎚 **Smooth User Experience**: Uses RecyclerView for fast and efficient image loading.

---

## 📜 Permissions Used

This app requires the following permissions:

- `CAMERA` → To capture new images using the device camera.
- `READ_EXTERNAL_STORAGE` → To read and display images from the device.
- `WRITE_EXTERNAL_STORAGE` → To save captured images (Required for Android versions below 10).
- `READ_MEDIA_IMAGES` → For Android 11+ versions.

For Android 10 (API 29) and above, `MediaStore` API is used to save images directly in the `Pictures` folder.

---

## 🛠 Technologies Used

- **Language**: Java (App Logic), Kotlin (UI using Jetpack Compose)
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI**: Jetpack Compose & RecyclerView
- **Dependency Injection**: Hilt
- **Networking**: Ktor

---

## 🚀 Getting Started

### 🔹 Prerequisites
- Android Studio Installed
- Java & Kotlin Knowledge (Basic)
- An Android Device or Emulator

### 🔹 Installation Steps

1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/android-gallery-app.git
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and Build the project.
4. Run the app on an **Emulator** or a **Real Device**.

---

## 📂 Project Structure

```
📦 android-gallery-app
├── 📂 app
│   ├── 📂 src
│   │   ├── 📂 main
│   │   │   ├── 📂 java/com/yourpackage/gallery
│   │   │   │   ├── HomeActivity.java  # Main activity showing images
│   │   │   │   ├── CameraActivity.java # Handles camera operations
│   │   │   │   ├── GalleryAdapter.java # RecyclerView Adapter
│   │   │   │   ├── ImageViewActivity.java # Fullscreen Image View
│   │   │   ├── 📂 res
│   │   │   │   ├── 📂 layout
│   │   │   │   │   ├── activity_home.xml
│   │   │   │   │   ├── activity_camera.xml
│   │   │   │   │   ├── activity_image_view.xml
│   │   │   │   ├── 📂 drawable
│   │   │   │   ├── 📂 values
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── themes.xml
```

---

## 🐛 Troubleshooting

### ❌ App Restarts After Taking a Photo
- Ensure you're using `MediaStore` API instead of `FileProvider`.
- Use `cameraLauncher` instead of `startActivityForResult()`.

### ❌ Images Not Displaying
- Ensure `READ_EXTERNAL_STORAGE` permission is granted.
- Check if images are being saved to `/storage/emulated/0/Pictures/`.

### ❌ Camera Permission Not Triggering Camera
- Ensure `requestPermissionLauncher` handles `CAMERA` permission properly.
- After permission is granted, open the camera immediately without requiring the user to click again.

---
