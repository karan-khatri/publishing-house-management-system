/**
 * =============================================================================
 * BASE MODELS & COMMON INTERFACES
 * =============================================================================
 */

/**
 * Base interface that all entity models extend from
 * Provides common fields for database entities
 */
export interface BaseModel {
  id: number;
  createdAt: Date;
  updatedAt: Date;
}

/**
 * Standard pagination interface for list queries
 */
export interface Pagination {
  page: number;
  limit: number;
  totalItems: number;
}

/**
 * Generic option interface for dropdowns and select components
 */
export interface SelectOption {
  label: string;
  value: string;
  disabled?: boolean;
}

/**
 * =============================================================================
 * API RESPONSE MODELS
 * =============================================================================
 */

/**
 * Standard API response wrapper for single entities
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  error?: any;
}

/**
 * API response wrapper for paginated data
 */
export interface PagedApiResponse<T> {
  success: boolean;
  message: string;
  data: {
    content: T;
    page: number;
    limit: number;
    totalItems: number;
  };
  error?: any;
}

/**
 * =============================================================================
 * USER & AUTHENTICATION MODELS
 * =============================================================================
 */

/**
 * User role definition with hierarchical levels
 */
export interface Role {
  id: number;
  name: string;
  level: number;
  description: string;
}

/**
 * User account model
 */
export interface User extends BaseModel {
  name: string;
  email: string;
  password?: string; // Optional for security - not always returned from API
  role: Role;
}

/**
 * =============================================================================
 * EMPLOYEE MODELS
 * =============================================================================
 */

/**
 * Employee model representing staff members
 */
export interface Employee extends BaseModel {
  phone: string;
  address: string;
  position: string;
  department: number;
  user: User;
  employeeId: number;
}

/**
 * =============================================================================
 * AUTHOR MODELS
 * =============================================================================
 */

/**
 * Author model for publication creators
 */
export interface Author extends BaseModel {
  name: string;
  email: string;
  birthDate: Date;
  nationality: string;
  biography?: string;
  publicationCount: number;
}

/**
 * =============================================================================
 * PUBLICATION MODELS
 * =============================================================================
 */

/**
 * Publication types enum - categorizes different kinds of publications
 */
export enum PublicationType {
  TEXTBOOK = 'TEXTBOOK',
  NOVEL = 'NOVEL',
  BIOGRAPHY = 'BIOGRAPHY',
  SCIENCE_FICTION = 'SCIENCE_FICTION',
  RESEARCH_PAPER = 'RESEARCH_PAPER',
}

/**
 * Publication status enum - tracks publication lifecycle
 */
export enum PublicationStatus {
  DRAFT = 'DRAFT',
  IN_REVIEW = 'IN_REVIEW',
  PUBLISHED = 'PUBLISHED'
}

/**
 * Publication model representing books, journals, magazines, etc.
 */
export interface Publication extends BaseModel {
  title: string;
  isbn: string;
  description: string;
  price: number;
  summary: string;
  authorIds: number[];
  authors: Author[];
  publicationDate: Date;
  edition: string;
  pages: number;
  type: PublicationType;
  status: PublicationStatus;
}

/**
 * =============================================================================
 * UTILITY TYPES
 * =============================================================================
 */

/**
 * Generic type for form data that may or may not have an ID
 * Useful for create/update operations
 */
export type EntityFormData<T extends BaseModel> = Partial<T> & {
  id?: number;
};

/**
 * Type for entities without base model fields
 * Useful for create operations
 */
export type CreateEntityData<T extends BaseModel> = Omit<T, keyof BaseModel>;

/**
 * Type for update operations - all fields optional except ID
 */
export type UpdateEntityData<T extends BaseModel> = Partial<Omit<T, keyof BaseModel>> & {
  id: number;
};