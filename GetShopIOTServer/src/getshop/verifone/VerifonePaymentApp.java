package getshop.verifone;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileWriter;
import java.util.logging.*;

import no.point.paypoint.*;

public class VerifonePaymentApp implements PayPointListener, ActionListener{
	String sPurchase = "Purchase";
	String sReturn = "Return";
	String sReversal = "Reversal";
	String sCashback = "Cashback";
	String sDeposit = "Deposit";
	String sWithdrawal = "Withdrawal";
	String sAdminCode = "Admin code";
	String sModeNorm = "Normal";
	String sModeReqRes = "Previous result";
	String sModeSimul = "Simulation / training";
	String sLevelError = "Error     ";
	String sLevelWarning = "Warning   ";
	String sLevelInfo = "Info      ";
	String sLevelDebug = "Debug     ";
	String sPayPoint = "paypoint ";
	String sPayPointEth = "paypoint ethernet";
	String sSofie = "Sofie     ";
	String s2400 = "2400      ";
	String s4800 = "4800      ";
	String s9600 = "9600      ";
	String s19200 = "19200     ";
	String s38400 = "38400     ";
	String s57600 = "57600     ";
	String s115200 = "115200    ";
	
	PayPoint payPoint;
	DefaultLogger log;
	TextArea tResult, tDisplay, tStatusInfo, tPrint;

	int iTransType=0;
	
	public VerifonePaymentApp(){
            log = new DefaultLogger();
            log.setLogLevel(Level.ALL);
            payPoint = new PayPoint(log);
	}
	
	void logPrintToFile(PayPointResultEvent result){
		File printFile;
		FileWriter writer;
		String print = result.getNormalPrint() + (result.getSignaturePrint()!=null ? result.getSignaturePrint() : "");
		int startIndex, endIndex;
		boolean fileExists=false;
		try {
			printFile = new File("paypoint print.log");
			if(printFile.exists()){
				if(printFile.length()>10000000){
					File oldFile = new File("paypoint print old.log");
					oldFile.delete();
					printFile.renameTo(oldFile);
					printFile = new File("paypoint print.log");
				}
				else
					fileExists = true;
			}
			printFile.createNewFile();
			writer = new FileWriter(printFile, true);
			if(fileExists)
				writer.write("====================\r\n");
			writer.write("\r\n");
			while((endIndex=print.indexOf(0x0C, 0))!=-1)
				print = print.substring(0, endIndex) + print.substring(endIndex+1);
			startIndex = 0;
			while((endIndex=print.indexOf(0x0A, startIndex))!=-1){
				writer.write(print.substring(startIndex, endIndex) + "\r\n");
				startIndex = endIndex+1;
			}
			writer.write(print.substring(startIndex) + "\r\n");
			writer.write("\r\n");
			if(iTransType!=0)
				writer.write("Trans.type:" + iTransType + "\r\n");
			writer.write("LM: " + Integer.toHexString(result.getResult()) + 
					Integer.toHexString(result.getAccumulator()));
			if(result.getIssuerId()!=0)
				writer.write(" " + result.getIssuerId());
			writer.write("\r\n");
			if(result.getLocalModeData()!=null)
				writer.write("LM data: " + result.getLocalModeData() + "\r\n");
			writer.flush();
		} catch(Exception e) {}
	}

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	class ManualCardDataEntry implements ActionListener{
		
		public ManualCardDataEntry(){
		}
		

		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
		}
	}
	
	public synchronized void getPayPointEvent(PayPointEvent event) {
            
	}
	
	void handleException(Exception e){
            System.out.println(e.toString());
	}
	
	void performTransaction(byte transType, int amountTotal, int amountPurchase){
		//String sMode = modeGroup.getSelection().getActionCommand();
		byte mode;

		
		mode = PayPoint.MODE_NORMAL;
		
		try {
			iTransType = transType;
			payPoint.startTransaction(transType, amountTotal, amountPurchase, mode);
		} catch (UnknownTransactionCodeException e) {
			handleException(e);
		} catch (TerminalInBankModeException e) {
			handleException(e);
		} catch (NoListenerRegisteredException e) {
			handleException(e);
		} catch (IllegalAmountException e) {
			handleException(e);
		} catch (ComNotInitialisedException e) {
			handleException(e);
		} catch (UnknownModeException e) {
			handleException(e);
		}
	}
	
	void performAdmin(int adminCode, boolean clearFields){
		if(clearFields)
		try {
			payPoint.startAdmin(adminCode);
		} catch (TerminalInBankModeException e) {
			handleException(e);
		} catch (NoListenerRegisteredException e) {
			handleException(e);
		} catch (ComNotInitialisedException e) {
			handleException(e);
		} catch (TerminalInLocalModeException e) {
			handleException(e);
		}
	}
	
	void openCom(String ipAddr, PayPointListener listener){
		try {
			payPoint.open(ipAddr, 0, "ver1.1", PayPoint.PROTOCOL_ETHERNET);
			payPoint.setPayPointListener(listener);
			//payPoint.setEcrLanguage(PayPoint.LANG_SWE);
		} catch (IllegalAppVersionException e) {
			handleException(e);
		} catch (ComAlreadyInitialisedException e) {
			handleException(e);
		} catch (ComNotInitialisedException e) {
			handleException(e);
		} catch (IllegalIpAddressException e){
			handleException(e);
		} catch(TerminalInBankModeException e){
			handleException(e);
		}
	}
        
	void closeCom(){
            try {
                if(payPoint.isOpen()) {
                    payPoint.cancelRequest();
                }
            } catch(NoListenerRegisteredException e){
                    handleException(e);
            } catch (TerminalInLocalModeException ex) {
                Logger.getLogger(VerifonePaymentApp.class.getName()).log(Level.SEVERE, null, ex);
            }
	}


}