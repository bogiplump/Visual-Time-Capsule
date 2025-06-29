import { CapsuleStatus } from './capsule-status.enum'; // Ensure this path is correct: usually 'enums' folder
import { GoalDto } from './goal.dto';
import { MemoryDto } from './memory.dto';
import {Goal} from '../models/goal.model';
import {UserProfileDto} from './user-profile.dto'; // NEW: Import MemoryDto

export interface CapsuleResponseDto {
  id: number;
  capsuleName: string;
  status: CapsuleStatus;
  creationDate: string; // LocalDateTime in Java typically maps to string (ISO 8601)
  lockDate: string | null;    // Optional, as it can be null
  openDateTime: string | null;
  creator: UserProfileDto// Optional, as it can be null
  goal: GoalDto;       // UNCOMMENTED: Include GoalDto for the associated goal
  memories?: MemoryDto[]; // NEW: Include a list/array of MemoryDto for associated memories
}
