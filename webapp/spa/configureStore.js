import { createStore, applyMiddleware } from 'redux'
import thunkMiddleware from 'redux-thunk'
//import createLogger from 'redux-logger'
import {enableBatching} from 'redux-batched-actions';
import rootReducer from './reducers'

//const loggerMiddleware = createLogger();

export default function configureStore(preloadedState) {
  return createStore(
    enableBatching(rootReducer),
    preloadedState,
    applyMiddleware(
      thunkMiddleware,
      //loggerMiddleware
    )
  )
}