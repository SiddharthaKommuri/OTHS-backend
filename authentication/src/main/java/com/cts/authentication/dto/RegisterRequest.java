package com.cts.authentication.dto;
import com.cts.authentication.validation.ValidContactNumber;
import com.cts.authentication.validation.ValidPassword; // Custom annotation for strong password
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Password is required")
    @ValidPassword // Using our custom strong password validation
    private String password;
    @NotBlank(message = "Role is required")
    // Pattern to allow both uppercase and lowercase roles using (?i) for case-insensitive matching
    @Pattern(regexp = "(?i)^(ADMIN|TRAVELER|HOTEL_MANAGER|TRAVEL_AGENT)$", message = "Invalid role. Must be ADMIN, TRAVELER, HOTEL_MANAGER, or TRAVEL_AGENT")
    private String role;
    @NotBlank(message = "Contact number is required")
    @ValidContactNumber // Using our custom contact number validation
    private String contactNumber;

    // Custom setter for role to convert it to UPPERCASE for consistent storage
    public void setRole(String role) {
        if (role != null) {
            this.role = role.toUpperCase(); // Convert to uppercase for storage
        } else {
            this.role = null;
        }
    }
}