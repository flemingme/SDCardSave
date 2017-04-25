package com.example.fleming.sdcardsave;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * SDCardSaveFragment
 * Created by Fleming on 2016/11/23.
 */

public class SDCardSaveFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.tv_download_status)
    TextView tvDownloadStatus;
    @BindView(R.id.number_progress_bar)
    NumberProgressBar npb;
    @BindView(R.id.bt_download)
    Button btDownload;
    @BindView(R.id.bt_read)
    Button btRead;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    private static final String IMAGE_URL = "https://dn-coding-net-production-static.qbox.me/b8713bab-1567-49ab-9c9f-18e4e7dbaf3b.png";
    private static final String DIRECTORY = "image";
    private static final String FILENAME = "coding.png";
    private static final int BEGIN = 0;
    private static final int LOADING = 1;
    private static final int COMPLETE = 2;
    private static final int ERROR = -1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!Thread.currentThread().isInterrupted()) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case BEGIN:
                        int maxProgress = (int) msg.obj;
                        Log.d(TAG, "max file size: " + maxProgress);
                        npb.setMax(maxProgress);
                        break;
                    case LOADING:
                        int progress = (int) msg.obj;
                        tvDownloadStatus.setText("开始下载");
                        Log.d(TAG, "loading size: " + progress);
                        npb.setProgress(progress);
                        break;
                    case COMPLETE:
                        byte[] data = (byte[]) msg.obj;
                        tvDownloadStatus.setText("下载完成");
                        boolean isSaved = SDCardHelper.saveFile2SDCard(data, DIRECTORY, FILENAME);
                        if (isSaved) {
                            Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case ERROR:
                        String errorMsg = (String) msg.obj;
                        tvDownloadStatus.setText(errorMsg);
                        break;
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sdcard_save_fragment, container, false);
        ButterKnife.bind(this, v);
        init();
        return v;
    }

    private void init() {
        RxPermissions rxPermissions = new RxPermissions(getActivity());
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        Log.i(TAG, "授权成功");
                    } else {
                        Log.i(TAG, "授权失败");
                    }
                });
    }

    @OnClick({R.id.bt_download, R.id.bt_read})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_download:
                downloadImg();
                break;
            case R.id.bt_read:
                loadImgFromSdCard();
                break;
        }
    }

    private void downloadImg() {
        new Thread(() ->
                DownLoader.with()
                        .download(IMAGE_URL, new DownLoader.OnDownloadListener() {
                            @Override
                            public void maxProgress(int fileSize) {
                                sendMsg(BEGIN, fileSize);
                            }

                            @Override
                            public void loadProgress(int progress) {
                                sendMsg(LOADING, progress);
                            }

                            @Override
                            public void complete(byte[] data) {
                                sendMsg(COMPLETE, data);
                            }

                            @Override
                            public void onError(String error) {
                                sendMsg(ERROR, error);
                            }
                        })
        ).start();
    }

    private void sendMsg(int what, Object data) {
        Message message = new Message();
        message.what = what;
        message.obj = data;
        mHandler.sendMessage(message);
    }

    private void loadImgFromSdCard() {
        String fileSavePath = SDCardHelper.getSDCardPath() + File.separator + DIRECTORY;
        byte[] bytes = SDCardHelper.loadFileFromSDCard(fileSavePath + File.separator + FILENAME);
        Bitmap bitmap = BitmapUtils.bytes2Bitmap(bytes);
        ivImage.setImageBitmap(bitmap);
    }
}
