export interface UserProfile {
  userId?: number;
  fullName: string;
  username: string;
  email: string;
  avatarUrl: string;
  userRole?: string;
  bio: string;
  university: string;
  faculty: string;
  major?: string;
  className?: string;
  academicYear?: string;
  studentId: string;
  cpa: number;
  coverUrl?: string | null;
  isMe?: boolean;
  isFollowing?: boolean;
}

export class ProfileHeaderState {
  currentUser = $state<UserProfile | null>(null);

  updateUser(newData: Partial<UserProfile>) {
    if (!this.currentUser) return;
    this.currentUser = { ...this.currentUser, ...newData };
  }
}

export const headerState = new ProfileHeaderState();