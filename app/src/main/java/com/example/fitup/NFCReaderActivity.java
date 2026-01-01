package com.example.fitup;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitup.models.PersonalInfo;
import com.example.fitup.utils.NFCHelper;

import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;
import net.sf.scuba.smartcards.CommandAPDU;
import net.sf.scuba.smartcards.ResponseAPDU;

import org.jmrtd.BACKey;
import org.jmrtd.PassportService;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.icao.MRZInfo;

import java.security.Security;

public class NFCReaderActivity extends AppCompatActivity {

    private static final String TAG = "NFCReader";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    private TextView statusText;
    private ProgressBar progressBar;

    private String documentNumber;
    private String dateOfBirth;
    private String dateOfExpiry;

    private boolean isReading = false;

    // eMRTD Application AID (Application Identifier)
    private static final byte[] MRTD_AID = {
            (byte) 0xA0, 0x00, 0x00, 0x02, 0x47, 0x10, 0x01
    };

    static {
        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                boolean isSuccess = false;

                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    isSuccess = result.getData().getBooleanExtra("IS_SUCCESS", false);
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("IS_SUCCESS", isSuccess);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_reader);

        statusText = findViewById(R.id.statusText);
        progressBar = findViewById(R.id.progressBar);

        // Get MRZ data from intent
        documentNumber = getIntent().getStringExtra("documentNumber");
        dateOfBirth = getIntent().getStringExtra("dateOfBirth");
        dateOfExpiry = getIntent().getStringExtra("dateOfExpiry");

        if (documentNumber == null || dateOfBirth == null || dateOfExpiry == null) {
            Toast.makeText(this, "MRZ data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Doc: " + documentNumber + ", DOB: " + dateOfBirth + ", Exp: " + dateOfExpiry);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_MUTABLE);

        statusText.setText("Hold your ID card against the back of the phone");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (isReading) {
            return;
        }

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction()) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                readNFCTag(tag);
            }
        }
    }

    private void readNFCTag(Tag tag) {
        isReading = true;
        statusText.setText("Reading NFC chip...");
        progressBar.setVisibility(ProgressBar.VISIBLE);

        new Thread(() -> {
            try {
                PersonalInfo info = readChipData(tag);

                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    if (info != null) {
                        statusText.setText("Read successful!");
                        showResults(info);
                    } else {
                        statusText.setText("Failed to read chip data");
                        Toast.makeText(this, "Failed to read chip data",
                                Toast.LENGTH_SHORT).show();
                    }
                    isReading = false;
                });

            } catch (Exception e) {
                Log.e(TAG, "Error reading NFC", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    statusText.setText("Error: " + e.getMessage());
                    Toast.makeText(this, "Error reading chip: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    isReading = false;
                });
            }
        }).start();
    }

    private PersonalInfo readChipData(Tag tag) throws Exception {
        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep == null) {
            throw new Exception("IsoDep not supported");
        }

        // Increase timeout for Vietnamese cards
        isoDep.setTimeout(5000);

        CardService cardService = CardService.getInstance(isoDep);
        cardService.open();

        PassportService passportService = null;

        try {
            Log.d(TAG, "Selecting eMRTD application...");
            selecteMRTDApplication(cardService);

            passportService = new PassportService(
                    cardService,
                    PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                    PassportService.DEFAULT_MAX_BLOCKSIZE,
                    false,
                    true
            );

            passportService.open();
            Log.d(TAG, "PassportService opened");

            BACKey bacKey = new BACKey(documentNumber, dateOfBirth, dateOfExpiry);

            Log.d(TAG, "Starting BAC authentication...");
            Log.d(TAG, "BAC params - Doc: " + documentNumber + ", DOB: " + dateOfBirth + ", Exp: " + dateOfExpiry);

            try {
                passportService.doBAC(bacKey);
                Log.d(TAG, "BAC authentication successful!");

                Log.d(TAG, "Selecting Master File after BAC...");
                selectMasterFileAfterBAC(passportService);

                if (!passportService.isOpen()) {
                    throw new Exception("PassportService not properly opened after BAC");
                }

                Log.d(TAG, "Secure messaging established, reading data groups...");

            } catch (CardServiceException e) {
                Intent intent = new Intent(NFCReaderActivity.this, ScanResultActivity.class);
                intent.putExtra("ERROR_MESSAGE", "Authentication failed. Please verify the MRZ data was scanned correctly.");
                intent.putExtra("IS_SUCCESS", false);

                startActivity(intent);
                finish();

                throw new Exception("Authentication failed. Please verify the MRZ data was scanned correctly.");
            }

            Log.d(TAG, "Reading DG1...");
            try {
                byte[] dg1Bytes = null;

                try {
                    Log.d(TAG, "Attempting to read EF.COM...");
                    java.io.InputStream comStream = passportService.getInputStream(PassportService.EF_COM);
                    comStream.close();
                    Log.d(TAG, "EF.COM accessible, trying DG1 with standard method...");

                    dg1Bytes = readAllBytes(passportService.getInputStream(PassportService.EF_DG1));
                } catch (Exception e1) {
                    Log.w(TAG, "Standard method failed, trying alternative...", e1);

                    try {
                        Log.d(TAG, "Trying with short FID...");
                        short shortFID = 0x0101;
                        dg1Bytes = readAllBytes(passportService.getInputStream(shortFID));
                    } catch (Exception e2) {
                        Intent intent = new Intent(NFCReaderActivity.this, ScanResultActivity.class);
                        intent.putExtra("ERROR_MESSAGE", "All methods to read DG1 failed.");
                        intent.putExtra("IS_SUCCESS", false);

                        startActivity(intent);
                        finish();

                        throw new Exception("All methods to read DG1 failed. May need PACE instead of BAC.");
                    }
                }

                if (dg1Bytes == null || dg1Bytes.length == 0) {
                    throw new Exception("DG1 data is empty");
                }

                DG1File dg1 = new DG1File(new java.io.ByteArrayInputStream(dg1Bytes));
                MRZInfo mrzInfo = dg1.getMRZInfo();
                Log.d(TAG, "DG1 read successfully!");

                byte[] photoData = null;
                try {
                    Log.d(TAG, "Reading DG2...");
                    byte[] dg2Bytes = readAllBytes(passportService.getInputStream(PassportService.EF_DG2));
                    if (dg2Bytes != null && dg2Bytes.length > 0) {
                        DG2File dg2 = new DG2File(new java.io.ByteArrayInputStream(dg2Bytes));
                        photoData = NFCHelper.extractPhoto(dg2);
                        Log.d(TAG, "Photo extracted: " + (photoData != null ? photoData.length + " bytes" : "null"));
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Could not read DG2 (photo), continuing without it", e);
                }

                PersonalInfo info = new PersonalInfo();
                info.setDocumentNumber(mrzInfo.getDocumentNumber());
                info.setName(mrzInfo.getPrimaryIdentifier() + " " +
                        mrzInfo.getSecondaryIdentifier());

                info.setNationality(mrzInfo.getNationality());
                info.setDateOfBirth(mrzInfo.getDateOfBirth());
                info.setDateOfExpiry(mrzInfo.getDateOfExpiry());
                info.setGender(mrzInfo.getGender().toString());
                info.setPhotoData(photoData);

                Log.d(TAG, "Successfully read all data: " + info.toString());

                return info;

            } catch (Exception e) {
                Intent intent = new Intent(NFCReaderActivity.this, ScanResultActivity.class);
                intent.putExtra("ERROR_MESSAGE", "Failed to read card data after authentication: " + e.getMessage());
                intent.putExtra("IS_SUCCESS", false);

                startActivity(intent);
                finish();

                throw new Exception("Failed to read card data after authentication: " + e.getMessage());
            }

        } finally {
            try {
                if (passportService != null) {
                    passportService.close();
                }
                cardService.close();
            } catch (Exception e) {
                Log.e(TAG, "Error closing services", e);
            }
        }
    }

    /**
     * Select Master File (MF) after BAC authentication.
     * This is required for some cards including Vietnamese CIC.
     */
    private void selectMasterFileAfterBAC(PassportService ps) throws Exception {
        try {
            // Try to use PassportService's sendSelectApplet which uses secure messaging
            ps.sendSelectApplet(true); // true = select eMRTD applet with secure messaging
            Log.d(TAG, "Master File selected successfully with secure messaging");
        } catch (Exception e) {
            Log.w(TAG, "Could not select MF, continuing anyway...", e);
            // Don't throw - some cards may not need this
        }
    }

    /**
     * Helper method to read all bytes from an InputStream
     */
    private byte[] readAllBytes(java.io.InputStream is) throws Exception {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    /**
     * Read a data group using READ BINARY with SFI (Short File Identifier).
     * This avoids the SELECT FILE command that causes issues with Vietnamese cards.
     */
    private byte[] readDataGroupDirect(CardService cs, byte dgNumber) throws Exception {
        try {
            Log.d(TAG, "Reading DG" + dgNumber + " using SFI...");

            // Calculate SFI from DG number
            // DG1 -> SFI 0x01, DG2 -> SFI 0x02, etc.
            byte sfi = dgNumber;

            // Read first 4 bytes to get the length
            // READ BINARY with SFI: CLA=0x00, INS=0xB0, P1=(0x80 | SFI), P2=0x00
            byte p1 = (byte)(0x80 | sfi);
            CommandAPDU readHeader = new CommandAPDU(0x00, 0xB0, p1, 0x00, 4);

            ResponseAPDU response = cs.transmit(readHeader);
            if (response.getSW() != 0x9000) {
                Log.e(TAG, "Failed to read DG" + dgNumber + " header: " +
                        String.format("0x%04X", response.getSW()));
                throw new Exception("Failed to read header: " + String.format("0x%04X", response.getSW()));
            }

            byte[] header = response.getData();
            if (header.length < 4) {
                throw new Exception("Header too short");
            }

            // Parse TLV length (simplified - assumes length < 128)
            int totalLength;
            int offset = 0;

            if (header[0] == 0x61 || header[0] == 0x75 || header[0] == 0x63) {
                // Standard DG tag
                offset = 1;
                if ((header[1] & 0x80) == 0) {
                    // Short form length
                    totalLength = header[1] & 0xFF;
                    offset = 2;
                } else {
                    // Long form length
                    int numLengthBytes = header[1] & 0x7F;
                    totalLength = 0;
                    for (int i = 0; i < numLengthBytes && (offset + i) < header.length; i++) {
                        totalLength = (totalLength << 8) | (header[offset + i] & 0xFF);
                    }
                    offset += numLengthBytes;
                }
                totalLength += offset; // Include tag and length bytes
            } else {
                throw new Exception("Unexpected tag: " + String.format("0x%02X", header[0]));
            }

            Log.d(TAG, "DG" + dgNumber + " total length: " + totalLength);

            // Read the entire file in chunks
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            int bytesRead = 0;
            int chunkSize = 224; // Safe chunk size for most cards

            while (bytesRead < totalLength) {
                int toRead = Math.min(chunkSize, totalLength - bytesRead);

                // P1 = (0x80 | SFI) for first read, then changes based on offset
                // For large files, we need to handle offset properly
                int readOffset = bytesRead;
                byte p1Read = (byte)(0x80 | sfi);
                byte p2Read = 0;

                if (readOffset > 0) {
                    // For subsequent reads, don't use SFI, use normal addressing
                    p1Read = (byte)((readOffset >> 8) & 0xFF);
                    p2Read = (byte)(readOffset & 0xFF);
                }

                CommandAPDU readCmd = new CommandAPDU(0x00, 0xB0, p1Read, p2Read, toRead);
                ResponseAPDU readResp = cs.transmit(readCmd);

                if (readResp.getSW() != 0x9000) {
                    Log.e(TAG, "Failed to read chunk at offset " + bytesRead + ": " +
                            String.format("0x%04X", readResp.getSW()));
                    break;
                }

                byte[] chunk = readResp.getData();
                baos.write(chunk);
                bytesRead += chunk.length;

                Log.d(TAG, "Read " + bytesRead + "/" + totalLength + " bytes");

                if (chunk.length < toRead) {
                    // Card sent less than requested, we're done
                    break;
                }
            }

            byte[] result = baos.toByteArray();
            Log.d(TAG, "Successfully read DG" + dgNumber + ": " + result.length + " bytes");
            return result;

        } catch (Exception e) {
            Log.e(TAG, "Failed to read DG" + dgNumber, e);
            throw e;
        }
    }

    /**
     * Read a data group using SFI (Short File Identifier).
     * This is an alternative method that works better with Vietnamese cards.
     */
    private byte[] readDataGroup(PassportService ps, short fid) throws Exception {
        try {
            // Try using PassportService's getInputStream but catch and log errors
            java.io.InputStream is = ps.getInputStream(fid);
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

            byte[] buffer = new byte[256];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();

        } catch (Exception e) {
            Log.e(TAG, "Failed to read data group 0x" + Integer.toHexString(fid), e);
            throw e;
        }
    }

    /**
     * Select the eMRTD application on the card.
     * This MUST be done before attempting BAC.
     */
    private void selecteMRTDApplication(CardService cardService) throws Exception {
        try {
            // Build SELECT FILE command for eMRTD application
            CommandAPDU selectApplet = new CommandAPDU(
                    0x00, // CLA
                    0xA4, // INS (SELECT FILE)
                    0x04, // P1 (Select by name)
                    0x0C, // P2 (First or only occurrence)
                    MRTD_AID, // Application ID
                    256   // Le (expected length)
            );

            Log.d(TAG, "Sending SELECT eMRTD command...");
            ResponseAPDU response = cardService.transmit(selectApplet);

            int sw = response.getSW();
            Log.d(TAG, "SELECT response SW: " + String.format("0x%04X", sw));

            if (sw == 0x9000) {
                Log.d(TAG, "eMRTD application selected successfully");
            } else {
                throw new Exception("Failed to select eMRTD application. SW: " +
                        String.format("0x%04X", sw));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error selecting eMRTD application", e);
            throw new Exception("Failed to select eMRTD application: " + e.getMessage());
        }
    }

    private void showResults(PersonalInfo info) {
        Intent intent = new Intent(this, ScanResultActivity.class);
        intent.putExtra("personalInfo", info);
        intent.putExtra("IS_SUCCESS", true);
        launcher.launch(intent);

        //startActivity(intent);
        //finish();
    }
}