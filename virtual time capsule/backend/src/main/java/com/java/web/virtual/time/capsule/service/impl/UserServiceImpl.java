package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.FriendshipDto;
import com.java.web.virtual.time.capsule.dto.UserCreateDto;
import com.java.web.virtual.time.capsule.dto.UserLoginDto;
import com.java.web.virtual.time.capsule.dto.UserResponseDto;
import com.java.web.virtual.time.capsule.dto.UserUpdateDto;
import com.java.web.virtual.time.capsule.enums.FriendShipStatus;
import com.java.web.virtual.time.capsule.exception.user.EmailAlreadyTakenException;
import com.java.web.virtual.time.capsule.exception.user.InvitationAlreadySent;
import com.java.web.virtual.time.capsule.exception.user.UserNotFoundException;
import com.java.web.virtual.time.capsule.exception.user.UsernameAlreadyTakenException;
import com.java.web.virtual.time.capsule.model.Friendship;
import com.java.web.virtual.time.capsule.model.User;
import com.java.web.virtual.time.capsule.repository.FriendshipRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public void registerUser(UserCreateDto user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyTakenException("Username is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyTakenException("Email is already taken");
        }

        User newUser = User.builder()
            .username(user.getUsername())
            .password(encoder.encode(user.getPassword()))
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .creationDate(LocalDate.now())
            .build();

        userRepository.save(newUser);
    }

    @Override
    public void updateUser(Long id, UserUpdateDto updateDto) {
        if(!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        User user = userRepository.findById(id).get();
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
    public void loginUser(UserLoginDto userLoginDto) {
        String hashedPassword = encoder.encode(userLoginDto.getPassword());
        User user = userRepository.findByUsername(userLoginDto.getUsername());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if(!encoder.matches(userLoginDto.getPassword(), hashedPassword)) {
            throw new BadCredentialsException("Wrong password");
        }
    }

    @Override
    public List<UserResponseDto> getUsers() {
        return userRepository.findAll()
            .stream().map(UserResponseDto::fromUser).toList();
    }

    @Override
    public void sendInvitation(Long sender, Long receiver) {
        User userRequester = userRepository.findById(sender).orElseThrow(() -> new UserNotFoundException("User not found"));
        User userReceiver = userRepository.findById(receiver).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(!userRepository.existsById(receiver)) {
            throw new UserNotFoundException("User not found");
        }
        Friendship friendship = friendshipRepository.findByRequesterAndResponder(userRequester, userReceiver);
        if(friendship != null && friendship.getStatus() == FriendShipStatus.PENDING) {
            throw new InvitationAlreadySent(String.format("Invitation by %s to %s already sent", userRequester.getUsername(), userReceiver.getUsername()));
        }

        if (friendship != null && friendship.getStatus() == FriendShipStatus.ACCEPTED) {
            throw new RuntimeException(String.format("Invitation by %s to %s already accepted", userRequester.getUsername(), userReceiver.getUsername()));
        }

        if (friendship != null && friendship.getStatus() == FriendShipStatus.DECLINED) {
            throw new RuntimeException(String.format("Invitation by %s to %s already declined", userRequester.getUsername(), userReceiver.getUsername()));
        }

        friendship = Friendship
            .builder()
            .status(FriendShipStatus.PENDING)
            .requester(userRequester)
            .responder(userReceiver)
            .lastUpdate(LocalDate.now())
            .build();
        friendshipRepository.save(friendship);
    }

    @Override
    public void answerInvitation(Long sender, FriendshipDto friendshipDto) {
        User userRequester = userRepository.findById(sender).orElseThrow(() -> new UserNotFoundException("User not found"));
        User userReceiver = userRepository.findById(friendshipDto.getReceiverId()).orElseThrow(() -> new UserNotFoundException("User not found"));
        if(userRequester.equals(userReceiver)) {
            throw new RuntimeException("You cannot send invitation to yourself");
        }
        if(!userRepository.existsById(friendshipDto.getReceiverId())) {
            throw new UserNotFoundException("User not found");
        }

        Friendship friendship = friendshipRepository.findByRequesterAndResponder(userRequester, userReceiver);
        friendship.setStatus(friendshipDto.getStatus());
        friendshipRepository.save(friendship);
    }
}
