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
import { AuthService } from '../services/auth.service'; // Assuming you have an Auth Service

// Models & DTOs
import { CapsuleUpdateDto } from '../dtos/capsule-update.dto';
import { UpdateGoalDto } from '../dtos/update-goal.dto';
import { MemoryCreateDto } from '../dtos/memory-create.dto';
import { CapsuleStatus } from '../enums/capsule-status.enum';
import { CapsuleResponseDto } from '../dtos/capsule-response.dto';
import { GoalDto } from '../dtos/goal.dto';
import { MemoryDto } from '../dtos/memory.dto';
import { MemoryType } from '../enums/memory-type.enum';

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
  currentGoal: GoalDto | null = null;

  editingCapsule = false;
  updatedCapsuleName = '';
  updatedOpenDate: string | null = ''; // Used for datetime-local binding

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

  currentAuthenticatedUsername: string = ''; // Property to hold current user's username

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private capsuleService: CapsuleService,
    private goalService: GoalService,
    private memoryService: MemoryService,
    private sanitizer: DomSanitizer,
    private authService: AuthService // Inject AuthService
  ) { }

  ngOnInit(): void {
    this.currentAuthenticatedUsername = this.authService.getCurrentUsername();

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
        console.log(this.capsule);
        this.updatedCapsuleName = capsuleData.capsuleName;
        // Use the openDateTime from the capsule response, formatted for datetime-local
        this.updatedOpenDate = capsuleData.openDateTime ? this.formatDateForDateTimeLocal(capsuleData.openDateTime) : '';

        console.log("Capsule loaded openDateTime:", capsuleData.openDateTime); // Still log this to verify backend output
        console.log("Capsule loaded lockDate:", capsuleData.lockDate); // Still log this to verify backend output


        this.currentGoal = capsuleData.goal || null;
        this.updatedGoalContent = capsuleData.goal?.content || '';

        this.memories = Array.from(capsuleData.memories || []);
        this.addMessage('Capsule details and memories loaded successfully!', 'success');
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
    const date = new Date(isoString); // This should correctly interpret the ISO string with 'Z'
    // Get the local components for the datetime-local input
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }


  // isEditable: Can edit capsule name, set open date, add memories, edit goal
  get isEditable(): boolean {
    return this.capsule !== null &&
      this.capsule.status === CapsuleStatus.CREATED &&
      this.capsule.creator?.username === this.currentAuthenticatedUsername;
  }

  // canDeleteCapsule: Can delete the capsule (only creator, regardless of status)
  get canDeleteCapsule(): boolean {
    return this.capsule !== null &&
      this.capsule.creator?.username === this.currentAuthenticatedUsername;
  }

  toggleEditCapsule(): void {
    if (this.isEditable) {
      this.editingCapsule = !this.editingCapsule;
      if (this.editingCapsule && this.capsule) {
        this.updatedCapsuleName = this.capsule.capsuleName;
        this.updatedOpenDate = this.capsule.openDateTime ? this.formatDateForDateTimeLocal(this.capsule.openDateTime) : '';
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

    const goalUpdatePart = this.currentGoal ? {
      content: this.updatedGoalContent,
      isAchieved: this.currentGoal.isAchieved,
      isVisible: this.currentGoal.isVisible
    } : null;

    // Send updatedOpenDate (from datetime-local) directly.
    // The backend's parseInputDateTimeToUTC will handle converting this local time to UTC.
    const updateDto: CapsuleUpdateDto = {
      capsuleName: this.updatedCapsuleName,
      openDateTime: this.updatedOpenDate || null, // Changed: Send directly from datetime-local input
      goal: goalUpdatePart
    };

    // Moved 'lock' logic into lockCapsuleAction(). This 'save' is now purely for update.
    this.capsuleService.updateCapsule(this.capsuleId, updateDto).subscribe({
      next: () => {
        this.addMessage('Capsule updated successfully!', 'success');
        this.loadCapsuleDetails();
        this.toggleEditCapsule();
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to update capsule: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => { this.loadingUpdateCapsule = false; }
    });
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

    if (this.capsule.creator?.username !== this.currentAuthenticatedUsername) {
      this.addMessage('You are not authorized to lock this capsule.', 'error');
      return;
    }

    // Use the stored openDateTime, which should already be in ISO UTC format
    if (!this.capsule.openDateTime) {
      this.addMessage('Please set an "Opens On" date for the capsule by editing it before locking.', 'error');
      return;
    }

    if (!confirm('Are you sure you want to lock this capsule? It will become inaccessible until its open date.')) {
      return;
    }

    this.loadingUpdateCapsule = true;
    // Pass the existing openDateTime from the capsule object
    this.capsuleService.lockCapsule(this.capsuleId, this.capsule.openDateTime).subscribe({
      next: () => {
        this.addMessage('Capsule locked successfully! It will open on the set date.', 'success');
        this.loadCapsuleDetails();
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to lock capsule: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => { this.loadingUpdateCapsule = false; }
    });
  }

  deleteCapsule(): void {
    // Check canDeleteCapsule instead of isEditable
    if (!this.capsuleId || !this.canDeleteCapsule || !confirm('Are you sure you want to delete this capsule? This action cannot be undone.')) {
      if (!this.canDeleteCapsule) { // Provide specific error if not authorized for deletion
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

    if (this.capsule.creator?.username !== this.currentAuthenticatedUsername) {
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
        this.loadCapsuleDetails();
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to open capsule: ${error.message || 'Unknown error'}`, 'error');
      }
    });
  }

  toggleEditGoal(): void {
    if (this.isEditable) {
      this.editingGoal = !this.editingGoal;
      if (this.editingGoal && this.currentGoal) {
        this.updatedGoalContent = this.currentGoal.content;
      }
    } else {
      this.addMessage('Goal cannot be edited. Capsule must be in CREATED status and you must be the creator.', 'error');
    }
  }

  saveGoalContent(): void {
    if (!this.currentGoal || !this.capsuleId) {
      this.addMessage('Goal or Capsule ID not loaded. Cannot save goal.', 'error');
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
      isVisible: this.currentGoal.isVisible,
      isAchieved: this.currentGoal.isAchieved
    };

    this.goalService.updateGoal(this.currentGoal.id, updateGoalDto).subscribe({
      next: () => {
        this.addMessage('Goal updated successfully!', 'success');
        this.loadCapsuleDetails();
        this.toggleEditGoal();
      },
      error: (error: HttpErrorResponse) => {
        this.addMessage(`Failed to update goal: ${error.message || 'Unknown error'}`, 'error');
      },
      complete: () => { this.loadingUpdateGoal = false; }
    });
  }

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
        if (error.error instanceof Blob) {
          error.error.text().then(text => {
            try {
              const errorObj = JSON.parse(text);
              if (errorObj.message) errorMessage = errorObj.message;
              else if (errorObj.error) errorMessage = errorObj.error;
            } catch {
              errorMessage = text || errorMessage;
            }
            this.addMemoryDialogMessage(errorMessage, 'error');
          });
        } else if (error.error && Array.isArray(error.error) && error.error.length > 0) {
          errorMessage = error.error.map((msg: string) => {
            const match = msg.match(/^[^:]+:\s*"(.*)"$/);
            return match && match[1] ? match[1] : msg;
          }).join('\n');
          this.addMemoryDialogMessage(errorMessage, 'error');
        } else if (error.error && typeof error.error.message === 'string') {
          errorMessage = error.error.message;
          this.addMemoryDialogMessage(errorMessage, 'error'); // Fixed: Changed addDialogMessage to addMemoryDialogMessage
        } else if (typeof error.error === 'string') {
          errorMessage = error.error;
          this.addMemoryDialogMessage(errorMessage, 'error'); // Fixed: Changed addDialogMessage to addMemoryDialogMessage
        } else {
          this.addMemoryDialogMessage(errorMessage, 'error');
        }
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
