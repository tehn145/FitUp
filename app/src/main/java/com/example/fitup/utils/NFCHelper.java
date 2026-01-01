package com.example.fitup.utils;

import android.util.Log;

import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

public class NFCHelper {

    private static final String TAG = "NFCHelper";

    /**
     * Extract photo data from DG2 file
     */
    public static byte[] extractPhoto(DG2File dg2) {
        try {
            List<FaceInfo> faceInfos = dg2.getFaceInfos();
            if (faceInfos != null && !faceInfos.isEmpty()) {
                FaceInfo faceInfo = faceInfos.get(0);
                List<FaceImageInfo> faceImageInfos = faceInfo.getFaceImageInfos();

                if (faceImageInfos != null && !faceImageInfos.isEmpty()) {
                    FaceImageInfo faceImageInfo = faceImageInfos.get(0);

                    // Get the image bytes
                    int imageLength = faceImageInfo.getImageLength();
                    InputStream imageInputStream = faceImageInfo.getImageInputStream();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    int totalRead = 0;

                    while (totalRead < imageLength &&
                            (bytesRead = imageInputStream.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                    }

                    byte[] imageBytes = baos.toByteArray();
                    Log.d(TAG, "Extracted photo: " + imageBytes.length + " bytes");

                    return imageBytes;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error extracting photo", e);
        }

        return null;
    }

    /**
     * Convert byte array to hex string for debugging
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return "";

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    /**
     * Format MRZ date (YYMMDD) to readable format
     */
    public static String formatDate(String mrzDate) {
        if (mrzDate == null || mrzDate.length() != 6) {
            return mrzDate;
        }

        try {
            String year = mrzDate.substring(0, 2);
            String month = mrzDate.substring(2, 4);
            String day = mrzDate.substring(4, 6);

            // Assume 20XX for years 00-30, 19XX for years 31-99
            int yearInt = Integer.parseInt(year);
            String fullYear = (yearInt <= 30) ? "20" + year : "19" + year;

            return day + "/" + month + "/" + fullYear;
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date", e);
            return mrzDate;
        }
    }

    /**
     * Clean up name from MRZ format
     */
    public static String formatName(String mrzName) {
        if (mrzName == null) return "";

        // Replace < with spaces and clean up
        String cleaned = mrzName.replace("<", " ")
                .replaceAll("\\s+", " ")
                .trim();

        // Capitalize each word
        String[] words = cleaned.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                result.append(word.substring(0, 1).toUpperCase());
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }

        return result.toString().trim();
    }
}