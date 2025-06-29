
export interface CapsuleUpdateDto {
  capsuleName?: string;
  openDateTime?: string | null;
  goal?: GoalUpdatePartDto | null;
  isReadyToClose?: boolean;
}

export interface GoalUpdatePartDto {
  content?: string;
  isAchieved?: boolean;
  isVisible?: boolean;
}
