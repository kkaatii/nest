import React, {PropTypes} from 'react'
import {displayCSS, NICKNAME} from '../constants'
import MyQuill from '../containers/MyQuill'
//import MyTinyMCE from './MyTinyMCE'

const FrameDropdownMenu = ({choices, setFrame}) => {
  let a = [], h = {};
  for (let i = 0; i < choices.length; i++) {
    let choice = choices[i].split('@');
    if (choice[1] === NICKNAME) {
      a.push(<li key={choices[i]}><a onClick={setFrame(choices[i])}>{choice[0]}</a></li>);
    } else {
      if (!h[choice[1]]) {
        h[choice[1]] = [];
        h[choice[1]].push(<li key={choice[1]} className="dropdown-header"
                              style={{fontWeight: "bold", color: "#8ad"}}>{choice[1]}</li>);
      }
      h[choice[1]].push(<li key={choices[i]}><a onClick={setFrame(choices[i])}>{choice[0]}</a></li>);
    }
  }
  Object.keys(h).map(key => a.push(h[key]));
  return <ul aria-labelledby="node-frame-select"
             className="dropdown-menu">{a}</ul>;
};

FrameDropdownMenu.propTypes = {
  choices: PropTypes.arrayOf(PropTypes.string.isRequired).isRequired,
  setFrame: PropTypes.func.isRequired,
};

const Editor = ({
  displaying, fetching, target, frameChoices, submitNode,
  handleNameChange, handleFrameChange, handleContentChange, deactivateNode,
}) =>
  <div id="node-editor" className={`container ${displayCSS(displaying, 'Editor')}`}>
    <form className="form-horizontal" onSubmit={submitNode}>
      <div className="form-group">
        <div className="col-lg-9 upper-margin">
          <label htmlFor="node-name">Title</label>
          <input type="text" className="form-control" id="node-name" placeholder={target.type}
                 onChange={handleNameChange} value={target.name}/>
        </div>
        <div className="col-lg-3 upper-margin">
          <label htmlFor="node-frame">Frame</label>
          <div className="dropdown">
            <button type="button" className="btn btn-default btn-block dropdown-toggle"
                    data-toggle="dropdown"
                    aria-haspopup="true" aria-expanded="false"
                    id="node-frame-select" style={{textAlign: "left"}}>
              {target.frame.split('@')[1] === NICKNAME ? target.frame.split('@')[0] : target.frame}
              <span className="caret" style={{
                position: "absolute",
                top: "50%",
                right: 8,
                transform: "translateY(-50%)"
              }}/></button>
            <FrameDropdownMenu choices={frameChoices} setFrame={handleFrameChange}/>
          </div>
        </div>
      </div>
      <div className="form-group">
        <div className="col-lg-12">
          <label htmlFor="node-content">Content</label>
          <MyQuill content={target.content} onChange={handleContentChange}/>
        </div>
      </div>
      <div className="btn-toolbar">
        <button className="btn btn-primary" type="submit" disabled={fetching}>Save</button>
        <button className="btn btn-danger pull-right" type="button" onClick={deactivateNode}>Delete</button>
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
  frameChoices: PropTypes.arrayOf(PropTypes.string.isRequired).isRequired,
  submitNode: PropTypes.func.isRequired,
  handleNameChange: PropTypes.func.isRequired,
  handleFrameChange: PropTypes.func.isRequired,
  handleContentChange: PropTypes.func.isRequired,
  deactivateNode: PropTypes.func.isRequired
};

export default Editor;
