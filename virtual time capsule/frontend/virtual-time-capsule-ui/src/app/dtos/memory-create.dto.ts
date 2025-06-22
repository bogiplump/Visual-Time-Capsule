import { MemoryType } from '../models/memory-type.enum'; // Assuming you have this enum defined

export interface MemoryCreateDto {
  name: string;
  description: string;
  type: MemoryType;
  content: File; // MultipartFile in Java maps to File or Blob in TypeScript for file uploads
}
