/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snmpv2;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
/**
 *
 * @author Револий
 */
public class SnmpWrapper extends Thread {
    
    //public volatile boolean recognize = false;
    //public volatile boolean searchResult = false;
   
    ResetRecognize resetRecognize = new ResetRecognize(false);
    ResetSearchDB resetSearchDB = new ResetSearchDB(false);
    
    private final String ipaddress; // IP адрес сервера на котором разворачивается ловушка
    
    private final String LPRDB;
    private final String ULPR1;
    private final String ULPR2;
    private final String UnipingIpaddress;  // IP адрес устройства SNMP
    private final String name;
    
    private Snmp snmp = null;
    private Address targetAddress = null;
    private TransportMapping transport = null;
    
   
    /**
     *
     * @param LPRDB
     * @param ULPR1
     * @param ULPR2
     * @param UnipingIpaddress
     * @param name
     * @param ipaddress
     * @throws Exception
     */
   
    
    public SnmpWrapper(String LPRDB, String ULPR1, String ULPR2, String UnipingIpaddress, String name, String ipaddress) throws Exception {
        this.ipaddress = ipaddress;
        transport = new DefaultUdpTransportMapping();
        targetAddress = GenericAddress.parse(this.ipaddress);
        snmp = new Snmp(transport);
        
        this.LPRDB = LPRDB;
        this.ULPR1 = ULPR1;
        this.ULPR2 = ULPR2;
        this.UnipingIpaddress = UnipingIpaddress;
        this.name = name;
        
        CommandResponder trapPrinter = (CommandResponderEvent e) -> {
            PDU command = e.getPDU();
            Date date = new Date();
            SimpleDateFormat formatForDateNow = new SimpleDateFormat("E yyyy.MM.dd kk:mm:ss");
            
            /*Внутри класса PDU реализован полный функционал для анализа SNMP трапа.*/
            /*
            if (command != null) {
                      System.out.println(this.name + " Получен трап: " + command.getVariableBindings().toString());
                                 } 
            */
            if ((this.ULPR1.equals(command.getVariableBindings().toString())) || (this.ULPR2.equals(command.getVariableBindings().toString()))) {
                       resetRecognize = new ResetRecognize(true);
                       resetRecognize.start();
            } 
            
            if (this.LPRDB.equals(command.getVariableBindings().toString()))  {
                       resetSearchDB = new ResetSearchDB(true);
                       resetSearchDB.start();
            }
            
            if ((resetRecognize.recognize) && (resetSearchDB.searchDB)) {
               
                        try {
                           
                           snmpSet(this.UnipingIpaddress, "SWITCH", ".1.3.6.1.4.1.25728.5800.3.1.3.2", 1);
                           
                           try {
                               sleep(3000);
                           } catch (InterruptedException ex) {
                               Logger.getLogger(SnmpWrapper.class.getName()).log(Level.SEVERE, null, ex);
                           }
                            snmpSet(this.UnipingIpaddress, "SWITCH", ".1.3.6.1.4.1.25728.5800.3.1.3.2", 0);
                            
                            System.out.println(formatForDateNow.format(date) + " Было пропущено ТС через шлагбаум: " + this.name);
                            
                            resetRecognize = new ResetRecognize(false);
                            resetSearchDB = new ResetSearchDB(false);
                            try {  
                                sleep(8000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(SnmpWrapper.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (IOException ex) {
                               Logger.getLogger(SnmpWrapper.class.getName()).log(Level.SEVERE, null, ex);
                        } 
            } 
        };
        boolean addNotificationListener = snmp.addNotificationListener(targetAddress, trapPrinter);   
    }
    
    /*Метод для посылки трапов. Посылаем пустой трап по адресу targetAddress.*/
	public void send(){
		// setting up target
		CommunityTarget c_target = new CommunityTarget();
		c_target.setCommunity(new OctetString("public"));
		c_target.setAddress(targetAddress);
		c_target.setRetries(0);
		c_target.setTimeout(1500);
		c_target.setVersion(SnmpConstants.version2c);	

		PDU pdu = new PDU();
		pdu.setType(PDU.INFORM);
		pdu.add(new VariableBinding(SnmpConstants.snmpTrapEnterprise));
		pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress));

		try {
			snmp.send(pdu, c_target, transport);
		} catch (IOException ex2) {
		}
	}
        
    /*Метод для посылки SET на устройство snmp (Uniping).*/    
        public void snmpSet(String strAddress, String community, String strOID, int Value) throws IOException {
            strAddress = strAddress + "/162";
            Address targetAddress_0 = GenericAddress.parse(strAddress);
            Snmp snmp_0;
            try {
            TransportMapping transport_0 = new DefaultUdpTransportMapping();
            snmp_0 = new Snmp(transport_0);
            transport_0.listen();
            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(community));
            target.setAddress(targetAddress_0);
            target.setRetries(2);
            target.setTimeout(5000);
            target.setVersion(SnmpConstants.version1);
            
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(strOID), new Integer32(Value)));
            pdu.setType(PDU.SET);
            ResponseListener listener = new ResponseListener() {
            @Override
            public void onResponse(ResponseEvent event) {
                    ((Snmp)event.getSource()).cancel(event.getRequest(), this);
                    System.out.println("Set Status is:" + event.getResponse().getErrorStatusText());      
            }
            };
            snmp_0.send(pdu, target, null, listener);
            snmp_0.close();
            }
            catch (IOException e) {
            }
        }
        
        /*Сам поток спит. От него лишь требуется держать в памяти наш обработчик trapPrinter*/
        @Override
	public void run() {
		while(true)
		try{
			SnmpWrapper.sleep(1000000);			
		}catch (InterruptedException e) {
		}			
	}
}
