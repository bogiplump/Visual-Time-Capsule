package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.capsule.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleResponseDto;
import com.java.web.virtual.time.capsule.enums.CapsuleStatus;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleHasBeenLocked;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotOwnedByYou;
import com.java.web.virtual.time.capsule.exception.sharedcapsuele.IsNotSharedCapsule;
import com.java.web.virtual.time.capsule.exception.sharedcapsuele.UserAlreadyInCapsule;
import com.java.web.virtual.time.capsule.exception.sharedcapsuele.UserNotInCapsule;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.CapsuleParticipant;
import com.java.web.virtual.time.capsule.model.SharedCapsule;
import com.java.web.virtual.time.capsule.model.User;
import com.java.web.virtual.time.capsule.repository.CapsuleParticipantRepository;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.SharedCapsuleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@AllArgsConstructor
@Service
public class SharedCapsuleServiceImpl implements SharedCapsuleService {
    private UserRepository userRepository;
    private CapsuleRepository capsuleRepository;
    private CapsuleParticipantRepository participantRepository;

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
    public Long createSharedCapsule(CapsuleCreateDto sharedCapsuleDto, String currentUser) {
        User user = userRepository.findByUsername(currentUser);

        Capsule capsule = Capsule.fromDTOAndUser(sharedCapsuleDto, user);
        capsule.setIsShared(true);

        Long capsuleId = capsuleRepository.save(capsule).getId();
        CapsuleParticipant participantToAdd =
            CapsuleParticipant.ofCapsuleAndParticipantId(capsuleId, user.getId(), false);

        participantRepository.save(participantToAdd);

        return capsuleId;
    }

    @Override
    public SharedCapsuleResponseDto getSharedCapsuleById(Long id, String currentUser) {
        handleIsIdToValidCapsule(id);
        Capsule capsule = capsuleRepository.findById(id).get();

        User user = userRepository.findByUsername(currentUser);
        if (!participantRepository.existsByCapsuleIdAndParticipantId(id, user.getId())) {
            throw new UserNotInCapsule("You are not in capsule with id " + id + " so you do not have access to it. ");
        }

        Set<CapsuleParticipant> participants = participantRepository.findByCapsuleId(id);
        SharedCapsule sharedCapsule = new SharedCapsule(capsule, participants);

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
                + capsuleId + ". You can only add a user to the shared capsule if he is not a participant. ");
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
                + capsuleId + ". You can only remove a user from the shared capsule if he is a participant. ");
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
