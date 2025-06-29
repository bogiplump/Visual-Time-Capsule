import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import {Router, RouterLink} from '@angular/router';
import { Subscription, interval } from 'rxjs';

// Services
import { CapsuleService } from '../services/capsule.service';
import { GoalService } from '../services/goal.service';
import { MemoryService } from '../services/memory.service';
import { AuthService } from '../services/auth.service';
import {UserService} from '../services/user.service';

// DTOs & Enums
import { CapsuleResponseDto } from '../dtos/capsule-response.dto';
import { CapsuleCreateDto } from '../dtos/capsule-create.dto';
import { MemoryCreateDto } from '../dtos/memory-create.dto';
import { CapsuleStatus } from '../enums/capsule-status.enum';
import { MemoryType } from '../enums/memory-type.enum';
import {FriendshipDto} from '../dtos/friendship.dto';
import {FriendshipStatus} from '../enums/friendship-status.enum';

interface Message {
  text: string;
  type: 'success' | 'error';
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  myCapsules: CapsuleResponseDto[] = [];
  loadingCapsules = true;
  messages: Message[] = [];

  // Friendships related properties
  myFriendships: FriendshipDto[] = [];
  loadingFriendships = true;
  currentUserId: number | null = null;

  // Modals and Forms
  showCreateCapsuleDialog = false;
  currentModalTab: 'capsule' | 'memory' = 'capsule';
  createdCapsuleId: number | null = null;

  newCapsuleData: CapsuleCreateDto = {
    capsuleName: '',
    openDateTime: '',
    goal: {
      id:  null,
      isVisible: false,
      isAchieved: false,
      creationDate: '',
      content: '',
      creator: null,
      capsuleId: null,
    }
  };
  loadingCreateCapsule = false;
  dialogMessages: Message[] = [];

  newMemoryData: MemoryCreateDto = {
    description: '',
    type: MemoryType.TEXT,
    content: null as any
  };
  selectedFile: File | null = null;
  memoryTypes = Object.values(MemoryType);
  loadingCreateMemory = false;

  // Date and Time related for canOpenCapsule in dashboard
  currentDate: Date = new Date();
  private dateUpdateSubscription: Subscription | undefined;
  capsuleStatus: typeof CapsuleStatus = CapsuleStatus;
  friendshipStatus: typeof FriendshipStatus = FriendshipStatus;

  constructor(
    private capsuleService: CapsuleService,
    private authService: AuthService,
    private goalService: GoalService,
    private memoryService: MemoryService,
    private userService: UserService,
    protected router: Router
  ) { }

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(user => {
      if (user?.id) {
        this.currentUserId = user.id;
        this.loadMyCapsules();
        this.loadMyFriendships();
      } else {
        this.addMessage('Authentication error. Please log in.', 'error');
        this.router.navigate(['/login']);
      }
    });

    this.dateUpdateSubscription = interval(60 * 1000).subscribe(() => {
      this.currentDate = new Date();
    });
  }

  ngOnDestroy(): void {
    if (this.dateUpdateSubscription) {
      this.dateUpdateSubscription.unsubscribe();
    }
  }

  addMessage(text: string, type: 'success' | 'error'): void {
    this.messages.push({ text, type });
    setTimeout(() => {
      this.messages = this.messages.filter(msg => msg.text !== text);
    }, 5000);
  }

  addDialogMessage(text: string, type: 'success' | 'error'): void {
    this.dialogMessages.push({ text, type });
    setTimeout(() => {
      this.dialogMessages = this.dialogMessages.filter(msg => msg.text !== text);
    }, 5000);
  }

  loadMyCapsules(): void {
    this.loadingCapsules = true;
    this.messages = [];
    this.addMessage('Loading your capsules...', 'success');

    this.capsuleService.getAllMyCapsules().subscribe({
      next: (capsulesData: CapsuleResponseDto[]) => {
        this.myCapsules = capsulesData;
        console.log(capsulesData);
        this.addMessage('Capsules loaded successfully!', 'success');
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to load capsules: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => {
        this.loadingCapsules = false;
      }
    });
  }

  loadMyFriendships(): void {
    if (this.currentUserId === null) {
      this.addMessage('User not authenticated, cannot load friendships.', 'error');
      this.loadingFriendships = false;
      return;
    }

    this.loadingFriendships = true;
    this.userService.getFriendships(this.currentUserId).subscribe({
      next: (friendships: FriendshipDto[]) => {
        this.myFriendships = friendships;
        console.log('My Friendships:', this.myFriendships);
      },
      error: (error: HttpErrorResponse) => {
        console.error('Failed to load friendships:', error);
        this.addMessage(`Failed to load friendships: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => {
        this.loadingFriendships = false;
      }
    });
  }

  // NEW: Accept Friend Request
  acceptFriendRequest(friendshipId: number): void {
    if (this.currentUserId === null) {
      this.addMessage('Authentication error: Cannot accept request.', 'error');
      return;
    }
    this.addMessage('Accepting friend request...', 'success');

    // Find the specific friendship from the list
    const friendshipToUpdate = this.myFriendships.find(f => f.id === friendshipId);

    if (!friendshipToUpdate) {
      this.addMessage('Friendship not found for acceptance.', 'error');
      return;
    }

    const friendshipUpdateDto: FriendshipDto = {
      id: friendshipId,
      requesterId: friendshipToUpdate.responderId, // Use the original requesterId
      responderId: friendshipToUpdate.requesterId, // Use the original responderId (should be currentUserId)
      requesterUsername: friendshipToUpdate.responderUsername, // Pass original username
      responderUsername: friendshipToUpdate.requesterUsername, // Pass original username
      status: FriendshipStatus.ACCEPTED, // Set status to ACCEPTED
      lastUpdate: new Date().toISOString()
    };

    this.userService.answerFriendshipInvitation(friendshipId, friendshipUpdateDto).subscribe({
      next: (response) => {
        this.addMessage(`Friend request accepted!`, 'success');
        this.loadMyFriendships(); // Reload friendships to update status
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to accept request: ${error.error?.message || 'Unknown error'}`, 'error');
      }
    });
  }

  // NEW: Decline Friend Request
  declineFriendRequest(friendshipId: number): void {
    if (this.currentUserId === null) {
      this.addMessage('Authentication error: Cannot decline request.', 'error');
      return;
    }
    this.addMessage('Declining friend request...', 'success');

    // Find the specific friendship from the list
    const friendshipToUpdate = this.myFriendships.find(f => f.id === friendshipId);

    if (!friendshipToUpdate) {
      this.addMessage('Friendship not found for decline.', 'error');
      return;
    }

    const friendshipUpdateDto: FriendshipDto = {
      id: friendshipId,
      requesterId: friendshipToUpdate.responderId, // Use the original requesterId
      responderId: friendshipToUpdate.requesterId, // Use the original responderId (should be currentUserId)
      requesterUsername: friendshipToUpdate.responderUsername, // Pass original username
      responderUsername: friendshipToUpdate.requesterUsername, // Pass original username
      status: FriendshipStatus.DECLINED, // Set status to DECLINED
      lastUpdate: new Date().toISOString()
    };

    this.userService.answerFriendshipInvitation(friendshipId, friendshipUpdateDto).subscribe({
      next: (response) => {
        this.addMessage(`Friend request declined!`, 'success');
        this.loadMyFriendships(); // Reload friendships to update status
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to decline request: ${error.error?.message || 'Unknown error'}`, 'error');
      }
    });
  }

  // --- Capsule Creation Modal ---
  openCreateCapsuleDialog(): void {
    this.showCreateCapsuleDialog = true;
    this.currentModalTab = 'capsule'; // Always start on capsule details tab
    this.createdCapsuleId = null; // Reset for new creation
    this.newCapsuleData = { // Reset form
      capsuleName: '',
      openDateTime: '', // Initialize as null or empty string
      goal: {
        id:  null,
        content: '',
        isVisible: false,
        isAchieved: false,
        creationDate: '',
        creator: null,
        capsuleId: null,
      }
    };
    this.newMemoryData = { // Reset memory form
      description: '',
      type: MemoryType.TEXT,
      content: null as any
    };
    this.selectedFile = null;
    this.dialogMessages = [];
  }

  closeCreateCapsuleDialog(): void {
    this.showCreateCapsuleDialog = false;
    this.loadMyCapsules(); // Refresh capsule list after creating/cancelling
  }

  switchToCapsuleTab(): void {
    this.currentModalTab = 'capsule';
    this.dialogMessages = []; // Clear messages when switching tabs
  }

  switchToMemoryTab(): void {
    this.currentModalTab = 'memory';
    this.dialogMessages = []; // Clear messages when switching tabs
  }

  submitCreateCapsule(): void {
    if (!this.newCapsuleData.capsuleName || !this.newCapsuleData.goal.content) {
      this.addDialogMessage('Capsule Name and Goal Content are required.', 'error');
      return;
    }

    this.loadingCreateCapsule = true;
    this.dialogMessages = [];

    const capsuleToCreate: CapsuleCreateDto = {
      capsuleName: this.newCapsuleData.capsuleName,
      openDateTime: this.newCapsuleData.openDateTime,
      goal: {
        id: this.newCapsuleData.goal.id,
        content: this.newCapsuleData.goal.content,
        isVisible: this.newCapsuleData.goal.isVisible,
        isAchieved: this.newCapsuleData.goal.isAchieved,
        creationDate: this.newCapsuleData.goal.creationDate,
        creator: this.newCapsuleData.goal.creator,
        capsuleId: this.newCapsuleData.goal.capsuleId,
      }
    };

    console.log("CapsuleCreateDto being sent:", capsuleToCreate);

    this.capsuleService.createCapsule(capsuleToCreate).subscribe({
      next: (response: CapsuleResponseDto) => {
        this.addDialogMessage('Capsule created successfully! You can now add a memory.', 'success');
        this.createdCapsuleId = response.id;
        this.switchToMemoryTab();
      },
      error: (error: HttpErrorResponse) => {
        this.addDialogMessage(`Failed to create capsule: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => {
        this.loadingCreateCapsule = false;
      }
    });
  }

  // --- Memory Addition to New Capsule ---
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.newMemoryData.content = this.selectedFile;
    } else {
      this.selectedFile = null;
      this.newMemoryData.content = null as any;
    }
  }

  submitCreateMemory(): void {
    if (!this.createdCapsuleId) {
      this.addDialogMessage('No capsule selected to add memory. Please create a capsule first.', 'error');
      return;
    }
    if (!this.selectedFile) {
      this.addDialogMessage('Please select a file for the memory content.', 'error');
      return;
    }
    if (!this.newMemoryData.description) {
      this.addDialogMessage('Please provide a description for the memory.', 'error');
      return;
    }
    if (!this.newMemoryData.type) {
      this.addDialogMessage('Please select a type for the memory.', 'error');
      return;
    }

    this.loadingCreateMemory = true;
    this.dialogMessages = [];

    this.memoryService.addNewMemoryToCapsule(this.createdCapsuleId, this.newMemoryData).subscribe({
      next: () => {
        this.addDialogMessage('Memory added successfully!', 'success');
        this.newMemoryData = { description: '', type: MemoryType.TEXT, content: null as any };
        this.selectedFile = null;
      },
      error: (error: HttpErrorResponse) => {
        let errorMessage = 'Failed to add memory. Please try again.';
        if (error.error instanceof Blob) {
          error.error.text().then(text => {
            try {
              const errorObj = JSON.parse(text);
              if (errorObj.message) errorMessage = errorObj.message;
              else if (errorObj.error) errorMessage = errorObj.error;
            } catch {
              errorMessage = text || errorMessage;
            }
            this.addDialogMessage(errorMessage, 'error');
          });
        } else if (error.error && Array.isArray(error.error) && error.error.length > 0) {
          errorMessage = error.error.map((msg: string) => {
            const match = msg.match(/^[^:]+:\s*"(.*)"$/);
            return match && match[1] ? match[1] : msg;
          }).join('\n');
          this.addDialogMessage(errorMessage, 'error');
        } else if (error.error && typeof error.error.message === 'string') {
          errorMessage = error.error.message;
          this.addDialogMessage(errorMessage, 'error');
        } else if (typeof error.error === 'string') {
          errorMessage = error.error;
          this.addDialogMessage(errorMessage, 'error');
        } else {
          this.addDialogMessage(errorMessage, 'error');
        }
      },
      complete: () => {
        this.loadingCreateMemory = false;
      }
    });
  }

  // --- Utility for Capsule Card display ---
  canOpenCapsule(capsule: CapsuleResponseDto): boolean {
    if (!capsule || capsule.status !== CapsuleStatus.CLOSED || !capsule.openDateTime) {
      return false;
    }
    const capsuleOpenDate = new Date(capsule.openDateTime);
    return this.currentDate >= capsuleOpenDate;
  }

  protected readonly FriendshipStatus = FriendshipStatus;
}
