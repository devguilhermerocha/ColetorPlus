package com.application.coletorplus.ui.scanner;

import androidx.activity.result.ActivityResultLauncher;

import com.application.coletorplus.ui.scanner.CaptureActivityPortrait;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScannerHelper {
    public static void escanearEndereco(ActivityResultLauncher<ScanOptions> launcher) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Aponte para o QR Code do Endereço / Rua");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE); // Foco exclusivo em QR Code
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureActivityPortrait.class);
        options.addExtra("HIDE_LASER", true);

        launcher.launch(options);
    }
    public static void escanearProduto(ActivityResultLauncher<ScanOptions> launcher) {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Aponte para o código de barras (EAN) do Produto");
        options.setDesiredBarcodeFormats(ScanOptions.PRODUCT_CODE_TYPES); // Foco exclusivo em EAN/UPC
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(false);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureActivityPortrait.class);
        options.addExtra("HIDE_VIEWFINDER", false); // Manter visual no Código de Barras

        launcher.launch(options);
    }
}