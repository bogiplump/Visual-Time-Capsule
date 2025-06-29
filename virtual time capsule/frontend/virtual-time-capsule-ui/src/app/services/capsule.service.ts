import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CapsuleResponseDto } from '../dtos/capsule-response.dto';
import { MemoryDto } from '../dtos/memory.dto';
import { CapsuleCreateDto } from '../dtos/capsule-create.dto';
import { CapsuleUpdateDto } from '../dtos/capsule-update.dto';

@Injectable({
  providedIn: 'root'
})
export class CapsuleService {
  private apiUrl = `${environment.backendUrl}/api/v1/capsules`;

  constructor(private http: HttpClient) { }

  /**
   * Creates a new time capsule.
   * POST /api/v1/capsules/
   * Now includes optional sharedWithUserIds.
   */
  createCapsule(capsuleDto: CapsuleCreateDto): Observable<CapsuleResponseDto> {
    return this.http.post<CapsuleResponseDto>(this.apiUrl, capsuleDto);
  }

  /**
   * Retrieves all time capsules for the authenticated user (created by or shared with).
   * GET /api/v1/capsules/all
   */
  getAllMyCapsules(): Observable<CapsuleResponseDto[]> {
    return this.http.get<CapsuleResponseDto[]>(`${this.apiUrl}/all`);
  }

  /**
   * Retrieves a specific time capsule by ID.
   * GET /api/v1/capsules/{id}
   */
  getCapsule(capsuleId: number): Observable<CapsuleResponseDto> {
    return this.http.get<CapsuleResponseDto>(`${this.apiUrl}/${capsuleId}`);
  }

  /**
   * Updates an existing time capsule.
   * PUT /api/v1/capsules/{id}
   * Returns CapsuleResponseDto now.
   */
  updateCapsule(id: number, capsuleDto: CapsuleUpdateDto): Observable<CapsuleResponseDto> {
    return this.http.put<CapsuleResponseDto>(`${this.apiUrl}/${id}`, capsuleDto);
  }

  /**
   * Deletes a time capsule by ID.
   * DELETE /api/v1/capsules/{id}
   */
  deleteCapsule(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Locks a specific time capsule, setting its open date.
   * PUT /api/v1/capsules/lock/{id}
   * Now returns CapsuleResponseDto.
   */
  lockCapsule(id: number, openDateTime: string): Observable<CapsuleResponseDto> {
    // Note: This uses RequestParam, so send as query parameter
    let params = new HttpParams().set('openDateTime', openDateTime); // Changed to openDateTime
    return this.http.put<CapsuleResponseDto>(`${this.apiUrl}/lock/${id}`, {}, { params });
  }

  /**
   * Opens a capsule.
   * PUT /api/v1/capsules/{id}/open
   * Returns CapsuleResponseDto now.
   */
  openCapsule(id: number): Observable<CapsuleResponseDto> {
    return this.http.put<CapsuleResponseDto>(`${this.apiUrl}/${id}/open`, {});
  }

  /**
   * Retrieves all memories associated with a specific capsule.
   * GET /api/v1/capsules/{id}/memories
   */
  getMemoriesForCapsule(capsuleId: number): Observable<MemoryDto[]> {
    return this.http.get<MemoryDto[]>(`${this.apiUrl}/${capsuleId}/memories`);
  }

  /**
   * Marks a user as "ready to close" for a specific shared capsule.
   * PUT /api/v1/capsules/{id}/ready-to-close
   */
  markReadyToClose(capsuleId: number): Observable<CapsuleResponseDto> {
    return this.http.put<CapsuleResponseDto>(`${this.apiUrl}/${capsuleId}/ready-to-close`, {});
  }

  // This method seems like a duplicate of getCapsule(id) or was for a different purpose
  // Depending on its actual use, it might be redundant or need adjustment.
  // getCapsulesOfUser(id: number | null): Observable<CapsuleResponseDto[]> {
  //   // Adjust this URL if it's meant to fetch public capsules for any user
  //   // or if it's indeed meant to be for the current user's capsules (which getAllMyCapsules already does)
  //   return this.http.get<CapsuleResponseDto[]>(`${this.apiUrl}/id`);
  // }
}
