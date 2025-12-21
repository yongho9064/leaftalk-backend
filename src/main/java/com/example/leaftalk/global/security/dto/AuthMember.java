package com.example.leaftalk.global.security.dto;

import com.example.leaftalk.domain.member.entity.Role;

public record AuthMember(Role role, String email) {
}
