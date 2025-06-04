package com.faang.postservice.dto.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    private Long id;
    @NotBlank(message = "Invalid Name: Empty name")
    @NotNull(message = "Invalid Name: Name is NULL")
    @Size(min = 3, max = 30, message = "Invalid Name: Exceeds 30 characters")
    private String username;
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "Invalid Phone number: Empty number")
    @NotNull(message = "Invalid Phone number: Number is NULL")
    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    String mobile;
    @Min(value = 1, message = "Invalid Age: Equals to zero")
    @Max(value = 200, message = "Invalid Age: Exceeds 200 years")
    Integer age;
    private Long lastPostId;
    private List<Long> followers;
}
