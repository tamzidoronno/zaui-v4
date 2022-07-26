
package com.thundashop.core.paymentmanager.EasyByNets.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class InvoiceDetails {

    @SerializedName("invoiceNumber")
    @Expose
    private String invoiceNumber;
    @SerializedName("ocr")
    @Expose
    private String ocr;
    @SerializedName("pdfLink")
    @Expose
    private String pdfLink;
    @SerializedName("dueDate")
    @Expose
    private String dueDate;
}
