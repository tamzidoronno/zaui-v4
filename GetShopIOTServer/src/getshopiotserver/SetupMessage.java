/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package getshopiotserver;

/**
 *
 * @author boggi
 */
public class SetupMessage {
    String token = "";
    String address = "";
    public String printerType = ""; //customk80
    public String paymentterminal = ""; //verifone,ingenico
    public String type = ""; //kiosk-herman,pos
    public String paymentterminalip = ""; //Ip adress to paymentterminal
    public String printerip = ""; //Ip to printer to print to socket.
    public Integer printerPort = 0; //Port for printer to print to socket // Default 9100
}
