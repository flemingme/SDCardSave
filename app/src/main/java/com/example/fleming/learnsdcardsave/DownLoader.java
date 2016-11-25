package com.example.fleming.learnsdcardsave;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * DownLoader
 * Created by Fleming on 2016/11/23.
 */

public class DownLoader {

    private static final String TAG = "DownLoader";
    private static final int READ_TIMEOUT = 10 * 1000;
    private static final int CONNECT_TIMEOUT = 15 * 1000;
    private static final String METHOD_GET = "GET";

    public void download(String urlPath, @NonNull OnDownloadListener listener) {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        BufferedInputStream bis = null;
        int fileSize;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setRequestMethod(METHOD_GET);
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.i(TAG, "download: " + response);
            if (response == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                fileSize = conn.getContentLength();
                if (fileSize != 0) {
                    listener.maxProgress(conn.getContentLength());
                } else {
                    listener.onError("file size is 0");
                }
                bis = new BufferedInputStream(is);
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[8 * 1024];
                int downloadSize = 0;
                do {
                    //循环读取
                    int numread = bis.read(buffer);
                    if (numread == -1) {
                        break;
                    }
                    baos.write(buffer, 0, numread);
                    baos.flush();
                    downloadSize += numread;
                    listener.loadProgress(downloadSize);
                } while (true);
                listener.complete(baos.toByteArray());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null && baos != null && bis != null) {
                try {
                    baos.close();
                    bis.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String downloadText(String urlPath) {
        InputStream is = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setRequestMethod(METHOD_GET);
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.i(TAG, "downloadText: " + response);
            if (response == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                String result = readIt(is);
                return result;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String readIt(InputStream is) {
        int len = 500;
        Reader reader = null;
        reader = new InputStreamReader(is);
        char[] cbuf = new char[len];
        try {
            reader.read(cbuf);
            return new String(cbuf);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public interface OnDownloadListener {
        void maxProgress(int filesize);
        void loadProgress(int progress);
        void complete(byte[] data);
        void onError(String error);
    }

}
