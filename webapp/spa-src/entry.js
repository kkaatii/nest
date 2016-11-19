import React from 'react';
import ReactDOM from 'react-dom';
import 'bootstrap/dist/js/bootstrap.min';
require('../public/css/tube.css');

import NodeEditor from './nodeeditor';
import PointGraph from './pointgraph';

const REMOTE_SERVER = document.getElementById('api').getAttribute('server');
const API_URL = REMOTE_SERVER + '/api/tube';

class PageShader extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    return this.props.displaying ? (<div style={{
      backgroundColor: "#aaaaaa", opacity: 0.5, zIndex: 100,
      position: 'fixed',
      top: 0,
      left: 0,
      width: '100%',
      height: '100%'
    }}/>) : null;
  }
}

class App extends React.Component {
  constructor() {
    super();
    this.state = {
      editor: {
        editing: {
          id: null,
          name: "",
          content: "",
          frameoptions: ["\<Private\>", "BeTrue@Dun", "Ohters@Dun", "Nonsense@John Doe"],
          frame: "\<Private\>",
          updateMode: false
        },
        displaying: false
      },
      points: []
    };
    let xhr = new XMLHttpRequest();
    xhr.open('GET', API_URL + '/point-get-owner', true);
    xhr.onload = function () {
      if (xhr.status === 200) {
        let state = this.state;
        state['points'] = JSON.parse(xhr.responseText);
        this.setState(state);
      }
    }.bind(this);
    xhr.send();
    this.toggleEditorDisplay = this.toggleEditorDisplay.bind(this);
    this.chooseNodeForEdit = this.chooseNodeForEdit.bind(this);
  }

  toggleEditorDisplay() {
    let state = this.state;
    state.editor.displaying = !state.editor.displaying;
    this.setState(state);
  }

  chooseNodeForEdit(id) {
    let xhr = new XMLHttpRequest();
    xhr.open('GET', API_URL + '/node-get?nid=' + id, true);
    xhr.onload = function () {
      if (xhr.status === 200) {
        let node = JSON.parse(xhr.responseText);
        let state = this.state;
        state.editor.editing.id = node.id;
        state.editor.editing.name = node.name;
        state.editor.editing.content = node.content;
        state.editor.editing.frame = node.frame.startsWith('@') ? "\<Private\>" : node.frame;
        state.editor.editing.updateMode = true;
        state.editor.displaying = true;
        this.setState(state);
      }
    }.bind(this);
    xhr.send();
  }

  render() {
    let displayingStyle = (displaying) => {
      if (displaying)
        return {
          position: 'fixed',
          display: 'block',
          zIndex: 101,
          top:'3.5em',
          left: '50%',
          transform: 'translateX(-50%)'
        };
      else return {display: 'none'};
    };
    return (
      <div>
        <div className="tube-nav">
          <div className="container-fluid">
            <a onClick={this.toggleEditorDisplay}><img src="/img/logo.png" height={40} className="upper-margin"/></a>
          </div>
          <hr className="nav-divider"/>
        </div>

        <PointGraph points={this.state.points} apiUrl={API_URL} chooseNodeForEdit={this.chooseNodeForEdit}/>

        <div id="node-editor-wrapper" style={displayingStyle(this.state.editor.displaying)}>
          <NodeEditor editing={this.state.editor.editing} apiUrl={API_URL}/>
        </div>
        <PageShader displaying={this.state.editor.displaying}/>
      </div>
    )
  }
}

ReactDOM.render(
  <App />,
  document.getElementById('root')
);