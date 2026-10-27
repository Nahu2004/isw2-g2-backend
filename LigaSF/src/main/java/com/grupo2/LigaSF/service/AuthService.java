package com.grupo2.LigaSF.service;

import com.grupo2.LigaSF.dto.request.LoginRequest;
import com.grupo2.LigaSF.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
