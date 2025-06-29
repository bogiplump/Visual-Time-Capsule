
export interface CapsuleCreateDto {
  capsuleName: string;
  openDateTime: string | null;
  goal: GoalCreateDto;
  sharedWithUserIds?: number[]; // NEW: Optional array of user IDs to share with
}

// Ensure GoalCreateDto is defined if it's not in its own file
// If you have a separate goal-create.dto.ts, remove this definition
// and ensure the import path above is correct.
export interface GoalCreateDto {
  content: string;
  isVisible: boolean;
}
