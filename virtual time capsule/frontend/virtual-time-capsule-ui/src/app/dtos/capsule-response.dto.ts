import { CapsuleStatus } from '../enums/capsule-status.enum';
import { GoalDto } from './goal.dto';
import { MemoryDto } from './memory.dto';
import { UserProfileDto } from './user-profile.dto';

export interface CapsuleResponseDto {
  id: number;
  capsuleName: string;
  status: CapsuleStatus;
  creationDate: string;
  lockDate: string | null;
  openDateTime: string | null;
  creator: UserProfileDto;
  goalId: number | null;
  memories?: MemoryDto[];
  isShared: boolean;
  sharedWithUsers?: UserProfileDto[];
  readyParticipantsCount?: number;
  totalParticipantsCount?: number;
  isCurrentUserReadyToClose?: boolean;
}
