package io.agora.chat.uikit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;


import java.io.File;
import java.io.FileOutputStream;

import io.agora.chat.ChatClient;
import io.agora.chat.uikit.R;
import io.agora.util.EMLog;
import io.agora.util.PathUtil;
import io.agora.util.VersionUtils;

/**
 * Created by zhangsong on 18-6-6.
 */

public class EaseCompat {
    private static final String TAG = "EaseCompat";

    public static void openImage(Activity context, int requestCode) {
        Intent intent = getOpenImageIntent(context);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * Open the system album.
     * @param context
     * @param requestCode
     * @deprecated Use {@link #openImage(ActivityResultLauncher, Context)} instead.
     */
    @Deprecated
    public static void openImage(Fragment context, int requestCode) {
        Intent intent = getOpenImageIntent(context.getActivity());
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * Open the system album.
     * @param launcher
     * @param context
     */
    public static void openImage(ActivityResultLauncher<Intent> launcher, Context context) {
        if(launcher != null) {
            launcher.launch(getOpenImageIntent(context));
        }
    }

    private static Intent getOpenImageIntent(Context context) {
        Intent intent = null;
        if(VersionUtils.isTargetQ(context)) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }else {
            if (Build.VERSION.SDK_INT < 19) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            } else {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            }
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setType("image/*");
        return intent;
    }

    /**
     * take picture by set file path
     * @param context
     * @param requestCode
     * @return
     */
    public static File takePicture(Activity context, int requestCode) {
        if(!EaseUtils.isSdcardExist()) {
            return null;
        }
        File cameraFile = getCameraFile();
        Intent intent = getCameraIntent(context, cameraFile);
        context.startActivityForResult(intent, requestCode);
        return cameraFile;
    }

    /**
     * take picture by set file path
     * @param context
     * @param requestCode
     * @return
     */
    public static File takePicture(Fragment context, int requestCode) {
        if(!EaseUtils.isSdcardExist()) {
            return null;
        }
        File cameraFile = getCameraFile();
        Intent intent = getCameraIntent(context.getContext(), cameraFile);
        context.startActivityForResult(intent, requestCode);
        return cameraFile;
    }

    /**
     * take video capture by set file path
     * @param context
     * @param requestCode
     * @return
     */
    public static File takeVideo(Activity context, int requestCode) {
        if(!EaseUtils.isSdcardExist()) {
            return null;
        }
        File videoFile = getVideoFile();
        Intent intent = getVideoIntent(context, videoFile);
        context.startActivityForResult(intent, requestCode);
        return videoFile;
    }

    /**
     * take video capture by set file path
     * @param context
     * @param requestCode
     * @return
     */
    public static File takeVideo(Fragment context, int requestCode) {
        if(!EaseUtils.isSdcardExist()) {
            return null;
        }
        File videoFile = getVideoFile();
        Intent intent = getVideoIntent(context.getContext(), videoFile);
        context.startActivityForResult(intent, requestCode);
        return videoFile;
    }

    private static Intent getCameraIntent(Context context, File cameraFile) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(context, cameraFile));
        return intent;
    }

    private static File getCameraFile() {
        File cameraFile = new File(PathUtil.getInstance().getImagePath()
                , ChatClient.getInstance().getCurrentUser() + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();
        return cameraFile;
    }

    private static Intent getVideoIntent(Context context, File videoFile) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, EaseCompat.getUriForFile(context, videoFile));
        return intent;
    }

    private static File getVideoFile() {
        File videoFile = new File(PathUtil.getInstance().getVideoPath()
                , System.currentTimeMillis() + ".mp4");
        //noinspection ResultOfMethodCallIgnored
        videoFile.getParentFile().mkdirs();
        return videoFile;
    }

    /**
     * Open file
     *
     * @param f
     * @param context
     */
    public static void openFile(File f, Activity context) {
        openFile(context, f);
    }

    /**
     * Open file
     * @param context
     * @param filePath
     */
    public static void openFile(Context context, String filePath) {
        if(TextUtils.isEmpty(filePath) || !new File(filePath).exists()) {
            EMLog.e(TAG, "File does not exist！");
            return;
        }
        openFile(context, new File(filePath));
    }

    /**
     * Open file
     * @param context
     * @param file
     */
    public static void openFile(Context context, File file) {
        if(file == null || !file.exists()) {
            EMLog.e(TAG, "Cannot open the file, because the file is not exit, file: "+file);
            return;
        }
        String filename = file.getName();
        String mimeType = getMimeType(context, file);
        /* get uri */
        Uri uri = getUriForFile(context, file);
        //To solve the local video file can not open the problem
//        if(isVideoFile(context, filename)) {
//            uri = Uri.parse(file.getAbsolutePath());
//        }
        openFile(context, uri, filename, mimeType);
    }

    /**
     * Open file
     * @param context
     * @param uri
     */
    public static void openFile(Context context, Uri uri) {
        if(!EaseFileUtils.isFileExistByUri(context, uri)) {
            EMLog.e(TAG, "Cannot open the file, because the file is not exit, uri: "+uri);
            return;
        }
        String filePath = EaseFileUtils.getFilePath(context, uri);
        if(!TextUtils.isEmpty(filePath) && new File(filePath).exists()) {
            openFile(context, new File(filePath));
            return;
        }
        String filename = getFileNameByUri(context, uri);
        String mimeType = getMimeType(context, filename);
        openFile(context, uri, filename, mimeType);
    }

    /**
     * Open file
     * @param context
     * @param uri
     * @param filename
     * @param mimeType
     */
    private static void openFile(Context context, Uri uri, String filename, String mimeType) {
        if(openApk(context, uri, filename)) {
            return;
        }
        EMLog.d(TAG, "openFile filename = "+filename + " mimeType = "+mimeType);
        EMLog.d(TAG, "openFile uri = "+ (uri != null ? uri.toString() : "uri is null"));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        setIntentByType(context, filename, intent);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        /* set intent's file and MimeType */
        intent.setDataAndType(uri, mimeType);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            EMLog.e(TAG, e.getMessage());
            Toast.makeText(context, "Can't find proper app to open this file", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Delete file
     * @param context
     * @param uri
     */
    public static void deleteFile(Context context, Uri uri) {
        EaseFileUtils.deleteFile(context, uri);
    }

    public static Uri getUriForFile(Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static int getSupportedWindowType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }

    /**
     * Gets the first frame of the video
     * @param context
     * @param videoUri
     * @return
     */
    public static String getVideoThumbnail(Context context, @NonNull Uri videoUri) {
        File file = new File(PathUtil.getInstance().getVideoPath(), "thvideo" + System.currentTimeMillis());
        try {
            FileOutputStream fos = new FileOutputStream(file);
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(context, videoUri);
            Bitmap frameAtTime = media.getFrameAtTime();
            frameAtTime.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }

    /**
     * Used to check whether a file obtained from multimedia is a video
     * @param context
     * @param uri
     * @return
     */
    public static boolean isVideoType(Context context, @NonNull Uri uri) {
        String mimeType = getMimeType(context, uri);
        if(TextUtils.isEmpty(mimeType)) {
            return false;
        }
        return mimeType.startsWith("video");
    }

    /**
     * Used to check whether a file obtained from multimedia is a picture
     * @param context
     * @param uri
     * @return
     */
    public static boolean isImageType(Context context, @NonNull Uri uri) {
        String mimeType = getMimeType(context, uri);
        if(TextUtils.isEmpty(mimeType)) {
            return false;
        }
        return mimeType.startsWith("image");
    }

    /**
     * Get mime type
     * @param context
     * @param uri
     * @return
     */
    public static String getMimeType(Context context, @NonNull Uri uri) {
        return context.getContentResolver().getType(uri);
    }

    public static String getMimeType(Context context, @NonNull File file) {
        return getMimeType(context, file.getName());
    }

    public static String getMimeType(Context context, String filename) {
        String mimeType = null;
        Resources resources = context.getResources();

        if(checkSuffix(filename, resources.getStringArray(R.array.ease_image_file_suffix))) {
            mimeType = "image/*";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_video_file_suffix))) {
            mimeType = "video/*";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_audio_file_suffix))) {
            mimeType = "audio/*";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_file_file_suffix))) {
            mimeType = "text/plain";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_word_file_suffix))) {
            mimeType = "application/msword";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_excel_file_suffix))) {
            mimeType = "application/vnd.ms-excel";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_pdf_file_suffix))) {
            mimeType = "application/pdf";
        }else if(checkSuffix(filename, resources.getStringArray(R.array.ease_apk_file_suffix))) {
            mimeType = "application/vnd.android.package-archive";
        }else {
            mimeType = "application/octet-stream";
        }
        return mimeType;
    }

    /**
     * Check whether it is a video file
     * @param context
     * @param filename
     * @return
     */
    public static boolean isVideoFile(Context context, String filename) {
        return checkSuffix(filename, context.getResources().getStringArray(R.array.ease_video_file_suffix));
    }

    public static void setIntentByType(Context context, String filename, Intent intent) {
        Resources rs = context.getResources();
        if(checkSuffix(filename, rs.getStringArray(R.array.ease_audio_file_suffix))
                || checkSuffix(filename, rs.getStringArray(R.array.ease_video_file_suffix))) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("oneshot", 0);
            intent.putExtra("configchange", 0);
        }else if(checkSuffix(filename, rs.getStringArray(R.array.ease_image_file_suffix))) {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }else if(checkSuffix(filename, rs.getStringArray(R.array.ease_excel_file_suffix))
                || checkSuffix(filename, rs.getStringArray(R.array.ease_word_file_suffix))
                || checkSuffix(filename, rs.getStringArray(R.array.ease_pdf_file_suffix))) {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }else {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    public static boolean openApk(Context context, Uri uri) {
        String filename = getFileNameByUri(context, uri);
        return openApk(context, uri, filename);
    }

    private static boolean openApk(Context context, Uri uri, @NonNull String filename) {
        if(filename.endsWith(".apk")) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, getMimeType(context, filename));
                context.startActivity(intent);
            }else {
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.setDataAndType(uri, getMimeType(context, filename));
                context.startActivity(installIntent);
            }
            return true;
        }
        return false;
    }

    /**
     * Check suffix
     * @param filename
     * @param fileSuffix
     * @return
     */
    public static boolean checkSuffix(String filename, String[] fileSuffix) {
        if(TextUtils.isEmpty(filename) || fileSuffix == null || fileSuffix.length <= 0) {
            return false;
        }
        int length = fileSuffix.length;
        for(int i = 0; i < length; i++) {
            String suffix = fileSuffix[i];
            if(filename.toLowerCase().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get file name
     * @param context
     * @param fileUri
     * @return
     */
    public static String getFileNameByUri(Context context, Uri fileUri) {
        return EaseFileUtils.getFileNameByUri(context, fileUri);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        return EaseFileUtils.getFilePath(context, uri);
    }

}
