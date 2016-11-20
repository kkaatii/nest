import React, {PropTypes} from 'react';
import {connect} from 'react-redux'
import Editor from '../components/Editor';
import Graph from '../components/Graph';
import {EditorActions, fetchAllPoints, createOrUpdateNode} from '../actions'

const PageShader = ({displaying, hide}) => (displaying ?
  (<div style={{
    backgroundColor: "#aaaaaa", opacity: 0.5, zIndex: 100,
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%'
  }} /*onClick={hide}*//>)
  : null);

PageShader.propTypes = {
  displaying: PropTypes.bool.isRequired
};

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      displayingEditor: false,
      editorMode: 'create',
    };
    this.toggleEditorDisplay = this.toggleEditorDisplay.bind(this);
    this.newNodeForEdit = this.newNodeForEdit.bind(this);
    this.hideEditor = this.hideEditor.bind(this);
    this.chooseNodeForEdit = this.chooseNodeForEdit.bind(this);
    this.handleEditorContentChange = this.handleEditorContentChange.bind(this);
    this.handleEditorFrameChange = this.handleEditorFrameChange.bind(this);
    this.handleEditorNameChange = this.handleEditorNameChange.bind(this);
    this.submitEditorNodeIn = this.submitEditorNodeIn.bind(this);
  };

  componentDidMount() {
    this.props.dispatch(fetchAllPoints());
  }

  chooseNodeForEdit(id) {
    const {dispatch, graph} = this.props;
    this.setState({
      displayingEditor: true,
      editorMode: 'update',
    });
    dispatch(EditorActions.fetchAndSetTarget(graph.pointMap[id]));
  }

  toggleEditorDisplay() {
    this.setState({
      displayingEditor: !this.state.displayingEditor,
    })
  }

  newNodeForEdit() {
    this.props.dispatch(EditorActions.newNode());
    this.setState({
      displayingEditor: true,
      editorMode: 'create',
    })
  }

  hideEditor() {
    this.setState({
      displayingEditor: false,
    });
  }

  handleEditorContentChange(e) {
    this.props.dispatch(EditorActions.changeTargetContent(e.target.getContent()));
  }

  handleEditorNameChange(e) {
    this.props.dispatch(EditorActions.changeTargetName(e.target.value));
  }

  handleEditorFrameChange(i) {
    return () => this.props.dispatch(EditorActions.changeTargetFrame(i));
  }

  submitEditorNodeIn(mode) {
    return (e) => {
      e.preventDefault();
      this.props.dispatch(createOrUpdateNode(mode));
      this.hideEditor();
    }
  }

  render() {
    const {editor, graph} = this.props;
    let displayingStyle = (displaying) => {
      if (displaying)
        return {
          position: 'fixed',
          display: 'block',
          zIndex: 101,
          top: '51px',
          left: '50%',
          transform: 'translateX(-50%)'
        };
      else return {display: 'none'};
    };
    return (
      <div>
        <div className="tube-nav">
          <div className="container-fluid">
            <a /*onClick={this.toggleEditorDisplay}*/>
              <img src="/img/logo.png" height={38} style={{margin: "6px 0 6px"}}/>
            </a>
            <div className="btn btn-default pull-right" style={{marginTop:8}} onClick={this.newNodeForEdit}>New</div>
          </div>
        </div>

        <hr className="nav-divider"/>

        <Graph graph={graph} chooseNodeForEdit={this.chooseNodeForEdit}/>

        <PageShader displaying={this.state.displayingEditor} hide={this.hideEditor}/>
        <div id="node-editor-wrapper" style={displayingStyle(this.state.displayingEditor)}>
          <Editor
            fetching={editor.fetching}
            target={editor.target}
            hide={this.hideEditor}
            frameChoices={editor.frameChoices}
            handleContentChange={this.handleEditorContentChange}
            handleFrameChange={this.handleEditorFrameChange}
            handleNameChange={this.handleEditorNameChange}
            submitNode={this.submitEditorNodeIn(this.state.editorMode)}
          />
        </div>
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