import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { UserService } from '../services/user.service';
import { AuthService } from '../services/auth.service';
import { FriendshipDto } from '../dtos/friendship.dto';
import { FriendshipStatus } from '../enums/friendship-status.enum';
import {UserResponseDto} from '../dtos/user-response.dto';
import {UserProfileDto} from '../dtos/user-profile.dto';


interface Message {
  text: string;
  type: 'success' | 'error';
}

@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.css']
})
export class UsersListComponent implements OnInit {
  users: UserProfileDto[] = [];
  loadingUsers = true;
  messages: Message[] = [];
  currentUserId: number | null = null;

  constructor(
    private userService: UserService,
    protected router: Router,
    private authService: AuthService
  ) {
    console.log('UsersListComponent constructor called.');
  }

  ngOnInit(): void {
    console.log('UsersListComponent ngOnInit called.');
    this.authService.getCurrentUser().subscribe((user: UserResponseDto | null) => {
      if (user?.id) {
        this.currentUserId = user.id;
        this.loadUsers();
      } else {
        this.addMessage('User not authenticated. Please log in.', 'error');
        this.loadingUsers = false;
        this.router.navigate(['/login']);
      }
    });
  }

  addMessage(text: string, type: 'success' | 'error'): void {
    this.messages.push({ text, type });
    setTimeout(() => {
      this.messages = this.messages.filter(msg => msg.text !== text);
    }, 5000);
  }

  loadUsers(): void {
    if (this.currentUserId === null) return;

    this.loadingUsers = true;
    this.messages = [];
    this.addMessage('Loading users...', 'success');

    console.log('UsersListComponent: Calling userService.getAllUsers() to get basic user list.');
    this.userService.getAllUsers().subscribe({
      next: (usersData: UserProfileDto[]) => {
        this.users = usersData.filter(user => user.id !== this.currentUserId);
        this.checkAndSetFriendshipStatuses();
        this.addMessage('Users loaded successfully!', 'success');
      },
      error: (error: HttpErrorResponse) => {
        console.error('UsersListComponent: Error loading users:', error);
        this.addMessage(`Failed to load users: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => {
        console.log('UsersListComponent: Initial user loading complete.');
      }
    });
  }

  checkAndSetFriendshipStatuses(): void {
    if (this.currentUserId === null || this.users.length === 0) {
      this.loadingUsers = false;
      return;
    }
    this.userService.getFriendships(this.currentUserId).subscribe({
      next: (friendships: FriendshipDto[]) => {
        this.users = this.users.map(user => {
          const existingFriendship = friendships.find(f =>
            (f.requesterId === this.currentUserId && f.responderId === user.id) ||
            (f.requesterId === user.id && f.responderId === this.currentUserId)
          );

          if (existingFriendship) {
            user.friendshipStatus = existingFriendship.status;
            user.associatedFriendshipId = existingFriendship.id;

            if (existingFriendship.status === FriendshipStatus.PENDING) {
              user.isRequestFromCurrentUser = (existingFriendship.requesterId === this.currentUserId);
              user.isRequestToCurrentUser = (existingFriendship.responderId === this.currentUserId);
            } else {
              user.isRequestFromCurrentUser = false;
              user.isRequestToCurrentUser = false;
            }
          } else {

            user.friendshipStatus = undefined;
            user.associatedFriendshipId = undefined;
            user.isRequestFromCurrentUser = false;
            user.isRequestToCurrentUser = false;
          }
          return user;
        });
        console.log('UsersListComponent: Users after friendship status processing (final state for UI):', this.users);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error loading friendships for status check:', error);
        this.addMessage(`Failed to load friendship statuses: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => {
        console.log('UsersListComponent: Friendship status processing complete.');
        this.loadingUsers = false;
      }
    });
  }

  sendFriendRequest(responderId: number): void {
    if (this.currentUserId === null) {
      this.addMessage('Authentication error: Cannot send request.', 'error');
      return;
    }
    this.addMessage('Sending friend request...', 'success');
    this.userService.sendFriendRequest(responderId).subscribe({
      next: (response) => {
        this.addMessage(`Friend request sent to user ID ${responderId}!`, 'success');
        this.loadUsers();
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error sending friend request:', error);
        if (typeof error.error === 'string') {
          this.addMessage(`Failed to send request: ${error.error}`, 'error');
        } else if (error.status === 409) {
          this.addMessage('Friend request already sent or users are already friends.', 'error');
        } else {
          this.addMessage(`Failed to send request: ${error.error?.message || 'Unknown error'}`, 'error');
        }
      }
    });
  }

  acceptFriendRequest(friendshipId: number): void {
    if (this.currentUserId === null) {
      this.addMessage('Authentication error: Cannot accept request.', 'error');
      return;
    }
    this.addMessage('Accepting friend request...', 'success');

    const friendshipUpdateDto: FriendshipDto = {
      id: friendshipId,
      requesterId: 0,
      responderId: 0,
      requesterUsername: '',
      responderUsername: '',
      status: FriendshipStatus.ACCEPTED,
      lastUpdate: new Date().toISOString()
    };

    this.userService.answerFriendshipInvitation(friendshipId, friendshipUpdateDto).subscribe({
      next: (response) => {
        this.addMessage(`Friend request accepted!`, 'success');
        this.loadUsers();
      },
      error: (error: HttpErrorResponse) => {
        if (typeof error.error === 'string') {
          this.addMessage(`Failed to accept request: ${error.error}`, 'error');
        } else {
          this.addMessage(`Failed to accept request: ${error.error?.message || 'Unknown error'}`, 'error');
        }
      }
    });
  }

  viewUserCapsules(userId: number): void {
    console.log('UsersListComponent: Navigating to user capsules for ID:', userId);
    this.router.navigate(['/users', userId, 'capsules']);
  }

  FriendshipStatus = FriendshipStatus;
}
