package com.example.ahmad2.shopproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class VersionApp {

    public static final String DOWN_URL = App.BASE_URL + "app/" + "NahavandPasazh.apk";
    private Context context;
    private ProgressDialog progDialog;
    private AlertDialog.Builder builder;
    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";

    public static String getVersionName() {
        String versionName = BuildConfig.VERSION_NAME;
        return versionName;
    }

    public static int getVersionCode() {
        int versionCode = BuildConfig.VERSION_CODE;
        return versionCode;
    }

    public void downloadNewVersin(Context context) {

        this.context = context;
        progDialog = new ProgressDialog(context);
        progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDialog.setProgress(0);
        progDialog.setMax(100);
        progDialog.setTitle(context.getResources().getString(R.string.tilt_download));
        builder = new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.path_download_app))
                .setMessage(PATH)
                .setPositiveButton(context.getResources().getString(R.string.install_app), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startInstall();
                    }
                }).setNegativeButton(context.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        new DownTask().execute(DOWN_URL);
    }

    private class DownTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            progDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String uri = strings[0];
            try {
                URL url = new URL(uri);
                URLConnection connection = url.openConnection();
                connection.connect();
                int length = connection.getContentLength();
                InputStream inputStream = url.openStream();
                String fileName = uri.substring(uri.lastIndexOf('/') + 1);
                File file = new File(PATH, fileName);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (file.exists()) {
                    file.delete();
                    file.createNewFile();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                BufferedInputStream buffer = new BufferedInputStream(inputStream, 8 * 1024);
                byte[] bytes = new byte[1024];
                int count = 0;
                int per = 0;
                while ((count = buffer.read(bytes)) != -1) {
                    per += count;
                    publishProgress((per * 100) / length);
                    outputStream.write(bytes, 0, count);

                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "file is download";

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            progDialog.dismiss();
            builder.show();

        }


        private void downloadFile(String uri) {
            try {
                URL url = new URL(uri);
                URLConnection connection = url.openConnection();
                connection.connect();
                int length = connection.getContentLength();
                InputStream inputStream = url.openStream();
                String fileName = uri.substring(uri.lastIndexOf('/') + 1);
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FileDownloadDirectory", fileName);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                FileOutputStream outputStream = new FileOutputStream(file);
                BufferedInputStream buffer = new BufferedInputStream(inputStream, 8 * 1024);
                byte[] bytes = new byte[1024];
                int count = 0;
                while ((count = buffer.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, count);
                }
                outputStream.close();
                inputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void installAppAndroid9() {
//            Intent intent = new Intent(Intent.ACTION_VIEW);

//            File file=new File(path);
//            String type = "application/vnd.android.package-archive";
//            Uri downloadedApk = FileProvider.getUriForFile(context, "ir.greencode", file);
////            intent.setData(Uri.fromFile(new File(path)));
//            intent.setDataAndType(downloadedApk, type);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            context.startActivity(intent);

        String path = Environment.getExternalStorageDirectory() + "/Download/NahavandPasazh.apk";
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private void installApp() {
        Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String path = Environment.getExternalStorageDirectory() + "/Download/NahavandPasazh.apk";
        installIntent.setData(Uri.fromFile(new File(path)));
        installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
        context.startActivity(installIntent);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            String type = "application/vnd.android.package-archive";
    }


    private void startInstall() {
        String path = Environment.getExternalStorageDirectory() + "/Download/NahavandPasazh.apk";
        File appApkFile = new File(path);
        if (appApkFile.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", appApkFile);
                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

                PackageManager packageManager = context.getPackageManager();
                if (intent.resolveActivity(packageManager) != null) {
                    context.startActivity(intent);
                }
            } else {
//                Uri apkUri = Uri.fromFile(appApkFile);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
////                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//                intent.setData(apkUri);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                PackageManager packageManager = context.getPackageManager();
//                if (intent.resolveActivity(packageManager) != null) {
//                    context.startActivity(intent);
//                }
                Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                String path = Environment.getExternalStorageDirectory() + "/Download/NahavandPasazh.apk";
                installIntent.setData(Uri.fromFile(new File(path)));
                installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                context.startActivity(installIntent);
            }
        }
    }


}
