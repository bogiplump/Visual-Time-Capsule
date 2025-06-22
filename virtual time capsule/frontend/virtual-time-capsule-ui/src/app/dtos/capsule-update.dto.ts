import { GoalDto } from './goal.dto';

export interface CapsuleUpdateDto {
  capsuleName?: string; // Marked as optional based on typical update DTOs
  openDate: string;     // LocalDateTime in Java maps to string (ISO 8601)
  goal: GoalDto;
}
