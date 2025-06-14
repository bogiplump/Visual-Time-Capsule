package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleResponseDto;
import com.java.web.virtual.time.capsule.enums.CapsuleStatus;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleHasBeenLocked;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotOwnedByYou;
import com.java.web.virtual.time.capsule.exception.sharedcapsuele.IsNotSharedCapsule;
import com.java.web.virtual.time.capsule.exception.sharedcapsuele.UserAlreadyInCapsule;
import com.java.web.virtual.time.capsule.exception.sharedcapsuele.UserNotInCapsule;
import com.java.web.virtual.time.capsule.model.CapsuleParticipant;
import com.java.web.virtual.time.capsule.model.SharedCapsule;
import com.java.web.virtual.time.capsule.model.User;
import com.java.web.virtual.time.capsule.repository.CapsuleParticipantRepository;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.SharedCapsuleRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.SharedCapsuleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SharedCapsuleServiceImpl implements SharedCapsuleService {
    private UserRepository userRepository;
    private CapsuleRepository capsuleRepository;
    private CapsuleParticipantRepository participantRepository;
    private SharedCapsuleRepository repository;

    private void handleIsIdToValidCapsule(Long capsuleId) {
        if (!capsuleRepository.existsById(capsuleId)) {
            throw new CapsuleNotFound("Shared capsule with id " + capsuleId + " was not found. ");
        }

        if (!capsuleRepository.findIsSharedById(capsuleId)) {
            throw new IsNotSharedCapsule("Capsule with id " + capsuleId + " is not a shared capsule. ");
        }
    }

    private void handleUserNotCapsuleOwner(Long capsuleId, Long userId) {
        if (!capsuleRepository.existsByIdAndCreatorId(capsuleId, userId)) {
            throw new CapsuleNotOwnedByYou("You are not the creator of the capsule with id " + capsuleId + ". ");
        }
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

        if (!sharedCapsule.getCapsule().getIsShared()) {
            throw new IsNotSharedCapsule("Capsule with id " + id + " is not a shared capsule. ");
        }

        User user = userRepository.findByUsername(currentUser);
        if (!participantRepository.existsByCapsuleIdAndParticipantId(id, user.getId())) {
            throw new UserNotInCapsule("You are not in capsule with id " + id + ". ");
        }

        return sharedCapsule.toResponseDto();
    }

    @Override
    public void addUserToCapsule(Long capsuleId, Long userId, String currentUser) {
        handleIsIdToValidCapsule(capsuleId);
        if (capsuleRepository.findStatusById(capsuleId) == CapsuleStatus.CLOSED) {
            throw new CapsuleHasBeenLocked("Can not add participants to a locked shared capsule. ");
        }

        User currUser = userRepository.findByUsername(currentUser);
        try {
            handleUserNotCapsuleOwner(capsuleId, currUser.getId());
        } catch (CapsuleNotOwnedByYou ex) {
            throw new CapsuleNotOwnedByYou(ex.getMessage() + "Only the creator of the shared capsule can add participants. ");
        }

        if (participantRepository.existsByCapsuleIdAndParticipantId(capsuleId, userId)) {
            throw new UserAlreadyInCapsule("The user with id " + userId + " is in the shared capsule with id "
                + capsuleId + ". You can only remove a user from the shared capsule if he is a participant. ");
        }

        CapsuleParticipant participantToAdd = CapsuleParticipant.ofCapsuleAndParticipantId(capsuleId, userId, false);

        participantRepository.save(participantToAdd);
    }

    @Override
    public void removeUserFromCapsule(Long capsuleId, Long userId, String currentUser) {
        handleIsIdToValidCapsule(capsuleId);
        if (capsuleRepository.findStatusById(capsuleId) == CapsuleStatus.CLOSED) {
            throw new CapsuleHasBeenLocked("Can not remove participants from a locked shared capsule. ");
        }

        User currUser = userRepository.findByUsername(currentUser);
        try {
            handleUserNotCapsuleOwner(capsuleId, currUser.getId());
        } catch (CapsuleNotOwnedByYou ex) {
            throw new CapsuleNotOwnedByYou(ex.getMessage() + "Only the creator of the shared capsule can remove participants. ");
        }

        if (!participantRepository.existsByCapsuleIdAndParticipantId(capsuleId, userId)) {
            throw new UserNotInCapsule("The user with id " + userId + " is not in the shared capsule with id "
                + capsuleId + ". You can only add a user in the shared capsule if he is not a participant. ");
        }

        participantRepository.deleteByCapsuleIdAndParticipantId(capsuleId, userId);
    }

    @Override
    public void deleteSharedCapsuleById(Long id, String currentUser) {
        handleIsIdToValidCapsule(id);

        User currUser = userRepository.findByUsername(currentUser);
        try {
            handleUserNotCapsuleOwner(id, currUser.getId());
        } catch (CapsuleNotOwnedByYou ex) {
            throw new CapsuleNotOwnedByYou(ex.getMessage() + "Only the creator of the shared capsule can delete it. ");
        }

        participantRepository.deleteByCapsuleId(id);
        capsuleRepository.deleteById(id);
    }
}
