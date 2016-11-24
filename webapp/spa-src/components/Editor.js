import React, {PropTypes} from 'react'
import TinyMCE from 'react-tinymce'
import isEqual from 'lodash/isEqual'
import {displayCSS} from '../constants'
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

const FrameDropdownMenu = ({choices, display}) => {
  let h = [];
  let lastHeader = '';
  for (let i = 0; i < choices.length; i++) {
    let choice = choices[i].split('@');
    if (choice[1] && choice[1] !== lastHeader) {
      lastHeader = choice[1];
      h.push(<li key={-i} className="dropdown-header" style={{fontWeight: "bold", color: "#8ad"}}>{choice[1]}</li>);
    }
    h.push(<li key={i}><a href="#" onClick={display(i)}>{choice[0]}</a></li>);
  }
  return <ul aria-labelledby="node-frame-select"
             className="dropdown-menu">{h}</ul>;
};

FrameDropdownMenu.propTypes = {
  choices: PropTypes.arrayOf(PropTypes.string.isRequired).isRequired,
  display: PropTypes.func.isRequired,
};


const Editor = ({displaying, fetching, target, frameChoices, hide, submitNode, handleNameChange, handleFrameChange, handleContentChange}) =>
  <div id="node-editor" className={`container ${displayCSS(displaying, 'Editor')}`}>
    <p className="text-center"><span className="glyphicon glyphicon-chevron-up" aria-hidden="true"
                                     style={{cursor: 'pointer', marginBottom: 6}} onClick={hide}/></p>
    <form className="form-horizontal" onSubmit={submitNode}>
      <div className="form-group">
        <div className="col-lg-9 upper-margin">
          <label htmlFor="node-name">Title</label>
          <input type="text" className="form-control" id="node-name" placeholder="Article"
                 onChange={handleNameChange} value={target.name}/>
        </div>
        <div className="col-lg-3 upper-margin">
          <label htmlFor="node-frame">Frame</label>
          <div className="dropdown">
            <button type="button" className="btn btn-default btn-block dropdown-toggle"
                    data-toggle="dropdown"
                    aria-haspopup="true" aria-expanded="false"
                    id="node-frame-select" style={{textAlign: "left"}}>{target.frame}
              <span className="caret" style={{
                position: "absolute",
                top: "50%",
                right: 8,
                transform: "translateY(-50%)"
              }}/></button>
            <FrameDropdownMenu choices={frameChoices} display={handleFrameChange}/>
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
              //height: '40rem',
              content_style: 'body.mce-content-body {font-size:14px}',
            }}
            content={target.content}
            onChange={handleContentChange}
          />
        </div>
      </div>
      <div className="btn-toolbar">
        <button className="btn btn-primary" type="submit" disabled={fetching}>Submit</button>
        <button className="btn btn-default" type="button" disabled="true">Save draft</button>
        <button className="btn btn-danger pull-right" type="button" disabled="true">Delete</button>
      </div>
    </form>
  </div>;

Editor.propTypes = {
  displaying: PropTypes.string.isRequired,
  fetching: PropTypes.bool.isRequired,
  target: PropTypes.shape({
    id: PropTypes.number,
    content: PropTypes.string.isRequired,
    frame: PropTypes.string.isRequired,
    name: PropTypes.string.isRequired,
    updated: PropTypes.number,
    created: PropTypes.number,
    owner: PropTypes.string,
    digest: PropTypes.string,
    active: PropTypes.bool
  }),
  hide: PropTypes.func.isRequired,
  frameChoices: PropTypes.arrayOf(PropTypes.string.isRequired).isRequired,
  submitNode: PropTypes.func.isRequired,
  handleNameChange: PropTypes.func.isRequired,
  handleFrameChange: PropTypes.func.isRequired,
  handleContentChange: PropTypes.func.isRequired,
};

export default Editor;
