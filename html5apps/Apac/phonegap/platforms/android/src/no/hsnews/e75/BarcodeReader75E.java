/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hsnews.e75;

import android.content.Context;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.ScannerNotClaimedException;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author ktonder
 */
public class BarcodeReader75E extends CordovaPlugin implements BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener {
    private AidcManager manager;
    private BarcodeReader barcodeReader;
    private CallbackContext callBackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callBackContext = callbackContext;
        
        if (barcodeReader == null)
            createReader();
        
        if (action.equals("stop")) {
            try {
                stopRead();
                return true;
            } catch (UnsupportedPropertyException ex) {
                Logger.getLogger(BarcodeReader75E.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ScannerNotClaimedException ex) {
                Logger.getLogger(BarcodeReader75E.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ScannerUnavailableException ex) {
                Logger.getLogger(BarcodeReader75E.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (action.equals("echo")) {
            try {
                startRead();
                return true;
            } catch (UnsupportedPropertyException ex) {
                Logger.getLogger(BarcodeReader75E.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ScannerNotClaimedException ex) {
                Logger.getLogger(BarcodeReader75E.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ScannerUnavailableException ex) {
                Logger.getLogger(BarcodeReader75E.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }
    
    

    private void createReader() {
        Context context=this.cordova.getActivity().getApplicationContext();

        AidcManager.create(context, new AidcManager.CreatedCallback() {
        
            @Override
            public void onCreated(AidcManager aidcManager) {
                manager = aidcManager;
                barcodeReader = manager.createBarcodeReader();
            }
        });
    }

    private void stopRead() throws UnsupportedPropertyException, ScannerNotClaimedException, ScannerUnavailableException {
        if (barcodeReader != null) {
            barcodeReader.claim();
            barcodeReader.light(false);
            barcodeReader.decode(false);
            barcodeReader.softwareTrigger(false);
            barcodeReader.release();
        }
    }
    
    private void startRead() throws UnsupportedPropertyException, ScannerNotClaimedException, ScannerUnavailableException {
        if (barcodeReader != null) {

            // register bar code event listener
            barcodeReader.addBarcodeListener(this);

            // set the trigger mode to client control
            barcodeReader.setProperty(BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE, BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);
            
            // register trigger state change listener
            barcodeReader.addTriggerListener(this);

            Map<String, Object> properties = new HashMap<String, Object>();
            // Set Symbologies On/Off
            properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
            
            properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, true);
            
            // UPC - A
            properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_TWO_CHAR_ADDENDA_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_ADDENDA_REQUIRED_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_CHECK_DIGIT_TRANSMIT_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_COMBINE_COUPON_CODE_MODE_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_A_COUPON_CODE_MODE_ENABLED, true);
            
            // UPC - E
            properties.put(BarcodeReader.PROPERTY_UPC_E_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_E_EXPAND_TO_UPC_A, false);
            properties.put(BarcodeReader.PROPERTY_UPC_E_ADDENDA_SEPARATOR_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_E_NUMBER_SYSTEM_TRANSMIT_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_UPC_E_TWO_CHAR_ADDENDA_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_UPC_E_ADDENDA_REQUIRED_ENABLED, true);
            
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_CHECK_DIGIT_MODE, false);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_MINIMUM_LENGTH, 8);
            properties.put(BarcodeReader.PROPERTY_INTERLEAVED_25_MAXIMUM_LENGTH, 10);
            
            properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
            properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, false);
            
            properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
            properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
            
            // Set Max Code 39 barcode length
            properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 80);
            // Turn on center decoding
            properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
            // Enable bad read response
            properties.put(BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);
            // Apply the settings
            barcodeReader.setProperties(properties);
            
            barcodeReader.claim();
            barcodeReader.light(true);
            barcodeReader.decode(true);
            barcodeReader.softwareTrigger(true);
        }
    }

    @Override
    public void onBarcodeEvent(BarcodeReadEvent event) {
        System.out.println("===============================");
        System.out.println("===============================");
        System.out.println("===============================");
        System.out.println("===============================");
        System.out.println(event.getBarcodeData());
        System.out.println("===============================");
        System.out.println("===============================");
        System.out.println("===============================");
        System.out.println("===============================");
        System.out.println("===============================");
        callBackContext.success(event.getBarcodeData());
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent bfe) {
        callBackContext.error(bfe.getTimestamp());
    }

    @Override
    public void onTriggerEvent(TriggerStateChangeEvent tsce) {
        System.out.println("Triggered");
    }
}
