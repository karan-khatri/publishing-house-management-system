export const ROLES = {
  ADMIN: 1,
  MANAGER: 2,
  EDITOR: 3,
  AUTHOR: 4,
  REVIEWER: 5,
  USER: 6
} as const;

export const ROLE_NAMES = {
  [ROLES.ADMIN]: 'ADMIN',
  [ROLES.MANAGER]: 'MANAGER',
  [ROLES.EDITOR]: 'EDITOR',
  [ROLES.AUTHOR]: 'AUTHOR',
  [ROLES.REVIEWER]: 'REVIEWER',
  [ROLES.USER]: 'USER'
} as const;

export const ROLE_DESCRIPTIONS = {
  [ROLES.ADMIN]: 'System administrator with full access to all features and user management',
  [ROLES.MANAGER]: 'Publishing house manager with access to most features except system administration',
  [ROLES.EDITOR]: 'Content editor responsible for reviewing and editing manuscripts',
  [ROLES.AUTHOR]: 'Author who can submit and manage their own manuscripts',
  [ROLES.REVIEWER]: 'External reviewer who can review assigned manuscripts',
  [ROLES.USER]: 'Basic user with limited read access'
} as const;

export type RoleId = typeof ROLES[keyof typeof ROLES];
export type RoleName = typeof ROLE_NAMES[RoleId];
