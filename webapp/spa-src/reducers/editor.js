import {Editor} from './actionTypes'

const editorTarget = (state = {}, action)=> {
  switch (action.type) {
    case Editor.CHANGE_TARGET_FRAME:
      return {...state, frame: action.payload.frame};
    case Editor.CHANGE_TARGET_CONTENT:
      return {...state, content: action.payload.content};
    case Editor.CHANGE_TARGET_NAME:
      return {...state, name: action.payload.name};
    default:
      return state;
  }
};

const editor = (state = {}, action) => {
  switch (action.type) {
    case Editor.REQUEST_FETCH:
      return {...state, fetching: true, target: action.payload.placeholder};
    case Editor.REQUEST_POST:
      return {...state, posting: action.payload.posting};
    case Editor.SET_TARGET:
      return {...state, target: action.payload.target, fetching: false};
    case Editor.SET_FRAMECHOICES:
      return {...state, frameChoices: action.payload.frameChoices};
    case Editor.CHANGE_TARGET_FRAME:
    case Editor.CHANGE_TARGET_CONTENT:
    case Editor.CHANGE_TARGET_NAME:
      return {...state, target: editorTarget(state.target, action)};
    default:
      return state;
  }
};

export default editor;