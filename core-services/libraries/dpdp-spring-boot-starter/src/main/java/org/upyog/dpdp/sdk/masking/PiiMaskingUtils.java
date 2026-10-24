package org.upyog.dpdp.sdk.masking;

public final class PiiMaskingUtils {

    private PiiMaskingUtils() {
    }

    public static String maskMobile(String value) {
        return keepLast(value, 4, '*');
    }

    public static String maskEmail(String value) {
        if (value == null || !value.contains("@")) {
            return keepLast(value, 2, '*');
        }
        String[] parts = value.split("@", 2);
        String visible = parts[0].isEmpty() ? "*" : parts[0].substring(0, 1);
        return visible + "****@" + parts[1];
    }

    public static String maskAadhaar(String value) {
        return keepLast(value, 4, 'X');
    }

    public static String maskPan(String value) {
        return keepLast(value, 4, 'X');
    }

    public static String maskAccountNumber(String value) {
        return keepLast(value, 4, '*');
    }

    private static String keepLast(String value, int keep, char mask) {
        if (value == null || value.isBlank()) {
            return value;
        }
        int keepCount = Math.min(keep, value.length());
        return String.valueOf(mask).repeat(Math.max(0, value.length() - keepCount))
                + value.substring(value.length() - keepCount);
    }
}
