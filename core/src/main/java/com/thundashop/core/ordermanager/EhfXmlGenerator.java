/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.usermanager.data.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author ktonder
 */
public class EhfXmlGenerator {

    private Order order;
    private SimpleDateFormat dateFormatter;
    private final AccountingDetails details;
    private final User customer;
    private final boolean isCreditNote;

    public EhfXmlGenerator(Order order, AccountingDetails details, User customer) {
        this.order = order;
        this.details = details;
        this.isCreditNote = order.isCreditNote;
        this.customer = customer;
        createSimpleDateFormatter();
    }

    public String generateXml() {
        String xml = generateXmlInternal();
        
        if (!validateXml(xml)) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter("/tmp/debugehf.xml", "UTF-8");
                writer.println(xml);
                writer.close();
                throw new ErrorException(1050);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EhfXmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(EhfXmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                writer.close();
            }
        }
        
        return xml;
    }

    public String generateXmlInternal() {
        String useId = order.kid != null && !order.kid.isEmpty() ? order.kid : ""+order.incrementOrderId;
        
        String vatNumberWithoutMVAextention = details.vatNumber.toLowerCase().replaceAll("mva", "");
        
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        
        if (!isCreditNote) {
            xml += "<Invoice xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\" xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:ccts=\"urn:un:unece:uncefact:documentation:2\" xmlns:qdt=\"urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2\" xmlns:udt=\"urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2\">\n"
                    + "	<cbc:UBLVersionID>2.1</cbc:UBLVersionID>\n"
                    + "<cbc:CustomizationID>urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0</cbc:CustomizationID>\n";
        } else {
            xml += "<CreditNote xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2\" xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:ccts=\"urn:un:unece:uncefact:documentation:2\" xmlns:qdt=\"urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2\" xmlns:udt=\"urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2\">\n" +
                    "        <cbc:UBLVersionID>2.1</cbc:UBLVersionID>\n" +
                    "<cbc:CustomizationID>urn:www.cenbii.eu:transaction:biitrns014:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:kreditnota:ver2.0</cbc:CustomizationID>\n";     
        }
        
        xml += "    <cbc:ProfileID>urn:www.cenbii.eu:profile:bii05:ver2.0</cbc:ProfileID>\n";
        
        xml += "    <cbc:ID>" + order.incrementOrderId + "</cbc:ID>\n";
        xml += "         <cbc:IssueDate>" + dateFormatter.format(order.rowCreatedDate) + "</cbc:IssueDate>\n";
        if (!isCreditNote) {
            xml += "         <cbc:InvoiceTypeCode listID=\"UNCL1001\">380</cbc:InvoiceTypeCode>\n";
        }
        if (order.invoiceNote != null && !order.invoiceNote.isEmpty()) {
            xml += "         <cbc:Note>" + order.invoiceNote + "</cbc:Note>\n";
        }
        xml += "         <cbc:TaxPointDate>" + dateFormatter.format(order.getStartDateByItems()) + "</cbc:TaxPointDate>\n";
        xml += "         <cbc:DocumentCurrencyCode listID=\"ISO4217\">" + getCurrentByCode() + "</cbc:DocumentCurrencyCode>\n";
        if (order.invoiceNote != null && !order.invoiceNote.isEmpty()) {
            xml += "         <cbc:AccountingCost>" + order.invoiceNote + "</cbc:AccountingCost>\n";
        }
        xml += "         <cac:OrderReference>\n";
        xml += "             <cbc:ID>" + useId + "</cbc:ID>\n";
        xml += "         </cac:OrderReference>\n";
        
        if (isCreditNote) {
            xml +=  "         <cac:BillingReference>\n" +
                    "                 <cac:CreditNoteDocumentReference>\n" +
                    "                         <cbc:ID>"+order.parentOrder+"</cbc:ID>\n" +
                    "                 </cac:CreditNoteDocumentReference>\n" +
                    "         </cac:BillingReference>\n";
        }
        
        
        xml += "         <cac:ContractDocumentReference>\n";
        xml += "                 <cbc:ID>" + useId + "</cbc:ID>\n";
        xml += "                 <cbc:DocumentTypeCode listID=\"UNCL1001\">2</cbc:DocumentTypeCode>\n";
        xml += "                 <cbc:DocumentType>Normal</cbc:DocumentType>\n";
        xml += "         </cac:ContractDocumentReference>\n";

        xml
                += "        <cac:AccountingSupplierParty>\n"
                + "                <cac:Party>\n"
                + "                        <cbc:EndpointID schemeID=\"NO:ORGNR\">" + vatNumberWithoutMVAextention + "</cbc:EndpointID>\n"
                + "                        <cac:PartyName>\n"
                + "                                <cbc:Name>" + details.companyName + "</cbc:Name>\n"
                + "                        </cac:PartyName>\n"
                + "                        <cac:PostalAddress>\n"
                + "                                <cbc:StreetName>" + details.address + "</cbc:StreetName>\n"
                + "                                <cbc:CityName>" + details.city + "</cbc:CityName>\n"
                + "                                <cbc:PostalZone>" + details.postCode + "</cbc:PostalZone>\n"
                + "                                <cac:Country>\n"
                + "                                        <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n"
                + "                                </cac:Country>\n"
                + "                        </cac:PostalAddress>\n"
                + "                        <cac:PartyTaxScheme>\n"
                + "                                <cbc:CompanyID schemeID=\"NO:VAT\">" + details.vatNumber + "</cbc:CompanyID>\n"
                + "                                <cac:TaxScheme>\n"
                + "                                        <cbc:ID>VAT</cbc:ID>\n"
                + "                                </cac:TaxScheme>\n"
                + "                        </cac:PartyTaxScheme>\n"
                + "                        <cac:PartyLegalEntity>\n"
                + "                                <cbc:RegistrationName>" + details.companyName + "</cbc:RegistrationName>\n"
                + "                                <cbc:CompanyID schemeID=\"NO:ORGNR\" schemeName=\"Foretaksregisteret\">" + vatNumberWithoutMVAextention + "</cbc:CompanyID>\n"
                + "                                <cac:RegistrationAddress>\n"
                + "                                        <cbc:CityName>" + details.city + "</cbc:CityName>\n"
                + "                                        <cac:Country>\n"
                + "                                                <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n"
                + "                                        </cac:Country>\n"
                + "                                </cac:RegistrationAddress>\n"
                + "                        </cac:PartyLegalEntity>\n"
                + "                        <cac:Contact>\n"
                + "                                <cbc:ID>"+details.companyName+"</cbc:ID>\n"
                + "                                <cbc:ElectronicMail>"+details.contactEmail+"</cbc:ElectronicMail>\n"
                + "                        </cac:Contact>\n"
                + "                </cac:Party>\n"
                + "        </cac:AccountingSupplierParty>\n";

        xml += "        <cac:AccountingCustomerParty>\n"
                + "                <cac:Party>\n"
                + "                        <cbc:EndpointID schemeID=\"NO:ORGNR\">" + customer.companyObject.vatNumber + "</cbc:EndpointID>\n"
                + "                        <cac:PartyIdentification>\n"
                + "                                <cbc:ID schemeID=\"ZZZ\">"+customer.customerId+"</cbc:ID>\n"
                + "                        </cac:PartyIdentification>\n"
                + "                        <cac:PartyName>\n"
                + "                                <cbc:Name>" + customer.fullName + "</cbc:Name>\n"
                + "                        </cac:PartyName>\n"
                + "                        <cac:PostalAddress>\n"
                + "                                <cbc:StreetName>" + customer.address.address + "</cbc:StreetName>\n";

        if (customer.address.address2 != null && !customer.address.address2.isEmpty()) {
            xml += "                                <cbc:AdditionalStreetName>" + customer.address.address2 + "</cbc:AdditionalStreetName>\n";
        }

        xml
                += "                                <cbc:CityName>" + customer.address.city + "</cbc:CityName>\n"
                + "                                <cbc:PostalZone>" + customer.address.postCode + "</cbc:PostalZone>\n"
                + "                                <cac:Country>\n"
                + "                                        <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n"
                + "                                </cac:Country>\n"
                + "                        </cac:PostalAddress>\n"
                + "                        <cac:PartyTaxScheme>\n"
                + "                                <cbc:CompanyID schemeID=\"NO:VAT\">" + customer.companyObject.vatNumber + "MVA</cbc:CompanyID>\n"
                + "                                <cac:TaxScheme>\n"
                + "                                        <cbc:ID>VAT</cbc:ID>\n"
                + "                                </cac:TaxScheme>\n"
                + "                        </cac:PartyTaxScheme>\n"
                + "                        <cac:PartyLegalEntity>\n"
                + "                                <cbc:RegistrationName>" + customer.companyObject.name + "</cbc:RegistrationName>\n"
                + "                                <cbc:CompanyID schemeID=\"NO:ORGNR\">" + customer.companyObject.vatNumber + "</cbc:CompanyID>\n"
                + "                                <cac:RegistrationAddress>\n"
                + "                                        <cbc:CityName>" + customer.address.city + "</cbc:CityName>\n"
                + "                                        <cac:Country>\n"
                + "                                                <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n"
                + "                                        </cac:Country>\n"
                + "                                </cac:RegistrationAddress>\n"
                + "                        </cac:PartyLegalEntity>\n"
                + "                        <cac:Contact>\n"
                + "                                <cbc:ID>"+customer.fullName+"</cbc:ID>\n"
                + "                                <cbc:Name>"+customer.fullName+"</cbc:Name>\n"
                + "                        </cac:Contact>\n"
                + "                </cac:Party>\n"
                + "        </cac:AccountingCustomerParty>\n";

        xml += "        <cac:PayeeParty>\n"
                + "                <cac:PartyName>\n"
                + "                        <cbc:Name>" + customer.companyObject.name + "</cbc:Name>\n"
                + "                </cac:PartyName>\n"
                + "                <cac:PartyLegalEntity>\n"
                + "                        <cbc:CompanyID schemeID=\"NO:ORGNR\">" + customer.companyObject.vatNumber + "</cbc:CompanyID>\n"
                + "                </cac:PartyLegalEntity>\n"
                + "        </cac:PayeeParty>\n";

        xml += "        <cac:Delivery>\n"
                + "                <cbc:ActualDeliveryDate>" + dateFormatter.format(order.createdDate) + "</cbc:ActualDeliveryDate>\n"
                + "                <cac:DeliveryLocation>\n"
                + "                        <cac:Address>\n"
                + "                                <cbc:StreetName>" + details.address + "</cbc:StreetName>\n";

        if (customer.address.address2 != null && !customer.address.address2.isEmpty()) {
            xml += "                                <cbc:AdditionalStreetName></cbc:AdditionalStreetName>\n";
        }

        xml
                += "                                <cbc:CityName>" + details.city + "</cbc:CityName>\n"
                + "                                <cbc:PostalZone>" + details.postCode + "</cbc:PostalZone>\n"
                + "                                <cac:Country>\n"
                + "                                        <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n"
                + "                                </cac:Country>\n"
                + "                        </cac:Address>\n"
                + "                </cac:DeliveryLocation>\n"
                + "        </cac:Delivery>\n";

        xml +=  "       <cac:PaymentMeans>\n" +
                "            <cbc:PaymentMeansCode listID=\"UNCL4461\">31</cbc:PaymentMeansCode>\n" +
                "            <cbc:PaymentDueDate>"+dateFormatter.format(order.getDueDate())+"</cbc:PaymentDueDate>\n" +
                "            <cbc:PaymentID>"+order.incrementOrderId+"</cbc:PaymentID>\n" +
                "            <cac:PayeeFinancialAccount>\n" +
                "              <cbc:ID schemeID=\"BBAN\">"+details.accountNumber+"</cbc:ID>\n" +
                "            </cac:PayeeFinancialAccount>\n" +
                "       </cac:PaymentMeans>\n";

        xml += "        <cac:TaxTotal>\n"
                + "                <cbc:TaxAmount currencyID=\"NOK\">" + makePositive(order.getTotalAmountVatRoundedTwoDecimals()) + "</cbc:TaxAmount>\n";

        xml += generateSubTaxes();

        xml += "        </cac:TaxTotal>\n";

        double totalWithoutVat = order.getTotalAmountRoundedTwoDecimals() - order.getTotalAmountVatRoundedTwoDecimals();
        double roundedTotalWithoutVat = Math.round(totalWithoutVat * 100.0) / 100.0;
        xml += "        <cac:LegalMonetaryTotal>\n"
                + "                <cbc:LineExtensionAmount currencyID=\"NOK\">" + makePositive(roundedTotalWithoutVat) + "</cbc:LineExtensionAmount>\n"
                + "                <cbc:TaxExclusiveAmount currencyID=\"NOK\">" + makePositive(roundedTotalWithoutVat) + "</cbc:TaxExclusiveAmount>\n"
                + "                <cbc:TaxInclusiveAmount currencyID=\"NOK\">" + makePositive(order.getTotalAmountRoundedTwoDecimals()) + "</cbc:TaxInclusiveAmount>\n"
                + "                <cbc:ChargeTotalAmount currencyID=\"NOK\">0</cbc:ChargeTotalAmount>\n"
                + "                <cbc:PrepaidAmount currencyID=\"NOK\">0</cbc:PrepaidAmount>\n"
                + "                <cbc:PayableRoundingAmount currencyID=\"NOK\">0</cbc:PayableRoundingAmount>\n"
                + "                <cbc:PayableAmount currencyID=\"NOK\">" + makePositive(order.getTotalAmountRoundedTwoDecimals()) + "</cbc:PayableAmount>\n"
                + "        </cac:LegalMonetaryTotal>\n";

        xml += createInvoiceLines();

        if (isCreditNote) {
            xml += "</CreditNote>";
        } else {
            xml += "</Invoice>";
        }

        return xml;
    }

    private String createInvoiceLines() {
        String xml = "";

        int i = 0;
        for (CartItem item : order.cart.getItems()) {
            i++;
            double unitPrice =  isCreditNote ? makePositive(Math.round(item.getProduct().priceExTaxes * 100.0) / 100.0) : Math.round(item.getProduct().priceExTaxes * 100.0) / 100.0;
            double total = unitPrice * item.getCount();
            double count = isCreditNote ? makePositive(item.getCount()) : item.getCount();
            
            String taxCode = item.getProduct().taxGroupObject.taxRate < 25.0 ? "AA" : "S";
            String invoicelinetext = isCreditNote ? "CreditNoteLine" : "InvoiceLine";
            String invoieqtytext = isCreditNote ? "CreditedQuantity" : "InvoicedQuantity";
            xml += "        <cac:"+invoicelinetext+">\n"
                    + "                <cbc:ID>" + i + "</cbc:ID>\n"
                    + "                <cbc:"+invoieqtytext+" unitCode=\"NAR\" unitCodeListID=\"UNECERec20\">" + count + "</cbc:"+invoieqtytext+">\n"
                    + "                <cbc:LineExtensionAmount currencyID=\"NOK\">" + total + "</cbc:LineExtensionAmount>\n"
                    + "                <cbc:AccountingCost>BookingCode001</cbc:AccountingCost>\n"
                    + "                <cac:InvoicePeriod>\n"
                    + "                        <cbc:StartDate>" + dateFormatter.format(item.getStartingDate()) + "</cbc:StartDate>\n"
                    + "                        <cbc:EndDate>" + dateFormatter.format(item.getEndingDate()) + "</cbc:EndDate>\n"
                    + "                </cac:InvoicePeriod>\n"
                    + "                <cac:OrderLineReference>\n"
                    + "                        <cbc:LineID>" + i + "</cbc:LineID>\n"
                    + "                </cac:OrderLineReference>\n"
                    + "                <cac:Item>\n"
                    + "                        <cbc:Description>" + createLineText(item) + "</cbc:Description>\n"
                    + "                        <cbc:Name>" + createLineText(item) + "</cbc:Name>\n"
                    + "                        <cac:SellersItemIdentification>\n"
                    + "                                <cbc:ID>" + item.getProduct().incrementalProductId + "</cbc:ID>\n"
                    + "                        </cac:SellersItemIdentification>\n"
                    + "                        <cac:StandardItemIdentification>\n"
                    + "                                <cbc:ID schemeID=\"GTIN\">" + item.getProduct().incrementalProductId + "</cbc:ID>\n"
                    + "                        </cac:StandardItemIdentification>\n"
                    + "                        <cac:OriginCountry>\n"
                    + "                        <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n"
                    + "                        </cac:OriginCountry>\n"
                    + "                        <cac:ClassifiedTaxCategory>\n"
                    + "                                <cbc:ID schemeID=\"UNCL5305\">" + taxCode + "</cbc:ID>\n"
                    + "                                <cbc:Percent>" + item.getProduct().taxGroupObject.taxRate + "</cbc:Percent>\n"
                    + "                                <cac:TaxScheme>\n"
                    + "                                        <cbc:ID>VAT</cbc:ID>\n"
                    + "                                </cac:TaxScheme>\n"
                    + "                        </cac:ClassifiedTaxCategory>\n"
                    + "                </cac:Item>\n"
                    + "                <cac:Price>\n"
                    + "                        <cbc:PriceAmount currencyID=\"NOK\">" + makePositive(unitPrice) + "</cbc:PriceAmount>\n"
                    + "                        <cbc:BaseQuantity>1</cbc:BaseQuantity>\n"
                    + "                </cac:Price>\n";
                xml += "        </cac:"+invoicelinetext+">\n";
        }

        return xml;
    }
    
     private String createLineText(CartItem item) {
        if(item == null) {
            return "";
        }
        
        String lineText = "";
        String startDate = "";
        if(item.startDate != null) {
            DateTime start = new DateTime(item.startDate);
            startDate = start.toString("dd.MM.yy");
        }

        String endDate = "";
        if(item.endDate != null) {
            DateTime end = new DateTime(item.endDate);
            endDate = end.toString("dd.MM.yy");
        }
        
        lineText = "";
        if(item.getProduct() != null && 
                item.getProduct().additionalMetaData != null && 
                !item.getProduct().additionalMetaData.isEmpty()) {
            lineText = item.getProduct().additionalMetaData; 
        }
        
        if(item.getProduct() != null && item.getProduct().name != null && !item.getProduct().name.equals("null")) {
            lineText += " " + item.getProduct().name;
        }
        
        if(item.getProduct() != null && item.getProduct().metaData != null && !item.getProduct().metaData.isEmpty()) {
            lineText += " " + item.getProduct().metaData;
        }
        if(!startDate.isEmpty() && !endDate.isEmpty() && !item.hideDates) {
            lineText += " (" + startDate + " - " + endDate + ")";
        }
        
        return lineText;
    }

    private String generateSubTaxes() {
        Map<TaxGroup, Double> taxes = order.getTaxesRoundedWithTwoDecimals();
        String xml = "";
        for (TaxGroup group : taxes.keySet()) {
            String taxCode = group.taxRate < 25.0 ? "AA" : "S";
            xml += "                <cac:TaxSubtotal>\n"
                    + "                        <cbc:TaxableAmount currencyID=\"NOK\">" + makePositive(order.getTotalAmountForTaxGroupRoundedWithTwoDecimals(group)) + "</cbc:TaxableAmount>\n"
                    + "                        <cbc:TaxAmount currencyID=\"NOK\">" + makePositive(taxes.get(group)) + "</cbc:TaxAmount>\n"
                    + "                        <cac:TaxCategory>\n"
                    + "                                <cbc:ID schemeID=\"UNCL5305\">" + taxCode + "</cbc:ID>\n"
                    + "                                <cbc:Percent>" + group.taxRate + "</cbc:Percent>\n"
                    + "                                <cac:TaxScheme>\n"
                    + "                                        <cbc:ID>VAT</cbc:ID>\n"
                    + "                                </cac:TaxScheme>\n"
                    + "                        </cac:TaxCategory>\n"
                    + "                </cac:TaxSubtotal>\n";
        }

        return xml;
    }

    private String getCurrentByCode() {
        return "NOK";
    }

    private void createSimpleDateFormatter() {
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    private boolean validateXml(String xml) {
        BufferedWriter bw = null;
        String fileName = UUID.randomUUID().toString();

        try {
            File temp = File.createTempFile(fileName, ".xml");

            //write it
            bw = new BufferedWriter(new FileWriter(temp));
            bw.write(xml);
            bw.close();

            String cmd = "java -jar /opt/getshop/java/EhfValidator-1.0-jar-with-dependencies.jar " + temp.getCanonicalPath();
//            Process p = Runtime.getRuntime().exec("ls -aF");                                                                                                                                                     
            Process p = Runtime.getRuntime().exec(cmd);

            String resultText = "";
            try {
                p.waitFor();
                try (final BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    if ((line = b.readLine()) != null) {
                        resultText += line;
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            temp.delete();
            
            return resultText.equals("OK");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(EhfXmlGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }

    private double makePositive(double number) {
        if (number < 0) {
            return number * -1;
        }
        return number;
    }
}
