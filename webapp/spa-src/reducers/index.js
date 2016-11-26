import { combineReducers } from 'redux'
import editor from './editor'
import graph from './graph'
import context from './context'

const rootReducer = combineReducers({
  editor,
  graph,
  context,
});

export default rootReducer;