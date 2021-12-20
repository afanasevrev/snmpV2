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
public class ResetSearchDB extends Thread {
    
    public volatile boolean searchDB;
    
    public ResetSearchDB(boolean searchDB) {
        this.searchDB = searchDB;
    }
    
    @Override
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ResetSearchDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.searchDB) this.searchDB = false;
    }
}
