export interface GoalDto {
  id: number | null;
  visible: boolean;
  achieved: boolean;
  content: string;
  creator: number | null;
  creationDate: string | null;
  capsuleId: number | null;
}
