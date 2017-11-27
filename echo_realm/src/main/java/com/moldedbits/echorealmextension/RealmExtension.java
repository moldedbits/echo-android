package com.moldedbits.echorealmextension;

import android.content.Context;

import com.moldedbits.echo.chat.core.model.EchoMessage;
import com.moldedbits.echo.chat.database.DatabaseExtension;
import com.moldedbits.echo.chat.database.SortOrder;
import com.moldedbits.echorealmextension.model.Message;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Author viveksingh
 * Date 15/06/17.
 */

public class RealmExtension implements DatabaseExtension<EchoMessage> {

    private static RealmExtension instance;

    public static RealmExtension getInstance(Context context) {
        if (instance == null) {
            instance = new RealmExtension(context);
        }
        return instance;
    }

    private RealmExtension(Context context) {
        Realm.init(context);
        // TDOD:: Realm init work here like configuration or migration
    }

    @Override
    public Single<EchoMessage> storeMessage(final EchoMessage echoMessage) {

        return Single.create(new SingleOnSubscribe<EchoMessage>() {
            @Override
            public void subscribe(final SingleEmitter<EchoMessage> e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmAdapter adapter = new RealmAdapter();
                            Message message =
                                    realm.createObject(Message.class, echoMessage.getMessageId());
                            realm.insertOrUpdate(adapter.fromEchoMessage(echoMessage, message));
                            e.onSuccess(echoMessage);
                        }
                    });
                } catch (Exception ex) {
                    e.onError(ex);
                } finally {
                    realm.close();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<EchoMessage> updateMessage(final EchoMessage updatedMessage) {
        return Single.create(new SingleOnSubscribe<EchoMessage>() {
            @Override
            public void subscribe(final SingleEmitter<EchoMessage> e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmAdapter adapter = new RealmAdapter();
                            Message message = realm.where(Message.class)
                                    .equalTo(Message.ColumnName.MESSAGE_ID.getValue(),
                                            updatedMessage.getMessageId()).findFirst();
                            realm.insertOrUpdate(adapter.fromEchoMessage(updatedMessage, message));
                            e.onSuccess(updatedMessage);
                        }
                    });
                } catch (Exception ex) {
                    e.onError(ex);
                } finally {
                    realm.close();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<EchoMessage> deleteMessage(final EchoMessage message) {
        return Single.create(new SingleOnSubscribe<EchoMessage>() {
            @Override
            public void subscribe(final SingleEmitter<EchoMessage> e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<Message> result = realm.where(Message.class)
                                    .equalTo(Message.ColumnName.MESSAGE_ID.getValue(),
                                            message.getMessageId())
                                    .equalTo(Message.ColumnName.TOPIC.getValue(),
                                            message.getTopic())
                                    .findAll();
                            result.deleteAllFromRealm();
                            e.onSuccess(message);
                        }
                    });
                } catch (Exception ex) {
                    e.onError(ex);
                } finally {
                    realm.close();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EchoMessage> deleteSelectedMessages(final List<EchoMessage> messages) {
        return Observable.create(new ObservableOnSubscribe<EchoMessage>() {
            @Override
            public void subscribe(final ObservableEmitter<EchoMessage> e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                try {
                    for (final EchoMessage message : messages) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<Message> result = realm.where(Message.class)
                                        .equalTo(Message.ColumnName.MESSAGE_ID.getValue(),
                                                message.getMessageId())
                                        .equalTo(Message.ColumnName.TOPIC.getValue(),
                                                message.getTopic())
                                        .findAll();
                                result.deleteAllFromRealm();
                                e.onNext(message);
                            }
                        });
                    }
                } catch (Exception ex) {
                    e.onError(ex);
                } finally {
                    realm.close();
                    e.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Single<Boolean> deleteAllMessages(final String topic) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final SingleEmitter<Boolean> e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmResults<Message> result = realm.where(Message.class)
                                    .equalTo(Message.ColumnName.TOPIC.getValue(), topic)
                                    .findAll();
                            e.onSuccess(result.deleteAllFromRealm());
                        }
                    });
                } catch (Exception ex) {
                    e.onError(ex);
                } finally {
                    realm.close();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<EchoMessage>> fetchMessages(final String topic) {
        Observable<RealmResults<Message>> ob =
                Observable.just(topic).map(new Function<String, RealmResults<Message>>() {
                    @Override
                    public RealmResults<Message> apply(String s) throws Exception {
                        Realm realm = Realm.getDefaultInstance();
                        return realm.where(Message.class)
                                .equalTo(Message.ColumnName.TOPIC.getValue(), topic)
                                .findAll();
                    }
                });
        return createObservable(ob);
    }

    @Override
    public Observable<List<EchoMessage>> fetchMessages(final String topic, final String[] subjects,
            final String[] predicates) {

        Observable<RealmResults<Message>> ob =
                Observable.just(topic).map(new Function<String, RealmResults<Message>>() {
                    @Override
                    public RealmResults<Message> apply(String s) throws Exception {
                        Realm realm = Realm.getDefaultInstance();
                        RealmQuery<Message> query = realm.where(Message.class).
                                equalTo(Message.ColumnName.TOPIC.getValue(), topic);
                        if (subjects.length != predicates.length) {
                            throw new IllegalArgumentException(
                                    "subjects and predicates for a query should have same size");
                        }
                        int size = subjects.length;
                        for (int i = 0; i < size; i++) {
                            query = query.equalTo(subjects[i], predicates[i]);
                        }
                        return query.findAll();
                    }
                });
        return createObservable(ob);
    }

    @Override
    public Observable<List<EchoMessage>> fetchMessagesByOrder(final String topic,
            final String sortColumn,
            final SortOrder order) {

        Observable<RealmResults<Message>> ob =
                Observable.just(topic).map(new Function<String, RealmResults<Message>>() {
                    @Override
                    public RealmResults<Message> apply(String s) throws Exception {
                        Sort sort = order == SortOrder.ASCEND ? Sort.ASCENDING : Sort.DESCENDING;
                        Realm realm = Realm.getDefaultInstance();
                        return realm.where(Message.class)
                                .equalTo(Message.ColumnName.TOPIC.getValue(), topic)
                                .findAllSorted(sortColumn, sort);
                    }
                });
        return createObservable(ob);
    }

    @Override
    public Observable<List<EchoMessage>> fetchMessagesByOrder(final String topic,
            final String[] subjects, final String[] predicates, final String sortColumn,
            final SortOrder order) {

        Observable<RealmResults<Message>> ob =
                Observable.just(topic).map(new Function<String, RealmResults<Message>>() {
                    @Override
                    public RealmResults<Message> apply(String s) throws Exception {
                        Sort sort = order == SortOrder.ASCEND ? Sort.ASCENDING : Sort.DESCENDING;
                        Realm realm = Realm.getDefaultInstance();
                        RealmQuery<Message> query = realm.where(Message.class).
                                equalTo(Message.ColumnName.TOPIC.getValue(), topic);
                        if (subjects.length != predicates.length) {
                            throw new IllegalArgumentException(
                                    "subjects and predicates for a query should have same size");
                        }
                        int size = subjects.length;
                        for (int i = 0; i < size; i++) {
                            query = query.equalTo(subjects[i], predicates[i]);
                        }
                        String columnName = Message.ColumnName.valueOf(sortColumn).getValue();
                        return query.findAllSorted(columnName, sort);
                    }
                });
        return createObservable(ob);
    }

    private Observable<List<EchoMessage>> createObservable(
            Observable<RealmResults<Message>> result) {
        return result
                .map(new Function<RealmResults<Message>, List<EchoMessage>>() {
                    @Override
                    public List<EchoMessage> apply(
                            RealmResults<Message> messages) throws Exception {
                        RealmAdapter adapter = new RealmAdapter();
                        return adapter.toEchoMessages(messages);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
