export interface CreateGroupDto {
  name: string;
  theme: string;
  openTime: string;
  timeBetweenCapsules: string;
  capsuleIds: number[];
}
