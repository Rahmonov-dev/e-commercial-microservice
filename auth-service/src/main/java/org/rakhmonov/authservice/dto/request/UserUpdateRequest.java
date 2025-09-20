package org.rakhmonov.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Phone number must be in format +998XXXXXXXXX")
    private String phoneNumber;
    
    private String password; // Optional for updates
}







