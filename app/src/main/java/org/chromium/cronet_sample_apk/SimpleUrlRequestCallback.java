package org.chromium.cronet_sample_apk;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class SimpleUrlRequestCallback extends UrlRequest.Callback {
    private static final String TAG = SimpleUrlRequestCallback.class.getSimpleName();
    private ByteArrayOutputStream mBytesReceived = new ByteArrayOutputStream();
    private WritableByteChannel mReceiveChannel = Channels.newChannel(mBytesReceived);

    private long mRequestStartTime;

    public SimpleUrlRequestCallback(long startTime) {
        mRequestStartTime = startTime;
    }

    @Override
    public void onRedirectReceived(
            UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
        request.followRedirect();
    }

    @Override
    public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
        request.read(ByteBuffer.allocateDirect(32 * 1024));
    }

    @Override
    public void onReadCompleted(
            UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
        byteBuffer.flip();

        try {
            mReceiveChannel.write(byteBuffer);
        } catch (IOException e) {
            org.chromium.base.Log.i(TAG, "IOException during ByteBuffer read. Details: ", e);
        }
        byteBuffer.clear();
        request.read(byteBuffer);
    }

    @Override
    public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
        String receivedData = mBytesReceived.toString();
        receivedData = receivedData.replaceAll("^\"+", "").replaceAll("\"+$", "");
        final String url = info.getUrl();
        org.chromium.base.Log.i(TAG, "ReceivedData = " + receivedData);
        org.chromium.base.Log.i(TAG, "RequestUrl = " + url + " (" + info.getHttpStatusCode() + ")" +
                "; ReceiveBytes = " + info.getReceivedByteCount() +
                "; RequestTime = " + (System.currentTimeMillis() - mRequestStartTime));
    }

    @Override
    public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
        org.chromium.base.Log.i(TAG, "****** onFailed, error is: %s", error.getMessage());

        final String url = info.getUrl();
        final String text = "Failed " + url + " (" + error.getMessage() + ")";
    }
}