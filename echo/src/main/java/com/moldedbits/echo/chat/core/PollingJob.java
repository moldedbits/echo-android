package com.moldedbits.echo.chat.core;

import android.content.Context;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

public class PollingJob extends Job {

    private Context mContext;
    private static int sJobId;

    public PollingJob(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        if (mContext != null && !MqttSession.getInstance().isSessionValid()) {
            if (MqttSession.getInstance().getCurrentState() == ConnectionStates.DISCONNECTED) {
                ChatUtils.refreshChatServer(mContext);
            }
        }
        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        if (!JobManager.instance().getAllJobRequestsForTag(PollingJobCreator.JOB_TAG).isEmpty()) {
            cancelJob();
        }
        sJobId = new JobRequest.Builder(PollingJobCreator.JOB_TAG)
                .setPeriodic(120000)
                .build()
                .schedule();
    }

    public static int getRunningJobId() {
        return sJobId;
    }

    public static void cancelJob() {
        JobManager.instance().cancelAllForTag(PollingJobCreator.JOB_TAG);
    }
}
