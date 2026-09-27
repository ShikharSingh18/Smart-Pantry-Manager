package com.shikharsingh.smartpantrymanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Shared date-comparison helper so MainActivity and PantryAdapter stay in sync
public class ExpiryUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    //  Days until expiry (negative if already expired), or Integer.MIN_VALUE if no valid date
    public static int daysUntilExpiry(String expiryDate) {
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            return Integer.MIN_VALUE;
        }
        try {
            Date expiry = DATE_FORMAT.parse(expiryDate.trim());
            if (expiry == null) return Integer.MIN_VALUE;

            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            long diffMillis = expiry.getTime() - today.getTimeInMillis();
            return (int) (diffMillis / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            return Integer.MIN_VALUE;
        }
    }

    public static boolean isExpired(String expiryDate) {
        int days = daysUntilExpiry(expiryDate);
        return days != Integer.MIN_VALUE && days < 0;
    }

    public static boolean isExpiringSoon(String expiryDate, int thresholdDays) {
        int days = daysUntilExpiry(expiryDate);
        return days != Integer.MIN_VALUE && days >= 0 && days <= thresholdDays;
    }
}