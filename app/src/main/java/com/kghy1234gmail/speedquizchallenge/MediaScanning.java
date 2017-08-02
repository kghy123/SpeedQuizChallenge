package com.kghy1234gmail.speedquizchallenge;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

public class MediaScanning implements MediaScannerConnection.MediaScannerConnectionClient {

    MediaScannerConnection conn;
    File targetFile;

    public MediaScanning(Context context, File targetFile) {
        this.targetFile = targetFile;
        conn = new MediaScannerConnection(context, this);
        conn.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        conn.scanFile(targetFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        conn.disconnect();
    }
}
