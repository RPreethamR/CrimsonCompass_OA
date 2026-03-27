package tms.crimsoncompass.TMS_OAuth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Payload sent to the TMS main service to synchronize an OAuth-authenticated user.
 */
public record UserSyncRequest(

        @NotBlank(message = "authId is required")
        String authId,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        String email,

        @NotBlank(message = "name is required")
        String name
) {}
