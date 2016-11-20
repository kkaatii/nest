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

  changeTargetFrame: function (frameNo) {
    return {
      type: Editor.CHANGE_TARGET_FRAME,
      payload: {
        frameNo: frameNo
      }
    }
  },
  changeTargetName: function (name) {
    return {
      type: Editor.CHANGE_TARGET_NAME,
      payload: {
        name: name
      }
    }
  },
  changeTargetContent: function (content) {
    return {
      type: Editor.CHANGE_TARGET_CONTENT,
      payload: {
        content: content
      }
    }
  },

  requestFetch: function () {
    return {
      type: Editor.REQUEST_FETCH,
    }
  },

  fetchAndSetTarget: function (target) {
    return (dispatch) => {
      if (shouldFetchNode(target)) {
        dispatch(EditorActions.requestFetch());
        return dispatch(fetchNode(target.id));
      } else {
        return dispatch(EditorActions.setTarget(target));
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
        frame: reverseFrameMap(json.frame)
      }),
      GraphActions.refreshOne(json)
    ])));
}

export function fetchAllPoints() {
  return dispatch => fetch(`${API_URL}/point-get-owner`)
    .then(response => response.json())
    .then(json => dispatch(GraphActions.refreshMulti(json)));
}

const frameMap = (frame) => frame === "\<Private\>" ? null : frame;

const reverseFrameMap = (frame) => frame.startsWith('@') ? "\<Private\>" : frame;

export const MOCK_TARGET = {
  id: null,
  name: "",
  content: "",
  frame: "\<Private\>",
};

export function createOrUpdateNode(mode) {
  return (dispatch, getState) => {
    const target = getState().editor.target;
    let data = {...target, frame: frameMap(target.frame), type: 'ARTICLE'};
    return fetch(`${API_URL}/node-${mode}`, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      method: 'POST',
      body: data,
    }).then(response => console.log(response.json()));
  }
}