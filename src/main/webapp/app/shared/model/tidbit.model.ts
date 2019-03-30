export interface ITidbit {
  id?: number;
  content?: string;
  author?: string;
  source?: string;
  url?: string;
  categoryName?: string;
  categoryId?: number;
}

export const defaultValue: Readonly<ITidbit> = {};
