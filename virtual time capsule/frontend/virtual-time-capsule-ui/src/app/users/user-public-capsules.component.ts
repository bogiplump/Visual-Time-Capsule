import {Component, OnInit} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';

// Services
import {CapsuleService} from '../services/capsule.service'; // Re-use existing CapsuleService
// DTOs
import {CapsuleResponseDto} from '../dtos/capsule-response.dto';
import {CapsuleStatus} from '../enums/capsule-status.enum'; // Use the correct enum import
import {UserService} from '../services/user.service'; // Re-use existing DTO

interface Message {
  text: string;
  type: 'success' | 'error';
}

@Component({
  selector: 'app-user-public-capsules',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe],
  templateUrl: './user-public-capsules.component.html',
  styleUrls: ['./user-public-capsules.component.css']
})
export class UserPublicCapsulesComponent implements OnInit {
  userId: number | null = null;
  username: string | null = null; // To display whose capsules are being viewed
  publicCapsules: CapsuleResponseDto[] = [];
  loadingCapsules = true;
  messages: Message[] = [];

  constructor(
    private route: ActivatedRoute,
    protected router: Router,
    private capsuleService: CapsuleService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('userId');
      // Attempt to get username from route param, or fetch it if needed (though backend provides it in CapsuleResponseDto)
      const name = params.get('username');

      if (id) {
        this.userId = +id;
        this.username = name; // Set username if available from route
        this.loadUserAndCapsules(this.userId); // Consolidated loading
      } else {
        this.addMessage('User ID not found in route. Redirecting to users list.', 'error');
        this.router.navigate(['/users']);
      }
    });
  }

  addMessage(text: string, type: 'success' | 'error'): void {
    this.messages.push({ text, type });
    setTimeout(() => {
      this.messages = this.messages.filter(msg => msg.text !== text);
    }, 5000);
  }

  loadUserAndCapsules(userId: number): void {
    this.loadingCapsules = true;
    this.messages = [];
    this.addMessage(`Loading capsules for user ID: ${userId}...`, 'success');

    // First, try to get the username if not already in the route (optional, but good for display)
    // You might have a userService.getUserProfile(userId) to get this
    // For now, assume username might come from route or later from capsule data if it includes creator info.

    // Request all capsules for the target user.
    // The backend's getPublicCapsulesByUserId should already filter for public goals.
    // Ensure your backend's getPublicCapsulesByUserId returns *all* capsules
    // for that user, and then the frontend conditionally displays goals/memories.
    // OR, if the backend truly only sends "public" capsules, then the naming of
    // this.publicCapsules implies that. Let's stick with the current backend filtering
    // in getPublicCapsulesByUserId.
    this.userService.getPublicCapsulesByUserId(userId).subscribe({
      next: (capsulesData: CapsuleResponseDto[]) => {
        this.publicCapsules = capsulesData;
        console.log(capsulesData);
        // If username wasn't in route, try to get it from the first capsule
        if (!this.username && capsulesData.length > 0 && capsulesData[0].creator.id) {
          // You might need a way to map creator ID to username if not directly in DTO
          // For now, if your CapsuleResponseDto has creator username, use it.
          // Assuming CapsuleResponseDto has a `creatorUsername` or similar.
          // If not, you'd need another UserService call to get the username by ID.
        }
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

  viewCapsuleDetails(capsuleId: number): void {
    // Navigate to the capsule details page.
    // The capsule-details component already has logic to conditionally hide edit buttons.
    this.router.navigate(['/capsules', capsuleId]);
  }

  // Helper method to determine if a goal should be displayed on this "public" page
  isGoalContentVisible(capsule: CapsuleResponseDto): boolean {
    // Goal content is visible ONLY if the goal exists and its 'isVisible' flag is true.
    return capsule.goal?.isVisible === true;
  }

  // Helper method to determine if memories should be shown on this "public" page
  isMemoriesVisible(capsule: CapsuleResponseDto): boolean {
    // Memories are visible only if the capsule's status is 'OPENED'.
    return capsule.status === CapsuleStatus.OPEN;
  }
}
