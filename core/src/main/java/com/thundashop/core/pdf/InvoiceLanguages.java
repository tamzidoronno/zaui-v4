package com.thundashop.core.pdf;

import java.util.HashMap;

public class InvoiceLanguages {
    HashMap<String, HashMap<String, String>> languages = new HashMap();
    

    public InvoiceLanguages() {
        //English
        HashMap<String, String> languageMap = new HashMap();
        languageMap.put("Invoice", "");
        languageMap.put("Order date", "");
        languageMap.put("Order number", "");
        languageMap.put("Due date", "");
        languageMap.put("To account", "");
        languageMap.put("Note", "");
        languageMap.put("Sumary", "");
        languageMap.put("Net amount", "");
        languageMap.put("Total", "");
        languageMap.put("Already paid", "");
        languageMap.put("Pay to account", "");
        languageMap.put("Amount", "");
        languageMap.put("Paid by account", "");
        languageMap.put("Payment information", "");
        languageMap.put("Invoice date", "");
        languageMap.put("Invoice number", "");
        languageMap.put("Paid by", "");
        languageMap.put("Paid to", "");
        languageMap.put("Customer identification (KID)", "");
        languageMap.put("To account", "");
        languages.put("en_en", languageMap);
        
        languageMap = new HashMap();
        languageMap.put("Invoice", "Faktura");
        languageMap.put("Order date", "Ordre dato");
        languageMap.put("Order number", "Ordrenummer");
        languageMap.put("Due date", "Forfallsdato");
        languageMap.put("To account", "Til konto");
        languageMap.put("Note", "Bemerkning");
        languageMap.put("Sumary", "Sammendrag");
        languageMap.put("Net amount", "Netto beløp");
        languageMap.put("Total", "Total");
        languageMap.put("Already paid", "Allerede betalt");
        languageMap.put("Pay to account", "Betal til konto");
        languageMap.put("Amount", "Beløp");
        languageMap.put("Paid by account", "Betalt fra konto");
        languageMap.put("Payment information", "Betalingsinformasjon");
        languageMap.put("Invoice date", "Fakturadato");
        languageMap.put("Invoice number", "Fakturanummer");
        languageMap.put("Paid by", "Betalt av");
        languageMap.put("Paid to", "Betalt til");
        languageMap.put("Customer identification (KID)", "Kundeindenfikasjon (KID)");
        languageMap.put("To account", "Til konto");
        languages.put("nb_no", languageMap);
        
    }

    HashMap<String, String> getLanguageMatrix(String lang) {
        if(languages.containsKey(lang)) {
            return languages.get(lang);
        }
        return languages.get("en_en");
    }
    
}
