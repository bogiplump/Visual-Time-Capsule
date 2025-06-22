import { Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { CommonModule, DatePipe } from '@angular/common'; // Import DatePipe for date formatting
import { RouterModule } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms'; // Import FormsModule for ngModel

// Import your models and DTOs
import { Capsule } from '../models/capsule.model';
import { Memory } from '../models/memory.model'; // Even if not displayed, you might fetch via capsules
import { Goal } from '../models/goal.model';
import { Friendship } from '../models/friendship.model';
import { CapsuleService } from '../services/capsule.service';
import { GoalService } from '../services/goal.service';
import { MemoryService } from '../services/memory.service';
import { UserService } from '../services/user.service';
import { CapsuleCreateDto } from '../dtos/capsule-create.dto';
import { GoalDto } from '../dtos/goal.dto';
import {UserResponseDto} from '../dtos/user-response.dto'; // Ensure GoalDto is imported

interface Message {
  text: string;
  type: 'success' | 'error';
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, DatePipe], // Add FormsModule and DatePipe
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  messages: Message[] = [];
  loadingRefresh = false;

  myCapsules: Capsule[] = [];
  myMemories: Memory[] = []; // Placeholder, if you plan to retrieve them through capsules or other means
  myGoals: Goal[] = []; // Placeholder, if you plan to retrieve them directly
  myFriendships: Friendship[] = [];

  loadingCapsules = false;
  loadingMemories = false; // Add this and other loading flags as needed
  loadingGoals = false;
  loadingFriendships = false;

  // Dialog specific properties
  showCreateCapsuleDialog = false;
  loadingCreateCapsule = false;
  newCapsuleData: CapsuleCreateDto = {
    capsuleName: '',
    goal: {
      isVisible: true,
      content: '',
    }
  };
  dialogMessages: Message[] = []; // Messages specific to the dialog

  constructor(
    private authService: AuthService,
    private capsuleService: CapsuleService,
    private goalService: GoalService, // Inject GoalService if you fetch goals directly later
    private memoryService: MemoryService, // Inject MemoryService if you interact with memories later
    private userService: UserService // Inject UserService for fetching goals and friendships
  ) { }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  /**
   * Helper method to add a message to the main dashboard messages array.
   */
  addMessage(text: string, type: 'success' | 'error'): void {
    this.messages.push({ text, type });
    setTimeout(() => {
      this.messages = this.messages.filter(msg => msg.text !== text);
    }, 5000); // Messages disappear after 5 seconds
  }

  /**
   * Helper method to add a message to the dialog's messages array.
   */
  addDialogMessage(text: string, type: 'success' | 'error'): void {
    this.dialogMessages.push({ text, type });
    setTimeout(() => {
      this.dialogMessages = this.dialogMessages.filter(msg => msg.text !== text);
    }, 5000); // Messages disappear after 5 seconds
  }

  /**
   * Loads all dashboard-related data by making actual API calls.
   */
  private loadDashboardData(): void {
    this.addMessage('Loading dashboard data...', 'success');

    // Fetch Capsules
    this.loadingCapsules = true;
    this.capsuleService.getAllCapsules().subscribe({
      next: (capsules) => {
        this.myCapsules = capsules;
        this.addMessage('Capsules loaded successfully!', 'success');
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to load capsules: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => {
        this.loadingCapsules = false;
      }
    });

    // Fetch Friendships
    this.loadingFriendships = true;
    const currentUserId = this.authService.getCurrentUserId();
    if (currentUserId) {
      // Assuming a method like getFriendshipsForUser exists in UserService
      // or you add a dedicated FriendshipService.
      // For now, if no specific endpoint, this will just show 'No friends found'.
      // You would implement this based on your backend API structure (e.g., /users/{id}/friendships)
      // Example placeholder:
      this.userService.getUserProfile(currentUserId).subscribe({ // Assuming user profile might contain friends
        next: (userProfile) => {
          this.myFriendships = userProfile.friendships || [];
          this.addMessage('Friendships loaded successfully!', 'success');
        },
        error: (error: HttpErrorResponse) => {
          this.addMessage(`Failed to load friendships: ${error.message || 'Unknown error'}`, 'error');
        },
        complete: () => {
          this.loadingFriendships = false;
        }
      });
      // If you have a direct endpoint for user's friendships, use that instead:
      // this.userService.getAllFriendshipsForUser(currentUserId).subscribe({...});
    } else {
      this.addMessage('Current user ID '+ currentUserId +' not available to load friendships.', 'error');
      this.loadingFriendships = false;
    }
  }

  /**
   * Handles the refresh token action.
   */
  handleRefreshToken(): void {
    this.loadingRefresh = true;
    this.messages = []; // Clear previous messages

    this.authService.refreshToken().subscribe({
      next: () => {
        this.addMessage('Access token refreshed successfully!', 'success');
        this.loadDashboardData(); // Reload data after token refresh
      },
      error: (err: HttpErrorResponse) => {
        const errorMessage = err.message || 'Failed to refresh token. Please log in again.';
        this.addMessage(errorMessage, 'error');
      },
      complete: () => {
        this.loadingRefresh = false;
      }
    });
  }

  /**
   * Handles the logout action.
   */
  logout(): void {
    this.authService.logout();
  }

  // --- Dialog Methods ---

  /**
   * Opens the create capsule dialog.
   */
  openCreateCapsuleDialog(): void {
    this.showCreateCapsuleDialog = true;
    this.dialogMessages = []; // Clear previous dialog messages
    // Reset form data for a new entry
    this.newCapsuleData = {
      capsuleName: '',
      goal: {
        isVisible: true,
        content: ''
      }
    };
  }

  /**
   * Closes the create capsule dialog.
   */
  closeCreateCapsuleDialog(): void {
    this.showCreateCapsuleDialog = false;
  }

  /**
   * Submits the new capsule creation form.
   */
  submitCreateCapsule(): void {
    this.loadingCreateCapsule = true;
    this.dialogMessages = []; // Clear previous dialog messages

    this.capsuleService.createCapsule(this.newCapsuleData).subscribe({
      next: (response) => {
        this.addDialogMessage('Capsule created successfully!', 'success');
        this.closeCreateCapsuleDialog();
        this.loadDashboardData(); // Refresh dashboard data to show the new capsule
      },
      error: (error: HttpErrorResponse) => {
        let errorMessage = 'Failed to create capsule. Please try again.';
        if (error.error && Array.isArray(error.error) && error.error.length > 0) {
          // Assuming the backend returns an array of strings for validation errors
          errorMessage = error.error.map((msg: string) => {
            const match = msg.match(/^[^:]+:\s*"(.*)"$/); // Remove field prefix and quotes
            return match && match[1] ? match[1] : msg;
          }).join('\n'); // Join with newlines
        } else if (error.error && typeof error.error.message === 'string') {
          errorMessage = error.error.message;
        } else if (typeof error.error === 'string') {
          errorMessage = error.error;
        }
        this.addDialogMessage(errorMessage, 'error');
      },
      complete: () => {
        this.loadingCreateCapsule = false;
      }
    });
  }
}
