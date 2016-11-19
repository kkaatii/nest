import 'babel-polyfill'

import React from 'react'
import {render} from 'react-dom'
import {Provider} from 'react-redux'
import App from './containers/App'
import configureStore from './configureStore'
import 'bootstrap/dist/js/bootstrap.min';
require('../public/css/tube.css');

let store = configureStore({
  graph: {pointMap: {}},
  editor: {
    frameChoices: ["\<Private\>", "BeTrue@Dun", "Ohters@Dun", "Nonsense@John Doe"],
    displaying: false,
    target: {
      name: "",
      content: "",
      frame: "\<Private\>",
    },
  }
});

render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
);