package com.example.leaftalk.global.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseTimeEntity extends BaseCreateTimeEntity{

    @LastModifiedDate
    private LocalDateTime updatedAt;
    
}
