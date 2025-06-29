import {Component, OnInit} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {CapsuleService} from '../services/capsule.service';
import {CapsuleResponseDto} from '../dtos/capsule-response.dto';
import {CapsuleStatus} from '../enums/capsule-status.enum';
import {UserService} from '../services/user.service';
import {GoalDto} from '../dtos/goal.dto';
import {GoalService} from '../services/goal.service';
import {Observable} from 'rxjs';

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
  username: string | null = null;
  capsulesWrappers: CapsuleGoalPair[] = [];
  loadingCapsules = true;
  messages: Message[] = [];

  constructor(
    private route: ActivatedRoute,
    protected router: Router,
    private userService: UserService,
    private goalService: GoalService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('userId');

      const name = params.get('username');

      if (id) {
        this.userId = +id;
        this.username = name;
        this.loadUserAndCapsules(this.userId);
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
    this.router.navigate(['/capsules', capsuleId]);
  }

  getGoal(goalId: number | null): Observable<GoalDto> {
    return this.goalService.getGoal(goalId);
  }

  isGoalContentVisible(goalDto: GoalDto): boolean {
    return goalDto?.visible === true;
  }

  isMemoriesVisible(capsule: CapsuleResponseDto): boolean {
    return capsule.status === CapsuleStatus.OPEN || capsule.status === CapsuleStatus.CREATED;
  }
}
