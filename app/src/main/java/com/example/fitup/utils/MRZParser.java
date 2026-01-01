package com.example.fitup.utils;

import android.util.Log;

import com.example.fitup.models.MRZData;

public class MRZParser {

    private static final String TAG = "MRZParser";

    // Vietnamese ID cards use TD1 format (3 lines, 30 characters each)
    // Line 1: IDVNMDOCUMENT_NUMBER<<<<<<<<<<<<<
    // Line 2: YYMMDDXYYMMDDXNATIONALITY<<<<<<<<<
    // Line 3: SURNAME<<GIVEN_NAMES<<<<<<<<<<<<<<<

    public static MRZData parseMRZ(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        // Clean up text
        text = text.replaceAll("[^A-Z0-9<\n]", "");

        // Try to find 3 consecutive lines of 30 characters (TD1 format)
        String[] lines = text.split("\n");

        for (int i = 0; i <= lines.length - 3; i++) {
            String line1 = cleanLine(lines[i]);
            String line2 = cleanLine(lines[i + 1]);
            String line3 = cleanLine(lines[i + 2]);

            // Check if these look like MRZ lines
            if (line1.length() >= 28 && line2.length() >= 28 && line3.length() >= 28) {
                MRZData mrzData = parseTD1Format(line1, line2, line3);
                if (mrzData != null) {
                    Log.d(TAG, "Successfully parsed MRZ: " + mrzData.toString());
                    return mrzData;
                }
            }
        }

        // Also try to detect TD3 format (2 lines, 44 characters each) - for passports
        for (int i = 0; i <= lines.length - 2; i++) {
            String line1 = cleanLine(lines[i]);
            String line2 = cleanLine(lines[i + 1]);

            if (line1.length() >= 42 && line2.length() >= 42) {
                MRZData mrzData = parseTD3Format(line1, line2);
                if (mrzData != null) {
                    Log.d(TAG, "Successfully parsed MRZ (TD3): " + mrzData.toString());
                    return mrzData;
                }
            }
        }

        return null;
    }

    private static String cleanLine(String line) {
        // Remove spaces and normalize
        return line.replaceAll(" ", "").trim();
    }

    private static MRZData parseTD1Format(String line1, String line2, String line3) {
        try {
            // Ensure lines are at least 30 characters
            line1 = padLine(line1, 30);
            line2 = padLine(line2, 30);
            line3 = padLine(line3, 30);

            // Line 1: IDVNMDOCUMENT_NUMBER<<<<<<<<<<<<
            if (!line1.startsWith("I") && !line1.startsWith("A") && !line1.startsWith("C")) {
                return null;
            }

            String documentType = line1.substring(0, 2);
            String nationality = line1.substring(2, 5);
            String documentNumber = line1.substring(5, 14).replace("<", "").trim();

            // Line 2: YYMMDDXYYMMDDXNATIONALITY<<<<<<<<
            String dateOfBirth = line2.substring(0, 6);
            String gender = line2.substring(7, 8);
            String dateOfExpiry = line2.substring(8, 14);
            String nationalityCheck = line2.substring(15, 18);

            // Line 3: NAME
            String nameLine = line3.replace("<", " ").trim();

            // Validate dates
            if (!isValidDate(dateOfBirth) || !isValidDate(dateOfExpiry)) {
                Log.w(TAG, "Invalid dates in MRZ");
                return null;
            }

            // Create MRZData object
            MRZData mrzData = new MRZData(documentNumber, dateOfBirth, dateOfExpiry);
            mrzData.setDocumentType(documentType);
            mrzData.setNationality(nationality);
            mrzData.setGender(gender);
            mrzData.setName(nameLine);

            return mrzData;

        } catch (Exception e) {
            Log.e(TAG, "Error parsing TD1 MRZ", e);
            return null;
        }
    }

    private static MRZData parseTD3Format(String line1, String line2) {
        try {
            // Ensure lines are at least 44 characters
            line1 = padLine(line1, 44);
            line2 = padLine(line2, 44);

            // Line 1: Type + Nationality + Name
            String documentType = line1.substring(0, 2);
            String nationality = line1.substring(2, 5);
            String nameLine = line1.substring(5, 44).replace("<", " ").trim();

            // Line 2: Document number + DOB + Gender + Expiry
            String documentNumber = line2.substring(0, 9).replace("<", "").trim();
            String dateOfBirth = line2.substring(13, 19);
            String gender = line2.substring(20, 21);
            String dateOfExpiry = line2.substring(21, 27);

            // Validate dates
            if (!isValidDate(dateOfBirth) || !isValidDate(dateOfExpiry)) {
                Log.w(TAG, "Invalid dates in MRZ (TD3)");
                return null;
            }

            MRZData mrzData = new MRZData(documentNumber, dateOfBirth, dateOfExpiry);
            mrzData.setDocumentType(documentType);
            mrzData.setNationality(nationality);
            mrzData.setGender(gender);
            mrzData.setName(nameLine);

            return mrzData;

        } catch (Exception e) {
            Log.e(TAG, "Error parsing TD3 MRZ", e);
            return null;
        }
    }

    private static String padLine(String line, int length) {
        if (line.length() >= length) {
            return line.substring(0, length);
        }
        StringBuilder sb = new StringBuilder(line);
        while (sb.length() < length) {
            sb.append("<");
        }
        return sb.toString();
    }

    private static boolean isValidDate(String date) {
        if (date == null || date.length() != 6) {
            return false;
        }

        // Check if all digits
        if (!date.matches("\\d{6}")) {
            return false;
        }

        int year = Integer.parseInt(date.substring(0, 2));
        int month = Integer.parseInt(date.substring(2, 4));
        int day = Integer.parseInt(date.substring(4, 6));

        // Basic validation
        return month >= 1 && month <= 12 && day >= 1 && day <= 31;
    }
}