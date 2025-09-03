package com.oghab.mapviewer.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.oghab.mapviewer.MainActivity;

import java.io.File;
import java.net.URLConnection;

public class IntentHelper {

	static public void start_mapviewer(Context context){
		try{
			Intent intent2 = context.getPackageManager().getLaunchIntentForPackage("com.oghab.mapviewer");
			if (intent2 != null) {
				intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent2.addCategory(Intent.CATEGORY_LAUNCHER);
				context.startActivity(intent2);
			}
		} catch (Throwable ex) {
			MainActivity.MyLog(ex);
		}
	}

	/**
	 * Send email.
	 *
	 * @param context the context
	 * @param email   the email
	 * @param subject the subject
	 * @param text    the text
	 */
	static public void sendEmail(Context context, String email, String subject,
						  String text) {
		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND);
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[]{email});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

	/**
	 * Dial.
	 *
	 * @param context the context
	 * @param number  the number
	 */
	static public void dial(Context context, String number) {
		Uri dialUri = Uri.parse("tel:" + number);
		Intent dialIntent = new Intent(Intent.ACTION_DIAL, dialUri);
		context.startActivity(dialIntent);
	}

	/**
	 * Call.
	 *
	 * @param context the context
	 * @param number  the number
	 */
	static public void call(Context context, String number) {
		Uri callUri = Uri.parse("tel:" + number);
		Intent callIntent = new Intent(Intent.ACTION_CALL, callUri);
		context.startActivity(callIntent);
	}

	/**
	 * Open file
	 *
	 * @param uri path to file
	 */
	static public void openUri(Uri uri) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

		intent.setDataAndType(uri, URLConnection.guessContentTypeFromName(uri.toString()));

//		if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
//			// Word document
//			intent.setDataAndType(uri, "application/msword");
//		} else if (uri.toString().contains(".pdf")) {
//			// PDF file
//			intent.setDataAndType(uri, "application/pdf");
//		} else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
//			// Powerpoint file
//			intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//		} else if (uri.toString().contains(".xls") || uri.toString().contains(".xlsx")) {
//			// Excel file
//			intent.setDataAndType(uri, "application/vnd.ms-excel");
//		} else if (uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
//			// WAV audio file
//			intent.setDataAndType(uri, "application/x-wav");
//		} else if (uri.toString().contains(".rtf")) {
//			// RTF file
//			intent.setDataAndType(uri, "application/rtf");
//		} else if (uri.toString().contains(".wav") || uri.toString().contains(".mp3")) {
//			// WAV audio file
//			intent.setDataAndType(uri, "audio/x-wav");
//		} else if (uri.toString().contains(".gif")) {
//			// GIF file
//			intent.setDataAndType(uri, "image/gif");
//		}  else if (uri.toString().contains(".png")) {
//			// PNG file
//			intent.setDataAndType(uri, "image/png");
//		} else if (uri.toString().contains(".jpg") || uri.toString().contains(".jpeg")) {
//			// JPG file
//			intent.setDataAndType(uri, "image/jpeg");
//		} else if (uri.toString().contains(".txt")) {
//			// Text file
//			intent.setDataAndType(uri, "text/plain");
//		} else if (uri.toString().contains(".apk")) {
//			// Text file
//			intent.setDataAndType(uri, "application/vnd.android.package-archive");
//		} else if (uri.toString().contains(".3gp") || uri.toString().contains(".mpg") || uri.toString().contains(".mpeg") || uri.toString().contains(".mpe") || uri.toString().contains(".mp4") || uri.toString().contains(".avi")) {
//			// Video files
//			intent.setDataAndType(uri, "video/*");
//		} else {
//			// Other files
//			intent.setDataAndType(uri, "*/*");
//		}

		MainActivity.ctx.startActivity(intent);
//		MainActivity.ctx.startActivity(android.content.Intent.createChooser(intent, "Open File..."));
	}

	static public void openFile(String filename){
		openUri(Uri.fromFile(new File(filename)));

//		MimeTypeMap myMime = MimeTypeMap.getSingleton();
//		Intent newIntent = new Intent(Intent.ACTION_VIEW);
//		String mimeType = myMime.getMimeTypeFromExtension(FileHelper.fileExt(filename).substring(1));
//		newIntent.setDataAndType(Uri.fromFile(new File(filename)),mimeType);
//		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//		Intent newIntent = new Intent();
//		newIntent.setAction(Intent.ACTION_VIEW);
//		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_NEW_TASK);
//		newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//		Uri uri = Uri.fromFile(new File(filename));
//		newIntent.setDataAndType(uri, URLConnection.guessContentTypeFromName(uri.toString()));
//
//		try {
//			MainActivity.ctx.startActivity(newIntent);
//		} catch (ActivityNotFoundException e) {
//			Toast.makeText(MainActivity.ctx, "No handler for this type of file.", Toast.LENGTH_SHORT).show();
//		}
	}
	
/**
 * Open file
 * 
 * @param uri path to file
 */
public void openFile(Uri uri) {
     Intent intent = new Intent();
     intent.setAction(Intent.ACTION_VIEW);
     if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
          // Word document
          intent.setDataAndType(uri, "application/msword");
     } else if (uri.toString().contains(".pdf")) {
          // PDF file
          intent.setDataAndType(uri, "application/pdf");
     } else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
          // Powerpoint file
          intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
     } else if (uri.toString().contains(".xls") || uri.toString().contains(".xlsx")) {
          // Excel file
          intent.setDataAndType(uri, "application/vnd.ms-excel");
     } else if (uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
          // WAV audio file
          intent.setDataAndType(uri, "application/x-wav");
     } else if (uri.toString().contains(".rtf")) {
          // RTF file
          intent.setDataAndType(uri, "application/rtf");
     } else if (uri.toString().contains(".wav") || uri.toString().contains(".mp3")) {
          // WAV audio file
          intent.setDataAndType(uri, "audio/x-wav");
     } else if (uri.toString().contains(".gif")) {
          // GIF file
          intent.setDataAndType(uri, "image/gif");
     } else if (uri.toString().contains(".jpg") || uri.toString().contains(".jpeg") || uri.toString().contains(".png")) {
          // JPG file
          intent.setDataAndType(uri, "image/jpeg");
     } else if (uri.toString().contains(".txt")) {
          // Text file
          intent.setDataAndType(uri, "text/plain");
     } else if (uri.toString().contains(".3gp") || uri.toString().contains(".mpg") || uri.toString().contains(".mpeg") || uri.toString().contains(".mpe") || uri.toString().contains(".mp4") || uri.toString().contains(".avi")) {
          // Video files
          intent.setDataAndType(uri, "video/*");
     } else {
          // Other files
          intent.setDataAndType(uri, "*/*");
     }
     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	MainActivity.ctx.startActivity(intent);
}

}
