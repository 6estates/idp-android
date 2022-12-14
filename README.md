# 6Estates idp-android

Android SDK for communicating with the 6Estates Intelligent Document Processing(IDP) Platform.

## Download

1. Add repository to build.gradle of root directory and settings.gradle

```sh
buildscript {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
    ...
}
```

```sh
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
...
```

2. Add dependency

```sh
dependencies {
    implementation 'com.sixestates:idp-android:1.0.2'
}
```

Note: due to the use of JNI, please do not confuse

```sh
-keep class me.pqpo.smartcropperlib.**{*;}
```

## Permission

```sh
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission 
    android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
<uses-permission
    android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
    tools:ignore="ScopedStorage" />
```

## Usage

A simple use case is shown below:

1. Get IDP Authorization

You can get Client ID, Client Secret on the IDP Platform. Then register in AndroidManifest.xml.
   
```sh
<application>
  ...
  <meta-data
    android:name="com.sixestates.client.id"
    android:value="Your Client ID"/>

  <meta-data
    android:name="com.sixestates.client.secret"
    android:value="Your Client Secret"/>
  ...
</application>
```

2. Initialize in application

```sh
import com.sixe.idplib.Idp;

Idp.init(this);
```

3. Submit a PDF task

```sh
// Path is absolute path of PDF
TaskInfo taskInfo = new TaskInfo.Builder()
                      .filePath(path)
                      .fileType(FileMimeType.FILE_BANK_STATEMENT)
                      .hitl(false)
                      .build();

ExtractSubmitter.submitPdf(taskInfo, new TaskCallback() {
    @Override
    public void success(String response) {
        // Submit succeed return json string
    }

    @Override
    public void failure(String error) {
        // Submit failed return error message
    }
});
```

4. Submit multiple pictures task

```sh
// photosPath is absolute path list of multiple pictures
TaskInfo taskInfo = new TaskInfo.Builder()
                    .imagePaths(photosPath)
                    .fileType(FileMimeType.FILE_BANK_STATEMENT)
                    .hitl(false)
                    .build();
ExtractSubmitter.submitImages(taskInfo, new TaskCallback() {
    @Override
    public void success(String response) {
        // Submit succeed return json string
    }

    @Override
    public void failure(String error) {
        // Submit failed return error message
    }
});
```

5. Query the extraction result with the task id

- get json result

```sh
ResultExtractor.extractResultByTaskId(id, new ResultCallback() {
    @Override
    public void success(String data) {
        // Query succeed return the result
    }

    @Override
    public void failure(String error) {
        // Query failed return error message
    }
});
```

- get excel file

```sh
ResultExtractor.getTaskExcel(context,id, new ResultCallback() {
    @Override
    public void success(String data) {
        // succeed return the excel path
    }

    @Override
    public void failure(String error) {
        // failed return error message
    }
});
```

6. Send Excel of the result by email

```sh

ResultExtractor.sendEmail(context, id, title, email, new ResultCallback() {
    @Override
    public void success(String data) {
        // Send succeed return the message
    }

    @Override
    public void failure(String error) {
        // Send failed return error message
    }
});
```

7. Use the image Crop

```sh
Intent intent = new Intent(UploadActivity.this, CropActivity.class);
intent.putExtra("tempImage", currentPhotoPath);// absolute path
startActivityForResult(intent, requestCode);

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // get path after crop
    String imgPath = data.getStringExtra("imgPath");    
}
```

8. Use the image Rotate

```sh
ImageTools.rotateBitmap(bitmap, angle)
```
