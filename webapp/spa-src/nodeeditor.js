import React from 'react';
import TinyMCE from 'react-tinymce';
import isEqual from 'lodash/isEqual';
var tinymce = require('tinymce');

class MyTinyMCE extends TinyMCE {
  componentWillReceiveProps(nextProps) {
    if (!isEqual(this.props.config, nextProps.config)) {
      this._init(nextProps.config, nextProps.content)
    }
    if (!isEqual(this.props.id, nextProps.id)) {
      this.id = nextProps.id
    }
    // Added
    const editor = tinymce.EditorManager.get(this.id);
    if (editor && !isEqual(editor.getContent(), nextProps.content)) {
      tinymce.EditorManager.get(this.id).setContent(nextProps.content)
    }
  }
}

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

class NodeEditor extends React.Component {
  constructor(props) {
    super(props);
    this.state = this.props.editing;
    this.API_URL = this.props.apiUrl;
    this.handleEditorChange = this.handleEditorChange.bind(this);
    this.handleEditorSubmit = this.handleEditorSubmit.bind(this);
    this.handleNameChange = this.handleNameChange.bind(this);
    this.changeDropdownDisplay = this.changeDropdownDisplay.bind(this);
  }

  componentWillReceiveProps(nextProps) {
    if(this.state.id !== nextProps.editing.id) {
      this.state = nextProps.editing;
    }
  }

  handleEditorChange(e) {
    let state = this.state;
    state.content = e.target.getContent();
    this.setState(state)
  }

  handleNameChange(e) {
    let state = this.state;
    state.name = e.target.value;
    this.setState(state);
  }

  handleEditorSubmit(e) {
    console.log(this.state.content);
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
      name: this.state.name,
      frame: frameMap(this.state.frame),
      content: this.state.content,
      type: 'ARTICLE'
    };
    let xhr = new XMLHttpRequest();
    xhr.open('POST', this.API_URL + (this.state.updateMode ? '/node-update' : '/node-create'), true);
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
      state.frame = state.frameoptions[i];
      self.setState(state);
    }
  }

  render() {
    return (
      <div id="node-editor" className="container"
           style={{backgroundColor: 'white', marginTop: '1.5em', paddingTop: '0.5em', paddingBottom: '1em'}}>
        <form className="form-horizontal" onSubmit={this.handleEditorSubmit}>
          <div className="form-group">
            <div className="col-lg-9 upper-margin">
              <label htmlFor="node-name">Title</label>
              <input type="text" className="form-control" id="node-name" placeholder="Article"
                     onChange={this.handleNameChange} value={this.state.name}/>
            </div>
            <div className="col-lg-3 upper-margin">
              <label htmlFor="node-frame">Frame</label>
              <div className="dropdown">
                <button type="button" className="btn btn-default btn-block dropdown-toggle"
                        data-toggle="dropdown"
                        aria-haspopup="true" aria-expanded="false"
                        id="node-frame-select" style={{textAlign: "left"}}>{this.state.frame.split('@')[0]}
                  <span className="caret" style={{
                    position: "absolute",
                    top: "50%",
                    right: 8,
                    transform: "translateY(-50%)"
                  }}/></button>
                <FrameSelect options={this.state.frameoptions} display={this.changeDropdownDisplay}/>
              </div>
            </div>
          </div>
          <div className="form-group">
            <div className="col-lg-12">
              <label htmlFor="node-content">Content</label>
              <MyTinyMCE
                id='node-content'
                config={{
                  selector: '#node-content',
                  height: '40rem',
                  content_style: 'body.mce-content-body {font-size:14px}',
                }}
                content={this.state.content}
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
    )
  }
}

export default NodeEditor;