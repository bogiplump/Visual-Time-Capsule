import { GoalDto } from './goal.dto';

export interface CapsuleUpdateDto {
  capsuleName?: string; // Optional for partial updates
  openDateTime?: string | null; // Changed from 'openDate' to 'openDateTime'
  goal?: NestedGoalUpdateDto | null; // Allow goal to be optional or null
}

export interface NestedGoalUpdateDto {
  content: string;
  isAchieved: boolean;
  isVisible: boolean;
}
