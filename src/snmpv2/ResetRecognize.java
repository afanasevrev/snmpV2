/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snmpv2;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AfanasevReV
 */
public class ResetRecognize extends Thread {
    public volatile boolean recognize;

    /**
     *
     * @param recognize
     */
    public ResetRecognize(boolean recognize) {
        this.recognize = recognize;
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ResetRecognize.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.recognize) this.recognize = false;
    }
}
