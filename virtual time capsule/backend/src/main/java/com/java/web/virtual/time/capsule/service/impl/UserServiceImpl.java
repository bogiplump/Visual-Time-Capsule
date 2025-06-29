package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.FriendshipDto;
import com.java.web.virtual.time.capsule.dto.UserCreateDto;
import com.java.web.virtual.time.capsule.dto.UserLoginDto;
import com.java.web.virtual.time.capsule.dto.UserProfileDto;
import com.java.web.virtual.time.capsule.dto.UserUpdateDto;
import com.java.web.virtual.time.capsule.enums.FriendshipStatus;
import com.java.web.virtual.time.capsule.exception.user.EmailAlreadyTakenException;
import com.java.web.virtual.time.capsule.exception.user.InvitationAlreadySent;
import com.java.web.virtual.time.capsule.exception.user.UserNotFoundException;
import com.java.web.virtual.time.capsule.exception.user.UsernameAlreadyTakenException;
import com.java.web.virtual.time.capsule.mapper.CapsuleMapper;
import com.java.web.virtual.time.capsule.mapper.FriendshipMapper;
import com.java.web.virtual.time.capsule.model.Friendship;
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.FriendshipRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final CapsuleRepository capsuleRepository;
    private final BCryptPasswordEncoder encoder;
    private final CapsuleMapper capsuleMapper;
    private final FriendshipMapper friendshipMapper;

    @Override
    public void registerUser(UserCreateDto user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyTakenException("Username is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException("Email is already taken");
        }

        UserModel newUser = UserModel.builder()
            .username(user.getUsername())
            .password(encoder.encode(user.getPassword()))
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .creationDate(LocalDate.now())
            .build();

        userRepository.save(newUser);
        userRepository.flush();
        log.info("Registered user: {}", userRepository.findByUsername(user.getUsername()));
    }

    @Override
    public void updateUser(Long id, UserUpdateDto updateDto) {
        if(!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        UserModel user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        user.setFirstName(updateDto.getFirstName());
        user.setLastName(updateDto.getLastName());
        user.setUsername(user.getUsername());
        user.setPassword(updateDto.getPassword());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserModel loginUser(UserLoginDto userLoginDto) {
        String hashedPassword = encoder.encode(userLoginDto.getPassword());
        UserModel user = userRepository.findByUsername(userLoginDto.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if(!encoder.matches(userLoginDto.getPassword(), hashedPassword)) {
            throw new BadCredentialsException("Wrong password");
        }
        return user;
    }

    @Override
    public void sendInvitation(Long sender, Long receiver) {
        UserModel userRequester = userRepository.findById(sender).orElseThrow(() -> new UserNotFoundException("User not found"));
        UserModel userReceiver = userRepository.findById(receiver).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!userRepository.existsById(receiver)) {
            throw new UserNotFoundException("User not found");
        }
        Friendship friendship = friendshipRepository.findByRequesterAndResponder(userRequester, userReceiver);

        if(friendship != null && friendship.getStatus() == FriendshipStatus.PENDING) {
            throw new InvitationAlreadySent(String.format("Invitation by %s to %s already sent", userRequester.getUsername(), userReceiver.getUsername()));
        }

        if (friendship != null && friendship.getStatus() == FriendshipStatus.ACCEPTED) {
            throw new RuntimeException(String.format("Invitation by %s to %s already accepted", userRequester.getUsername(), userReceiver.getUsername()));
        }

        if (friendship != null && friendship.getStatus() == FriendshipStatus.DECLINED) {
            throw new RuntimeException(String.format("Invitation by %s to %s already declined", userRequester.getUsername(), userReceiver.getUsername()));
        }

        friendship = Friendship
            .builder()
            .status(FriendshipStatus.PENDING)
            .requester(userRequester)
            .responder(userReceiver)
            .lastUpdate(LocalDate.now())
            .build();
        friendshipRepository.save(friendship);
    }

    @Override
    public void answerInvitation(Long id, @NotNull FriendshipDto friendshipDto) {
        Friendship friendship = friendshipRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        friendship.setStatus(friendshipDto.getStatus());
        friendshipRepository.save(friendship);
    }

    @Override
    public UserModel getUser(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Set<CapsuleResponseDto> getCapsules(Long id) {
        return capsuleRepository.findByCreator_Id(id).stream().map(capsuleMapper::toDto).collect(Collectors.toSet());
    }

    @Override
    public UserModel getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public List<FriendshipDto> getFriendships(Long id) {
        UserModel user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));

        return friendshipRepository
            .findAllByRequesterOrResponder(user,user)
            .stream()
            .map(friendshipMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileDto> getAllUserProfilesExceptCurrentUser(String username) {
        log.info("Fetching all user profiles except current user: {}", username);

        UserModel currentUser = userRepository.findByUsername(username);
        
        List<Friendship> currentUserFriendships = friendshipRepository.findAllByRequesterOrResponder(currentUser, currentUser);
        log.info("Found {} friendships for current user {}", currentUserFriendships.size(), username);

        return userRepository.findAll().stream()
            .filter(u -> !u.getUsername().equals(username)) 
            .map(otherUser -> convertToUserProfileDto(otherUser, currentUser, currentUserFriendships)) 
            .collect(Collectors.toList());
    }

    private UserProfileDto convertToUserProfileDto(UserModel otherUser, UserModel currentUser, List<Friendship> currentUserFriendships) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(otherUser.getId());
        dto.setUsername(otherUser.getUsername());
        dto.setIsRequestFromCurrentUser(false);
        dto.setIsRequestToCurrentUser(false);
        dto.setFriendshipStatus(null);
        dto.setAssociatedFriendshipId(null);
        
        Optional<Friendship> foundFriendship = currentUserFriendships.stream()
            .filter(f -> (f.getRequester().getId().equals(currentUser.getId()) && f.getResponder().getId().equals(otherUser.getId())) ||
                (f.getRequester().getId().equals(otherUser.getId()) && f.getResponder().getId().equals(currentUser.getId())))
            .findFirst();


        if(foundFriendship.isEmpty()) {
            return dto;
        }

        Friendship friendship = foundFriendship.get();
        dto.setFriendshipStatus(friendship.getStatus());
        dto.setAssociatedFriendshipId(friendship.getId());

        if(friendship.getStatus() != FriendshipStatus.PENDING) {
            return dto;
        }

        if (friendship.getRequester().getId().equals(currentUser.getId())) {
            dto.setIsRequestFromCurrentUser(true);
        } else if (friendship.getResponder().getId().equals(currentUser.getId())) {
            dto.setIsRequestToCurrentUser(true);
        }

        return dto;
    }
}
