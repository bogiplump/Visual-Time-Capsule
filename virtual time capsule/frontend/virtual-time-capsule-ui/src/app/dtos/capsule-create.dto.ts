import { GoalDto } from './goal.dto';

export interface CapsuleCreateDto {
  capsuleName: string;
  openDateTime: string | null;
  goal: GoalDto;
}
