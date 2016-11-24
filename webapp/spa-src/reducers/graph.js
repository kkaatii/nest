import { Graph} from './actionTypes'

const graph = (state={}, action) => {
  let newState = {...state};
  if (typeof newState.pointMap === 'undefined')
    newState.pointMap = [];
  switch (action.type) {
    case Graph.REFRESH_MULTI:
      action.payload.nodes.map((node) => newState.pointMap[node.id] = node);
      return newState;
    case Graph.REFRESH_ONE:
      newState.pointMap[action.payload.node.id] = action.payload.node;
      return newState;
    case Graph.REMOVE_ONE:
      delete newState.pointMap[action.payload.id];
      return newState;
    default:
      return state;
  }
};

export default graph;