import React, {PropTypes} from 'react';
import {connect} from 'react-redux'
import Editor from '../components/Editor';
import Graph from '../components/Graph';
import {EditorActions, fetchAllPoints} from '../actions'

const PageShader = ({displaying, hide}) => (displaying ?
  (<div style={{
    backgroundColor: "#aaaaaa", opacity: 0.5, zIndex: 100,
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%'
  }} onClick={hide}/>)
  : null);

PageShader.propTypes = {
  displaying: PropTypes.bool.isRequired
};

class App extends React.Component {
  constructor(props) {
    super(props);
    this.toggleEditorDisplay = this.toggleEditorDisplay.bind(this);
    this.hideEditor = this.hideEditor.bind(this);
    this.chooseNodeForEdit = this.chooseNodeForEdit.bind(this);
    this.handleEditorContentChange = this.handleEditorContentChange.bind(this);
    this.handleEditorFrameChange = this.handleEditorFrameChange.bind(this);
    this.handleEditorNameChange = this.handleEditorNameChange.bind(this);
    this.submitEditorNode = this.submitEditorNode.bind(this);
  };

  componentDidMount() {
    this.props.dispatch(fetchAllPoints());
  }

  chooseNodeForEdit(id) {
    const {dispatch, graph} = this.props;
    dispatch(EditorActions.setMode('update'));
    dispatch(EditorActions.setTargetAndShow(graph.pointMap[id]));
  }

  toggleEditorDisplay() {
    this.props.dispatch(EditorActions.setMode('create'));
    this.props.dispatch(EditorActions.toggleDisplay());
  }

  hideEditor() {
    this.props.dispatch(EditorActions.hide());
  }

  handleEditorContentChange() {

  }

  handleEditorNameChange() {

  }

  handleEditorFrameChange() {

  }

  submitEditorNode() {

  }

  render() {
    const {editor, graph} = this.props;
    let displayingStyle = (displaying) => {
      if (displaying)
        return {
          position: 'fixed',
          display: 'block',
          zIndex: 101,
          top: '3.5em',
          left: '50%',
          transform: 'translateX(-50%)'
        };
      else return {display: 'none'};
    };
    return (
      <div>
        <div className="tube-nav">
          <div className="container-fluid">
            <a onClick={this.toggleEditorDisplay}><img src="/img/logo.png" height={38}
                                                       style={{margin: "6px 0 6px"}}/></a>
          </div>
        </div>

        <hr className="nav-divider"/>

        <Graph graph={graph} chooseNodeForEdit={this.chooseNodeForEdit}/>

        <div id="node-editor-wrapper" style={displayingStyle(editor.displaying)}>
          <Editor
            target={editor.target}
            frameChoices={editor.frameChoices}
            handleContentChange={this.handleEditorContentChange}
            handleFrameChange={this.handleEditorFrameChange}
            handleNameChange={this.handleEditorNameChange}
            submitNode={this.submitEditorNode}
          />
        </div>
        <PageShader displaying={editor.displaying} hide={this.hideEditor}/>
      </div>
    )
  }
}

App.propTypes = {
  editor: PropTypes.object.isRequired,
  graph: PropTypes.object.isRequired,
};

function mapStateToProps(state) {
  const {editor, graph} = state;

  return {
    editor, graph
  }
}

export default connect(mapStateToProps)(App);