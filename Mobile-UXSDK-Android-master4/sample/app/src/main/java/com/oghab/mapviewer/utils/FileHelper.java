package com.oghab.mapviewer.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.documentfile.provider.DocumentFile;

import com.blankj.utilcode.util.UriUtils;
import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;
import com.oghab.mapviewer.mapviewer.Tab_Messenger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Objects;

//import dji.midware.data.model.P3.Ma;

public class FileHelper {

    static public boolean FileExists(String fname) {
        try {
            File file = new File(fname);
            return file.exists();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return false;
        }
    }

    static public boolean FileNotExists(String filename) {
        try {
            File file = new File(filename);
            return !file.exists();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return true;
        }
    }

    static public void save_image_as_png(File file, Bitmap bmp){
        try{
            FileOutputStream outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 95, outStream);
            outStream.flush();
            outStream.close();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void save_image_as_webp(File file, Bitmap bmp){
        try{
            FileOutputStream outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.WEBP, 95, outStream);
            outStream.flush();
            outStream.close();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void save_image_as_jpg(File file, Bitmap bmp){
        try{
            FileOutputStream outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 95, outStream);
            outStream.flush();
            outStream.close();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static void copy_file(String src, String dst){
        try {
            copy_file(new File(src), new File(dst));
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public static void copy_file(File src, File dst){
        try {
            InputStream in = new FileInputStream(src);
            try {
                OutputStream out = new FileOutputStream(dst);
                try {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    static public void delete_file(String filename){
        File file = new File(filename);
        if(file.exists()){
            file.delete();
        }
    }

    static public long file_size(String filename){
        File file = new File(filename);
        return file.length();
    }

    static public String file_name(String filename){
        File file = new File(filename);
        return file.getName();
    }

    static public String fileExt(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    static public String fileExt(File file) {
        return fileExt(file.getAbsolutePath());
    }

    public static  void deleteRecursive(File fileOrDirectory) {
        if(fileOrDirectory == null) return;
        if (fileOrDirectory.isDirectory()) {
            File[] files = fileOrDirectory.listFiles();
            if((files != null) && (files.length > 0)) {
                for (File file : files) {
                    deleteRecursive(file);
                }
            }
        }
        fileOrDirectory.delete();
    }

    public static  String readTextFile(String filename, String strDefault){
        String line = strDefault;
        try {
            File file = new File(filename);
            if(file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append(System.getProperty("line.separator"));
                }
                fileInputStream.close();
                line = stringBuilder.toString();
                bufferedReader.close();
            }
        } catch (Throwable ex) {
            line = strDefault;
            MainActivity.MyLog(ex);
        }
        return line.trim();
    }

    public static void writeTextFile(String filename, String text){
        try {
            File file = new File(filename);
            FileOutputStream fileOutputStream = new FileOutputStream(file,false);
            fileOutputStream.write((text + System.getProperty("line.separator")).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    private void writeTextToOutputFile(String text) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MainActivity.ctx.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(text);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    private String readTextFromInputFile() {
        String ret = "";
        try {
            InputStream inputStream = MainActivity.ctx.openFileInput("config.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return ret;
    }

    static public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static public String saveImage(Bitmap bitmap) {
        String strFilename = null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                ContentValues values = contentValues();
//            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + MainActivity.ctx.getString(R.string.app_name));
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MapViewer4");
//            values.put(MediaStore.Images.Media.RELATIVE_PATH, "MapViewer/" + MainActivity.ctx.getString(R.string.app_name));
//            values.put(MediaStore.Images.Media.RELATIVE_PATH, MainActivity.ctx.getString(R.string.app_name));
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "map.png");
                values.put(MediaStore.Images.Media.IS_PENDING, true);// to let others see them

                Uri uri = MainActivity.ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try {
                        strFilename = getRealPathFromURI(MainActivity.ctx, uri);
                        if(bitmap != null){
                            saveImageToStream(bitmap, MainActivity.ctx.getContentResolver().openOutputStream(uri));
                            values.put(MediaStore.Images.Media.IS_PENDING, false);
                            MainActivity.ctx.getContentResolver().update(uri, values, null, null);
                        }
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
            } else {
                File directory = new File(Environment.getExternalStorageDirectory().toString() + '/' + MainActivity.ctx.getString(R.string.app_name));
                if (!directory.exists()) {
                    if(!directory.mkdirs()){
                        MainActivity.MyLogInfoSilent("Folder not created. ["+directory.getAbsolutePath()+"]");
                    }
                }
//            String fileName = System.currentTimeMillis() + ".png";
                String fileName = "map.png";
                File file = new File(directory, fileName);
                try {
                    strFilename = file.getAbsolutePath();
                    if(bitmap != null) {
                        saveImageToStream(bitmap, new FileOutputStream(file));
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                        MainActivity.ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    }
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return strFilename;
    }

    static public class SavingThread extends Thread {
        String filename;
        public SavingThread(String filename){
            this.filename = filename;
        }

        public void run(){
            try{
                String new_filename = saveFile(filename);
                Tab_Messenger.addError("File saved to: ["+new_filename+"]");
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }

    static public String saveFile(String filename) {
        String strFilename = null;
        try {
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DATE_ADDED, System.currentTimeMillis() / 1000);
//            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                values.put(MediaStore.Downloads.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/" + MainActivity.ctx.getString(R.string.app_name));
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, (new File(filename)).getName());
                values.put(MediaStore.Downloads.IS_PENDING, true);// to let others see them

                Uri uri = MainActivity.ctx.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try {
                        strFilename = getRealPathFromURI(MainActivity.ctx, uri);
                        saveFileToStream(filename, MainActivity.ctx.getContentResolver().openOutputStream(uri));
                        values.put(MediaStore.Downloads.IS_PENDING, false);
                        MainActivity.ctx.getContentResolver().update(uri, values, null, null);
                    } catch (Throwable ex) {
                        MainActivity.MyLog(ex);
                    }
                }
            } else {
                File directory = new File(Environment.getExternalStorageDirectory().toString() + "/Download/" + MainActivity.ctx.getString(R.string.app_name));
                if (!directory.exists()) {
                    directory.mkdirs();
                }
//            String fileName = System.currentTimeMillis() + ".png";
                String fileName = (new File(filename)).getName();
                File file = new File(directory, fileName);
                try {
                    strFilename = file.getAbsolutePath();
                    saveFileToStream(filename, new FileOutputStream(file));
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Downloads.DATA, file.getAbsolutePath());
//                MainActivity.ctx.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                } catch (Throwable ex) {
                    MainActivity.MyLog(ex);
                }
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return strFilename;
    }

    static private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        try {
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
        return values;
    }

    static private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, outputStream);
                outputStream.close();
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }

    static private void saveFileToStream(String filename, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                InputStream in = new FileInputStream(new File(filename));
                try {
                    try {
                        // Transfer bytes from in to out
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = in.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                    } finally {
                        outputStream.close();
                    }
                } finally {
                    in.close();
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }
    }

    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    public static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        public ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            try {
                ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(mFile);
                    output.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    mImage.close();
                    if (null != output) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Throwable ex) {
                MainActivity.MyLog(ex);
            }
        }

    }

    public static void saveUriToFile(Context context, Uri uri, String filePath) {
        try (BufferedInputStream bis = new BufferedInputStream(context.getContentResolver().openInputStream(uri)); BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath, false))) {
            byte[] buffer = new byte[1024];
            if(bis.read(buffer) != -1){
                do {
                    bos.write(buffer);
                } while (bis.read(buffer) != -1);
            }
        } catch (Throwable ex) {
            MainActivity.MyLog(ex);
        }
    }

    public static class FileScanner {
        static public String str;
        static private String extension = "*";
        public ArrayList<File> files = new ArrayList<File>();

        public FileScanner(String ext){
            extension = ext;
        }

        public void scan(DocumentFile root) {
            DocumentFile[] list = root.listFiles();
            for (DocumentFile f : list) {
                Uri uri = f.getUri();
                if (f.isDirectory()) {
                    scan(f);
                } else {
                    File file = UriUtils.uri2FileNoCacheCopy(uri);
                    if (file != null) {
                        if(Objects.equals(extension, "*")){
                            files.add(file);
                        }else{
                            String ext = FileHelper.fileExt(file);
                            if(ext != null) {
                                if (ext.contains(extension)) {
                                    files.add(file);
                                }
                            }
                        }
                    }
                }
            }
        }

        public void scan(File root) {
            File[] list = root.listFiles();
            if(list != null){
                for (File file : list) {
                    if (file.isDirectory()) {
                        scan(file);
                    } else {
                        if(Objects.equals(extension, "*")){
                            files.add(file);
                        }else{
                            String ext = FileHelper.fileExt(file);
                            if(ext != null) {
                                if (ext.contains(extension)) {
                                    files.add(file);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
