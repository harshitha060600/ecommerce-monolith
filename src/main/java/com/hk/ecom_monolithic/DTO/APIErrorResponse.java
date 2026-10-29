package com.hk.ecom_monolithic.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIErrorResponse {
    public String message;
    private boolean status;
}
