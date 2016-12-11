import {batchActions} from 'redux-batched-actions';
import {Graph, Editor, Context} from './reducers/actionTypes'
import {MOCK_TARGET, DEFAULT_FRAME, REMOTE_SERVER} from "./constants";
import {viewToNodeType} from "./constants";

const TUBE_API_URL = REMOTE_SERVER + '/api/tube';
const OAF_API_URL = REMOTE_SERVER + '/api/oaf';

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
  },

  removeOne: function (id) {
    return {
      type: Graph.REMOVE_ONE,
      payload: {
        id: id
      }
    }
  },

  fetchAllPoints: function () {
    return dispatch => fetch(`${TUBE_API_URL}/point-get-owner`, {credentials: 'include'})
      .then(response => response.json())
      .then(json => dispatch(GraphActions.refreshMulti(json)));
  }
};

export const EditorActions = {

  changeTargetFrame: function (frame) {
    return {
      type: Editor.CHANGE_TARGET_FRAME,
      payload: {
        frame: frame
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

  _requestFetch: function (placeholder) {
    return {
      type: Editor.REQUEST_FETCH,
      payload: {
        placeholder: {...placeholder, content: '(Loading...)'}
      }
    }
  },

  _requestPost: function (posting = true) {
    return {
      type: Editor.REQUEST_POST,
      payload: {
        posting: posting
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

  _newNodeOfType: function (type) {
    return {
      type: Editor.SET_TARGET,
      payload: {
        target: {...MOCK_TARGET, type: type}
      }
    }
  },

  newNode: function () {
    return (dispatch, getState) => dispatch(EditorActions._newNodeOfType(viewToNodeType(getState().context.view)));
  },

  setFrameChoices: function (frameChoices) {
    return {
      type: Editor.SET_FRAMECHOICES,
      payload: {
        frameChoices: frameChoices
      }
    }
  },

  fetchAndSetTarget: function (target) {
    return (dispatch) => {
      if (_shouldFetchNode(target)) {
        dispatch(EditorActions._requestFetch(target));
        return dispatch(_fetchNode(target.id));
      } else {
        return dispatch(EditorActions.setTarget(target));
      }
    }
  },

  deactivateNode: function () {
    return (dispatch, getState) => {
      let id = getState().editor.target.id;
      if (id !== null)
        fetch(`${TUBE_API_URL}/node-activate?nid=${id}&a=false`, {credentials: 'include', method: 'POST'})
          .then(response => response.text())
          .then(text => text === 'Success' ? dispatch(GraphActions.removeOne(id)) : {});
    }
  },

  createOrUpdateNode: function (mode) {
    return (dispatch, getState) => {
      dispatch(EditorActions._requestPost());
      return fetch(`${TUBE_API_URL}/node-${mode}`, {
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(getState().editor.target),
      }).then(response => response.json())
        .then(json => json === null ? (alert('Update failed!') || null) : dispatch(GraphActions.refreshOne(json))
        );
    }
  },

  fetchFrameChoices: function () {
    return dispatch => fetch(`${OAF_API_URL}/frames-readable`, {credentials: 'include'})
      .then(response => response.json())
      .then(json => dispatch(EditorActions.setFrameChoices(json)));
  }

};

export const ContextActions = {
  setView: function (view) {
    return {
      type: Context.SET_VIEW,
      payload: {
        view: view
      }
    }
  }
};

function _shouldFetchNode(point) {
  return (typeof point.content === 'undefined') || (point.content === null)
}

function _fetchNode(id) {
  return dispatch => fetch(`${TUBE_API_URL}/node-get?nid=${id}`, {credentials: 'include'})
    .then(response => response.json())
    .then(json => dispatch(batchActions([
      EditorActions.setTarget(json),
      GraphActions.refreshOne(json),
    ])));
}