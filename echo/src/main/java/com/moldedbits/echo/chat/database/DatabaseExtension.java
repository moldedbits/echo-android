package com.moldedbits.echo.chat.database;

import com.moldedbits.echo.chat.core.model.EchoMessage;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Author viveksingh
 * Date 15/06/17.
 * This interface provides the public api to create your own database extension. In order to create
 * your own database extension implement this interface and provide implementation of these methods
 * and you are good to go
 */

public interface DatabaseExtension<T> {
    /**
     * hook to save a message to database
     *
     * @param message message to be saved
     * @return single
     */
    Single<EchoMessage> storeMessage(T message);

    /**
     * hook to Update a single message
     *
     * @param updatedMessage
     * @return
     */
    Single<EchoMessage> updateMessage(T updatedMessage);

    /**
     * hook to delete a single message
     *
     * @param message message to be deleted
     * @return single
     */
    Single<EchoMessage> deleteMessage(T message);

    /**
     * hook to delete list of selected messages of same topic
     *
     * @param messages selected messages to be deleted
     * @return single
     */
    Observable<EchoMessage> deleteSelectedMessages(List<T> messages);

    /**
     * hook to delete all the messages from a topic
     *
     * @param topic from which all the messages needs to be deleted
     * @return single
     */
    Single<Boolean> deleteAllMessages(String topic);

    /**
     * Fetch all the messages of a particular topic
     * @param topic topic
     * @return
     */
    Observable<List<EchoMessage>> fetchMessages(String topic);

    /**
     * fetch all the messages from a particular topic
     * @param topic topic
     * @param subjects column to be used in where condition
     * @param predicates values to be checked for columns
     * @return messageList
     */
    Observable<List<EchoMessage>> fetchMessages(String topic, String[] subjects, String[] predicates);

    /**
     * fetch all the messages from a topic in asked order
     * @param topic topic
     * @param order order
     * @return list of messages
     */
    Observable<List<EchoMessage>> fetchMessagesByOrder(String topic, String sortColumn, SortOrder order);

    /**
     * fetch all the messages from a particular topic in asked order
     * @param topic topic
     * @param subjects column to be used in where condition
     * @param predicates values to be checked for columns
     * @param order order(Ascending/Descending)
     * @return messageList
     */
    Observable<List<EchoMessage>> fetchMessagesByOrder(String topic, String[] subjects, String[] predicates,
            String sortColumn, SortOrder order);
}
