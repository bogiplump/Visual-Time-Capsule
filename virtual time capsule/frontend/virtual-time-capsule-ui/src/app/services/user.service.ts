import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponseDto } from '../dtos/user-response.dto';
import { GoalDto } from '../dtos/goal.dto';
import { FriendshipDto } from '../dtos/friendship.dto';

// Define a placeholder for UserUpdateDto if not yet created.
// In a real app, you would have this as a separate file.
export interface UserUpdateDto {
  firstName?: string;
  lastName?: string;
  email?: string;
  // Add other updatable fields as per your backend's UserUpdateDto
}


@Injectable({
  providedIn: 'root'
})
export class UserService {
  // Assuming the base path for user-related operations is /api/v1/users
  private apiUrl = 'http://localhost:8080/api/v1/users'; // Adjust to your backend URL

  constructor(private http: HttpClient) { }

  /**
   * Retrieves a user's profile by ID.
   * GET /api/v1/users/{id} (inferred)
   */
  getUserProfile(id: number): Observable<UserResponseDto> {
    return this.http.get<UserResponseDto>(`${this.apiUrl}/${id}`);
  }

  /**
   * Updates a user's profile.
   * PUT /api/v1/users/{id} (inferred)
   */
  updateUser(id: number, userUpdateDto: UserUpdateDto): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}`, userUpdateDto);
  }

  /**
   * Deletes a user by ID.
   * DELETE /api/v1/users/{id} (inferred)
   */
  deleteUser(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
  }

  /**
   * Sends a friendship invitation to another user.
   * POST /api/v1/users/friendship/{id}
   * 'id' here is the receiverId based on the controller.
   */
  createFriendshipInvitation(receiverId: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/friendship/${receiverId}`, null);
  }

  /**
   * Answers a friendship invitation (accept/decline/block).
   * PUT /api/v1/users/friendship/{id}
   * 'id' here is the friendship ID from the path variable, not receiverId.
   */
  updateFriendship(friendshipId: number, friendshipDto: FriendshipDto): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/friendship/${friendshipId}`, friendshipDto);
  }

  /**
   * Retrieves a user's goals by user ID.
   * GET /api/v1/users/{id}/goals
   */
  getUserGoals(userId: number): Observable<GoalDto[]> {
    return this.http.get<GoalDto[]>(`${this.apiUrl}/${userId}/goals`);
  }
}
