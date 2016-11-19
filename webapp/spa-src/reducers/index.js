import { combineReducers } from 'redux'
import editor from './editor'
import graph from './graph'

const rootReducer = combineReducers({
  editor,
  graph
});

export default rootReducer;