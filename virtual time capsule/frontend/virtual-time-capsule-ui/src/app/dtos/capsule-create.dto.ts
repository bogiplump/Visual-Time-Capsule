
export interface CapsuleCreateDto {
  capsuleName: string;
  openDateTime: string | null;
  goal: GoalCreateDto;
  sharedWithUserIds?: number[];
}

export interface GoalCreateDto {
  content: string;
  isVisible: boolean;
}
