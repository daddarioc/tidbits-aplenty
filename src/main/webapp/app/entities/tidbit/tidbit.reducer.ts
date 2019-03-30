import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { ITidbit, defaultValue } from 'app/shared/model/tidbit.model';

export const ACTION_TYPES = {
  FETCH_TIDBIT_LIST: 'tidbit/FETCH_TIDBIT_LIST',
  FETCH_TIDBIT: 'tidbit/FETCH_TIDBIT',
  CREATE_TIDBIT: 'tidbit/CREATE_TIDBIT',
  UPDATE_TIDBIT: 'tidbit/UPDATE_TIDBIT',
  DELETE_TIDBIT: 'tidbit/DELETE_TIDBIT',
  RESET: 'tidbit/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ITidbit>,
  entity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type TidbitState = Readonly<typeof initialState>;

// Reducer

export default (state: TidbitState = initialState, action): TidbitState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_TIDBIT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_TIDBIT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_TIDBIT):
    case REQUEST(ACTION_TYPES.UPDATE_TIDBIT):
    case REQUEST(ACTION_TYPES.DELETE_TIDBIT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_TIDBIT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_TIDBIT):
    case FAILURE(ACTION_TYPES.CREATE_TIDBIT):
    case FAILURE(ACTION_TYPES.UPDATE_TIDBIT):
    case FAILURE(ACTION_TYPES.DELETE_TIDBIT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_TIDBIT_LIST):
      return {
        ...state,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_TIDBIT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_TIDBIT):
    case SUCCESS(ACTION_TYPES.UPDATE_TIDBIT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_TIDBIT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/tidbits';

// Actions

export const getEntities: ICrudGetAllAction<ITidbit> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_TIDBIT_LIST,
    payload: axios.get<ITidbit>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<ITidbit> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_TIDBIT,
    payload: axios.get<ITidbit>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<ITidbit> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_TIDBIT,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<ITidbit> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_TIDBIT,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  dispatch(getEntities());
  return result;
};

export const deleteEntity: ICrudDeleteAction<ITidbit> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_TIDBIT,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
