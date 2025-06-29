import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Capsule } from '../models/capsule.model';
import { Memory } from '../models/memory.model';
import { Goal } from '../models/goal.model';
import { CapsuleCreateDto } from '../dtos/capsule-create.dto';
import { CapsuleUpdateDto } from '../dtos/capsule-update.dto';
import {CapsuleResponseDto} from '../dtos/capsule-response.dto';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CapsuleService {
  private apiUrl = `${environment.backendUrl}/api/v1/capsules`; // Adjust to your backend URL

  constructor(private http: HttpClient) { }

  /**
   * Creates a new time capsule.
   * POST /api/v1/capsules/
   */
  createCapsule(capsuleDto: CapsuleCreateDto): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}`, capsuleDto);
  }

  /**
   * Retrieves a specific time capsule by ID.
   * GET /api/v1/capsules/{id}
   */
  getCapsule(id: number): Observable<CapsuleResponseDto> {
    const url = `${this.apiUrl}/${id}`;
    console.log(url);
    return this.http.get<CapsuleResponseDto>(`${this.apiUrl}/${id}`);
  }

  /**
   * Retrieves all time capsules for the authenticated user.
   * GET /api/v1/capsules/
   */
  getAllCapsules(): Observable<Capsule[]> {
    return this.http.get<Capsule[]>(`${this.apiUrl}/all`);
  }

  /**
   * Locks a specific time capsule, setting its open date.
   * PUT /api/v1/capsules/{id}/lock
   */
  lockCapsule(id: number, openDate: string): Observable<void> {
    let params = new HttpParams().set('openDate', openDate);
    return this.http.put<void>(`${this.apiUrl}/${id}/lock`, null, { params });
  }

  /**
   * Updates an existing time capsule.
   * PUT /api/v1/capsules/{id}
   */
  updateCapsule(id: number, capsuleDto: CapsuleUpdateDto): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}`, capsuleDto);
  }

  /**
   * Deletes a time capsule by ID.
   * DELETE /api/v1/capsules/{id}
   */
  deleteCapsule(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Retrieves all memories associated with a specific capsule.
   * GET /api/v1/capsules/{id}/memories
   */
  getMemoriesForCapsule(capsuleId: number): Observable<Memory[]> {
    return this.http.get<Memory[]>(`${this.apiUrl}/${capsuleId}/memories`);
  }

  /**
   * Retrieves the goal associated with a specific capsule.
   * GET /api/v1/capsules/{id}/goal
   */
  getGoalForCapsule(capsuleId: number): Observable<Goal> {
    return this.http.get<Goal>(`${this.apiUrl}/${capsuleId}/goal`);
  }

  openCapsule(id: number): Observable<any> {
    return this.http.put<Capsule>(`${this.apiUrl}/${id}/open`,null,{});
  }


  getAllMyCapsules(): Observable<CapsuleResponseDto[]> {
    return this.http.get<CapsuleResponseDto[]>(`${this.apiUrl}/all`);
  }

  getCapsulesOfUser(id: number | null): Observable<CapsuleResponseDto[]> {
    return this.http.get<CapsuleResponseDto[]>(`${this.apiUrl}/id`);
  }
}
