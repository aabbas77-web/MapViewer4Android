package com.oghab.mapviewer.mapviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oghab.mapviewer.MainActivity;
import com.oghab.mapviewer.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class ReaderActivity extends Activity {
    public Activity activity;
    public Context ctx;

    private String getRealPathFromURI(Uri contentURI) {
        String result = "";
        try
        {
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return result;
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
//            Log.e(TAG, "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            activity = this;
            ctx = getApplicationContext();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_reader);

            handleIntent();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private void handleIntent() {
        try
        {
            Uri uri = getIntent().getData();
            if (uri == null) {
                tellUserThatCouldntOpenFile();
                return;
            }

            String text;
            InputStream inputStream = getContentResolver().openInputStream(uri);
            text = getStringFromInputStream(inputStream);

            TextView tv_path = findViewById(R.id.tv_uri_path);
//            MainActivity.strCMDFile = uri.getEncodedPath();
//            MainActivity.strCMDFile = uri.getPath();
//            MainActivity.strCMDFile = getRealPathFromURI(uri);
//            MainActivity.strCMDFile = getRealPathFromURI(ctx,uri);
//            MainActivity.strCMDFile = uri.toString();
//            MainActivity.strCMDFile = getPath(uri);
            MainActivity.uriCMDFile = uri;
//            tv_path.setText(MainActivity.strCMDFile);
            tv_path.setText(uri.toString());

            TextView tv_content = findViewById(R.id.tv_uri_content);
            tv_content.setText(text);

            Button b_start_mapviewer = findViewById(R.id.b_start_mapviewer);
            b_start_mapviewer.setOnClickListener(v -> startActivity(new Intent(ctx, MainActivity.class)));
//            Uri file = Uri.fromFile(new File(MainActivity.strCMDFile));
//            String fileExt = MimeTypeMap.getFileExtensionFromUrl(file.toString());
            String fileExt = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            Toast.makeText(this, fileExt, Toast.LENGTH_SHORT).show();
            if(fileExt.equalsIgnoreCase("kml"))
            {
                b_start_mapviewer.setEnabled(true);
            }
            else
            {
                b_start_mapviewer.setEnabled(false);
            }
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    private void tellUserThatCouldntOpenFile() {
        try
        {
            Toast.makeText(this, getString(R.string.could_not_open_file), Toast.LENGTH_SHORT).show();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
    }

    public static String getStringFromInputStream(InputStream stream) {
        try
        {
            int n;
            char[] buffer = new char[1024 * 4];
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
            return writer.toString();
        }
        catch (Throwable ex)
        {
            MainActivity.MyLog(ex);
        }
        return "";
    }

}