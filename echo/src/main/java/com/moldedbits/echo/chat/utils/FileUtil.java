package com.moldedbits.echo.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.moldedbits.echo.chat.EchoConfiguration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import static android.R.attr.width;

public class FileUtil {
    public static final int REQUEST_IMAGE_CAPTURE = 1111;
    public static final int REQUEST_CROP_CAPTURE = 9911;
    public static final int REQUEST_PICK_DOCUMENT = 9999;
    public static final int REQUEST_PICK_MY_DOCUMENT = 9998;
    public static final int REQUEST_PICK_IMAGE = 9990;
    public static final int REQUEST_CROP_PICK = 9991;
    public static final int REQUEST_UPDATE_NOTES = 1000;
    public static final int PICK_IMAGE_REQUEST = 1001;
    private static final int EOF = -1;

    public static File copyFile(String src, String dest) {
        return copyFile(src, dest, null);
    }

    public static File copyFile(String src, String dest, String name) {
        File source = new File(src);

        File destination;
        if (!TextUtils.isEmpty(name)) {
            destination = new File(dest + "/" + name);
        } else {
            destination = new File(dest + "/" + source.getName());
        }

        if (FileUtil.isUploadedDocFileNameDuplicate(destination)) {
            return null;
        }

//        try {
//            org.apache.commons.io.FileUtils.copyFile(source, destination);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return destination;
    }

//    public static File writeBitmapToFile(Bitmap bitmap) throws IOException {
//        return writeBitmapToFile(bitmap, null, 0);
//    }

//    public static File writeProfileImageToFile(Bitmap bitmap, String s,
//                                               int width) throws IOException {
//        File file;
//        if (TextUtils.isEmpty(s)) {
//            file = getImageFile(Constants.PROFILE_PIC_DIRECTORY);
//        } else {
//            file = getImageFile(Constants.PROFILE_PIC_DIRECTORY, s);
//        }
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        FileOutputStream fOut = new FileOutputStream(file);
//        if (width > 0 && width < bitmap.getWidth()) {
//            int height =
//                    AppUtils.getHeightWithAspectRatio(bitmap.getWidth(), bitmap.getHeight(), width);
//
//            bitmap = FileUtil.getResizedBitmap(bitmap, width, height);
//        }
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fOut);
//
//        fOut.flush();
//        fOut.close();
//        return file;
//
//    }

//    public static File writeBitmapToFile(Bitmap bitmap, String name, int width) throws IOException {
//        File file;
//        if (TextUtils.isEmpty(name)) {
//            file = getImageFile(Constants.UPLOAD_DIRECTORY_IMAGE);
//        } else {
//            file = getImageFile(Constants.UPLOAD_DIRECTORY_IMAGE, name);
//        }
//        if (FileUtil.isUploadedImageFileNameDuplicate(file)) {
//            return null;
//        }
//        if (!file.exists()) {
//            file.createNewFile();
//        }
//        FileOutputStream fOut = new FileOutputStream(file);
//        if (width > 0 && width < bitmap.getWidth()) {
//            int height =
//                    AppUtils.getHeightWithAspectRatio(bitmap.getWidth(), bitmap.getHeight(), width);
//            bitmap = FileUtil.getResizedBitmap(bitmap, width, height);
//        }
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//
//        fOut.flush();
//        fOut.close();
//        return file;
//    }

    public static File writeBitmapToFile(Bitmap bitmap, String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fOut = new FileOutputStream(file);
        if (width > 0 && width < bitmap.getWidth()) {
            int height =
                    AppUtils.getHeightWithAspectRatio(bitmap.getWidth(), bitmap.getHeight(), width);
            bitmap = FileUtil.getResizedBitmap(bitmap, width, height);
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

        fOut.flush();
        fOut.close();
        return file;
    }

    public static Bitmap getBitmapFromFile(int width, File file) {
        return getDecodedImage(file.getAbsolutePath(), width, width);
    }

    public static void openFileSelection(String text, Fragment fragment) {
        fragment.startActivityForResult(selectFile(text), PICK_IMAGE_REQUEST);
    }

    public static void openFileSelection(String text, Activity activity) {
        activity.startActivityForResult(selectFile(text), PICK_IMAGE_REQUEST);
    }

    public static Bitmap getBitmapFromUri(Context context, Uri imageUri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

    }

    private static Intent selectFile(String text) {
        final Intent galleryIntent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");

        // Creating other intents you want
        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Add them to an intent array
        Intent[] intents = new Intent[]{cameraIntent};

        final Intent chooserIntent = Intent.createChooser(galleryIntent, text);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        return chooserIntent;
    }

//    public static File getImageFile(String dirPath) {
//        String filePath = Environment.getExternalStorageDirectory() + dirPath;
//        File dir = new File(filePath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        int id = LocalStorage.getInstance().getPatientId() == -1 ?
//                 LocalStorage.getInstance().getProfessionalId() :
//                 LocalStorage.getInstance().getPatientId();
//        return new File(dir, "Img" + id + System.currentTimeMillis() + ".jpg");
//    }

    public static File getImageFile(String dirPath, String name) {
        String filePath = Environment.getExternalStorageDirectory() + dirPath;
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!name.toLowerCase().contains(".jpg")) {
            name += ".jpg";
        }
        return getFileWithUniqueNameForDuplicateName(new File(dir, name));
    }

    public static File getDocFile(String dirPath, String name) {
        String filePath = Environment.getExternalStorageDirectory() + dirPath;
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return getFileWithUniqueNameForDuplicateName(new File(dir, name));
    }

//    public static String uploadFileToAws(File file) {
//        AmazonS3Client s3Client = new AmazonS3Client(
//                new BasicAWSCredentials(Constants.AWS_ACCESS_KEY_ID, Constants.AWS_SECRET_KEY));
//        PutObjectRequest por = new PutObjectRequest(Constants.BUCKET_ID, file.getName(),
//                new File(file.getAbsolutePath()));
//        s3Client.putObject(por);
//        return s3Client.getResourceUrl(Constants.IDENTITY_POOL_ID, file.getName());
//    }


//    public static void openBottomSheet(Context context, int menuResource,
//                                       DialogInterface.OnClickListener listener) {
//        new BottomSheet.Builder(context, R.style.BottomSheet_Dialog).title(R.string.select)
//                .grid() // <-- important part
//                .sheet(menuResource).listener(listener).show();
//    }

//    public static String dispatchTakePictureIntent(Activity activity) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        try {
//            File file = FileUtil.createImageFile(activity);
//
//            try {
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
//                Uri photoURI = Uri.fromFile(file);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                if (takePictureIntent.resolveActivity(activity.getPackageManager()) !=
//                        null) {
//                    activity.startActivityForResult(takePictureIntent,
//                            REQUEST_IMAGE_CAPTURE);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return file.getAbsolutePath();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static String dispatchTakePictureIntent(Fragment fragment) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        try {
//            File file = FileUtil.createImageFile(fragment.getActivity());
//
//            try {
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
//                Uri photoURI = Uri.fromFile(file);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                if (takePictureIntent.resolveActivity(fragment.getActivity().getPackageManager()) !=
//                        null) {
//                    fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return file.getAbsolutePath();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static void dispatchGalleryIntent(Fragment fragment) {
        final Intent intent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    public static void dispatchGalleryIntent(Activity fragment) {
        final Intent intent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    public static Intent getOpenDocumentIntent(String path) {

        File file = new File(path);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(Uri.fromFile(file), "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent docIntent = new Intent(Intent.ACTION_VIEW);
        docIntent.setDataAndType(Uri.fromFile(file), "application/doc");
        docIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        // Add them to an intent array
        Intent[] intents = new Intent[]{docIntent};

        final Intent chooserIntent = Intent.createChooser(pdfIntent, "open");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        return chooserIntent;
    }

    public static Intent getOpenDocumentIntent(Uri path) {
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent docIntent = new Intent(Intent.ACTION_VIEW);
        docIntent.setDataAndType(path, "application/doc");
        docIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        // Add them to an intent array
        Intent[] intents = new Intent[]{docIntent};

        final Intent chooserIntent = Intent.createChooser(pdfIntent, "open");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        return chooserIntent;
    }

    public static Bitmap createCircleBitmapForToolbar(Bitmap img) {
        img = Bitmap.createScaledBitmap(img, 64, 64, true);
        Bitmap output = Bitmap.createBitmap(img.getWidth(),
                img.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, img.getWidth(), img.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(img.getWidth() / 2, img.getHeight() / 2, img.getHeight() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(img, rect, rect, paint);
        return output;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getDecodedImage(String path, int reqWidth, int reqHeight) {
        try {
            System.gc();
            // decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError error) {
            System.gc();
            return null;
        }
    }

//    private static File createImageFile(Activity activity) throws IOException {
//        // Create an image file name
//        int id = LocalStorage.getInstance().getPatientId() == -1 ?
//                 LocalStorage.getInstance().getProfessionalId() :
//                 LocalStorage.getInstance().getPatientId();
//
//        String imageFileName = "Img" + id + System.currentTimeMillis();
//        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//        return image;
//    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

//    public static final void completelyRemovePic(String key) throws IOException {
//        // removing image prom cache
//        GeneralCacheManager.getManager().removeEntry(key);
//
//        // removing file from directory
//        File cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),
//                Constants.PROFILE_PIC_DIRECTORY);
//        final File file = new File(cacheDir, key);
//        if (file.exists()) {
//            FileUtils.forceDelete(file);
//        }
//    }

    public static boolean isUploadedImageFileNameDuplicate(File uploadedFile) {
        File uploadDir = new File(
                Environment.getExternalStorageDirectory() + Constants.UPLOAD_DIRECTORY_IMAGE);
        return isUploadedFileNameDuplicate(uploadDir, uploadedFile);
    }

    public static boolean isUploadedDocFileNameDuplicate(File uploadedFile) {
        File uploadDir = new File(
                Environment.getExternalStorageDirectory() + Constants.UPLOAD_DIRECTORY_DOCUMENT);
        return isUploadedFileNameDuplicate(uploadDir, uploadedFile);
    }

    public static boolean isUploadedFileNameDuplicate(File uploadDir, File uploadedFile) {
        uploadDir.mkdirs();
        File[] files = uploadDir.listFiles();
        if (files != null && files.length > 0) {
            return searchFileName(files, uploadedFile) != null;
        }
        return false;
    }

    public static boolean isDownloadedDocumentFileNameDuplicate(File downloadedFile) {
        File uploadDir = new File(
                Environment.getExternalStorageDirectory() + Constants.DOWNLOAD_DIRECTORY_DOCUMENT);
        uploadDir.mkdirs();
        File[] files = uploadDir.listFiles();
        if (files != null && files.length > 0) {
            return searchFileName(files, downloadedFile) != null;
        }
        return false;
    }

    public static File searchFileName(File[] files, File searchFile) {
        for (File file : files) {
            if (file.getName().equals(searchFile.getName())) {
                return file;
            }
        }
        return null;
    }

    public static File getFileWithUniqueNameForDuplicateName(File downloadedFile) {
        if (isDownloadedDocumentFileNameDuplicate(downloadedFile)) {
            String name = downloadedFile.getName();
            int lastDot = name.lastIndexOf('.');
            String uniqueName = name.substring(0, lastDot) + "_" + System.currentTimeMillis() +
                    name.substring(lastDot);
            File uniqueFile = new File(downloadedFile.getParent(), uniqueName);
            return uniqueFile;
        }
        return downloadedFile;
    }

    public static byte[] fileToByte(File file) throws IOException {
        return toByteArray(toInputStream(file), (int) file.length());
    }

    public static InputStream toInputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    public static byte[] toByteArray(InputStream input, int size) throws IOException {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        }

        if (size == 0) {
            return new byte[0];
        }

        byte[] data = new byte[size];
        int offset = 0;
        int read;

        while (offset < size && (read = input.read(data, offset, size - offset)) != EOF) {
            offset += read;
        }

        if (offset != size) {
            throw new IOException("Unexpected read size. current: " + offset + ", excepted: " + size);
        }

        return data;
    }

    public static File toFile(final byte[] data, File outputDirectory,
                              final String name) throws IOException {
        File file = new File(outputDirectory, name);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(data);
        bos.flush();
        bos.close();
        return file;
    }

    public static File inputStreamToFile(final InputStream input,
                                         File outputDirectory,
                                         final String name) throws IOException {
        File file = new File(outputDirectory, name);
        try {
            OutputStream output = new FileOutputStream(file);
            try {
                try {
                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                    int read;

                    while ((read = input.read(buffer)) != -1) {
                        output.write(buffer, 0, read);
                    }
                    output.flush();
                } finally {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace(); // handle exception, define IOException and others
            }
        } finally {
            input.close();
        }
        return file;
    }

    private static String getBasePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/"
                + EchoConfiguration.getInstance().getCacheDirectoryName()
                + "/";
    }

    public static void createDownloadDirectory() {
        createDirectory(getBasePath() + "Download/Images");
        createDirectory(getBasePath() + "Download/Videos");
        createDirectory(getBasePath() + "Download/Audios");
        createDirectory(getBasePath() + "Download/Documents");
    }

    public static void createUploadDirectory() {
        createDirectory(getBasePath() + "Upload/Images");
        createDirectory(getBasePath() + "Upload/Videos");
        createDirectory(getBasePath() + "Upload/Audios");
        createDirectory(getBasePath() + "Upload/Documents");
    }

    public static File getUploadedImageDirectory() {
        return new File(getBasePath() + "Upload/Images");
    }

    public static File getDownloadedImageDirectory() {
        return new File(getBasePath() + "Download/Images");
    }

    private static void createDirectory(String absoluteFilePath) {
        File file = new File(absoluteFilePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Could not create required directories");
            }
        }
    }

    public static boolean isFileSizeOk(File file) {
        return file.length() <= 268435456;
    }
//    public static File getDocumentFile(MessageTable mMessage) {
//        String name;
//        if (mMessage.getMessageType() == MessageType.P2P_DOC.getValue() ||
//                mMessage.getMessageType() == MessageType.P2P_FIRST_DOC.getValue()) {
//            name = mMessage.getDocName();
//            if (mMessage.isIncoming()) {
//                return FileUtil.getDocFile(Constants.DOWNLOAD_DIRECTORY_DOCUMENT, name);
//            } else {
//                return FileUtil.getDocFile(Constants.UPLOAD_DIRECTORY_DOCUMENT, name);
//            }
//        } else {
//            name = mMessage.getImage().replace("/", "_");
//            if (mMessage.isIncoming()) {
//                return FileUtil.getImageFile(Constants.DOWNLOAD_DIRECTORY_IMAGE, name);
//            } else {
//                return FileUtil.getImageFile(Constants.UPLOAD_DIRECTORY_IMAGE, name);
//            }
//        }
//    }
}
