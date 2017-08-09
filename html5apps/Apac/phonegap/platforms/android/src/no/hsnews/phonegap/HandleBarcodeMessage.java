/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hsnews.phonegap;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import com.honeywell.decodemanager.DecodeManager;
import com.honeywell.decodemanager.SymbologyConfigs;
import com.honeywell.decodemanager.barcode.DecodeResult;
import com.honeywell.decodemanager.symbologyconfig.SymbologyConfigCode39;
import com.honeywell.decodemanager.symbologyconfig.SymbologyConfigCodeEan13;
import com.honeywell.decodemanager.symbologyconfig.SymbologyConfigCodeUPCA;
import com.honeywell.decodemanager.symbologyconfig.SymbologyConfigCodeUPCE;
import org.apache.cordova.CallbackContext;

/**
 *
 * @author ktonder
 */
public class HandleBarcodeMessage extends Handler {

    private CallbackContext callbackContext;

    private DecodeManager mDecodeManager;
    
    
    public void setCallBackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }
    
    public void setDecoderManager(DecodeManager decodeManager) {
        mDecodeManager = decodeManager;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case DecodeManager.MESSAGE_DECODER_COMPLETE:
                DecodeResult decodeResult = (DecodeResult) msg.obj;
                callbackContext.success(decodeResult.barcodeData);
                break;

            case DecodeManager.MESSAGE_DECODER_FAIL: {
                callbackContext.error("Failed to read barcode");
            }
            break;
            case DecodeManager.MESSAGE_DECODER_READY: {
                try {
                    //mDecodeManager.disableSymbology(CommonDefine.SymbologyID.SYM_ALL);
                    SymbologyConfigCode39 code39 = new SymbologyConfigCode39();
                    code39.enableCheckEnable(false);
                    code39.enableSymbology(false);
                    code39.setMaxLength(48);
                    code39.setMinLength(2);
                    
                    SymbologyConfigCodeUPCA upca = new SymbologyConfigCodeUPCA();
                    SymbologyConfigCodeUPCE upce = new SymbologyConfigCodeUPCE();
                    
                    SymbologyConfigCodeEan13 ean13 = new SymbologyConfigCodeEan13();
                    ean13.enableSymbology(true);
                    ean13.enableAddendaRequired(true);
                    ean13.enableAddenda2Digit(true);
                    ean13.enableCheckTransmit(true);
					
                    SymbologyConfigs symconfig = new SymbologyConfigs();
                    symconfig.addSymbologyConfig(ean13);
                    symconfig.addSymbologyConfig(code39);
                    symconfig.addSymbologyConfig(upca);
                    symconfig.addSymbologyConfig(upce);

                    mDecodeManager.setSymbologyConfigs(symconfig);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}
