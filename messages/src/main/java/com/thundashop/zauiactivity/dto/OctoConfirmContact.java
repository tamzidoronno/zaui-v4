package com.thundashop.zauiactivity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OctoConfirmContact {
    private String fullName;
    private String emailAddress;
    private String phoneNumber;
    private String country;
}
