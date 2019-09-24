package com.spockchain.wallet.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.spockchain.wallet.BuildConfig;
import com.spockchain.wallet.R;

import org.json.JSONException;
import org.json.JSONObject;

public class VersionChecker {

    private static final String TAG = "VersionChecker";

    private static final String SHARED_PREFERENCE_FILE = "com.spockchain.wallet.VERSION_CHECK";
    private static final String LAST_TIME_CHECK_TIMESTAMP_KEY = "com.spockchain.wallet.LAST_TIME_CHECK_TIMESTAMP";
    private static final Long VERSION_CHECK_INTERNAL = DateUtils.HOUR_IN_MILLIS * 2; // 2 Hours.

    private static final String VERSION_CHECK_URL = "http://realtime.spock.network/api/client/android";
    private static final String VERSION_KEY = "version";
    private static final String DOWNLOAD_URL_KEY = "downloadUrl";
    private static final String RELEASE_NOTE_KEY = "changeLog";

    private final Context context;
    private final String versionName = BuildConfig.VERSION_NAME; // x.x.x

    public interface OnVersionCheckCompletedListener {
        void onCompleted(boolean hasNewUpdate);
    }

    public VersionChecker(Context context) {
        this.context = context;
    }

    public void checkVersionIfNeeded() {
        if (shouldCheckNewVersion()) {
            checkNewVersion(null);
        }
    }

    private boolean shouldCheckNewVersion() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);

        long lastCheckTimestamp = sharedPreferences.getLong(LAST_TIME_CHECK_TIMESTAMP_KEY, 0);
        long currentTimestamp = System.currentTimeMillis();
        return ((currentTimestamp - lastCheckTimestamp) >= VERSION_CHECK_INTERNAL);
    }

    public void checkNewVersion(@Nullable OnVersionCheckCompletedListener completedListener) {
        AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(VERSION_CHECK_URL)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        Log.d(TAG, "Version check result: " + result);

                        JSONObject resultJson;
                        try {
                            resultJson = new JSONObject(result);
                        } catch (JSONException e) {
                            Log.w(TAG, "Failed to parse version check result.", e);
                            if (completedListener != null) {
                                completedListener.onCompleted(false);
                            }
                            return null;
                        }

                        String newVersion = resultJson.optString(VERSION_KEY, "0.0.0");
                        if (isCurrentVersionTheLatest(newVersion)) {
                            if (completedListener != null) {
                                completedListener.onCompleted(false);
                            }
                            return null;
                        }

                        String downloadUrl;
                        try {
                            downloadUrl = resultJson.getString(DOWNLOAD_URL_KEY);
                        } catch (JSONException e) {
                            Log.w(TAG, "Version check result doesn't have download url!");
                            if (completedListener != null) {
                                completedListener.onCompleted(false);
                            }
                            return null;
                        }
                        String releaseNote = resultJson.optString(RELEASE_NOTE_KEY);

                        updateLastCheckTimestamp();

                        if (completedListener != null) {
                            completedListener.onCompleted(true);
                        }

                        return UIData
                                .create()
                                .setDownloadUrl(downloadUrl)
                                .setTitle(context.getString(R.string.version_check_title))
                                .setContent(context.getString(R.string.version_check_context, newVersion, releaseNote));
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {
                        Log.i("VersionChecker", "message: " + message);
                        updateLastCheckTimestamp();
                        if (completedListener != null) {
                            completedListener.onCompleted(false);
                        }
                    }
                })
                .setSilentDownload(true)
                .setForceRedownload(true)
                .executeMission(context);
    }

    private boolean isCurrentVersionTheLatest(String latestVersionName) {
        String[] latestVersionArray = latestVersionName.split("\\.");
        if (latestVersionArray.length != 3) {
            return true; // Unknown format.
        }

        String[] currentVersionArray = versionName.split("\\.");
        for (int i = 0; i < 3; i++) {
            if (Integer.parseInt(latestVersionArray[i]) > Integer.parseInt(currentVersionArray[i])) {
                return false;
            }

            if (Integer.parseInt(latestVersionArray[i]) < Integer.parseInt(currentVersionArray[i])) {
                return true;
            }
        }
        return true;
    }

    private void updateLastCheckTimestamp() {
        context.getSharedPreferences(SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE).edit().putLong(LAST_TIME_CHECK_TIMESTAMP_KEY, System.currentTimeMillis()).commit();
    }
}
