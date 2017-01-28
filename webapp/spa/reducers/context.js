import {Context} from './actionTypes'

const context = (state = {}, action) => {
  switch (action.type) {
    case Context.SET_VIEW:
      return {...state, view: action.payload.view};
    default:
      return state;
  }
};

export default context;