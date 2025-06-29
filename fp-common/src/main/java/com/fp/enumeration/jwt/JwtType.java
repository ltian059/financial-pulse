package com.fp.enumeration.jwt;

import lombok.Getter;

///
/// # Enumeration for JWT token types, which is in Jwt Claims
///
@Getter
public enum JwtType {
    /**
     * access: Used for API access
     */
    ACCESS("ACCESS"),
    /**
     * refresh: Used for refreshing access tokens
     */
    REFRESH("REFRESH"),
    /**
     * verify: Used for verifying account email or other verification processes
     */
    VERIFICATION("VERIFICATION")
    ;


    private final String type;

    JwtType(String type) {
        this.type = type;
    }

    public static JwtType fromString(String type) {
        for (JwtType jwtType : JwtType.values()) {
            if (jwtType.getType().equalsIgnoreCase(type)) {
                return jwtType;
            }
        }
        throw new IllegalArgumentException("Unknown JWT type: " + type);
    }


}
