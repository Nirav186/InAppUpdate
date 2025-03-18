# 📲 In-App Update Manager

A lightweight Android library for handling **Google Play In-App Updates** seamlessly.  
Supports both **Flexible** and **Immediate** updates with minimal setup.

## ✨ Features

✅ Supports **Flexible** and **Immediate** updates  
✅ Auto-resume updates when the app is restarted  
✅ Handles **update failures** and **cancellation** gracefully  
✅ **Minimal setup** – just call `init()`  
✅ Written in **Kotlin** with Coroutine support  

---

## 🚀 Installation

### Step 1️⃣: Add Dependency  
Add the following dependency to your **app-level `build.gradle`**:

```gradle
dependencies {
    implementation 'com.yourpackage:inappupdate:1.0.0'
}
```

For **Gradle (Kotlin DSL)**:

```kotlin
dependencies {
    implementation("com.yourpackage:inappupdate:1.0.0")
}
```

---

## 🛠️ Usage

### Step 1️⃣: Initialize in `onCreate()`
Call `InAppUpdateManager.init()` in your **SplashActivity** or any entry-point activity:

```kotlin
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        InAppUpdateManager.init(this, isForceUpdate = true) {
            startNextActivity() // Start main activity after update
        }
    }
}
```

---

### Step 2️⃣: Create a `BaseActivity` (Optional, Recommended)
To avoid writing `resumeUpdate()`, `destroyUpdate()`, and `handleResult()` in every activity, create a **BaseActivity**:

```kotlin
open class BaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        InAppUpdateManager.resumeUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        InAppUpdateManager.destroyUpdate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        InAppUpdateManager.handleResult(requestCode, resultCode)
    }
}
```

Then, simply **extend `BaseActivity`** in all activities where you want updates:

```kotlin
class MainActivity : BaseActivity() {
    // Your Activity Code
}
```

---

## 🔧 API Methods

| Method | Description |
|--------|------------|
| `init(activity: Activity, isForceUpdate: Boolean, startFlow: () -> Unit)` | Initializes the in-app update manager. If `isForceUpdate = true`, it forces an **Immediate** update. |
| `resumeUpdate()` | Resumes an ongoing update when the app is reopened. |
| `destroyUpdate()` | Cleans up update listeners to prevent memory leaks. |
| `handleResult(requestCode: Int, resultCode: Int)` | Handles the result of the update flow. |

---

## 📷 Screenshots

### 🔹 **Flexible Update (User can postpone)**
![Flexible Update](https://developer.android.com/static/images/app-bundle/flexible_flow.png)

### 🔹 **Immediate Update (Blocks UI until updated)**
![Immediate Update](https://developer.android.com/static/images/app-bundle/immediate_flow.png)

---

## 📄 License

```
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
```
---

## 💬 Need Help?

If you have any issues or feature requests, feel free to open an **issue** or submit a **pull request**! 🚀
