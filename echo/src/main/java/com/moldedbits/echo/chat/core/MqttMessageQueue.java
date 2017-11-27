package com.moldedbits.echo.chat.core;

import com.moldedbits.echo.chat.core.model.MqttAck;
import com.moldedbits.echo.chat.core.model.QueuedMessage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Might not be needed. Copied form nextcure. Purpose of this class is to make a queue of messages
 * and wait for some WAIT_TIME. If received ack does not come within time then send the Push to
 * receiver as he might be connected to internet but mqtt server connection might break
 *
 */
public class MqttMessageQueue {

    private static final long WAIT_TIME = 20000;
    private LinkedHashMap<String, List<QueuedMessage>> mMessageQueue;

    private static MqttMessageQueue sInstance;

    public static MqttMessageQueue getInstance() {
        if (sInstance == null) {
            sInstance = new MqttMessageQueue();
        }
        return sInstance;
    }

    private MqttMessageQueue() {
        this.mMessageQueue = new LinkedHashMap<>();
    }

    public synchronized void pushMessage(String topic, QueuedMessage token) {
        if (mMessageQueue.get(topic) == null) {
            // if no array is present creating  a new one
            List<QueuedMessage> tokenList = new ArrayList<>();
            tokenList.add(token);
            mMessageQueue.put(topic, tokenList);
        } else {
            List<QueuedMessage> tokenList = mMessageQueue.get(topic);
            tokenList.add(token);
        }
        Timer timer = new Timer();
        timer.schedule(new MyTimerTask(topic, token), WAIT_TIME);
    }

    public synchronized void popMessage(String topic, MqttAck message) {
        if (mMessageQueue.get(topic) != null) {
            List<QueuedMessage> tokenList = mMessageQueue.get(topic);
            for (QueuedMessage queuedMessage : tokenList) {
                if (message != null && message.getMessageId() != null &&
                        queuedMessage.getMessageId().contentEquals(message.getMessageId())) {
                    tokenList.remove(queuedMessage);
                    return;
                }
            }
        } else {
            // Removing the key when empty array is left
            mMessageQueue.remove(topic);
        }
    }

    private class MyTimerTask extends TimerTask {

        QueuedMessage mQueuedMessage;
        String mTopic;

        public MyTimerTask(String topic, QueuedMessage mToken) {
            this.mQueuedMessage = mToken;
            this.mTopic = topic;
        }

        @Override
        public void run() {
            if (mMessageQueue.get(mTopic).contains(mQueuedMessage)) {
                // TODO: 14/06/17 have a look later
//                StringBuilder builder = new StringBuilder(
//                        mQueuedMessage.getPushRequest().getNotificationBody().getSenderName());
//                builder.append(" sent you %1s new messages");
//                mQueuedMessage.getPushRequest()
//                        .getNotificationBody()
//                        .setMessage(String.format(builder.toString(),
//                                mMessageQueue.get(mTopic).size()));
//                // Sending push
//                PushUtils.sendPush(mQueuedMessage.getPushRequest());
//                mMessageQueue.get(mTopic).remove(mMessageQueue);
            }
        }
    }
}
