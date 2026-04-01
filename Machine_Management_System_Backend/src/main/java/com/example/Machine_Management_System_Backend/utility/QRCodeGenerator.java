package com.example.Machine_Management_System_Backend.utility;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.nio.file.Path;
import java.nio.file.Paths;

public class QRCodeGenerator {
    public static void generateQRCode(String text,String path, int width, int height) throws Exception {
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,width,height);
        Path filePath = Paths.get(path);
        MatrixToImageWriter.writeToPath(bitMatrix,"PNG",filePath);

    }
}
