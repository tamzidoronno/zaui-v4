package com.thundashop.core.verifonemanager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent; 
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.text.PlainDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.logging.*;

import no.point.paypoint.*;

public class VerifonePaymentApp extends Frame implements PayPointListener, ActionListener{
	static String sPurchase = "Purchase";
	static String sReturn = "Return";
	static String sReversal = "Reversal";
	static String sCashback = "Cashback";
	static String sDeposit = "Deposit";
	static String sWithdrawal = "Withdrawal";
	static String sAdminCode = "Admin code";
	static String sModeNorm = "Normal";
	static String sModeReqRes = "Previous result";
	static String sModeSimul = "Simulation / training";
	static String sLevelError = "Error     ";
	static String sLevelWarning = "Warning   ";
	static String sLevelInfo = "Info      ";
	static String sLevelDebug = "Debug     ";
	static String sPayPoint = "paypoint ";
	static String sPayPointEth = "paypoint ethernet";
	static String sSofie = "Sofie     ";
	static String s2400 = "2400      ";
	static String s4800 = "4800      ";
	static String s9600 = "9600      ";
	static String s19200 = "19200     ";
	static String s38400 = "38400     ";
	static String s57600 = "57600     ";
	static String s115200 = "115200    ";
	
	JButton bStart, bCancel;
	PayPoint payPoint;
	DefaultLogger log;
	ButtonGroup functionGroup; //, modeGroup;
	JTextField tAmount1, tAmount2, tVatAmount, tAddTransData, tAdminCode;
	JPanel pRadioPanel, pSouth;
	TextArea tResult, tDisplay, tStatusInfo, tPrint;
    JRadioButton bPurchase, bReturn, bReversal, bCashback, bDeposit, bWithdrawal,
    	/*bModeNorm, bModeReqRes,*/ bAdminCode;
    JLabel lTotAmount, lPurcAmount, lVatAmount, /*lMode,*/ lChooseFunc,
    	lAddTransData;
	JMenu menuQuit, menuFunc, menuStatus;

    JMenuItem itemQuit, itemStatusOpen, itemStatusBankMode, itemStatusListener;
    JMenuItem[] funcItems = new JMenuItem[4];
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

	class MyAdapter extends WindowAdapter{
		public void windowClosing(WindowEvent e){
			Window windowInFocus = e.getWindow();
			if(windowInFocus instanceof VerifonePaymentApp){
				try{
					payPoint.close();
				} catch (Exception ex){}
				System.exit(0);
			} else {
				windowInFocus.setVisible(false);
			}
		}
	}

	class JTextFieldLimit extends PlainDocument {
		private int limit;

		JTextFieldLimit(int limit) {
			super();
		    this.limit = limit;
		}

		JTextFieldLimit(int limit, boolean upper) {
			super();
		    this.limit = limit;
		}

		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str == null)
				return;
			
			char inputChar = str.charAt(0);

			if ((getLength() + str.length()) <= limit && Character.isDigit(inputChar)) {
				super.insertString(offset, str, attr);
			}
		}
	}
	
	class ManualCardDataEntry extends Frame implements ActionListener{
		JTextField tPan, tExpiryMonth, tExpiryYear;
		JButton bOk, bCancel;
		JLabel lErrorMsg;
		
		public ManualCardDataEntry(){
			setBackground(Color.lightGray);
			setTitle("Manual entry");
			Image icon = Toolkit.getDefaultToolkit().getImage("point.gif"); 
			prepareImage(icon, -1, -1, this); 
			setIconImage(icon);
			setLayout(new BorderLayout());
			setResizable(false);
			
			bOk = new JButton("Ok");
			bOk.setPreferredSize(new Dimension(80,25));
			bCancel = new JButton("Cancel");
			bCancel.setPreferredSize(new Dimension(80,25));
			bOk.addActionListener(this);
			bCancel.addActionListener(this);
			
			GridBagLayout panelGrid = new GridBagLayout();
	        GridBagConstraints c = new GridBagConstraints();
	        JPanel pCenter = new JPanel();
	        JPanel pSouth = new JPanel();
			JPanel pExpiryDate = new JPanel();
	        JLabel lPan = new JLabel("PAN");
	        JLabel lExpiryDate = new JLabel("Expiry date (MM/YY)");
	        JLabel lSeparate = new JLabel(" / ");
	        lErrorMsg = new JLabel("                              ");
	        lErrorMsg.setPreferredSize(new Dimension(200, 20));
	        tPan = new JTextField(19);
	        tExpiryMonth = new JTextField(2);
	        tExpiryYear = new JTextField(2);
	        tPan.setDocument(new JTextFieldLimit(19));
	        tExpiryMonth.setDocument(new JTextFieldLimit(2));
	        tExpiryYear.setDocument(new JTextFieldLimit(2));
	        
	        pCenter.setLayout(panelGrid);
			GridBagLayout expiryGrid = new GridBagLayout();
			pExpiryDate.setLayout(expiryGrid);
	        c.gridx = GridBagConstraints.RELATIVE;
	        c.gridy = 0;
			c.anchor = GridBagConstraints.NORTH;
			expiryGrid.setConstraints(tExpiryMonth, c);
			pExpiryDate.add(tExpiryMonth);
			expiryGrid.setConstraints(lSeparate, c);
			pExpiryDate.add(lSeparate);
			expiryGrid.setConstraints(tExpiryYear, c);
			pExpiryDate.add(tExpiryYear);

	        c.gridx = 0;
	        c.gridy = GridBagConstraints.RELATIVE;
	        c.anchor = GridBagConstraints.LINE_START;
	        panelGrid.setConstraints(lPan, c);
	        pCenter.add(lPan);
	        panelGrid.setConstraints(tPan, c);
	        pCenter.add(tPan);
	        panelGrid.setConstraints(lExpiryDate, c);
	        pCenter.add(lExpiryDate);
	        panelGrid.setConstraints(pExpiryDate, c);
	        pCenter.add(pExpiryDate);
	        panelGrid.setConstraints(lErrorMsg, c);
	        pCenter.add(lErrorMsg);
	        pCenter.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	        
	        pSouth.add(bOk);
	        pSouth.add(bCancel);
	        
	        add( pCenter, "Center" );
	        add( pSouth, "South" );
	        
	        AbstractAction action = new AbstractAction() { 
				public void actionPerformed(ActionEvent ae) { 
					validateInput();
	            } 
	        };
	        InputMap im = tPan.getInputMap();
	        ActionMap am = tPan.getActionMap();
	        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
	        am.put("enter", action);
	        im = tExpiryMonth.getInputMap();
	        am = tExpiryMonth.getActionMap();
	        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
	        am.put("enter", action);
	        im = tExpiryYear.getInputMap();
	        am = tExpiryYear.getActionMap();
	        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");
	        am.put("enter", action);
	        
	        addWindowListener(new MyAdapter());
		}
		
		void validateInput(){
			int month=0;
			String sMonth, sYear, sCardData;

			sMonth = tExpiryMonth.getText();
			sYear = tExpiryYear.getText();
			if(tPan.getText().length()==0){
				lErrorMsg.setText("Enter PAN");
				tPan.requestFocus();
				tPan.selectAll();
				return;
			}
			try{
				month = Integer.parseInt(sMonth);
			} catch(Exception e){}
			if(month>12 || month<1){
				lErrorMsg.setText("Month must have value 1-12");
				tExpiryMonth.requestFocus();
				tExpiryMonth.selectAll();
				return;
			}
			if(sYear.length()==0){
				lErrorMsg.setText("Enter expiry year");
				tExpiryYear.requestFocus();
				tExpiryYear.selectAll();
				return;
			}
			
			sMonth = sMonth.length()==1 ? "0" + sMonth : sMonth;
			sYear = sYear.length()==1 ? "0" + sYear : sYear;
			sCardData = ";" + tPan.getText() + "=" + sYear + sMonth + "?";
			
			try{
				payPoint.setManualCardData(sCardData);
			} catch(ComNotInitialisedException e){
				handleException(e);
			} catch(NoListenerRegisteredException e){
				handleException(e);
			} catch(IllegalCardDataException e){
				handleException(e);
			}
			
			setVisible(false);			
		}

		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			
			if(source == bOk){
				validateInput();
			} else if(source == bCancel){
				setVisible(false);
			}
		}
	}
	
	public synchronized void getPayPointEvent(PayPointEvent event) {
            
	}
	
	void handleException(Exception e){
		tResult.setText("Failed:\n");
		tResult.append(e.toString());
	}
	
	int parseAmount(JTextField tAmount, boolean requireAmount){
		String sAmount = tAmount.getText();
		int iAmount = 0;
		if(requireAmount && sAmount.length()==0){
			tResult.setText("Enter amount!");
			tAmount.requestFocus();
			return -1;
		}
		try {
			iAmount = sAmount.length()==0 ? 0 : (int)(Math.round(Double.parseDouble(sAmount.replace(',', '.'))*100));
		} catch (Exception e){
			tResult.setText("Error in amount");
			tAmount.requestFocus();
			tAmount.selectAll();
			return -1;
		}
		return iAmount;
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


	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		String result;
		
		if(source == bStart){
		} else if(source == bCancel){
			try {
				payPoint.cancelRequest();
			} catch (NoListenerRegisteredException e){
			} catch (TerminalInLocalModeException e){}
		} else if(source == bPurchase){
		} else if(source == bAdminCode){
			tAdminCode.setEnabled(true);
			tAdminCode.requestFocus();
		} else if(source == itemQuit){
			try{
				payPoint.close();
			} catch (Exception ex){}
			System.exit(0);
		} else if(source == itemStatusOpen){
			result = payPoint.isOpen() ? "Is open" : "Not open";
			tResult.setText(result);
		} else if(source == itemStatusBankMode){
			result = payPoint.isInBankMode() ? "In bankmode" : "Not in bankmode";
			tResult.setText(result);
		} else if(source == itemStatusListener){
			result = payPoint.hasListener() ? "Has listener" : "No listener";
			tResult.setText(result);
		} else if(source == funcItems[0]) {
			performAdmin(PayPoint.ADM_RECONCILIATION, true);
		} else if(source == funcItems[1]){
			try{
				payPoint.startTestCom();
			} catch(Exception e){}
		} else if(source == funcItems[2]){
			performAdmin(PayPoint.ADM_STORED_PRINT, true);
		} else if(source == funcItems[3]){
			performAdmin(PayPoint.ADM_HOST_TEST_MSG, true);
		}
	}
}