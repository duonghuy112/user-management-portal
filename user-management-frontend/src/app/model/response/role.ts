import { Authority } from './authority';
export interface Role {
    id: number;
    name: string;
    authorities: Authority[];
}