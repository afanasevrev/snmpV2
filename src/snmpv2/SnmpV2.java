/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snmpv2;

/**
 *
 * @author Револий
 */
public class SnmpV2 {

    /**
     * @param args the command line arguments
     */
    
    static String LPRDB1 = "[1.3.6.1.4.1.12.3.1.0 = LPRDB|2|SEARCH_RESULT->ARM:WIN-6JF3PBLIACQ,UserId:,Module:lprdb.run]";
    static String ULPR1 = "[1.3.6.1.4.1.12.3.1.0 = ULPR|3|NUMBER_DETECTED->ARM:WIN-6JF3PBLIACQ,UserId:,Module:urmlpr.run]";
    static String ULPR2 = "[1.3.6.1.4.1.12.3.1.0 = ULPR|4|NUMBER_DETECTED->ARM:WIN-6JF3PBLIACQ,UserId:,Module:urmlpr.run]";
    static String UnipingIpaddress_1 = "192.168.0.102";
    static String name_1 = "Центральный въезд";
  //  static String ipaddr_1 = "udp:192.168.1.51/162";
    static String ipaddr_1 = "udp:10.64.32.198/162";
    
    static String LPRDB2 = "[1.3.6.1.4.1.12.3.1.0 = LPRDB|1|SEARCH_RESULT->ARM:WIN-6JF3PBLIACQ,UserId:,Module:lprdb.run]";
    static String ULPR3 = "[1.3.6.1.4.1.12.3.1.0 = ULPR|1|NUMBER_DETECTED->ARM:WIN-6JF3PBLIACQ,UserId:,Module:urmlpr.run]";
    static String ULPR4 = "[1.3.6.1.4.1.12.3.1.0 = ULPR|2|NUMBER_DETECTED->ARM:WIN-6JF3PBLIACQ,UserId:,Module:urmlpr.run]";
    static String UnipingIpaddress_2 = "192.168.0.101";
    static String name_2 = "Задний двор";
    static String ipaddr_2 = "udp:127.0.0.1/162";
   // static String ipaddr_2 = "udp:10.64.32.198/161";
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        SnmpWrapper firstThread = new SnmpWrapper(LPRDB1,ULPR1,ULPR2,UnipingIpaddress_1,name_1,ipaddr_1);
        SnmpWrapper secondThread = new SnmpWrapper(LPRDB2,ULPR3,ULPR4,UnipingIpaddress_2,name_2, ipaddr_2);
        
        firstThread.start();
        secondThread.start();
        firstThread.send();
        secondThread.send();
        System.out.println("Модуль автоматического открытия шлагбаума запущен");
    }
}
