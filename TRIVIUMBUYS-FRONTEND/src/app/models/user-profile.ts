export interface UserProfile {
    id: number;
    name: string;
    email: string;
    username?: string;
    avatar?: string;
    role: string;
    createdAt?: Date;
    lastLogin?: Date;
    isActive?: boolean;
    address?: string;
    phone?: string;
    permissions?: string[];
}