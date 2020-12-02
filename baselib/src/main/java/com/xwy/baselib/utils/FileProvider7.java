package com.xwy.baselib.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * 适配Android7.0文件存储
 */
public class FileProvider7 {
    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = getUriForFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    private static Uri getUriForFile24(Context context, File file) {
        return FileProvider.getUriForFile(context,
                context.getPackageName() + ".android7.file.provider",
                file);
    }

    static void setIntentDataAndType(Context context,
                                     Intent intent,
                                     File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(getUriForFile(context, file), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
