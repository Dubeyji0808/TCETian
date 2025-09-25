package com.ayush.TCETian.payload;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
