import {Editor} from './actionTypes'

const editorTarget = (state = {}, action)=> {
  switch (action.type) {
    case Editor.CHANGE_TARGET_CONTENT:
      return {...state, content: action.payload.content};
    case Editor.CHANGE_TARGET_FRAME:
      return {...state, frame: action.payload.frame};
    case Editor.CHANGE_TARGET_NAME:
      return {...state, name: action.payload.name};
    default:
      return state;
  }
};

const editor = (state = {}, action) => {
  switch (action.type) {
    case Editor.SHOW_EDITOR:
      return {...state, displaying: true};
    case Editor.HIDE_EDITOR:
      return {...state, displaying: false};
    case Editor.TOGGLE_EDITOR_DISPLAY:
      return {...state, displaying: !state.displaying};
    case Editor.SET_EDITOR_MODE:
      return {...state, editorMode: action.payload.editorMode};
    case Editor.SET_TARGET:
      return {...state, target: action.payload.target};
    case Editor.SET_TARGET_AND_SHOW_EDITOR:
      return {...state, target: action.payload.target, displaying:true};
    case Editor.SET_FRAMECHOICES:
      return {...state, frameChoices: action.payload.frameChoices};
    case Editor.CHANGE_TARGET_CONTENT:
    case Editor.CHANGE_TARGET_FRAME:
    case Editor.CHANGE_TARGET_NAME:
      return {...state, target: editorTarget(state.target, action)};
    default:
      return state
  }
};

export default editor;