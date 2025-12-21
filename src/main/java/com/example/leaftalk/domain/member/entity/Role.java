package com.example.leaftalk.domain.member.entity;

public enum Role {
    USER, ADMIN;

    public static Role from(String role) {
        try {
            return Role.valueOf(role.replace("ROLE_", ""));
        } catch (IllegalArgumentException _) {
            throw new SecurityException("잘못된 권한 정보: " + role);
        }
    }

    public String toAuthority() {
        return "ROLE_" + this.name();
    }
}
