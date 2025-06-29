import {Component, OnInit} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';

// Services
import {CapsuleService} from '../services/capsule.service'; // Re-use existing CapsuleService
// DTOs
import {CapsuleResponseDto} from '../dtos/capsule-response.dto';
import {CapsuleStatus} from '../enums/capsule-status.enum'; // Use the correct enum import
import {UserService} from '../services/user.service';
import {GoalDto} from '../dtos/goal.dto';
import {GoalService} from '../services/goal.service';
import {Observable} from 'rxjs'; // Re-use existing DTO

interface CapsuleGoalPair {
  capsule: CapsuleResponseDto;
  goal: GoalDto;
}

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
  capsulesWrappers: CapsuleGoalPair[] = [];
  loadingCapsules = true;
  messages: Message[] = [];

  constructor(
    private route: ActivatedRoute,
    protected router: Router,
    private capsuleService: CapsuleService,
    private userService: UserService,
    private goalService: GoalService
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

    this.userService.getPublicCapsulesByUserId(userId).subscribe({
      next: (capsulesData: CapsuleResponseDto[]) => {
        for (const capsule of capsulesData) {
          this.goalService.getGoal(capsule.goalId).subscribe({
            next: goal => {
              this.capsulesWrappers.push({capsule,goal});
            }
          });
        }
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

  viewCapsuleDetails(capsuleId: number): void {
    // Navigate to the capsule details page.
    // The capsule-details component already has logic to conditionally hide edit buttons.
    this.router.navigate(['/capsules', capsuleId]);
  }

  getGoal(goalId: number | null): Observable<GoalDto> {
    return this.goalService.getGoal(goalId);
  }

  // Helper method to determine if a goal should be displayed on this "public" page
  isGoalContentVisible(goalDto: GoalDto): boolean {
    return goalDto?.visible === true;
  }

  // Helper method to determine if memories should be shown on this "public" page
  isMemoriesVisible(capsule: CapsuleResponseDto): boolean {
    // Memories are visible only if the capsule's status is 'OPENED'.
    return capsule.status === CapsuleStatus.OPEN || capsule.status === CapsuleStatus.CREATED;
  }
}
