package org.rakhmonov.authservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
