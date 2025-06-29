import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MemoryCreateDto } from '../dtos/memory-create.dto';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MemoryService {
  // Note: The Java controller used @RestController("api/v1/memories") which is unusual.
  // Assuming the actual base path is /api/v1/memories.
  private apiUrl = `${environment.backendUrl}/api/v1/memories`; // Adjust to your backend URL

  constructor(private http: HttpClient) { }

    /**
   * Adds a new memory to a capsule.
   * Handles file upload via FormData.
   * POST /api/v1/memories
   */
  addNewMemoryToCapsule(capsuleId: number, memoryDto: MemoryCreateDto): Observable<any> {
      const formData = new FormData();

      formData.append('description', memoryDto.description); // Matches @RequestParam("description")
      formData.append('type', memoryDto.type);             // Matches @RequestParam("type")

      if (memoryDto.content) {
        formData.append('content', memoryDto.content, memoryDto.content.name); // Matches @RequestPart("content")
      } else {
        // Error handling for missing file
      }

      console.log(memoryDto);
      let params = new HttpParams().set('capsuleId', capsuleId.toString()); // Matches @RequestParam("capsuleId")

      return this.http.post<any>(`${this.apiUrl}`, formData, { params });
  }

    /**
   * Adds an existing memory to a capsule.
   * PUT /api/v1/memories/{id}/addMemory
   */
  addExistingMemoryToCapsule(memoryId: number, capsuleId: number): Observable<any> {
    let params = new HttpParams().set('capsuleId', capsuleId.toString());
    return this.http.put<any>(`${this.apiUrl}/${memoryId}/addMemory`, null, { params });
  }

  /**
   * Removes a memory from a capsule (deletes the association).
   * DELETE /api/v1/memories/{id}/removeMemory
   */
  removeMemoryFromCapsule(memoryId: number, capsuleId: number): Observable<void> {
    let params = new HttpParams().set('capsuleId', capsuleId.toString());
    return this.http.delete<void>(`${this.apiUrl}/${memoryId}/removeMemory`, { params });
  }

  // New method for fetching memories for a capsule (if not already there)
  getMemoriesForCapsule(capsuleId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${capsuleId}/memories`);
  }

  /**
   * Fetches the binary content of a memory.
   * This method uses HttpClient, which will automatically include the JWT token via your interceptor.
   */
  getMemoryContent(filename: string): Observable<Blob> {
    // HttpClient will add Authorization header via your interceptor
    return this.http.get(`${this.apiUrl}/content/${filename}`, { responseType: 'blob' });
  }
}
