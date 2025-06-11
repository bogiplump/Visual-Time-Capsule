package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleResponseDto;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.model.SharedCapsule;
import com.java.web.virtual.time.capsule.model.User;
import com.java.web.virtual.time.capsule.repository.SharedCapsuleRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.CapsuleService;
import com.java.web.virtual.time.capsule.service.SharedCapsuleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SharedCapsuleServiceImpl implements SharedCapsuleService {
    private UserRepository userRepository;
    private CapsuleService capsuleService;
    private SharedCapsuleRepository repository;

    public static void handleUserNotInCapsule(SharedCapsule capsule, Long userId) {

    }

    @Override
    public Long createSharedCapsule(SharedCapsuleCreateDto sharedCapsuleDto, String currentUser) {
        User user = userRepository.findByUsername(currentUser);

        SharedCapsule sharedCapsule = SharedCapsule.fromDtoAndUser(sharedCapsuleDto, user);

        return repository.save(sharedCapsule).getCapsule().getId();
    }



    @Override
    public SharedCapsuleResponseDto getSharedCapsuleById(Long id, String currentUser) {
        SharedCapsule sharedCapsule = repository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Shared capsule with id " + id + " was not found. "));

        User user = userRepository.findByUsername(currentUser);
        handleUserNotInCapsule(sharedCapsule, user.getId());

        return sharedCapsule.toResponseDto();
    }

    public SharedCapsule retrieveCapsuleById(Long id, String currentUser) {
        SharedCapsule sharedCapsule = repository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Shared capsule with id " + id + " was not found. "));

        User user = userRepository.findByUsername(currentUser);
        handleUserNotInCapsule(sharedCapsule, user.getId());

        return sharedCapsule;
    }

    @Override
    public void addUserToCapsule(Long capsuleId, Long userId, String currentUser) {
        SharedCapsule sharedCapsule = retrieveCapsuleById(capsuleId, currentUser);

        sharedCapsule.addUserToCapsule(userId);
        repository.save(sharedCapsule);
    }

    @Override
    public void removeUserFromCapsule(Long capsuleId, Long userId, String currentUser) {
        SharedCapsule sharedCapsule = retrieveCapsuleById(capsuleId, currentUser);

        sharedCapsule.removeUserFromCapsule(userId);
        repository.save(sharedCapsule);
    }

    @Override
    public void deleteSharedCapsuleById(Long id, String currentUser) {
        User user = userRepository.findByUsername(currentUser);

        if (repository.existsByIdAndCreator_Id(id,user.getId())) {
            throw new CapsuleNotFound("Capsule with  id " + id + " was not found or is not owned by you. ");
        }

        repository.deleteById(id);
    }
}
