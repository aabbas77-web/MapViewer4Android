package com.oghab.mapviewer.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.util.Objects;

public class UriUtil {
    static public String getFileName(Context ctx, Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
            if(cursor != null){
                try {
                    if (cursor.moveToFirst()) {
                        int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if(idx >= 0) {
                            result = cursor.getString(idx);
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            if(result != null){
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }
}
