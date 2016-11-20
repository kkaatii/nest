import {batchActions} from 'redux-batched-actions';
import {Graph, Editor} from './reducers/actionTypes'

const REMOTE_SERVER = document.getElementById('api').getAttribute('server');
const API_URL = REMOTE_SERVER + '/api/tube';

export const GraphActions = {
  refreshMulti: function (nodes) {
    return {
      type: Graph.REFRESH_MULTI,
      payload: {
        nodes: nodes
      }
    }
  },

  refreshOne: function (node) {
    return {
      type: Graph.REFRESH_ONE,
      payload: {
        node: node
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

  requestPost: function (posting = true) {
    return {
      type: Editor.REQUEST_POST,
      payload: {
        posting: posting
      }
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
  },

  newNode: function () {
    return {
      type: Editor.NEW_TARGET
    }
  },
};

function shouldFetchNode(point) {
  return (typeof point.content === 'undefined') || (point.content === null)
}

function fetchNode(id) {
  return dispatch => fetch(`${API_URL}/node-get?nid=${id}`)
    .then(response => response.json())
    .then(reverseFrameMapForNode)
    .then(json => dispatch(batchActions([
      EditorActions.setTarget(json),
      GraphActions.refreshOne(json),
    ])));
}

export function fetchAllPoints() {
  return dispatch => fetch(`${API_URL}/point-get-owner`)
    .then(response => response.json())
    .then(json => json.map(reverseFrameMapForNode))
    .then(json => dispatch(GraphActions.refreshMulti(json)));
}

const frameMap = (frame) => frame === "\<Private\>" ? null : frame;

const reverseFrameMap = (frame) => frame.startsWith('@') ? "\<Private\>" : frame;

const reverseFrameMapForNode = (node) => ({...node, frame: reverseFrameMap(node.frame)});

export function createOrUpdateNode(mode) {
  return (dispatch, getState) => {
    const target = getState().editor.target;
    let data = {...target, frame: frameMap(target.frame), type: 'ARTICLE'};
    dispatch(EditorActions.requestPost());
    return fetch(`${API_URL}/node-${mode}`, {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      method: 'POST',
      body: JSON.stringify(data),
    }).then(response => response.json())
      .then(json => {
        if (json === null) alert('Update failed!');
        return dispatch(GraphActions.refreshOne(reverseFrameMapForNode(json)));
      });
  }
}