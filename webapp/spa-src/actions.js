import {batchActions} from 'redux-batched-actions';
import {Graph, Editor} from './reducers/actionTypes'

const REMOTE_SERVER = document.getElementById('api').getAttribute('server');
const API_URL = REMOTE_SERVER + '/api/tube';

export const GraphActions = {
  refreshMulti: function (json) {
    return {
      type: Graph.REFRESH_MULTI,
      payload: {
        nodes: json
      }
    }
  },

  refreshOne: function (json) {
    return {
      type: Graph.REFRESH_ONE,
      payload: {
        node: json
      }
    }
  }
};

export const EditorActions = {

  toggleDisplay: function () {
    return {
      type: Editor.TOGGLE_EDITOR_DISPLAY,
    }
  },

  setMode: function (mode) {
    return {
      type: Editor.SET_EDITOR_MODE,
      payload: {
        editorMode: mode
      }
    }
  },

  show: function () {
    return {
      type: Editor.SHOW_EDITOR,
    }
  },

  hide: function () {
    return {
      type: Editor.HIDE_EDITOR,
    }
  },

  setTargetAndShow: function (target) {
    return (dispatch) => {
      if (shouldFetchNode(target)) {
        return dispatch(fetchNode(target.id));
      } else {
        return dispatch(batchActions([EditorActions.setTarget(target), EditorActions.show()]))
      }
    }
  },

  setTarget: function (target) {
    return {
      type: Editor.SET_TARGET,
      payload: {
        target: target
      }
    }
  }
};

function shouldFetchNode(point) {
  return (typeof point.content === 'undefined') || (point.content === null)
}

function fetchNode(id) {
  return dispatch => fetch(`${API_URL}/node-get?nid=${id}`)
    .then(response => response.json())
    .then(json => dispatch(batchActions([
      EditorActions.setTarget({
        ...json,
        frame: json.frame.startsWith('@') ? "\<Private\>" : json.frame
      }),
      EditorActions.show(),
      GraphActions.refreshOne(json)
    ])));
}

export function fetchAllPoints() {
  return dispatch => fetch(`${API_URL}/point-get-owner`)
    .then(response => response.json())
    .then(json => dispatch(GraphActions.refreshMulti(json)));
}