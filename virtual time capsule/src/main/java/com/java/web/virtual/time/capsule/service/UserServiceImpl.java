package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dtos.FriendshipDto;
import com.java.web.virtual.time.capsule.dtos.UserCreateDto;
import com.java.web.virtual.time.capsule.dtos.UserLoginDto;
import com.java.web.virtual.time.capsule.dtos.UserResponseDto;
import com.java.web.virtual.time.capsule.enums.FriendShipStatus;
import com.java.web.virtual.time.capsule.exception.EmailAlreadyTakenException;
import com.java.web.virtual.time.capsule.exception.InvitationAlreadySent;
import com.java.web.virtual.time.capsule.exception.UserNotFoundException;
import com.java.web.virtual.time.capsule.exception.UsernameAlreadyTakenException;
import com.java.web.virtual.time.capsule.model.CapsuleUser;
import com.java.web.virtual.time.capsule.model.Friendship;
import com.java.web.virtual.time.capsule.repository.FriendshipRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
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

        CapsuleUser newUser = CapsuleUser.builder()
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
    public void updateUser(Long id) {
        if(!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found");
        }
        CapsuleUser user = userRepository.findById(id).get();
        user.setFirstName(user.getFirstName());
        user.setLastName(user.getLastName());
        user.setEmail(user.getEmail());
        user.setPassword(user.getPassword());
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
        CapsuleUser user = userRepository.findByUsername(userLoginDto.getUsername());
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
    public void sendInvitation(String sender, Long receiver) {
        CapsuleUser userRequester = userRepository.findByUsername(sender);
        CapsuleUser userReceiver = userRepository.findById(receiver).get();
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
    public void answerInvitation(String sender, FriendshipDto friendshipDto) {
        CapsuleUser userRequester = userRepository.findByUsername(sender);
        CapsuleUser userReceiver = userRepository.findById(friendshipDto.getReceiverId()).get();
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
