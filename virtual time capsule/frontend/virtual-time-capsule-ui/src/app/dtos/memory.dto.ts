import { MemoryType } from '../enums/memory-type.enum'; // Assuming MemoryType is also in 'enums'

export interface MemoryDto {
  id: number;
  memoryType: MemoryType;
  path: string;
  creationDate: string; // LocalDate in Java maps to string in TS
  description: string;

}
