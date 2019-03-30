import { ITidbit } from 'app/shared/model/tidbit.model';

export interface ICategory {
  id?: number;
  name?: string;
  tidbit?: ITidbit[];
}

export const defaultValue: Readonly<ICategory> = {};
