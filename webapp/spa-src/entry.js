import React from 'react';
import ReactDOM from 'react-dom';
import TinyMCE from 'react-tinymce';
import 'bootstrap/dist/js/bootstrap.min';

const REMOTE_SERVER = document.getElementById('api').getAttribute('server');
const API_URL = REMOTE_SERVER + '/api';

class FrameSelect extends React.Component {
  render() {
    let self = this;
    let h = [];
    let options = this.props.options;
    let lastHeader = '';
    for (let i = 0; i < options.length; i++) {
      let option = options[i].split('@');
      if (option[1] && option[1] !== lastHeader) {
        lastHeader = option[1];
        h.push(<li key={-i} className="dropdown-header" style={{fontWeight: "bold", color: "#8ad"}}>{option[1]}</li>);
      }
      h.push(<li key={i}><a href="#" onClick={self.props.display(i)}>{option[0]}</a></li>);
    }
    return <ul aria-labelledby="node-frame-select"
               className="dropdown-menu">{h}</ul>;
  }
}

class App extends React.Component {
  constructor() {
    super();
    this.state = {
      editor: {
        name: "",
        content: "",
        frameoptions: ["\<Private\>", "BeTrue@Dun", "Ohters@Dun", "Nonsense@John Doe"],
        frame: "\<Private\>"
      }
    };
    this.handleEditorChange = this.handleEditorChange.bind(this);
    this.handleEditorSubmit = this.handleEditorSubmit.bind(this);
    this.handleNameChange = this.handleNameChange.bind(this);
    this.changeDropdownDisplay = this.changeDropdownDisplay.bind(this);
  }

  handleEditorChange(e) {
    let state = this.state;
    state.editor.content = e.target.getContent();
    this.setState(state)
  }

  handleNameChange(e) {
    let state = this.state;
    state.editor.name = e.target.value;
    this.setState(state);
  }

  handleEditorSubmit(e) {
    console.log(this.state.editor.content);
    e.preventDefault();
    let frameMap = (frame) => {
      switch (frame) {
        case "\<Private\>":
          return null;
        default:
          return frame;
      }
    };
    let node = {
      name: this.state.editor.name,
      frame: frameMap(this.state.editor.frame),
      content: this.state.editor.content,
      type: 'ARTICLE'
    };
    let xhr = new XMLHttpRequest();
    xhr.open('POST', API_URL + '/tube/node-create', true);
    xhr.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
    xhr.onload = function () {
      if (xhr.status === 200) {
        console.log(xhr.responseText);
      }
    };
    xhr.send(JSON.stringify(node));
  }

  changeDropdownDisplay(i) {
    let state = this.state;
    let self = this;
    return function () {
      state.editor.frame = state.editor.frameoptions[i];
      self.setState(state);
    }
  }

  render() {
    return (
      <div>
        <div className="container upper-margin">
          <a href="/"><img src="/img/logo.png" height={40}/></a>
        </div>
        <hr/>
        <div className="container">
          <form className="form-horizontal" onSubmit={this.handleEditorSubmit} style={{marginTop: 20}}>
            <div className="form-group">
              <div className="col-lg-9 upper-margin">
                <label htmlFor="node-name">Title</label>
                <input type="text" className="form-control" id="node-name" placeholder="Article"
                       onChange={this.handleNameChange} value={this.state.editor.name}/>
              </div>
              <div className="col-lg-3 upper-margin">
                <label htmlFor="node-frame">Frame</label>
                <div className="dropdown">
                  <button type="button" className="btn btn-default btn-block dropdown-toggle"
                          data-toggle="dropdown"
                          aria-haspopup="true" aria-expanded="false"
                          id="node-frame-select" style={{textAlign: "left"}}>{this.state.editor.frame.split('@')[0]}
                    <span className="caret" style={{
                      position: "absolute",
                      top: "50%",
                      right: 8,
                      transform: "translateY(-50%)"
                    }}/></button>
                  <FrameSelect options={this.state.editor.frameoptions} display={this.changeDropdownDisplay}/>
                </div>
              </div>
            </div>
            <div className="form-group">
              <div className="col-lg-12">
                <label htmlFor="node-content">Content</label>
                <TinyMCE
                  id='node-content'
                  content={this.state.editor.content}
                  config={{
                    selector: '#node-content',
                    height: '40rem',
                    content_style: 'body.mce-content-body {font-size:14px}'
                  }}
                  onChange={this.handleEditorChange}
                />
              </div>
            </div>
            <div className="btn-toolbar">
              <button className="btn btn-primary" type="submit">Submit</button>
              <button className="btn btn-default" type="button" disabled="true">Save draft</button>
              <button className="btn btn-danger pull-right" type="button">Discard</button>
            </div>
          </form>
        </div>
      </div>
    )
  }
}

ReactDOM.render(
  <App />,
  document.getElementById('root')
);