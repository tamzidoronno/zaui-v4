/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshop.verifone;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import no.point.paypoint.PayPointEvent;
import no.point.paypoint.PayPointListener;

/**
 *
 * @author ktonder
 */
public class VerifoneTerminalListener implements PayPointListener, ActionListener {

    private PayPointEvent ppe;
    private ActionEvent e;
    private final VerifoneApp app;

    public VerifoneTerminalListener(VerifoneApp app) {
        this.app = app;
    }
    

    @Override
    public void getPayPointEvent(PayPointEvent ppe) {
        clear();
        this.ppe = ppe;
        app.getPayPointEvent(ppe);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        clear();
        app.actionPerformed(e);
    }

    private void clear() {
        this.e = null;
        this.ppe = null;
    }
    
}
