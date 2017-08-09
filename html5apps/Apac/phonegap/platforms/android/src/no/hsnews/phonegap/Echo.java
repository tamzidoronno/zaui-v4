/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hsnews.phonegap;

import android.os.RemoteException;
import com.honeywell.decodemanager.DecodeManager;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author ktonder
 */
public class Echo extends CordovaPlugin {
    private HandleBarcodeMessage handler = new HandleBarcodeMessage();
    private DecodeManager mDecodeManager = null;
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("echo")) {
            if (mDecodeManager == null) {
                mDecodeManager = new DecodeManager(this.cordova.getActivity(), handler);
                handler.setDecoderManager(mDecodeManager);
            }
            
            try {
                handler.setCallBackContext(callbackContext);
                mDecodeManager.doDecode(10000);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            return true;
        }
        
        return false;
    }    

    
}
