package com.moldedbits.echo.chat;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.moldedbits.echo.chat.cache.DiskCache;
import com.moldedbits.echo.chat.cache.MemoryCache;
import com.moldedbits.echo.chat.core.MessageType;
import com.moldedbits.echo.chat.core.model.EchoMessage;
import com.moldedbits.echo.chat.utils.BitmapUtils;
import com.moldedbits.echo.chat.utils.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatAdapterPresenter implements ChatAdapterContract.PresenterContract/*,
        FileActionListener*/ {

    private final ChatAdapterContract.ViewContract contract;
    private boolean isDocumentAvailable = false;
    private boolean isImageAvailable = false;
    private final List<Disposable> disposableBucket;

    public ChatAdapterPresenter(ChatAdapterContract.ViewContract contract) {
        this.contract = contract;
        this.disposableBucket = new ArrayList<>();
    }

    @Override
    public void bindData(final EchoMessage message) {
        MessageType type = message.getMessageType();
        if (type == MessageType.P2P_CHAT) {
            contract.displayMessage(message.getMessage());
        } else if (type == MessageType.P2P_ATTACHMENT_IMAGE) {
            showImage(message).subscribe(new SingleObserver<Bitmap>() {
                @Override
                public void onSubscribe(Disposable d) {
                    disposableBucket.add(d);
                }

                @Override
                public void onSuccess(Bitmap bitmap) {
                    contract.displayImage(bitmap, message, true);

                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }
            });
        }
//        // showing timestamp
//        contract.displayTimestamp(AppUtils.formatDuration(messageBox.get));
//
//        // showing message sending status icon
//        contract.displayStatusIcons(getMessageSendingStatusResource());
//
//        isDocumentAvailable =
//                messageBox.isHasDocument() && !TextUtils.isEmpty(messageBox.getDocument());
//        isImageAvailable = messageBox.isHasImage() && !TextUtils.isEmpty(messageBox.getImage());
//
//        if (isDocumentAvailable) {
//            // showing documents if present
////            getDocument(FileUtil.getDocumentFile(messageBox));
//        } else if (isImageAvailable) {
//            // showing images if present
////            getImage(FileUtil.getDocumentFile(messageBox));
//        } else {
//            // showing message if present
//            contract.displayMessage(messageBox.getMessage());
//        }
    }


    private Single<Bitmap> showImage(final EchoMessage message) {
        return Single.create(new SingleOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(SingleEmitter<Bitmap> e) throws Exception {
                // getting bitmap from memory
                Bitmap bmp = MemoryCache.getInstance().get(message.getMessageId());
                if (bmp == null) {

                    File dir = FileUtil.getUploadedImageDirectory();
                    if (message.isIncoming()) {
                        dir = FileUtil.getDownloadedImageDirectory();
                    }

                    byte[] imageArr = Base64.decode(message.getAttachment(), Base64.DEFAULT);
                    DiskCache cache = new DiskCache();
                    cache.setCacheDirectory(dir);

                    // checking from disk cache
                    File file = cache.get(dir, message.getExtras());
                    if (file == null) {
                        // saving file and writing to disk cache
                        file = new DiskCache().add(imageArr, new File(dir, message.getExtras()));
                    }
                    try {
                        if (file.exists()) {
                            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(file.getAbsolutePath(), 120, 120);
                            // adding to memory cache
                            MemoryCache.getInstance().add(message.getMessageId(), bitmap);
                            e.onSuccess(bitmap);
                        } else {
                            e.onError(new FileNotFoundException("Image could not be downloaded"));
                        }
                    } catch (NullPointerException e1) {
                        e.onError(e1);
                    } finally {
                        imageArr = null;
                    }
                } else {
                    e.onSuccess(bmp);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
//    private int getMessageSendingStatusResource() {
//        switch (messageBox.getMessageSendingStatus()) {
//            case Constants.STATUS_SENT: {
//                return R.drawable.ic_check_grey;
//            }
//            case Constants.STATUS_RECEIVED: {
//                return R.drawable.ic_check_all_grey;
//            }
//            case Constants.STATUS_ERROR: {
//                return R.drawable.ic_alert_circle_grey;
//            }
//            case Constants.STATUS_UPLOADING: {
//                return R.drawable.ic_cloud_upload_grey;
//            }
//            case Constants.STATUS_UPLOADED: {
//                return R.drawable.ic_cloud_check_grey;
//            }
//            default: {
//                return R.drawable.ic_alert_circle_grey;
//            }
//        }
//    }

//    private void fileDownloadAction(@Nullable final File file) {
//        AwsUtils.downloadFromAws(NextCureApplication.getInstance(), file, messageBox.getDocument(),
//                new TransferListener() {
//                    @Override
//                    public void onStateChanged(int id, TransferState state) {
//                        if (state == TransferState.COMPLETED) {
//                            Document document = new Document();
//                            document.setName(file.getName());
//                            document.setPath(file.getAbsolutePath());
//                            document.setFileType(messageBox.getMessageType());
//                            document.setTime(AppUtils.formatDuration(messageBox.getTime()));
//                            PersistenceManager.addDocument(document);
//                            PersistenceManager.updateImageFile(messageBox.getTopic(),
//                                    file.getAbsolutePath(), messageBox.getMessageId());
//
//                            Uri fileUri = Uri.fromFile(file);
//                            if (fileUri == null) {
//                                fileUri = Uri.fromFile(file);
//                            }
//                            contract.displayDocument(file.getName(), messageBox.getDocDate(),
//                                    fileUri, isDocumentAvailable);
//                        }
//                    }
//
//                    @Override
//                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//
//                    }
//
//                    @Override
//                    public void onError(int id, Exception ex) {
//
//                    }
//                });
//    }

//    private void getDocument(File file) {
//        if (!messageBox.isValid()) {
//            return;
//        }
//
//        // if local path is present then no need to download again
//        if (!TextUtils.isEmpty(messageBox.getDocumentLocalPath())) {
//            File localFile = new File(messageBox.getDocumentLocalPath());
//            if (localFile.exists()) {
//                contract.displayDocument(file.getName(), messageBox.getDocDate(), Uri.fromFile(file),
//                        isDocumentAvailable);
//            } else {
//                fileDownloadAction(file);
//            }
//        } else {
//
//            // No local path present or file at local path not available so downloading
//            fileDownloadAction(file);
//        }
//    }
//
//
//
//    private void getImage(File file) {
//        if(mCacheManager == null) {
//            mCacheManager = CacheManager.getManager();
//        }
//        if (!messageBox.isValid()) {
//            return;
//        }
//        mSelectedImage = new SelectedImage();
//        mSelectedImage.setPath(file.getAbsolutePath());
//        mSelectedImage.setName(file.getName());
//        // not in cache
//        if (mCacheManager.getBitmapFromCache(messageBox.getImage()) == null) {
//            // Checking in Disk. If in disk cache it from disk, Download it otherwise
//            if (TextUtils.isEmpty(messageBox.getDocumentLocalPath()) ||
//                    !new File(messageBox.getDocumentLocalPath()).exists()) {
//
//                // Downloading image
//                AwsUtils.downloadAndAddToDb(NextCureApplication.getInstance(), file, messageBox,
//                        this);
//            } else {
//                final File file1 = new File(messageBox.getDocumentLocalPath());
//                mSelectedImage.setPath(file1.getAbsolutePath());
//                mSelectedImage.setName(file1.getName());
//                final String locPath = messageBox.getDocumentLocalPath();
//                // putting the operation in thread to load image smoothly
//                new AsyncTask<String, String, Bitmap>() {
//
//                    @Override
//                    protected Bitmap doInBackground(String... strings) {
//                        int dim = NextCureApplication.getInstance().getResources()
//                                .getDimensionPixelOffset(R.dimen.image_width);
//                        final Bitmap bitmap =
//                                FileUtil.getDecodedImage(file1.getAbsolutePath(), dim, dim);
//                        mSelectedImage.setBitmap(bitmap);
//                        mCacheManager.addBitmapToCache(locPath, bitmap);
//                        return bitmap;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Bitmap bitmap) {
//                        super.onPostExecute(bitmap);
//                        if (contract != null) {
//                            contract.displayImage(bitmap,messageBox, isImageAvailable);
//                        }
//                    }
//                }.execute();
//
//            }
//        } else {
//            // in cache
//            Bitmap bitmap = mCacheManager.getBitmapFromCache(messageBox.getImage());
//            mSelectedImage.setBitmap(bitmap);
//            contract.displayImage(bitmap, messageBox, isImageAvailable);
//
//        }
//    }

//    @Override
//    public void onActionCompleted(File file, Uri fileUri) {
//        if (file != null) {
//            int width = NextCureApplication.getInstance().getResources()
//                    .getDimensionPixelOffset(R.dimen.image_width);
//            Bitmap bitmap = FileUtil.getBitmapFromFile(width, file);
//            mCacheManager.addBitmapToCache(messageBox.getImage(), bitmap);
//            mSelectedImage.setBitmap(bitmap);
//            contract.displayImage(bitmap, messageBox, isImageAvailable);
//        }
//    }
}