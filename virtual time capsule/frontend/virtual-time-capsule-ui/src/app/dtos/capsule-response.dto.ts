import { CapsuleStatus } from '../enums/capsule-status.enum';
import { GoalDto } from './goal.dto';
import { MemoryDto } from './memory.dto';
import { UserProfileDto } from './user-profile.dto'; // Import UserProfileDto

export interface CapsuleResponseDto {
  id: number;
  capsuleName: string;
  status: CapsuleStatus;
  creationDate: string; // LocalDateTime in Java typically maps to string (ISO 8601)
  lockDate: string | null;    // Optional, as it can be null
  openDateTime: string | null;
  creator: UserProfileDto; // Updated to UserProfileDto

  goal: GoalDto;
  memories?: MemoryDto[];

  // NEW: Fields for shared capsules
  isShared: boolean;
  sharedWithUsers?: UserProfileDto[]; // List of users the capsule is shared with
  readyParticipantsCount?: number; // 'm' in m/n
  totalParticipantsCount?: number; // 'n' in m/n
  isCurrentUserReadyToClose?: boolean; // True if the current authenticated user has marked themselves "ready"
}
