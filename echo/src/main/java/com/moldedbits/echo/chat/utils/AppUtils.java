package com.moldedbits.echo.chat.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.cocosw.bottomsheet.BottomSheetHelper;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import static android.content.Context.VIBRATOR_SERVICE;

public class AppUtils {
    private static final String DOB_DATE_FORMAT = "dd-MM-yyyy";
    private static final String SERVER_ACCEPTED_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm";

    public static final int REQUEST_PERMISSION = 1000;

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View focus = activity.getCurrentFocus();
            if (focus != null) {
                inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

//    public static void showTimePicker(final Context context, final View time) {
//        final Calendar calender = Calendar.getInstance();
//        int hour = calender.get(Calendar.HOUR_OF_DAY);
//        int minute = calender.get(Calendar.MINUTE);
//        TimePickerDialog timePickerDialog = new TimePickerDialog(context, R.style.ThemeDialog,
//                new TimePickerDialog.OnTimeSetListener() {
//
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        ((TextView) time).setText(getTimeIn12Hours(hourOfDay, minute));
//                    }
//                }, hour, minute, false);
//        timePickerDialog.show();
//    }

    public static BottomSheet.Builder uploadPhoto(final Context context) {
        final Intent shareIntent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        return BottomSheetHelper.shareAction((Activity) context, shareIntent);
    }

    private static String getTimeIn12Hours(int hour, int min) {
        String state = "am";
        if (hour > 12) {
            hour -= 12;
            state = "pm";
        }
        return String.format(Locale.getDefault(), "%02d:%02d %s", hour, min, state);
    }

    public static long convertStringToTimestamp(String str_date) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat(Constants.DATE_FORMAT_DD_MM_YYYY);
            Date date = (Date) formatter.parse(str_date);
            java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate.getTime();
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return 0L;
        }
    }

    public static String getCurrentTimeForTracking() {
        return new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                .format(System.currentTimeMillis());
    }

//    public static void showDatePicker(final Context context, final View dateView) {
//        final Calendar calender = Calendar.getInstance();
//        int year = calender.get(Calendar.YEAR);
//        int month = calender.get(Calendar.MONTH);
//        int day = calender.get(Calendar.DAY_OF_MONTH);
//        DatePickerDialog datePickerDialog = new DatePickerDialog(context, R.style.ThemeDialog,
//                new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                          int dayOfMonth) {
//                        ((TextView) dateView)
//                                .setText(getFormattedDate(year, monthOfYear, dayOfMonth));
//                    }
//                }, year, month, day);
//        datePickerDialog.show();
//    }

    public static String getFormattedDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return new SimpleDateFormat(DOB_DATE_FORMAT, Locale.getDefault())
                .format(calendar.getTime());
    }

    public static List<String> getWeightOptions() {
        List<String> list = new ArrayList<>();
        for (int i = Constants.WEIGHT_MIN; i <= Constants.WEIGHT_MAX; i++) {
            list.add(String.valueOf(i) + " Kg");
        }
        return list;
    }

    public static String parseDOBFormatToServer(String dateString) {
        if (!TextUtils.isEmpty(dateString)) {
            try {
                Date date = new SimpleDateFormat(DOB_DATE_FORMAT, Locale.getDefault())
                        .parse(dateString);
                return new SimpleDateFormat(SERVER_ACCEPTED_DATE_FORMAT, Locale.getDefault())
                        .format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String parseDateServerToDOB(String dateString) {
        if (!TextUtils.isEmpty(dateString)) {
            try {
                Date date = new SimpleDateFormat(SERVER_ACCEPTED_DATE_FORMAT, Locale.getDefault())
                        .parse(dateString);
                return new SimpleDateFormat(DOB_DATE_FORMAT, Locale.getDefault()).format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static boolean validateObject(Object object) {
        if (object == null || object.equals(null)) {
            return false;
        }
        if (object instanceof String && ((String) object).trim().length() <= 0) {
            return false;
        } else if (object instanceof List && ((List) object).size() <= 0) {
            return false;
        } else if (object instanceof Set && ((Set) object).size() <= 0) {
            return false;
        }
        return true;
    }

    public static SpannableString getSpannedBoldString(String strStart, String strEnd) {
        int length = strStart.length();
        SpannableString ss = new SpannableString(strStart + strEnd);
        ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    public static String getAbsolutePathFromContent(Activity activity, Intent data) {
        Uri uri = data.getData();
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        String picturePath = cursor.getString(columnIndex); // returns null
        cursor.close();
        return picturePath;
    }

    public static String formatDuration(long duration) {
        String dateFormat = "MMM d, HH:mm";
        if (duration <= 0) {
            duration = System.currentTimeMillis();
        }
        return formatDuration(duration, dateFormat);
    }

    public static String formatDuration(long duration, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);

        // Create a calendar object that will convert the date and time value in milliseconds to
        // date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(duration);
        return formatter.format(calendar.getTime());
    }
//
//    public static String getDeviceId() {
//        String deviceId = "";
//
//        final TelephonyManager mTelephony = (TelephonyManager) NextCureApplication.getInstance()
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        if (mTelephony.getDeviceId() != null) {
//            deviceId = mTelephony.getDeviceId(); //*** use for mobiles
//        } else {
//            deviceId = Settings.Secure
//                    .getString(NextCureApplication.getInstance().getContentResolver(),
//                            Settings.Secure.ANDROID_ID); //*** use for tablets
//        }
//
//        return deviceId;
//    }

//    public static String getUploadedImageSignedUrl(AmazonS3Client s3Client, String key) {
//        java.util.Date expiration = new java.util.Date();
//        long msec = expiration.getTime();
//        msec += 1000 * 60 * 60; // 1 hour.
//        expiration.setTime(msec);
//
//        GeneratePresignedUrlRequest generatePresignedUrlRequest =
//                new GeneratePresignedUrlRequest(Constants.BUCKET_ID, key);
//        generatePresignedUrlRequest.setMethod(HttpMethod.GET); // Default.
//        generatePresignedUrlRequest.setExpiration(expiration);
//
//        return s3Client.generatePresignedUrl(generatePresignedUrlRequest).toString();
//    }

    public static String getCurrentVisibleActivityName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            final Set<String> activePackages = new HashSet<>();
            final List<ActivityManager.RunningAppProcessInfo> processInfos =
                    am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (processInfo.importance ==
                        ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    activePackages.addAll(Arrays.asList(processInfo.pkgList));
                }
            }
            return activePackages.toArray(new String[activePackages.size()])[0];
        } else {
            return am.getRunningTasks(1).get(0).topActivity.toString();
        }
    }

    public static String fileSizeConversion(long size) {
        float inBytes;
        if ((inBytes = size / 1024) < 1024) {
            return inBytes + " KB";
        } else if ((inBytes = inBytes / 1024) < 1024) {
            return inBytes + " MB";
        } else if ((inBytes = inBytes / 1024) < 1024) {
            return inBytes + " GB";
        }
        return null;
    }

    public static boolean isPermissionGranted(Activity activity, String permission) {
        int permissionGrantedStatus = ContextCompat.checkSelfPermission(activity, permission);
        return permissionGrantedStatus == PackageManager.PERMISSION_GRANTED;

    }

    public static void showPermissionRequiredDialog(final Activity activity,
            final String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Permission %1s is required for this app to Download PDF.")
                .setTitle("Permission required");

        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermission(activity, new String[]{permission}, REQUEST_PERMISSION);
            }
        });
        builder.setNegativeButton("Revoke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showPermissionRequiredDialog(final Fragment activity,
            final String permission) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity.getActivity());
        builder.setMessage("Permission %1s is required for this app to Download PDF.")
                .setTitle("Permission required");

        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermission(activity, new String[]{permission}, REQUEST_PERMISSION);
            }
        });
        builder.setNegativeButton("Revok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void requestPermission(Activity activity, String[] permisionSet,
                                         int requestCode) {
        ActivityCompat.requestPermissions(activity, permisionSet, requestCode);
    }

    public static String getResponseTimeInMinutes(String responseTime, String units) {
        long intMilliSec = (int) (Double.parseDouble(responseTime));
        if (TimeUnit.MILLISECONDS.toMinutes(intMilliSec) > 0) {
            return String.format(Locale.getDefault(), "%d %s",
                    TimeUnit.MILLISECONDS.toMinutes(intMilliSec), units);
        }
        return getResponseTimeInSeconds(responseTime, "Seconds");
    }

    public static String getResponseTimeInSeconds(String responseTime, String units) {
        long intMilliSec = (int) (Double.parseDouble(responseTime));
        return String
                .format(Locale.getDefault(), "%d %s", TimeUnit.MILLISECONDS.toSeconds(intMilliSec),
                        units);
    }

    public static void requestPermission(Fragment fragment, String[] permisionSet,
                                         int requestCode) {
        fragment.requestPermissions(permisionSet, requestCode);
    }


    public static int getHeightWithAspectRatio(int originalWidth, int originalHeight,
            int curWidth) {
        return (int) (((float) originalHeight / (float) originalWidth) * curWidth);
    }

    public static void vibrate(Context activity) {
        // Vibrating if Chat Screen is not visible
        ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(800);
    }

    public static void underlineText(TextView textView, String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
    }

    public static Bundle parseUrl(String url) {
        String[] objects = url.split("&");
        Bundle bundle = new Bundle();
        for (String s : objects) {
            String[] inner = s.split("=");
            bundle.putString(inner[0], inner[1]);
        }
        return bundle;
    }

    public static String getFirstNSpaceSeparatedValues(String secOption, int n) {
        StringBuilder builder = new StringBuilder();
        String[] list = secOption.split(" ");
        if (n < 0) {
            throw new IllegalArgumentException("Second argument cannot be less than 0");
        }
        if (n > list.length) {
            n = list.length;
        }
        for (int i = 0; i < n; i++) {
            builder.append(list[i]);
        }

        return builder.toString();
    }

    @SuppressLint("WrongConstant")
    public static String getAgeFromDateOfBirth(int year, int month, int day) {
            Calendar dob = Calendar.getInstance();
            Calendar today = Calendar.getInstance();
            dob.set(year, month, day);
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
                age--;
            }
            return String.valueOf(age);
    }
}