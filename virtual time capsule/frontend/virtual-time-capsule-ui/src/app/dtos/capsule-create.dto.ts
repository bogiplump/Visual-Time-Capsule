import { GoalDto } from './goal.dto';

export interface CapsuleCreateDto {
  capsuleName: string;
  goal: GoalDto;
}
