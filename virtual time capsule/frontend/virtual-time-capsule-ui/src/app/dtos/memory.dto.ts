import { MemoryType } from '../enums/memory-type.enum';

export interface MemoryDto {
  id: number;
  memoryType: MemoryType;
  path: string;
  creationDate: string;
  description: string;

}
