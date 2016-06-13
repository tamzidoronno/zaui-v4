/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author boggi
 */
public class SveaXML {
          public static File createXmlFile() {
            try {
                // Create temp file.
                File temp = File.createTempFile("pattern", ".suffix");

                // Delete temp file when program exits.
                temp.deleteOnExit();

                // Write to temp file
                BufferedWriter out = new BufferedWriter(new FileWriter(temp));
                out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
"<!-- edited with XMLSpy v2015 rel. 3 (x64) (http://www.altova.com) by Kentg (Svea Finans NUF) -->\n" +
"<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\">\n" +
"	<xs:element name=\"CustomerData\">\n" +
"		<xs:annotation>\n" +
"			<xs:documentation>Root element for invoicedata to collection</xs:documentation>\n" +
"		</xs:annotation>\n" +
"		<xs:complexType>\n" +
"			<xs:sequence>\n" +
"				<xs:element name=\"Creditor\">\n" +
"					<xs:annotation>\n" +
"						<xs:documentation>Name and contact information to creditor</xs:documentation>\n" +
"					</xs:annotation>\n" +
"					<xs:complexType>\n" +
"						<xs:sequence>\n" +
"							<xs:element name=\"CustomerIdentification\" type=\"xs:string\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Creditors identification number at Svea. Used to identify customer in import module.</xs:documentation>\n" +
"								</xs:annotation>\n" +
"							</xs:element>\n" +
"							<xs:element name=\"CaseFromCreditorWeb\" type=\"xs:boolean\"/>\n" +
"							<xs:element name=\"Name\" type=\"xs:string\" minOccurs=\"0\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Creditors name. Only for information.</xs:documentation>\n" +
"								</xs:annotation>\n" +
"							</xs:element>\n" +
"							<xs:element name=\"OrgNumber\" type=\"xs:integer\" minOccurs=\"0\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Creditors Organization number. Only for information.</xs:documentation>\n" +
"								</xs:annotation>\n" +
"							</xs:element>\n" +
"							<xs:element name=\"BirthDate\" type=\"xs:date\" minOccurs=\"0\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Creditors BirthDate. Only for information.</xs:documentation>\n" +
"								</xs:annotation>\n" +
"							</xs:element>\n" +
"							<xs:element name=\"Address\" type=\"AddressType\" minOccurs=\"0\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Address information. only for information.</xs:documentation>\n" +
"								</xs:annotation>\n" +
"							</xs:element>\n" +
"							<xs:element name=\"PhoneNumber\" type=\"xs:string\" minOccurs=\"0\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Creditors phonenumber. only for information.</xs:documentation>\n" +
"								</xs:annotation>\n" +
"							</xs:element>\n" +
"							<xs:element name=\"Fax\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"							<xs:element name=\"CellularPhone\" type=\"xs:string\" minOccurs=\"0\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Creditors CellularPhone. only for information.</xs:documentation>\n" +
"								</xs:annotation>\n" +
"							</xs:element>\n" +
"							<xs:element name=\"Email\" type=\"xs:string\" minOccurs=\"0\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Creditors email. only for information.</xs:documentation>\n" +
"								</xs:annotation>\n" +
"							</xs:element>\n" +
"							<xs:element name=\"Internet\" type=\"xs:string\" minOccurs=\"0\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Creditors web-address. only for information.</xs:documentation>\n" +
"								</xs:annotation>\n" +
"							</xs:element>\n" +
"							<xs:element name=\"GUID\" type=\"xs:string\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Used as a uniq key for each file.</xs:documentation>\n" +
"								</xs:annotation>\n" +
"							</xs:element>\n" +
"							<xs:element name=\"Cases\">\n" +
"								<xs:annotation>\n" +
"									<xs:documentation>Includes new case assignments to be submitted debt collection company</xs:documentation>\n" +
"								</xs:annotation>\n" +
"								<xs:complexType>\n" +
"									<xs:sequence>\n" +
"										<xs:element name=\"Case\" maxOccurs=\"unbounded\">\n" +
"											<xs:annotation>\n" +
"												<xs:documentation>total claims pr. customer ledge. CREATE NEW CASE</xs:documentation>\n" +
"											</xs:annotation>\n" +
"											<xs:complexType>\n" +
"												<xs:sequence>\n" +
"													<xs:element name=\"CaseType\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>I = Collection, P = Reminder UPDATE 'cas_fk_role'  (Reminder or Debt.collection)</xs:documentation>\n" +
"														</xs:annotation>\n" +
"														<xs:simpleType>\n" +
"															<xs:restriction base=\"xs:string\">\n" +
"																<xs:enumeration value=\"I\"/>\n" +
"																<xs:enumeration value=\"P\"/>\n" +
"															</xs:restriction>\n" +
"														</xs:simpleType>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"SplitValue\" type=\"xs:string\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>If a Customer needs splitting of the portifolio on several customernumer, the split value on Case-level can be put here.</xs:documentation>\n" +
"														</xs:annotation>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"CustomerReferance1\" type=\"xs:string\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>CustomerNumber, etc </xs:documentation>\n" +
"														</xs:annotation>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"CustomerReferance2\" type=\"xs:string\" minOccurs=\"0\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>Extra referance if several ledges under same customernumber, etc  </xs:documentation>\n" +
"														</xs:annotation>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"CustomerReferance3\" type=\"xs:string\" minOccurs=\"0\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>extra field for referances</xs:documentation>\n" +
"														</xs:annotation>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"CustomerNumberAtSvea\" type=\"xs:string\" minOccurs=\"0\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>If the Client is sending this information to us</xs:documentation>\n" +
"														</xs:annotation>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"CustomerType\" type=\"xs:string\" minOccurs=\"0\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>Code for specification of customertype for example VIP, A - customer, B - customer</xs:documentation>\n" +
"														</xs:annotation>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"ExternalId\" type=\"xs:string\" minOccurs=\"0\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>Creditors internal id/number for the generated case. This is if the system generates an Id to the case when it is exported</xs:documentation>\n" +
"														</xs:annotation>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"HasObjections\" type=\"xs:boolean\" minOccurs=\"0\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>if there is registered objections to the claim. then Svea can't start debt.collection directly</xs:documentation>\n" +
"														</xs:annotation>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"CaseNotes\" type=\"xs:string\" minOccurs=\"0\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>Notes/comments that would be useful for Svea to have</xs:documentation>\n" +
"														</xs:annotation>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"Debtor\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>Information about debtor  </xs:documentation>\n" +
"														</xs:annotation>\n" +
"														<xs:complexType>\n" +
"															<xs:sequence>\n" +
"																<xs:element name=\"Name\" type=\"xs:string\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>Lastname firstname</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"CustomerNumber\" type=\"xs:integer\" minOccurs=\"0\"/>\n" +
"																<xs:element name=\"OrgNumber_Socialnumber\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>Organizationnumber 9 digits, birthdate (ddmmyy) or full socialnumber 11 digits</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"Address\" type=\"AddressType\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"BirthDate\" type=\"xs:date\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"PhoneNumber\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>Other then cellularphonenumber</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"OtherNumber\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>other then cellularphponenumber</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"CellularPhoneNumber\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																	<xs:complexType>\n" +
"																		<xs:simpleContent>\n" +
"																			<xs:extension base=\"xs:string\">\n" +
"																				<xs:attribute name=\"Is_SMS_Verified\" type=\"xs:boolean\">\n" +
"																					<xs:annotation>\n" +
"																						<xs:documentation>set to true if customer has agreed to this number beeing used for SMS communication</xs:documentation>\n" +
"																					</xs:annotation>\n" +
"																				</xs:attribute>\n" +
"																			</xs:extension>\n" +
"																		</xs:simpleContent>\n" +
"																	</xs:complexType>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"Email\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"Internet\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>not in use</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"AccountNumber\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>not in use</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"NumbersOfContracts\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																<xs:element name=\"Debtor2\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>If more then one debtor connected to the claim.</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																	<xs:complexType>\n" +
"																		<xs:sequence>\n" +
"																			<xs:element name=\"CustomerNumber\" type=\"xs:integer\" minOccurs=\"0\"/>\n" +
"																			<xs:element name=\"Name\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																			<xs:element name=\"OrgNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																			<xs:element name=\"BirthDate\" type=\"xs:date\" minOccurs=\"0\"/>\n" +
"																			<xs:element name=\"Address\" type=\"AddressType\" minOccurs=\"0\"/>\n" +
"																			<xs:element name=\"PhoneNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																			<xs:element name=\"CellularPhoneNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																			<xs:element name=\"Email\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																			<xs:element name=\"Internet\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																		</xs:sequence>\n" +
"																		<xs:attribute name=\"Debtor2Response\">\n" +
"																			<xs:annotation>\n" +
"																				<xs:documentation>Information about debtor2 Respons - solidarity, guarantee liability, etc.: S = Solidarity, K = Guarantee Liability </xs:documentation>\n" +
"																			</xs:annotation>\n" +
"																			<xs:simpleType>\n" +
"																				<xs:restriction base=\"xs:string\">\n" +
"																					<xs:enumeration value=\"S\"/>\n" +
"																					<xs:enumeration value=\"K\"/>\n" +
"																				</xs:restriction>\n" +
"																			</xs:simpleType>\n" +
"																		</xs:attribute>\n" +
"																		<xs:attribute name=\"IsFirm\" type=\"xs:boolean\"/>\n" +
"																	</xs:complexType>\n" +
"																</xs:element>\n" +
"															</xs:sequence>\n" +
"															<xs:attribute name=\"IsFirm\" type=\"xs:boolean\">\n" +
"																<xs:annotation>\n" +
"																	<xs:documentation>If debtor is person or company (Firm)</xs:documentation>\n" +
"																</xs:annotation>\n" +
"															</xs:attribute>\n" +
"														</xs:complexType>\n" +
"													</xs:element>\n" +
"													<xs:element name=\"Invoice\" maxOccurs=\"unbounded\">\n" +
"														<xs:annotation>\n" +
"															<xs:documentation>Will contain 1 to n numbers invoice item per case. Each bill represents one claim to the end customer with associated kid, amount and payment deadline</xs:documentation>\n" +
"														</xs:annotation>\n" +
"														<xs:complexType>\n" +
"															<xs:sequence>\n" +
"																<xs:element name=\"DueDate\" type=\"xs:date\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceDate\" type=\"xs:date\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceNumber\" type=\"xs:string\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"CreditorClaimNumber\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>For additional id/number that is needed. For example if system generated debt.collection number on the invoice</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceAmount\" minOccurs=\"0\">\n" +
"																	<xs:simpleType>\n" +
"																		<xs:restriction base=\"xs:decimal\">\n" +
"																			<xs:fractionDigits value=\"2\"/>\n" +
"																		</xs:restriction>\n" +
"																	</xs:simpleType>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceRemainingAmount\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																	<xs:simpleType>\n" +
"																		<xs:restriction base=\"xs:decimal\">\n" +
"																			<xs:fractionDigits value=\"2\"/>\n" +
"																		</xs:restriction>\n" +
"																	</xs:simpleType>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceInterestRate\" type=\"xs:decimal\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>If there is a set interest on the claim, this field should have that interestrate</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"CurrencyCode\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"CurrencyRate\" minOccurs=\"0\">\n" +
"																	<xs:simpleType>\n" +
"																		<xs:restriction base=\"xs:decimal\"/>\n" +
"																	</xs:simpleType>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceCurrencyAmount\" minOccurs=\"0\">\n" +
"																	<xs:simpleType>\n" +
"																		<xs:restriction base=\"xs:decimal\">\n" +
"																			<xs:fractionDigits value=\"2\"/>\n" +
"																		</xs:restriction>\n" +
"																	</xs:simpleType>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceCurrencyRemainingAmount\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																	<xs:simpleType>\n" +
"																		<xs:restriction base=\"xs:decimal\">\n" +
"																			<xs:fractionDigits value=\"2\"/>\n" +
"																		</xs:restriction>\n" +
"																	</xs:simpleType>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceInterestAmount\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>if invoice contains interestamount</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																	<xs:simpleType>\n" +
"																		<xs:restriction base=\"xs:decimal\">\n" +
"																			<xs:fractionDigits value=\"2\"/>\n" +
"																		</xs:restriction>\n" +
"																	</xs:simpleType>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceFeeAmount\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>if invoice contains fees, for example reminderfees</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																	<xs:simpleType>\n" +
"																		<xs:restriction base=\"xs:decimal\">\n" +
"																			<xs:fractionDigits value=\"2\"/>\n" +
"																		</xs:restriction>\n" +
"																	</xs:simpleType>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"Cid\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"ClosingRights\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>J = Yes, N = No. </xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																	<xs:simpleType>\n" +
"																		<xs:restriction base=\"xs:string\">\n" +
"																			<xs:enumeration value=\"J\"/>\n" +
"																			<xs:enumeration value=\"N\"/>\n" +
"																		</xs:restriction>\n" +
"																	</xs:simpleType>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceNotes\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>UPDATE 'inv_claimTypeInvoice'</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"ClaimTypeInvoice\" type=\"xs:string\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation></xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"ReminderSentDate\" type=\"xs:date\" minOccurs=\"0\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>If debtcollection we should get the date the reminderletter was sent.</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																</xs:element>\n" +
"																<xs:element name=\"InvoiceLines\" minOccurs=\"0\" maxOccurs=\"unbounded\">\n" +
"																	<xs:annotation>\n" +
"																		<xs:documentation>Will contain 1 to n number of invoicelines per invoice item. Contains information about the associated contracts and/or inoviced items</xs:documentation>\n" +
"																	</xs:annotation>\n" +
"																	<xs:complexType>\n" +
"																		<xs:sequence>\n" +
"																			<xs:element name=\"ClaimType\" type=\"xs:string\">\n" +
"																				<xs:annotation>\n" +
"																					<xs:documentation>letter code that describes the type of product or service</xs:documentation>\n" +
"																				</xs:annotation>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"ElectricitySystemInformation\" minOccurs=\"0\">\n" +
"																				<xs:complexType>\n" +
"																					<xs:sequence>\n" +
"																						<xs:element name=\"EanNr\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"MeterNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"Address\" type=\"AddressType\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"GridOwner\" minOccurs=\"0\">\n" +
"																							<xs:complexType>\n" +
"																								<xs:sequence>\n" +
"																									<xs:element name=\"Name\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																									<xs:element name=\"OrgNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																									<xs:element name=\"GlnNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																								</xs:sequence>\n" +
"																							</xs:complexType>\n" +
"																						</xs:element>\n" +
"																						<xs:element name=\"ElectricitySupplier\" minOccurs=\"0\">\n" +
"																							<xs:complexType>\n" +
"																								<xs:sequence>\n" +
"																									<xs:element name=\"Name\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																									<xs:element name=\"OrgNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																									<xs:element name=\"GlnNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																								</xs:sequence>\n" +
"																							</xs:complexType>\n" +
"																						</xs:element>\n" +
"																						<xs:element name=\"MeterReading\" minOccurs=\"0\">\n" +
"																							<xs:complexType>\n" +
"																								<xs:sequence>\n" +
"																									<xs:element name=\"LastDate\" type=\"xs:date\" minOccurs=\"0\">\n" +
"																										<xs:annotation>\n" +
"																											<xs:documentation>Siste dato for avlesing</xs:documentation>\n" +
"																										</xs:annotation>\n" +
"																									</xs:element>\n" +
"																									<xs:element name=\"LastMeterReading\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																										<xs:annotation>\n" +
"																											<xs:documentation>Last reading</xs:documentation>\n" +
"																										</xs:annotation>\n" +
"																									</xs:element>\n" +
"																									<xs:element name=\"LastActualDate\" type=\"xs:date\" minOccurs=\"0\">\n" +
"																										<xs:annotation>\n" +
"																											<xs:documentation>Last actual date, not stipulated</xs:documentation>\n" +
"																										</xs:annotation>\n" +
"																									</xs:element>\n" +
"																									<xs:element name=\"LastActualMeterReading\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																										<xs:annotation>\n" +
"																											<xs:documentation>Last actual reading, not stipulated</xs:documentation>\n" +
"																										</xs:annotation>\n" +
"																									</xs:element>\n" +
"																								</xs:sequence>\n" +
"																							</xs:complexType>\n" +
"																						</xs:element>\n" +
"																						<xs:element name=\"MeterStatus\" minOccurs=\"0\">\n" +
"																							<xs:complexType>\n" +
"																								<xs:sequence>\n" +
"																									<xs:element name=\"Id\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																									<xs:element name=\"Text\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																								</xs:sequence>\n" +
"																							</xs:complexType>\n" +
"																						</xs:element>\n" +
"																						<xs:element name=\"GridStaion\" minOccurs=\"0\">\n" +
"																							<xs:complexType>\n" +
"																								<xs:sequence>\n" +
"																									<xs:element name=\"GridStationNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																									<xs:element name=\"GridStationName\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																								</xs:sequence>\n" +
"																							</xs:complexType>\n" +
"																						</xs:element>\n" +
"																						<xs:element name=\"MeterDescription\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"MeterId\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																							<xs:annotation>\n" +
"																								<xs:documentation>Old EanNr</xs:documentation>\n" +
"																							</xs:annotation>\n" +
"																						</xs:element>\n" +
"																					</xs:sequence>\n" +
"																				</xs:complexType>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"InvoiceLineCompany\" minOccurs=\"0\">\n" +
"																				<xs:complexType>\n" +
"																					<xs:sequence>\n" +
"																						<xs:element name=\"CompanyOrgnr\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"CompanyGLNnr\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"CompanyName\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																					</xs:sequence>\n" +
"																				</xs:complexType>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"TollRoadDetails\" minOccurs=\"0\">\n" +
"																				<xs:complexType>\n" +
"																					<xs:sequence>\n" +
"																						<xs:element name=\"TollRoadName\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"TollRoadField\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"TollRoadProject\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"TollTagNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"CarNumber\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"TollRoadPassageDate\" type=\"xs:date\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"TollRoadPassageTime\" type=\"xs:time\" minOccurs=\"0\"/>\n" +
"																						<xs:element name=\"TollRoadNumberOfPasses\" type=\"xs:integer\" minOccurs=\"0\"/>\n" +
"																					</xs:sequence>\n" +
"																				</xs:complexType>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"InvoicelineAmount\" minOccurs=\"0\">\n" +
"																				<xs:simpleType>\n" +
"																					<xs:restriction base=\"xs:decimal\">\n" +
"																						<xs:fractionDigits value=\"2\"/>\n" +
"																					</xs:restriction>\n" +
"																				</xs:simpleType>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"InvoicelineRemainingAmount\">\n" +
"																				<xs:simpleType>\n" +
"																					<xs:restriction base=\"xs:decimal\">\n" +
"																						<xs:fractionDigits value=\"2\"/>\n" +
"																					</xs:restriction>\n" +
"																				</xs:simpleType>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"CurrencyCode\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																			<xs:element name=\"CurrencyRate\" minOccurs=\"0\">\n" +
"																				<xs:simpleType>\n" +
"																					<xs:restriction base=\"xs:float\"/>\n" +
"																				</xs:simpleType>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"InvoicelineCurrencyAmount\" minOccurs=\"0\">\n" +
"																				<xs:simpleType>\n" +
"																					<xs:restriction base=\"xs:decimal\">\n" +
"																						<xs:fractionDigits value=\"2\"/>\n" +
"																					</xs:restriction>\n" +
"																				</xs:simpleType>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"InvoicelineCurrencyRemainingAmount\" minOccurs=\"0\">\n" +
"																				<xs:simpleType>\n" +
"																					<xs:restriction base=\"xs:decimal\">\n" +
"																						<xs:fractionDigits value=\"2\"/>\n" +
"																					</xs:restriction>\n" +
"																				</xs:simpleType>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"InterestRate\" minOccurs=\"0\">\n" +
"																				<xs:simpleType>\n" +
"																					<xs:restriction base=\"xs:decimal\">\n" +
"																						<xs:fractionDigits value=\"2\"/>\n" +
"																					</xs:restriction>\n" +
"																				</xs:simpleType>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"ClaimStatus\" minOccurs=\"0\">\n" +
"																				<xs:annotation>\n" +
"																					<xs:documentation>Subscription status - A = Active, O = Terminated</xs:documentation>\n" +
"																				</xs:annotation>\n" +
"																				<xs:simpleType>\n" +
"																					<xs:restriction base=\"xs:string\">\n" +
"																						<xs:enumeration value=\"A\"/>\n" +
"																						<xs:enumeration value=\"O\"/>\n" +
"																					</xs:restriction>\n" +
"																				</xs:simpleType>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"ClaimCompanyId\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"																			<xs:element name=\"TerminatedDate\" type=\"xs:date\" minOccurs=\"0\">\n" +
"																				<xs:annotation>\n" +
"																					<xs:documentation>Date for termination of contract/subscription</xs:documentation>\n" +
"																				</xs:annotation>\n" +
"																			</xs:element>\n" +
"																			<xs:element name=\"SubscriptionPeriod\" type=\"xs:string\" minOccurs=\"0\">\n" +
"																				<xs:annotation>\n" +
"																					<xs:documentation>Subscription period the invoiceline/invoice is for</xs:documentation>\n" +
"																				</xs:annotation>\n" +
"																			</xs:element>\n" +
"																		</xs:sequence>\n" +
"																		<xs:attribute name=\"InvoiceLineId\" type=\"xs:string\" use=\"required\"/>\n" +
"																	</xs:complexType>\n" +
"																</xs:element>\n" +
"															</xs:sequence>\n" +
"														</xs:complexType>\n" +
"													</xs:element>\n" +
"												</xs:sequence>\n" +
"											</xs:complexType>\n" +
"										</xs:element>\n" +
"									</xs:sequence>\n" +
"								</xs:complexType>\n" +
"							</xs:element>\n" +
"						</xs:sequence>\n" +
"						<xs:attribute name=\"IsFirm\" type=\"xs:boolean\"/>\n" +
"					</xs:complexType>\n" +
"				</xs:element>\n" +
"			</xs:sequence>\n" +
"		</xs:complexType>\n" +
"	</xs:element>\n" +
"	<xs:complexType name=\"AddressType\">\n" +
"		<xs:sequence minOccurs=\"0\">\n" +
"			<xs:element name=\"Address1\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"			<xs:element name=\"Address2\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"			<xs:element name=\"Address3\" type=\"xs:string\" minOccurs=\"0\"/>\n" +
"			<xs:element name=\"PostalNumber\" type=\"xs:string\"/>\n" +
"			<xs:element name=\"PostalPlace\" type=\"xs:string\"/>\n" +
"			<xs:element name=\"CountryCode\" type=\"xs:string\"/>\n" +
"		</xs:sequence>\n" +
"	</xs:complexType>\n" +
"</xs:schema>");
                out.close();
                return temp;
            } catch (IOException e) {
        }
            return null;
       }
}
