package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.model.CapsuleEntity;

import org.springframework.stereotype.Service;

@Service
public interface CapsuleService {
    CapsuleEntity getCapsuleById(Long id);
}
