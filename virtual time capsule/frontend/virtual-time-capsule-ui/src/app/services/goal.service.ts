import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Goal } from '../models/goal.model';
import { GoalDto } from '../dtos/goal.dto';
import { UpdateGoalDto } from '../dtos/update-goal.dto';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GoalService {
  private apiUrl = `${environment.backendUrl}/api/v1/goals`; // Adjust to your backend URL

  constructor(private http: HttpClient) { }

  /**
   * Retrieves a specific goal by ID.
   * GET /api/v1/goals/{id}
   */
  getGoal(id: number): Observable<Goal> {
    return this.http.get<Goal>(`${this.apiUrl}/${id}`);
  }

  /**
   * Creates a new goal and associates it with a capsule.
   * POST /api/v1/goals/
   */
  createGoal(capsuleId: number, goalDto: GoalDto): Observable<Goal> {
    let params = new HttpParams().set('capsuleId', capsuleId.toString());
    return this.http.post<Goal>(`${this.apiUrl}/`, goalDto, { params });
  }

  /**
   * Deletes a goal by ID.
   * DELETE /api/v1/goals/{id}
   */
  deleteGoal(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Updates the content of a specific goal.
   * PUT /api/v1/goals/{id}/update
   */
  updateGoal(id: number | null, updateGoalDto: UpdateGoalDto): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/update`, updateGoalDto);
  }
}
