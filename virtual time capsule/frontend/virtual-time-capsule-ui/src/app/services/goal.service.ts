import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GoalDto } from '../dtos/goal.dto';
import { UpdateGoalDto } from '../dtos/update-goal.dto';
import {environment} from '../../environments/environment';
import {GoalCreateDto} from '../dtos/capsule-create.dto';

@Injectable({
  providedIn: 'root'
})
export class GoalService {
  private apiUrl = `${environment.backendUrl}/api/v1/goals`; // Adjust to your backend URL

  constructor(private http: HttpClient) { }

  getGoal(id: number | null | undefined): Observable<GoalDto> {
    return this.http.get<GoalDto>(`${this.apiUrl}/${id}`);
  }

  createGoal(capsuleId: number, goalDto: GoalCreateDto): Observable<void> {
    let params = new HttpParams().set('capsuleId', capsuleId.toString());
    return this.http.post<void>(`${this.apiUrl}/`, goalDto, { params });
  }

  updateGoal(id: number | null, updateGoalDto: UpdateGoalDto): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/update`, updateGoalDto);
  }

  setIsAchieved(id:number | null,isAchieved: boolean): Observable<void> {
    console.log("Sending is Achieved request");
    return this.http.put<void>(
      `${this.apiUrl}/${id}/setIsAchieved?isAchieved=${isAchieved}`,
      null // no request body
    );
  }
}
