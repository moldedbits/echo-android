package com.moldedbits.echo.chat.core;

import android.content.Context;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class PollingJobCreator implements JobCreator {
    static final String JOB_TAG = "polling_job_mqtt";

    Context mContext;

    public PollingJobCreator(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public Job create(String tag) {
        switch (tag) {
            case JOB_TAG:
                Log.e(getClass().getName(), "polling --> created polling job");
                return new PollingJob(mContext);
            default:
                return null;
        }
    }
}