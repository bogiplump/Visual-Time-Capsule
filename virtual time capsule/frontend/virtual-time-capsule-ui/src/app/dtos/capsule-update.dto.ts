
export interface CapsuleUpdateDto {
  capsuleName?: string; // Optional for partial updates
  openDateTime?: string | null; // Changed from 'openDate' to 'openDateTime'
  goal?: GoalUpdatePartDto | null; // Allow goal to be optional or null

  // NEW: Field for a user to mark themselves as ready to close a shared capsule
  isReadyToClose?: boolean;
}

// Assuming GoalUpdatePartDto is defined in its own file (goal-update-part.dto.ts)
// If not, you can define it here:
export interface GoalUpdatePartDto {
  content?: string;
  isAchieved?: boolean;
  isVisible?: boolean;
}
