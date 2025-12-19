package com.example.leaftalk.domain.member.repository;

import com.example.leaftalk.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    Optional<Member> findByEmailAndIsLockAndIsSocial(String email, boolean isLock, boolean isSocial);

}
