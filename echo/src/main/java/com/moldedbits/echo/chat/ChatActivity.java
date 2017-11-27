package com.moldedbits.echo.chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.moldedbits.echo.chat.core.EchoMessageFactory;
import com.moldedbits.echo.chat.core.MessageType;
import com.moldedbits.echo.chat.core.MqttService;
import com.moldedbits.echo.chat.core.MqttSession;
import com.moldedbits.echo.chat.core.model.EchoMessage;
import com.moldedbits.echo.chat.database.DatabaseExtension;
import com.moldedbits.echo.chat.utils.FileUtil;
import com.moldedbits.echo.chat.views.SpeechToTextView;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import moldedbits.com.echo.R;

public class ChatActivity extends AppCompatActivity
        implements IMqttActionListener, SpeechToTextView.OnActionPerformListener,
        ChatAdapter.OnItemClickListener, DialogInterface.OnClickListener {

    private static final String BOTTOM_SHEET = "bottom_sheet";
    private static final int REQUEST_GALLERY = 1000;
    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_DOCUMENT = 1002;

    private File fileToSend;

    private RecyclerView chatRv;

    TextView userNotPresentView;

    private ChatAdapter chatAdapter;

    private String clientId;
    private String topic = "world";
    private SpeechToTextView messageBox;

    private final List<Disposable> disposableBucket = new ArrayList<>();

    /**
     * Handler of incoming messages from service.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MqttService.CHAT_MSG_RECEIVE:
                    EchoMessage payload = msg.getData().getParcelable(MqttService.CHAT_MESSAGE_KEY);
                    Log.i(getClass().toString(), payload.getMessage());
                    chatAdapter.add(payload);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    /**
     * Messenger for communicating with service.
     */
    Messenger mService = null;

    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mIsBound;

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);
            Snackbar.make(chatRv, "Attached", Snackbar.LENGTH_LONG).show();

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null,
                        MqttService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null,
                        MqttService.CHAT_MSG_RECEIVE, this.hashCode(), 0);
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            Log.i(getClass().toString(), getString(R.string.remote_service_connected));
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            Log.i(getClass().toString(), getString(R.string.remote_service_disconnected));
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(this,
                MqttService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.i(getClass().getName(), "Binding");
    }

    @Override
    protected void onStart() {
        super.onStart();
        doBindService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        doUnbindService();
    }

    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null,
                            MqttService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            Toast.makeText(ChatActivity.this, "Unbinding",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageBox = (SpeechToTextView) findViewById(R.id.speech_to_text);
        chatRv = (RecyclerView) findViewById(R.id.rv_chat);

        // getting extra fields
        initDataAndMessages();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        chatRv.setLayoutManager(layoutManager);
        messageBox.setOnActionPerformListener(this);

        //        Realm.getDefaultInstance().addChangeListener(this);
        //        PersistenceManager.removeUnreadCount(topic);

        chatAdapter =
                new ChatAdapter(ChatActivity.this, null, clientId);
        chatRv.setAdapter(chatAdapter);
        fetchMessages();

        // creating the directories
        FileUtil.createDownloadDirectory();
        FileUtil.createUploadDirectory();
    }

    private void fetchMessages() {
        EchoConfiguration.getInstance().getExtension().fetchMessages(topic).subscribe(
                new Observer<List<EchoMessage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposableBucket.add(d);
                    }

                    @Override
                    public void onNext(List<EchoMessage> echoMessages) {
                        chatAdapter.addAll(echoMessages);
                        chatAdapter.setOnItemClickListener(ChatActivity.this);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(getClass().toString(), e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(getClass().toString(), "fetch message stream completed");
                    }
                });
    }

    private void setupToolBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //            View v = LayoutInflater
            //                    .from(this).inflate(R.layout.view_chat_toolbar, null);
            //            TextView mDoctorName = (TextView) v.findViewById(R.id.tv_name);
            //            CircleImageView mPhotoIcon = (CircleImageView) v.findViewById(R.id
            // .iv_photo);
            //            getSupportActionBar().setCustomView(v);
            //            ((Toolbar) getSupportActionBar().getCustomView().getParent())
            //                    .setContentInsetStartWithNavigation(0);
        }
    }

    private void initDataAndMessages() {
        //        clientId = "sfsdg";// getIntent().getExtras().getString(Constants.KEY_CLIENT_ID);
        //        topic = "CustomTopic"; //getIntent().getExtras().getString(Constants.KEY_TOPIC);
        //        if (TextUtils.isEmpty(mNotificationTypeId)) {
        //            if (LocalStorage.getInstance(this).getPatientId() == -1) {
        //                mNotificationTypeId = Constants.MESSAGE_PATIENT_TO_PROFESSIONAL;
        //            } else {
        //                mNotificationTypeId = Constants.MESSAGE_PROFESSIONAL_TO_PATIENT;
        //            }
        //        }
        // Setting up the toolbar
        setupToolBar();
    }

    private void setIcon(Drawable d) {
        if (d != null) {
            getSupportActionBar().setIcon(d);
        } else {
            getSupportActionBar().setIcon(ContextCompat
                    .getDrawable(ChatActivity.this, R.drawable.ic_account_circle_white));
        }
    }

    private void setCircularIcon(String path) {
        if (!TextUtils.isEmpty(path)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = true;
            final Bitmap selectImg = BitmapFactory.decodeFile(path, options);
            Bitmap bitmap = FileUtil.createCircleBitmapForToolbar(selectImg);

            BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
            setIcon(drawable);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_clear_chat) {
            deleteAllMessages();
        } else if (itemId == R.id.action_attachment) {
            openBottomSheet();
        }
        //        switch (item.getItemId()) {
        //            case R.id.action_clear_chat:
        //                createConfirmationDialog(getString(R.string.clear_chat),
        //                        getString(R.string.clear_chat_confirm),
        //                        new DialogInterface.OnClickListener() {
        //                            @Override
        //                            public void onClick(DialogInterface dialogInterface,
        // int i) {
        //                                chatAdapter.addAll(new ArrayList<MessageTable>());
        //                                chatAdapter.notifyDataSetChanged();
        //                            }
        //                        });
        //                return true;
        //            case R.id.action_attachment:
        //                if (mChatClients != null && mChatClients.isRelationshipActive() &&
        //                        !mChatClients.isFirstTimeUser()) {
        //                    FileUtil.openBottomSheet(this, this);
        //                }
        //                return true;
        //            case R.id.action_clear_chat:
        //                createConfirmationDialog(getString(R.string.clear_chat),
        //                        getString(R.string.clear_chat_confirm),
        //                        new DialogInterface.OnClickListener() {
        //                            @Override
        //                            public void onClick(DialogInterface dialogInterface,
        // int i) {
        //                                chatAdapter.addAll(new ArrayList<MessageTable>());
        //                                chatAdapter.notifyDataSetChanged();
        //                            }
        //                        });
        //                return true;
        //            case R.id.action_view_contact: {
        //                // TODO: 06/06/17
        //            }
        //            return true;
        //            default:
        //                return super.onOptionsItemSelected(item);
        //        }

        return super.onOptionsItemSelected(item);
    }

    private void openBottomSheet() {
        BottomSheetFragment bottomSheet = new BottomSheetFragment();
        bottomSheet.show(getSupportFragmentManager(), BOTTOM_SHEET);
        bottomSheet.setOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.attachment_document) {
                    // TODO: 22/06/17 Open Document
                } else if (id == R.id.attachment_gallery) {
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, REQUEST_GALLERY);
                } else {
                    try {
                        dispatchTakePictureIntent();
                    } catch (IOException e) {
                        Snackbar.make(chatRv, "Could not upload image.", Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });
    }

    private void sendImageFile(File file) {

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + System.currentTimeMillis();
        // TODO: 22/06/17 Change the directory according to need
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        fileToSend = new File(FileUtil.getUploadedImageDirectory(), imageFileName + ".jpg");
        return fileToSend;
    }

    private void dispatchTakePictureIntent() throws IOException {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();
            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.example.android.fileprovider",
                    photoFile);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                List<ResolveInfo> resInfoList = getPackageManager()
                        .queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, photoURI,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                    | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        }
    }

    private void deleteAllMessages() {
        EchoConfiguration.getInstance().getExtension().deleteAllMessages(topic).subscribe(
                new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposableBucket.add(d);
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            chatAdapter.clear();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(ChatActivity.this, "Could not remove all the messages",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //    private void createConfirmationDialog(String title, String message,
    //                                          DialogInterface.OnClickListener listener) {
    //        AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //        builder.setTitle(title);
    //        builder.setMessage(message);
    //        builder.setPositiveButton(R.string.yes, listener);
    //        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialogInterface, int i) {
    //                dialogInterface.dismiss();
    //            }
    //        });
    //        builder.show();
    //    }

    @Override
    public void onSuccess(final IMqttToken asyncActionToken) {

    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.e("mqtt", "could not message sent");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            sendImage();
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {

        }



        //        if (requestCode == FileUtil.REQUEST_IMAGE_CAPTURE && resultCode == Activity
        // .RESULT_OK) {
        //            if (TextUtils.isEmpty(mCapturedImagePath)) {
        //                Snackbar.make(mNotesView, R.string.image_upload_failed, Snackbar
        // .LENGTH_LONG)
        //                        .show();
        //            } else {
        //                Intent intent = new Intent(this, ImageCropActivity.class);
        //                intent.putExtra(Constants.KEY_PATH, mCapturedImagePath);
        //                intent.putExtra(Constants.KEY_TOPIC, topic);
        //                intent.putExtra(Constants.KEY_CLIENT_ID, clientId);
        //                startActivityForResult(intent, FileUtil.REQUEST_CROP_CAPTURE);
        //            }
        //        } else if (requestCode == FileUtil.REQUEST_PICK_IMAGE && resultCode ==
        // RESULT_OK) {
        //            Intent intent = new Intent(this, ImageCropActivity.class);
        //            intent.putExtra(Constants.KEY_URI, data.getData());
        //            intent.putExtra(Constants.KEY_TOPIC, topic);
        //            intent.putExtra(Constants.KEY_CLIENT_ID, clientId);
        //            startActivityForResult(intent, FileUtil.REQUEST_CROP_PICK);
        //        } else if (requestCode == FileUtil.REQUEST_CROP_CAPTURE &&
        //                resultCode == Activity.RESULT_OK) {
        //            uploadCroppedImages(data);
        //        } else if (requestCode == FileUtil.REQUEST_UPDATE_NOTES &&
        //                resultCode == Activity.RESULT_OK) {
        //            mNotesView.setNotes(data.getStringExtra(Constants.KEY_NOTES));
        //        } else if (requestCode == FileUtil.REQUEST_CROP_PICK && resultCode == Activity
        // .RESULT_OK) {
        //            uploadCroppedImages(data);
        //        } else if (requestCode == FileUtil.REQUEST_PICK_DOCUMENT && resultCode ==
        // RESULT_OK) {
        //            uploadDocument(data);
        //        } else if (requestCode == FileUtil.REQUEST_PICK_MY_DOCUMENT &&
        //                resultCode == Activity.RESULT_OK) {
        //            if (data.getBooleanExtra("is_image", false)) {
        //                Intent intent = new Intent(this, ImageCropActivity.class);
        //                intent.putExtra(Constants.KEY_PATH, data.getStringExtra(Constants
        // .KEY_PATH));
        //                intent.putExtra(Constants.KEY_TOPIC, topic);
        //                intent.putExtra(Constants.KEY_CLIENT_ID, clientId);
        //                startActivityForResult(intent, FileUtil.REQUEST_CROP_PICK);
        //            } else {
        //                uploadDocument(data);
        //            }
        //        } else if (requestCode == SpeechToTextView.REQ_CODE_SPEECH_INPUT &&
        //                resultCode == RESULT_OK) {
        //            if (data != null) {
        //                ArrayList<String> result = data
        //                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        //                messageBox.setText(result.get(0));
        //            }
        //        }
    }

    private void sendImage() {
        // Image Received From Camera
        if (fileToSend != null) {
            Single.create(new SingleOnSubscribe<EchoMessage>() {
                @Override
                public void subscribe(SingleEmitter<EchoMessage> e) {
                    EchoMessage message = null;
                    try {

                        message = EchoMessageFactory.generateImageOnlyMessage(topic,
                                fileToSend, "");
                        e.onSuccess(message);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        e.onError(e1);
                    }


                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<EchoMessage>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(EchoMessage message) {
                            try {
                                sendMessage(message);
                            } catch (SocketException e) {
                                e.printStackTrace();
                            }
                            fileToSend = null;

                        }

                        @Override
                        public void onError(Throwable e) {
                            fileToSend = null;

                        }
                    });


        }
    }

    @Override
    public void onPerformAction(View view) {
        String message = messageBox.getText().toString().trim();
        EchoMessage echoMessage;
        if (!TextUtils.isEmpty(message)) {
            echoMessage = EchoMessageFactory.generateChatOnlyMessage(topic, message);
            try {
                sendMessage(echoMessage);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        } else {
            Snackbar.make(view, R.string.plz_enter_msg, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(final EchoMessage message) throws SocketException {
        if (message.getMessageType() == MessageType.P2P_ATTACHMENT_DOC
                || message.getMessageType() == MessageType.P2P_ATTACHMENT_IMAGE) {
            // TODO:: show loading dialog
        }
        Single.create(new SingleOnSubscribe<MqttMessage>() {
            @Override
            public void subscribe(SingleEmitter<MqttMessage> e) throws Exception {
                e.onSuccess(MqttSession.generateMqttPayload(message));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<MqttMessage>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(MqttMessage mqttMessage) {
                        send(message, mqttMessage, message.getTopic());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(getClass().getName(), e.getMessage());
                        e.printStackTrace();
                    }
                });

    }

    private void send(final EchoMessage echoMessage, MqttMessage message, String topic) {
        try {
            // Sending message
            MqttSession.getInstance().publish(message, topic, this, new IMqttActionListener() {
                @Override
                public void onSuccess(final IMqttToken asyncActionToken) {
                    Log.i(getClass().toString(), "Sent Message success");
                    saveMessage(echoMessage);
                    // TODO: 14/06/17 check for msg non delivery, some sort of push based
                    // keep alive message
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(getClass().toString(), "Sent Message error", exception);
                    exception.printStackTrace();
                }
            });

            messageBox.setText("");
        } catch (Exception e) {
            Log.e(getClass().toString(), e.getMessage());
        }

    }

    private void saveMessage(final EchoMessage message) {
        DatabaseExtension<EchoMessage> ext = EchoConfiguration.getInstance().getExtension();
        ext.storeMessage(message).subscribe(new SingleObserver<EchoMessage>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposableBucket.add(d);
            }

            @Override
            public void onSuccess(EchoMessage message) {
                chatAdapter.add(message);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ChatActivity.this, "CouldNot send the message", Toast.LENGTH_LONG)
                        .show();
                e.printStackTrace();
            }
        });
    }

    private void disposeAll() {
        for (Disposable disposable : disposableBucket) {
            dispose(disposable);
        }
    }

    private void dispose(Disposable disposable) {
        if (!disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeAll();
    }

    @Override
    public void onItemClick(EchoMessage selectedMessage) {
        //        updateSelectedMessage(selectedMessage);
        //        if (selectedImage != null) {
        //            Intent intent = new Intent(this, ShowImageActivity.class);
        //            intent.putExtra(Constants.EXTRA_TOPIC, topic);
        ////            NextCureApplication.getInstance().setSelectedMessage(selectedImage);
        //            startActivity(intent);
        //        }
    }

    private void updateSelectedMessage(EchoMessage selectedMessage) {
        //TODO:: This message was just to check if updating realm is working
        selectedMessage.setMessage("updated");
        EchoConfiguration.getInstance().getExtension().updateMessage(selectedMessage).subscribe(
                new SingleObserver<EchoMessage>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposableBucket.add(d);
                    }

                    @Override
                    public void onSuccess(EchoMessage message) {
                        chatAdapter.updateMessage(message);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        //        switch (which) {
        //            case R.id.documents:
        //                Intent intent = new Intent(this, DocumentSelectorActivity.class);
        //                intent.putExtra(Constants.KEY_TOPIC, topic);
        //                intent.putExtra(Constants.KEY_CLIENT_ID, clientId);
        //                startActivityForResult(intent, FileUtil.REQUEST_PICK_DOCUMENT);
        //                break;
        //            case R.id.my_medical_files:
        //                Intent intent2 = new Intent(this, MedicalFileActivity.class);
        //                intent2.putExtra("select_image", true);
        //                startActivityForResult(intent2, FileUtil.REQUEST_PICK_MY_DOCUMENT);
        //                break;
        //            case R.id.camera:
        //                mCapturedImagePath = FileUtil.dispatchTakePictureIntent(this);
        //                break;
        //            case R.id.gallery:
        //                FileUtil.dispatchGalleryIntent(this);
        //                break;
        //        }
    }
}