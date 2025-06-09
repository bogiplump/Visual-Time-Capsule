package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.capsule.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.capsule.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotOwnedByYou;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Goal;
import com.java.web.virtual.time.capsule.model.Memory;
import com.java.web.virtual.time.capsule.model.User;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.CapsuleService;
import com.java.web.virtual.time.capsule.service.GoalService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class CapsuleServiceImpl implements CapsuleService {
    private final static String dateTimePattern = "HH-mm-ss_dd-MM-yyyy";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);

    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;
    private final GoalService goalService;

    public static void handleCapsuleNotOwnedByUser(Capsule capsule, Long userId) {
        if (capsule == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::isCapsuleOwnedByCurrentUser()");
        }

        if (!Objects.equals(capsule.getCreator().getId(), userId)) {
            throw new CapsuleNotOwnedByYou("Capsule with id " + capsule.getId() + " is not owned by you");
        }
    }

    @Override
    public Long createCapsule(CapsuleCreateDto capsuleDto,String currentUser) {
        User user = userRepository.findByUsername(currentUser);
        Capsule capsule = Capsule.fromDTOAndUser(capsuleDto,user);
        capsule.getGoal().setCapsule(capsule);

        return capsuleRepository.save(capsule).getId();
    }

    @Override
    public Capsule getCapsule(Long id, String currentUser) { //TODO: How a locked capsule is send to client
        User user = userRepository.findByUsername(currentUser);
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + id + " was not found. "));

        handleCapsuleNotOwnedByUser(capsule,user.getId());

        return capsule;
    }

    @Override
    public Set<Capsule> getAllCapsulesOfUser(String currentUser) {
        User user = userRepository.findByUsername(currentUser);
       return capsuleRepository.findByCreator_Id(user.getId());
    }

    @Override
    public void lockCapsule(Long id, String openDateInString, String lockingUsername) {//TODO better parsing of openDateInString
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + id + " was not found"));
        User lockingUser = userRepository.findByUsername(lockingUsername);

        handleCapsuleNotOwnedByUser(capsule,lockingUser.getId());

        LocalDateTime openDate = LocalDateTime.parse(openDateInString, formatter);

        capsule.lock(openDate);

        capsuleRepository.save(capsule);
    }

    @Override
    public void updateCapsule(Long id, CapsuleUpdateDto capsuleDto, String currentUser) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + id + " was not found. "));

        User user = userRepository.findByUsername(currentUser);
        handleCapsuleNotOwnedByUser(capsule,user.getId());

        capsule.setCapsuleName(capsuleDto.getCapsuleName());
        capsule.setOpenDate(capsuleDto.getOpenDate());
        Goal goal = Goal.fromDTO(capsuleDto.getGoal(),user);

        if (capsule.getGoal() != null) {
            goalService.deleteGoal(capsule.getGoal().getId());
        }
        capsule.setGoal(goal);
        capsuleRepository.save(capsule);
    }

    @Override
    public void deleteCapsule(Long id, String currentUser) {
        User user = userRepository.findByUsername(currentUser);

        if (capsuleRepository.existsByIdAndCreator_Id(id,user.getId())) {
           throw new CapsuleNotFound("Capsule with  id " + id + " was not found or is not owned by you. ");
        }

        capsuleRepository.deleteById(id);
    }

    @Override
    public Set<Memory> getMemoriesFromCapsule(Long id, String currentUser) {
        User user = userRepository.findByUsername(currentUser);
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + id + " was not found"));

        handleCapsuleNotOwnedByUser(capsule,user.getId());

        return capsule.getMemoryEntries();
    }

    @Override
    public Goal getGoalForCapsule(Long id, String currentUser) {
        User user = userRepository.findByUsername(currentUser);
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + id + " was not found"));

        handleCapsuleNotOwnedByUser(capsule,user.getId());

        return capsule.getGoal();
    }
}
