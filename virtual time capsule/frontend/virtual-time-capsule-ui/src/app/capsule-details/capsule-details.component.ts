import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Subscription, interval } from 'rxjs';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

// Services
import { CapsuleService } from '../services/capsule.service';
import { GoalService } from '../services/goal.service';
import { MemoryService } from '../services/memory.service';
import { AuthService } from '../services/auth.service';

// Models & DTOs
import { CapsuleUpdateDto, GoalUpdatePartDto } from '../dtos/capsule-update.dto';
import { MemoryCreateDto } from '../dtos/memory-create.dto';
import { CapsuleStatus } from '../enums/capsule-status.enum';
import { CapsuleResponseDto } from '../dtos/capsule-response.dto';
import { GoalDto } from '../dtos/goal.dto';
import { MemoryDto } from '../dtos/memory.dto';
import { MemoryType } from '../enums/memory-type.enum';
import {UpdateGoalDto} from '../dtos/update-goal.dto';

interface Message {
  text: string;
  type: 'success' | 'error';
}

@Component({
  selector: 'app-capsule-details',
  standalone: true,
  imports: [CommonModule, FormsModule, DatePipe],
  templateUrl: './capsule-details.component.html',
  styleUrls: ['./capsule-details.component.css']
})
export class CapsuleDetailsComponent implements OnInit, OnDestroy {
  capsuleId: number | null = null;
  capsule: CapsuleResponseDto | null = null;
  currentGoalId: number | null = null;
  displayingGoal: GoalDto | null = null;
  currentAuthenticatedUsername: string | null = null;
  goalAchievementMessage: string | null = null;
  hasAchievementBeenSet = false;

  editingCapsule = false;
  updatedCapsuleName = '';
  updatedOpenDate: string | null = null;
  capsuleStatus: typeof CapsuleStatus = CapsuleStatus;

  editingGoal = false;
  updatedGoalContent: string = '';

  addingMemory = false;
  newMemoryData: MemoryCreateDto = {
    description: '',
    type: MemoryType.TEXT,
    content: null as any
  };
  selectedFile: File | null = null;
  memoryTypes = Object.values(MemoryType);
  memories: MemoryDto[] = [];

  loadingCapsule = true;
  loadingMemories = true;
  loadingGoalDetails = false;
  loadingUpdateCapsule = false;
  loadingUpdateGoal = false;
  loadingAddMemory = false;
  loadingDeleteMemory = false;

  messages: Message[] = [];
  memoryDialogMessages: Message[] = [];

  currentMemoryContentUrl: SafeResourceUrl | null = null;
  displayingMemory: MemoryDto | null = null;
  loadingMemoryContent = false;

  currentDate: Date = new Date();
  private dateUpdateSubscription: Subscription | undefined;
  showGoalAchievedDialog = false;
  goalAchievedStatus: 'achieved' | 'notAchieved' | null = null;
  submittingGoalAchievedStatus = false;


  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private capsuleService: CapsuleService,
    private goalService: GoalService,
    private memoryService: MemoryService,
    private authService: AuthService,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {
    this.authService.getCurrentUser().subscribe(user => {
      this.currentAuthenticatedUsername = user?.username || null;
    });

    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.capsuleId = +id;
        this.loadCapsuleDetails();
      } else {
        this.addMessage('Capsule ID not found in route. Redirecting to dashboard.', 'error');
        this.router.navigate(['/dashboard']);
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
    this.closeMemoryContent();
  }

  addMessage(text: string, type: 'success' | 'error'): void {
    this.messages.push({ text, type });
    setTimeout(() => {
      this.messages = this.messages.filter(msg => msg.text !== text);
    }, 5000);
  }

  addMemoryDialogMessage(text: string, type: 'success' | 'error'): void {
    this.memoryDialogMessages.push({ text, type });
    setTimeout(() => {
      this.memoryDialogMessages = this.memoryDialogMessages.filter(msg => msg.text !== text);
    }, 5000);
  }

  loadCapsuleDetails(): void {
    if (this.capsuleId === null) return;

    this.loadingCapsule = true;
    this.loadingMemories = true;
    this.messages = [];
    this.addMessage('Loading capsule details...', 'success');

    this.capsuleService.getCapsule(this.capsuleId).subscribe({
      next: (capsuleData: CapsuleResponseDto) => {
        this.capsule = capsuleData;
        this.updatedCapsuleName = capsuleData.capsuleName;
        this.updatedOpenDate = capsuleData.openDateTime ? this.formatDateForDateTimeLocal(capsuleData.openDateTime) : null;
        this.currentGoalId = capsuleData.goalId || null;
        this.memories = Array.from(capsuleData.memories || []);
        this.addMessage('Capsule details and memories loaded successfully!', 'success');

        if (this.currentGoalId !== null) {
          this.fetchGoalDetails(this.currentGoalId);
        } else {
          this.displayingGoal = null;
        }
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to load capsule: ${error.message || 'Unknown error'}`, 'error');
        this.router.navigate(['/dashboard']);
      },
      complete: () => {
        this.loadingCapsule = false;
        this.loadingMemories = false;
      }
    });
  }

  fetchGoalDetails(goalId: number): void {
    this.loadingGoalDetails = true;
    this.goalService.getGoal(goalId).subscribe({
      next: (goalDto: GoalDto) => {
        this.displayingGoal = goalDto;
        this.updatedGoalContent = goalDto.content;
        this.goalAchievedStatus = goalDto.achieved ? 'achieved' : 'notAchieved';

        if (this.displayingGoal.achieved) {
          this.goalAchievementMessage = 'Congratulations! You achieved this goal.';
        } else if (this.capsule?.status === CapsuleStatus.OPEN) {
          this.goalAchievementMessage = 'You did not achieve this goal. Keep striving for your next one!';
        } else {
          this.goalAchievementMessage = '';
        }
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to load goal details: ${error.message || 'Unknown error'}`, 'error');
        this.displayingGoal = null;
      },
      complete: () => {
        this.loadingGoalDetails = false;
      }
    });
  }

  refetchMemories(): void {
    if (this.capsuleId === null) return;
    this.loadingMemories = true;
    this.memoryService.getMemoriesForCapsule(this.capsuleId).subscribe({
      next: (memoriesData: MemoryDto[]) => {
        this.memories = memoriesData;
        this.addMemoryDialogMessage('Memories list updated.', 'success');
      },
      error: (error: HttpErrorResponse) => {
        this.addMemoryDialogMessage(`Failed to reload memories: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => {
        this.loadingMemories = false;
      }
    });
  }

  private formatDateForDateTimeLocal(isoString: string): string {
    if (!isoString) return '';
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }


  // --- Capsule Management ---
  toggleEditCapsule(): void {
    if (this.isEditable) {
      this.editingCapsule = !this.editingCapsule;
      if (this.editingCapsule && this.capsule) {
        this.updatedCapsuleName = this.capsule.capsuleName;
        this.updatedOpenDate = this.capsule.openDateTime ? this.formatDateForDateTimeLocal(this.capsule.openDateTime) : null;
      }
    } else {
      this.addMessage('This capsule cannot be edited. It must be in CREATED status and you must be the creator.', 'error');
    }
  }

  saveCapsuleDetails(): void {
    if (!this.capsuleId || !this.capsule) {
      this.addMessage('Capsule data is not loaded. Cannot save changes.', 'error');
      return;
    }

    this.loadingUpdateCapsule = true;
    this.messages = [];

    const goalUpdatePart: GoalUpdatePartDto | null = this.displayingGoal ? {
      content: this.updatedGoalContent,
      isAchieved: this.displayingGoal.achieved,
      isVisible: this.displayingGoal.visible
    } : null;


    const updateDto: CapsuleUpdateDto = {
      capsuleName: this.updatedCapsuleName,
      openDateTime: this.updatedOpenDate ? new Date(this.updatedOpenDate).toISOString() : null,
      goal: goalUpdatePart
    };

    if (this.capsule.status === CapsuleStatus.CREATED && updateDto.openDateTime) {
      const selectedOpenDate = new Date(this.updatedOpenDate!);
      if (selectedOpenDate <= this.currentDate) {
        this.addMessage('Open date must be in the future to lock the capsule.', 'error');
        this.loadingUpdateCapsule = false;
        return;
      }

      console.log("Updateing ......")

      this.capsuleService.updateCapsule(this.capsuleId, updateDto).subscribe({
        next: (updatedCapsule: CapsuleResponseDto) => {
          this.addMessage('Capsule updated successfully!', 'success');
          this.capsule = updatedCapsule;
          this.toggleEditCapsule();
        },
        error: (error: HttpErrorResponse) => {

          this.addMessage(`Failed to update capsule: ${error.error || 'Unknown error'}`, 'error');
          this.loadingUpdateCapsule = false;
        },
        complete: () => { this.loadingUpdateCapsule = false; }
      });
    }
  }

  lockCapsuleAction(): void {
    if (!this.capsuleId || !this.capsule) {
      this.addMessage('Capsule data not loaded. Cannot lock capsule.', 'error');
      return;
    }

    if (this.capsule.status !== CapsuleStatus.CREATED) {
      this.addMessage('Capsule must be in CREATED status to lock it.', 'error');
      return;
    }

    console.log("Locking capsule..." + this.isAuthorized);
    if (!this.isAuthorized) {
      this.addMessage('You are not authorized to lock this capsule.', 'error');
      return;
    }

    if (!this.updatedOpenDate) {
      this.addMessage('Please set an "Opens On" date for the capsule by editing it before locking.', 'error');
      return;
    }

    const selectedOpenDate = new Date(this.updatedOpenDate);
    if (selectedOpenDate <= this.currentDate) {
      this.addMessage('Open date must be in the future to lock the capsule.', 'error');
      return;
    }

    if (!confirm('Are you sure you want to lock this capsule? It will become inaccessible until its open date.')) {
      return;
    }

    this.loadingUpdateCapsule = true;
    const openDateTimeISO = new Date(this.updatedOpenDate).toISOString();

    this.capsuleService.lockCapsule(this.capsuleId, openDateTimeISO).subscribe({
      next: (updatedCapsule: CapsuleResponseDto) => {
        this.addMessage('Capsule locked successfully! It will open on the set date.', 'success');
        this.capsule = updatedCapsule;
        if (this.capsule.status === CapsuleStatus.OPEN && this.currentGoalId !== null) {
          this.fetchGoalDetails(this.currentGoalId);
        }
      },
      error: (error: HttpErrorResponse) => {
        console.log(error);
        this.addMessage(`Failed to lock capsule: ${error.error || 'Unknown error'}`, 'error');
      },
      complete: () => {
        this.loadingUpdateCapsule = false;
      }
    });
  }


  deleteCapsule(): void {
    if (!this.capsuleId || !this.canDeleteCapsule || !confirm('Are you sure you want to delete this capsule? This action cannot be undone.')) {
      if (!this.canDeleteCapsule) {
        this.addMessage('You are not authorized to delete this capsule.', 'error');
      }
      return;
    }

    this.capsuleService.deleteCapsule(this.capsuleId).subscribe({
      next: () => {
        this.addMessage('Capsule deleted successfully!', 'success');
        this.router.navigate(['/dashboard']);
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to delete capsule: ${error.message || 'Unknown error'}`, 'error');
      }
    });
  }

  get canOpenCapsule(): boolean {
    if (!this.capsule || this.capsule.status !== CapsuleStatus.CLOSED || !this.capsule.openDateTime) {
      return false;
    }
    const capsuleOpenDate = new Date(this.capsule.openDateTime);
    return this.currentDate >= capsuleOpenDate;
  }

  openCapsule(): void {
    if (!this.capsuleId || !this.capsule) {
      this.addMessage('Capsule data is not loaded. Cannot open capsule.', 'error');
      return;
    }

    if (!this.isAuthorized) {
      this.addMessage('You are not authorized to open this capsule.', 'error');
      return;
    }

    if (!this.canOpenCapsule) {
      this.addMessage('Cannot open capsule. It must be in CLOSED status and its open date must be reached.', 'error');
      return;
    }

    if (!confirm('Are you sure you want to open this capsule? This action cannot be undone.')) {
      return;
    }

    this.capsuleService.openCapsule(this.capsuleId).subscribe({
      next: () => {
        this.addMessage('Capsule opened successfully! All contents are now visible.', 'success');
        this.loadCapsuleDetails(); // This will re-fetch capsule, which will then trigger fetchGoalDetails
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to open capsule: ${error.error || 'Unknown error'}`, 'error');
      }
    });
    this.showGoalAchievedDialog = true;
  }

  get isEditable(): boolean {
    return this.isAuthorized;
  }

  get isAuthorized(): boolean {
    return <boolean>(this.capsule?.creator?.username === this.currentAuthenticatedUsername ||
      (this.capsule?.isShared && this.capsule.sharedWithUsers?.map(u => u.username).includes(<string>this.currentAuthenticatedUsername)) &&
      this.capsule?.status === CapsuleStatus.CREATED);
  }

  get canDeleteCapsule(): boolean {
    return this.capsule !== null &&
      this.capsule.creator?.username === this.currentAuthenticatedUsername;
  }

  // --- Goal Management ---
  toggleEditGoal(): void {
    if (this.isEditable) {
      this.editingGoal = !this.editingGoal;
      if (this.editingGoal && this.displayingGoal) {
        this.updatedGoalContent = this.displayingGoal.content;
      }
    } else {
      this.addMessage('Goal cannot be edited. Capsule must be in CREATED status and you must be the creator.', 'error');
    }
  }

  saveGoalContent(): void {
    if (!this.displayingGoal || !this.displayingGoal.id) {
      this.addMessage('Goal details not loaded or Goal ID is missing. Cannot save goal.', 'error');
      return;
    }
    if (!this.isEditable) {
      this.addMessage('You are not authorized to save changes to this goal.', 'error');
      return;
    }

    this.loadingUpdateGoal = true;
    this.messages = [];

    const updateGoalDto: UpdateGoalDto = {
      contentUpdate: this.updatedGoalContent,
      isAchieved: this.displayingGoal.visible,
      isVisible: this.displayingGoal.visible,
    };

    this.goalService.updateGoal(this.displayingGoal.id, updateGoalDto)
  }

  submitGoalAchievedStatus(): void {
    if (!this.displayingGoal || !this.displayingGoal.id || this.goalAchievedStatus === null) {
      this.addMessage('Goal data is missing or achievement status not selected.', 'error');
      return;
    }

    this.submittingGoalAchievedStatus = true;

    console.log(this.goalAchievedStatus === 'achieved');
    console.log(typeof (this.goalAchievedStatus === 'achieved'));
    this.goalService.setIsAchieved(this.displayingGoal.id, this.goalAchievedStatus === 'achieved').subscribe({
      next: () => {
        console.log(this.showGoalAchievedDialog);
        this.showGoalAchievedDialog = false;
        this.hasAchievementBeenSet = true;
        this.loadCapsuleDetails();
      }
    });
    console.log(this.hasAchievementBeenSet);
  }


  // --- Memory Management ---
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

  submitAddMemory(): void {
    if (!this.capsuleId) {
      this.addMemoryDialogMessage('Capsule ID is not available. Cannot add memory.', 'error');
      return;
    }
    if (!this.isEditable) {
      this.addMemoryDialogMessage('Cannot add memory. Capsule must be in CREATED status and you must be the creator.', 'error');
      return;
    }
    if (!this.selectedFile) {
      this.addMemoryDialogMessage('Please select a file for the memory content.', 'error');
      return;
    }
    if (!this.newMemoryData.description) {
      this.addMemoryDialogMessage('Please provide a description for the memory.', 'error');
      return;
    }
    if (!this.newMemoryData.type) {
      this.addMemoryDialogMessage('Please select a type for the memory.', 'error');
      return;
    }

    this.loadingAddMemory = true;
    this.memoryDialogMessages = [];

    this.memoryService.addNewMemoryToCapsule(this.capsuleId, this.newMemoryData).subscribe({
      next: () => {
        this.addMemoryDialogMessage('Memory added successfully!', 'success');
        this.newMemoryData = { description: '', type: MemoryType.TEXT, content: null as any };
        this.selectedFile = null;
        this.refetchMemories();
      },
      error: (error: HttpErrorResponse) => {
        let errorMessage = 'Failed to add memory. Please try again.';
        this.addMessage(error.error, 'error');
        this.loadingAddMemory = false;
      },
      complete: () => { this.loadingAddMemory = false; }
    });
  }

  deleteMemory(memoryId: number): void {
    if (!this.capsuleId || !confirm('Are you sure you want to delete this memory? This action cannot be undone.')) {
      return;
    }

    this.loadingDeleteMemory = true;
    this.memoryDialogMessages = [];

    this.memoryService.removeMemoryFromCapsule(memoryId, this.capsuleId).subscribe({
      next: () => {
        this.addMemoryDialogMessage('Memory deleted successfully!', 'success');
        this.refetchMemories();
      },
      error: (error: HttpErrorResponse) => {
        this.addMemoryDialogMessage(`Failed to delete memory: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => { this.loadingDeleteMemory = false; }
    });
  }

  viewMemoryContent(memory: MemoryDto): void {
    if (!memory.path) {
      this.addMemoryDialogMessage('No content path available for this memory.', 'error');
      return;
    }

    this.loadingMemoryContent = true;
    this.currentMemoryContentUrl = null;
    this.displayingMemory = memory;

    const filename = memory.path.includes('/') ? this.getFilenameFromPath(memory.path) : memory.path;

    this.memoryService.getMemoryContent(filename).subscribe({
      next: (blob: Blob) => {
        const objectUrl = URL.createObjectURL(blob);
        this.currentMemoryContentUrl = this.sanitizer.bypassSecurityTrustResourceUrl(objectUrl);
        this.addMemoryDialogMessage('Memory content loaded for preview.', 'success');
      },
      error: (error: HttpErrorResponse) => {
        this.addMemoryDialogMessage(`Failed to load memory content: ${error.message || 'Unknown error'}. It might not be available or you might lack permissions.`, 'error');
        this.currentMemoryContentUrl = null;
        this.displayingMemory = null;
      },
      complete: () => {
        this.loadingMemoryContent = false;
      }
    });
  }

  closeMemoryContent(): void {
    if (this.currentMemoryContentUrl && typeof this.currentMemoryContentUrl === 'object' && 'changingThisBreaksApplicationSecurity' in this.currentMemoryContentUrl) {
      const objectUrl = (this.currentMemoryContentUrl as any).changingThisBreaksApplicationSecurity;
      URL.revokeObjectURL(objectUrl);
    }
    this.currentMemoryContentUrl = null;
    this.displayingMemory = null;
  }

  getFilenameFromPath(fullPath: string): string {
    if (!fullPath) return '';
    const parts = fullPath.split('/');
    return parts[parts.length - 1];
  }
}
