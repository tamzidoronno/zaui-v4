/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager;

import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pdf.data.AccountingDetails;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.usermanager.data.User;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class EhfXmlGenerator {
    private Order order;
    private SimpleDateFormat dateFormatter;
    private final AccountingDetails details;
    private final User customer;

    public EhfXmlGenerator(Order order, AccountingDetails details, User customer) {
        this.order = order;
        this.details = details;
        this.customer = customer;
        createSimpleDateFormatter();
    }    
    
    public String generateXml() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        xml += "<Invoice xmlns=\"urn:oasis:names:specification:ubl:schema:xsd:Invoice-2\" xmlns:cac=\"urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2\" xmlns:ccts=\"urn:un:unece:uncefact:documentat\n" +
"ion:2\" xmlns:qdt=\"urn:oasis:names:specification:ubl:schema:xsd:QualifiedDatatypes-2\" xmlns:udt=\"urn:un:unece:uncefact:data:specification:UnqualifiedDataTypesSchemaModule:2\">\n" +
"	<cbc:UBLVersionID>2.1</cbc:UBLVersionID>\n";
        
        xml += "<cbc:CustomizationID>urn:www.cenbii.eu:transaction:biitrns010:ver2.0:extended:urn:www.peppol.eu:bis:peppol5a:ver2.0:extended:urn:www.difi.no:ehf:faktura:ver2.0</cbc:CustomizationID>\n";
        xml += "    <cbc:ProfileID>urn:www.cenbii.eu:profile:bii05:ver2.0</cbc:ProfileID>\n";
        xml += "    <cbc:ID>"+order.incrementOrderId+"</cbc:ID>\n";
        xml += "         <cbc:IssueDate>"+dateFormatter.format(order.createdDate)+"</cbc:IssueDate>\n";
        xml += "         <cbc:InvoiceTypeCode listID=\"UNCL1001\">380</cbc:InvoiceTypeCode>\n";
        if (order.invoiceNote != null && !order.invoiceNote.isEmpty()) {
            xml += "         <cbc:Note>"+order.invoiceNote+"</cbc:Note>\n";
        }
        xml += "         <cbc:TaxPointDate>"+dateFormatter.format(order.createdDate)+"</cbc:TaxPointDate>\n";
        xml += "         <cbc:DocumentCurrencyCode listID=\"ISO4217\">"+getCurrentByCode()+"</cbc:DocumentCurrencyCode>\n";
        if (order.invoiceNote != null && !order.invoiceNote.isEmpty()) {
            xml += "         <cbc:AccountingCost>"+order.invoiceNote+"</cbc:AccountingCost>\n";
        }
        xml += "         <cac:OrderReference>\n";
        xml += "             <cbc:ID>"+order.incrementOrderId+"</cbc:ID>\n";
        xml += "         </cac:OrderReference>\n";
        xml += "         <cac:ContractDocumentReference>\n";
        xml += "                 <cbc:ID>"+order.invoiceNote+"</cbc:ID>\n";
        xml += "                 <cbc:DocumentTypeCode listID=\"UNCL1001\">2</cbc:DocumentTypeCode>\n";
        xml += "                 <cbc:DocumentType>Normal</cbc:DocumentType>\n";
        xml += "         </cac:ContractDocumentReference>\n";
//        xml += "         <cac:AdditionalDocumentReference>\n";
//        xml += "                 <cbc:ID>Doc1</cbc:ID>\n";
//        xml += "                 <cbc:DocumentType>EHF specification</cbc:DocumentType>\n";
//        xml += "                 <cac:Attachment>\n";
//        xml += "                         <cbc:EmbeddedDocumentBinaryObject mimeCode=\"application/pdf\">"+getBase64EncodedInvoice()+"</cbc:EmbeddedDocumentBinaryObject>\n";
//        xml += "                 </cac:Attachment>\n";
//        xml += "         </cac:AdditionalDocumentReference>\n";
        
        xml += 
                "        <cac:AccountingSupplierParty>\n" +
                "                <cac:Party>\n" +
                "                        <cbc:EndpointID schemeID=\"NO:ORGNR\">"+details.vatNumber+"</cbc:EndpointID>\n" +
                "                        <cac:PartyName>\n" +
                "                                <cbc:Name>"+details.companyName+"</cbc:Name>\n" +
                "                        </cac:PartyName>\n" +
                "                        <cac:PostalAddress>\n" +
                "                                <cbc:StreetName>"+details.address+"</cbc:StreetName>\n" +
                "                                <cbc:CityName>"+details.city+"</cbc:CityName>\n" +
                "                                <cbc:PostalZone>"+details.postCode+"</cbc:PostalZone>\n" +
                "                                <cac:Country>\n" +
                "                                        <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n" +
                "                                </cac:Country>\n" +
                "                        </cac:PostalAddress>\n" +
                "                        <cac:PartyTaxScheme>\n" +
                "                                <cbc:CompanyID schemeID=\"NO:VAT\">"+details.vatNumber+"MVA</cbc:CompanyID>\n" +
                "                                <cac:TaxScheme>\n" +
                "                                        <cbc:ID>VAT</cbc:ID>\n" +
                "                                </cac:TaxScheme>\n" +
                "                        </cac:PartyTaxScheme>\n" +
                "                        <cac:PartyLegalEntity>\n" +
                "                                <cbc:RegistrationName>"+details.companyName+"</cbc:RegistrationName>\n" +
                "                                <cbc:CompanyID schemeID=\"NO:ORGNR\" schemeName=\"Foretaksregisteret\">"+details.vatNumber+"</cbc:CompanyID>\n" +
                "                                <cac:RegistrationAddress>\n" +
                "                                        <cbc:CityName>"+details.city+"</cbc:CityName>\n" +
                "                                        <cac:Country>\n" +
                "                                                <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n" +
                "                                        </cac:Country>\n" +
                "                                </cac:RegistrationAddress>\n" +
                "                        </cac:PartyLegalEntity>\n" +
                "                        <cac:Contact>\n" +
                "                                <cbc:ID>Our ref.</cbc:ID>\n" +
                "                                <cbc:Name></cbc:Name>\n" +
                "                                <cbc:Telephone></cbc:Telephone>\n" +
                "                                <cbc:ElectronicMail>"+details.contactEmail+"</cbc:ElectronicMail>\n" +
                "                        </cac:Contact>\n" +
                "                </cac:Party>\n" +
                "        </cac:AccountingSupplierParty>\n";

        xml +=  "        <cac:AccountingCustomerParty>\n" +
                "                <cac:Party>\n" +
                "                        <cbc:EndpointID schemeID=\"NO:ORGNR\">"+customer.companyObject.vatNumber+"</cbc:EndpointID>\n" +
                "                        <cac:PartyIdentification>\n" +
                "                                <cbc:ID schemeID=\"GLN\">3456789012098</cbc:ID>\n" +
                "                        </cac:PartyIdentification>\n" +
                "                        <cac:PartyName>\n" +
                "                                <cbc:Name>"+customer.fullName+"</cbc:Name>\n" +
                "                        </cac:PartyName>\n" +
                "                        <cac:PostalAddress>\n" +
                "                                <cbc:StreetName>"+customer.address.address+"</cbc:StreetName>\n";
        
        if (customer.address.address2 != null && !customer.address.address2.isEmpty()) {
            xml += "                                <cbc:AdditionalStreetName>"+customer.address.address2+"</cbc:AdditionalStreetName>\n";
        }
        
        xml +=
                "                                <cbc:CityName>"+customer.address.city+"</cbc:CityName>\n" +
                "                                <cbc:PostalZone>"+customer.address.postCode+"</cbc:PostalZone>\n" +
                "                                <cac:Country>\n" +
                "                                        <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n" +
                "                                </cac:Country>\n" +
                "                        </cac:PostalAddress>\n" +
                "                        <cac:PartyTaxScheme>\n" +
                "                                <cbc:CompanyID schemeID=\"NO:VAT\">"+customer.companyObject.vatNumber+"MVA</cbc:CompanyID>\n" +
                "                                <cac:TaxScheme>\n" +
                "                                        <cbc:ID>VAT</cbc:ID>\n" +
                "                                </cac:TaxScheme>\n" +
                "                        </cac:PartyTaxScheme>\n" +
                "                        <cac:PartyLegalEntity>\n" +
                "                                <cbc:RegistrationName>"+customer.companyObject.name+"</cbc:RegistrationName>\n" +
                "                                <cbc:CompanyID schemeID=\"NO:ORGNR\">"+customer.companyObject.vatNumber+"</cbc:CompanyID>\n" +
                "                                <cac:RegistrationAddress>\n" +
                "                                        <cbc:CityName>"+customer.address.city+"</cbc:CityName>\n" +
                "                                        <cac:Country>\n" +
                "                                                <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n" +
                "                                        </cac:Country>\n" +
                "                                </cac:RegistrationAddress>\n" +
                "                        </cac:PartyLegalEntity>\n" +
                "                        <cac:Contact>\n" +
                "                                <cbc:ID>3150bdn</cbc:ID>\n" +
                "                                <cbc:Name>"+customer.fullName+"</cbc:Name>\n" +
                "                                <cbc:Telephone>"+customer.cellPhone+"</cbc:Telephone>\n" +
                "                                <cbc:ElectronicMail>"+customer.emailAddress+"</cbc:ElectronicMail>\n" +
                "                        </cac:Contact>\n" +
                "                </cac:Party>\n" +
                "        </cac:AccountingCustomerParty>\n";
        
        xml +=  "        <cac:PayeeParty>\n" +
                "                <cac:PartyName>\n" +
                "                        <cbc:Name>"+customer.companyObject.name+"</cbc:Name>\n" +
                "                </cac:PartyName>\n" +
                "                <cac:PartyLegalEntity>\n" +
                "                        <cbc:CompanyID schemeID=\"NO:ORGNR\">"+customer.companyObject.vatNumber+"</cbc:CompanyID>\n" +
                "                </cac:PartyLegalEntity>\n" +
                "        </cac:PayeeParty>\n";
        
        xml +=  "        <cac:Delivery>\n" +
                "                <cbc:ActualDeliveryDate>"+dateFormatter.format(order.createdDate)+"</cbc:ActualDeliveryDate>\n" +
                "                <cac:DeliveryLocation>\n" +
                "                        <cac:Address>\n" +
                "                                <cbc:StreetName>"+details.address+"</cbc:StreetName>\n";
        
        if (customer.address.address2 != null && !customer.address.address2.isEmpty()) {
                xml += "                                <cbc:AdditionalStreetName></cbc:AdditionalStreetName>\n";
        }
        
        xml += 
                "                                <cbc:CityName>"+details.city+"</cbc:CityName>\n" +
                "                                <cbc:PostalZone>"+details.postCode+"</cbc:PostalZone>\n" +
                "                                <cac:Country>\n" +
                "                                        <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n" +
                "                                </cac:Country>\n" +
                "                        </cac:Address>\n" +
                "                </cac:DeliveryLocation>\n" +
                "        </cac:Delivery>\n";
        
        xml +=  "        <cac:PaymentMeans>\n" +
                "                <cbc:PaymentMeansCode listID=\"UNCL4461\">31</cbc:PaymentMeansCode>\n" +
                "                <cbc:PaymentDueDate>"+dateFormatter.format(order.getDueDate())+"</cbc:PaymentDueDate>\n" +
                "                <cbc:PaymentID>"+order.incrementOrderId+"</cbc:PaymentID>\n" +
                "                <cac:PayeeFinancialAccount>\n" +
                "                        <cbc:ID schemeID=\"IBAN\">"+details.iban+"</cbc:ID>\n" +
                "                        <cac:FinancialInstitutionBranch>\n" +
                "                        <cbc:ID>9710</cbc:ID>\n" +
                "                           <cac:FinancialInstitution>\n" +
                "                              <cbc:ID schemeID=\"BIC\">"+details.bankName+"</cbc:ID>\n" +
                "                            </cac:FinancialInstitution>\n" +
                "                        </cac:FinancialInstitutionBranch>\n" +
                "                </cac:PayeeFinancialAccount>\n" +
                "        </cac:PaymentMeans>\n";
        
        xml +=  "        <cac:TaxTotal>\n" +
                "                <cbc:TaxAmount currencyID=\"NOK\">"+order.getTotalAmountVat()+"</cbc:TaxAmount>\n";
        
        xml +=  generateSubTaxes();
        
        xml +=  "        </cac:TaxTotal>\n";
        
        
        xml +=  "        <cac:LegalMonetaryTotal>\n" +
                "                <cbc:LineExtensionAmount currencyID=\"NOK\">"+(order.getTotalAmount() - order.getTotalAmountVat())+"</cbc:LineExtensionAmount>\n" +
                "                <cbc:TaxExclusiveAmount currencyID=\"NOK\">"+(order.getTotalAmount() - order.getTotalAmountVat())+"</cbc:TaxExclusiveAmount>\n" +
                "                <cbc:TaxInclusiveAmount currencyID=\"NOK\">"+order.getTotalAmount()+"</cbc:TaxInclusiveAmount>\n" +
                "                <cbc:ChargeTotalAmount currencyID=\"NOK\">0</cbc:ChargeTotalAmount>\n" +
                "                <cbc:PrepaidAmount currencyID=\"NOK\">0</cbc:PrepaidAmount>\n" +
                "                <cbc:PayableRoundingAmount currencyID=\"NOK\">0</cbc:PayableRoundingAmount>\n" +
                "                <cbc:PayableAmount currencyID=\"NOK\">"+order.getTotalAmount()+"</cbc:PayableAmount>\n" +
                "        </cac:LegalMonetaryTotal>\n";
        
        xml += createInvoiceLines();
        
        xml +=  "</Invoice>";
        
        return xml;
    }
    
    private String createInvoiceLines() {
        String xml = "";
        
        for (CartItem item : order.cart.getItems()) {
            xml +=  "        <cac:InvoiceLine>\n" +
                    "                <cbc:ID>1</cbc:ID>\n" +
                    "                <cbc:Note>Scratch on box</cbc:Note>\n" +
                    "                <cbc:InvoicedQuantity unitCode=\"NAR\" unitCodeListID=\"UNECERec20\">1</cbc:InvoicedQuantity>\n" +
                    "                <cbc:LineExtensionAmount currencyID=\"NOK\">1273</cbc:LineExtensionAmount>\n" +
                    "                <cbc:AccountingCost>BookingCode001</cbc:AccountingCost>\n" +
                    "                <cac:InvoicePeriod>\n" +
                    "                        <cbc:StartDate>2018-08-01</cbc:StartDate>\n" +
                    "                        <cbc:EndDate>2018-08-30</cbc:EndDate>\n" +
                    "                </cac:InvoicePeriod>\n" +
                    "                <cac:OrderLineReference>\n" +
                    "                        <cbc:LineID>1</cbc:LineID>\n" +
                    "                </cac:OrderLineReference>\n" +
                    "                <cac:Item>\n" +
                    "                        <cbc:Description>"+item.getProduct().name+"</cbc:Description>\n" +
                    "                        <cbc:Name>"+item.getProduct().name+"</cbc:Name>\n" +
                    "                        <cac:SellersItemIdentification>\n" +
                    "                                <cbc:ID>"+item.getProduct().incrementalProductId+"</cbc:ID>\n" +
                    "                        </cac:SellersItemIdentification>\n" +
                    "                        <cac:StandardItemIdentification>\n" +
                    "                                <cbc:ID schemeID=\"GTIN\">"+item.getProduct().incrementalProductId+"</cbc:ID>\n" +
                    "                        </cac:StandardItemIdentification>\n" +
                    "                        <cac:OriginCountry>\n" +
                    "                        <cbc:IdentificationCode listID=\"ISO3166-1:Alpha2\">NO</cbc:IdentificationCode>\n" +
                    "                        </cac:OriginCountry>\n" +
                    "                        <cac:ClassifiedTaxCategory>\n" +
                    "                                <cbc:ID schemeID=\"UNCL5305\">S</cbc:ID>\n" +
                    "                                <cbc:Percent>"+item.getProduct().taxGroupObject.taxRate+"</cbc:Percent>\n" +
                    "                                <cac:TaxScheme>\n" +
                    "                                        <cbc:ID>VAT</cbc:ID>\n" +
                    "                                </cac:TaxScheme>\n" +
                    "                        </cac:ClassifiedTaxCategory>\n" +
                    "                </cac:Item>\n" +
                    "                <cac:Price>\n" +
                    "                        <cbc:PriceAmount currencyID=\"NOK\">"+item.getProduct().price+"</cbc:PriceAmount>\n" +
                    "                        <cbc:BaseQuantity>"+item.getCount()+"</cbc:BaseQuantity>\n" +
                    "                </cac:Price>\n" +
                    "        </cac:InvoiceLine>\n";
        }
        
        return xml;
    }
    
    private String generateSubTaxes() {
        Map<TaxGroup, Double> taxes = order.getTaxes();
        String xml = "";
        for (TaxGroup group : taxes.keySet()) {
            xml +=  "                <cac:TaxSubtotal>\n" +
                    "                        <cbc:TaxableAmount currencyID=\"NOK\">"+order.getTotalAmountForTaxGroup(group)+"</cbc:TaxableAmount>\n" +
                    "                        <cbc:TaxAmount currencyID=\"NOK\">"+taxes.get(group)+"</cbc:TaxAmount>\n" +
                    "                        <cac:TaxCategory>\n" +
                    "                                <cbc:ID schemeID=\"UNCL5305\">S</cbc:ID>\n" +
                    "                                <cbc:Percent>"+group.taxRate+"</cbc:Percent>\n" +
                    "                                <cac:TaxScheme>\n" +
                    "                                        <cbc:ID>VAT</cbc:ID>\n" +
                    "                                </cac:TaxScheme>\n" +
                    "                        </cac:TaxCategory>\n" +
                    "                </cac:TaxSubtotal>\n";
        }    
        
        return xml;
    }
     
    private String getCurrentByCode() {
        return "NOK";
    }

    private void createSimpleDateFormatter() {
        this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    }

    private String getBase64EncodedInvoice() {
        return "";
    }
}
