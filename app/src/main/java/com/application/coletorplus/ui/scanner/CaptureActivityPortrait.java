package com.application.coletorplus.ui.scanner;

import android.view.View;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

public class CaptureActivityPortrait extends CaptureActivity {
    @Override
    protected DecoratedBarcodeView initializeContent() {
        DecoratedBarcodeView decoratedBarcodeView = super.initializeContent();

        // Verifica se deve esconder o viewfinder (quadrado e linha) baseado no parâmetro enviado
        boolean hideViewfinder = getIntent().getBooleanExtra("HIDE_VIEWFINDER", false);
        boolean hideLaser = getIntent().getBooleanExtra("HIDE_LASER", false);

        if (hideLaser && decoratedBarcodeView.getViewFinder() != null) {
            decoratedBarcodeView.getViewFinder().setLaserVisibility(false);
        }

        if (hideViewfinder && decoratedBarcodeView.getViewFinder() != null) {
            decoratedBarcodeView.getViewFinder().setVisibility(View.INVISIBLE);
        }

        CameraSettings cameraSettings = new CameraSettings();
        cameraSettings.setRequestedCameraId(0);
        cameraSettings.setContinuousFocusEnabled(true);

        decoratedBarcodeView.getBarcodeView().setCameraSettings(cameraSettings);

        return decoratedBarcodeView;
    }
}