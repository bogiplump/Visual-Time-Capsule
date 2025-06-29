package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.FriendshipDto;
import com.java.web.virtual.time.capsule.dto.UserCreateDto;
import com.java.web.virtual.time.capsule.dto.UserLoginDto;
import com.java.web.virtual.time.capsule.dto.UserProfileDto;
import com.java.web.virtual.time.capsule.dto.UserResponseDto;
import com.java.web.virtual.time.capsule.dto.UserUpdateDto;
import com.java.web.virtual.time.capsule.enums.FriendshipStatus;
import com.java.web.virtual.time.capsule.exception.user.EmailAlreadyTakenException;
import com.java.web.virtual.time.capsule.exception.user.InvitationAlreadySent;
import com.java.web.virtual.time.capsule.exception.user.UserNotFoundException;
import com.java.web.virtual.time.capsule.exception.user.UsernameAlreadyTakenException;
import com.java.web.virtual.time.capsule.mapper.CapsuleMapper;
import com.java.web.virtual.time.capsule.mapper.FriendshipMapper;
import com.java.web.virtual.time.capsule.mapper.UserMapper;
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
    private final UserMapper userMapper;
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
        UserModel user = userRepository.findById(id).get();
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
    public List<UserResponseDto> getUsers() {
        return userRepository.findAll()
            .stream().map(userMapper::toUserResponseDto).toList();
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
        log.info("Friendship sent: {}", friendship.getStatus());
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

        // 1. Get the current authenticated user's model
        UserModel currentUser = userRepository.findByUsername(username);

        // 2. Fetch all friendships where the current user is either requester or responder.
        // You'll need a method in your FriendshipRepository for this, e.g.:
        // List<Friendship> findByRequesterIdOrResponderId(Long requesterId, Long responderId);
        List<Friendship> currentUserFriendships = friendshipRepository.findAllByRequesterOrResponder(currentUser, currentUser);
        log.info("Found {} friendships for current user {}", currentUserFriendships.size(), username);

        // 3. Stream all users, filter out the current user, and map them to UserProfileDto
        // The convertToUserProfileDto method now receives the necessary context (current user and their friendships)
        return userRepository.findAll().stream()
            .filter(u -> !u.getUsername().equals(username)) // Exclude the current user
            .map(otherUser -> convertToUserProfileDto(otherUser, currentUser, currentUserFriendships)) // Pass context for enrichment
            .collect(Collectors.toList());
    }

    /**
     * Converts a UserModel to UserProfileDto, populating friendship status fields
     * based on the context of the current authenticated user.
     *
     * @param otherUser The UserModel representing the "other" user whose profile is being created.
     * @param currentUser The UserModel of the currently authenticated user.
     * @param currentUserFriendships A list of all Friendship entities involving the currentUser.
     * @return A UserProfileDto with populated friendship-related fields.
     */
    private UserProfileDto convertToUserProfileDto(UserModel otherUser, UserModel currentUser, List<Friendship> currentUserFriendships) {
        log.info("Converting UserModel to UserProfileDto: {}", otherUser.getUsername());
        UserProfileDto dto = new UserProfileDto();
        dto.setId(otherUser.getId());
        dto.setUsername(otherUser.getUsername());

        // Initialize friendship-related flags to default false/null
        dto.setIsRequestFromCurrentUser(false);
        dto.setIsRequestToCurrentUser(false);
        dto.setFriendshipStatus(null);
        dto.setAssociatedFriendshipId(null);

        // Find the specific friendship (if any) between otherUser and currentUser
        Optional<Friendship> foundFriendship = currentUserFriendships.stream()
            .filter(f -> (f.getRequester().getId().equals(currentUser.getId()) && f.getResponder().getId().equals(otherUser.getId())) ||
                (f.getRequester().getId().equals(otherUser.getId()) && f.getResponder().getId().equals(currentUser.getId())))
            .findFirst();


        if (foundFriendship.isPresent()) {

            Friendship friendship = foundFriendship.get();
            log.info("Found friendship: {}", friendship.getStatus());
            log.info("Found friendship: {}", friendship.getId());
            dto.setFriendshipStatus(friendship.getStatus());
            dto.setAssociatedFriendshipId(friendship.getId());

            if (friendship.getStatus() == FriendshipStatus.PENDING) {
                if (friendship.getRequester().getId().equals(currentUser.getId())) {
                    // Current user sent the request to 'otherUser'
                    dto.setIsRequestFromCurrentUser(true);
                } else if (friendship.getResponder().getId().equals(currentUser.getId())) {
                    // 'otherUser' sent the request to current user
                    dto.setIsRequestToCurrentUser(true);
                }
            }
            // For ACCEPTED or DECLINED statuses, isRequestFromCurrentUser/isRequestToCurrentUser remain false
            // as they specifically relate to the PENDING state.
        }

        return dto;
    }
}
