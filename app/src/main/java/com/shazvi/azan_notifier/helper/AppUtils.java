package com.shazvi.azan_notifier.helper;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.system.ErrnoException;
import android.widget.DatePicker;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public final class AppUtils {
    private Context mContext;

    // constructor
    public AppUtils(Context context){
        this.mContext = context;
    }

    // Convert date string to DateTime
    public DateTime stringToDateTime(String dateString, String inputFormat) {
        return DateTime.parse(dateString, DateTimeFormat.forPattern(inputFormat));
    }

    // Retrieve DateTime object from date picker input
    public DateTime getDateTimeFromDatePicker(DatePicker datePicker) {
        String format = "yyyy-MM-dd";
        String formattedDate = getStringFromDatePicker(datePicker, format);
        return stringToDateTime(formattedDate, format);
    }

    // Retrieve date string from date picker input
    public String getStringFromDatePicker(DatePicker datePicker, String outputFormat) {
        int day  = datePicker.getDayOfMonth();
        int month= datePicker.getMonth();
        int year = datePicker.getYear();

        DateTime dateTime = new DateTime(year, month+1, day, 0, 0);
        DateTimeFormatter format = DateTimeFormat.forPattern(outputFormat);
        return format.print(dateTime);
    }

    // Read date string and return a date string in specified format
    // Ref:-  http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
    public String parseAndFormatDateTime(String dateString, String inputFormat, String outputFormat) {
        DateTime dateTime = stringToDateTime(dateString, inputFormat);
        return formatDateTime(dateTime, outputFormat);
    }

    // Return a date string in specified format
    // Ref:-  http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
    public String formatDateTime(DateTime dateTime, String outputFormat) {
        DateTimeFormatter format = DateTimeFormat.forPattern(outputFormat);
        return format.print(dateTime);

        // "Thu Jun 18 20:56:02 EDT 2009" --> "EEE MMM d HH:mm:ss zzz yyyy"
    }

    // Get difference between two dates
    public Duration getTimeDiff(String startTimeStr, String startTimeFormat, String endTimeStr, String endTimeFormat) {
        return getTimeDiff(startTimeStr, startTimeFormat, stringToDateTime(endTimeStr, endTimeFormat));
    }

    // Get difference between two dates
    public Duration getTimeDiff(String startTimeStr, String startTimeFormat, DateTime endTime) {
        DateTime startTime = stringToDateTime(startTimeStr, startTimeFormat);
        return getTimeDiff(startTime, endTime);
    }

    // Get difference between two dates
    public Duration getTimeDiff(DateTime startTime, DateTime endTime) {
        return new Duration(startTime, endTime);
    }

    // Get end of day time for given date
    public DateTime getEOD(DateTime dateTime) {
        return dateTime.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
    }

    // Set Datepicker date
    public void setDatePickerDate(DatePicker mDatePicker, String dateString, String inputFormat) {
        DateTime date = stringToDateTime(dateString, inputFormat);
        int year    = date.getYear() ;
        int month   = date.getMonthOfYear() - 1;
        int day     = date.getDayOfMonth();
        mDatePicker.updateDate(year, month, day);
    }


    /////////////////////// START FILE PICKER FUNCTIONS ///////////////////////
    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
    public Intent getPickImageChooserIntent() {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = mContext.getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list, so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent, add all other intents
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    // Get URI to image received from capture by camera.
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = mContext.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    //Test if we can open the given Android URI to test if permission required error is thrown
    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = mContext.getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /////////////////////// END FILE PICKER FUNCTIONS /////////////////////////


    // Returns date or time string in user friendly format
    public String prettyPrintTimeDuration(String startTimeString, String startTimeFormat) {
        return prettyPrintTimeDuration(startTimeString, startTimeFormat, DateTime.now());
    }
    public String prettyPrintTimeDuration(String startTimeString, String startTimeFormat, DateTime endTime) {
        DateTime startTime = stringToDateTime(startTimeString, startTimeFormat);
        return prettyPrintTimeDuration(startTime, endTime);
    }
    public String prettyPrintTimeDuration(DateTime startTime) {
        return prettyPrintTimeDuration(startTime, DateTime.now());
    }
    public String prettyPrintTimeDuration(DateTime startTime, DateTime endTime) {
        Duration duration = getTimeDiff(startTime, endTime);
        boolean withinLastMinute = startTime.isAfter(DateTime.now().minusMinutes(1));
        boolean withinLastHour = startTime.isAfter(DateTime.now().minusHours(1));
        boolean withinLastDay = startTime.isAfter(DateTime.now().minusDays(1));

        if(withinLastMinute) {
            return String.valueOf(duration.getStandardSeconds()) + ((duration.getStandardSeconds() == 1)? " second": " seconds")+" ago";
        } else if(withinLastHour) {
            return String.valueOf(duration.getStandardMinutes()) + ((duration.getStandardMinutes() == 1)? " minute": " minutes")+" ago";
        } else if(withinLastDay) {
            return String.valueOf(duration.getStandardHours()) + ((duration.getStandardHours() == 1)? " hour": " hours")+" ago";
        } else {
            return formatDateTime(startTime, "dd MMM yyyy");
        }
    }

    // Takes a date string as UTC timezone and returns DateTime in local timezone
    public DateTime utcToLocalDateTime(String inputDateString) {
        DateTime utcNotifTime = new DateTime(inputDateString, DateTimeZone.UTC);
        return new DateTime(utcNotifTime, DateTimeZone.forID( TimeZone.getDefault().getID() ) );
    }

    // Check if a provided string is a number
    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
