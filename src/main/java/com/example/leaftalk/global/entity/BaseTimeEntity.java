package com.example.leaftalk.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseTimeEntity extends BaseCreateTimeEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
