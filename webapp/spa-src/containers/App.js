import React, {PropTypes} from 'react'
import {connect} from 'react-redux'
import Editor from '../components/Editor'
import Viewer from '../components/Viewer'
import PageShader from '../components/PageShader'
import Graph from '../components/Graph'
import {EditorActions, fetchAllPoints, createOrUpdateNode, deactivateNode} from '../actions'

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      displaying: '',
      editorMode: 'create',
    };
    this.toggleEditorDisplay = this.toggleEditorDisplay.bind(this);
    this.newNodeForEdit = this.newNodeForEdit.bind(this);
    this.hideEditor = this.hideEditor.bind(this);
    this.hideViewer = this.hideViewer.bind(this);
    this.chooseNodeForEdit = this.chooseNodeForEdit.bind(this);
    this.chooseNodeForView = this.chooseNodeForView.bind(this);
    this.handleEditorContentChange = this.handleEditorContentChange.bind(this);
    this.handleEditorFrameChange = this.handleEditorFrameChange.bind(this);
    this.handleEditorNameChange = this.handleEditorNameChange.bind(this);
    this.submitEditorNodeIn = this.submitEditorNodeIn.bind(this);
    this.deactivateEditorTarget = this.deactivateEditorTarget.bind(this);
  };

  componentDidMount() {
    this.props.dispatch(fetchAllPoints());
  }

  chooseNodeForEdit(id) {
    const {dispatch, graph} = this.props;
    this.setState({
      displaying: 'Editor',
      editorMode: 'update',
    });
    dispatch(EditorActions.fetchAndSetTarget(graph.pointMap[id]));
  }

  chooseNodeForView(id) {
    const {dispatch, graph} = this.props;
    this.setState({
      displaying: 'Viewer',
    });
    dispatch(EditorActions.fetchAndSetTarget(graph.pointMap[id]));
  }

  toggleEditorDisplay() {
    this.setState({
      displaying: this.state.displaying === 'Editor' ? '' : 'Editor',
    })
  }

  newNodeForEdit() {
    this.props.dispatch(EditorActions.newNode());
    this.setState({
      displaying: 'Editor',
      editorMode: 'create',
    })
  }

  hideEditor() {
    this.setState({
      displaying: this.state.displaying === 'Editor' ? '' : this.state.displaying,
    });
  }

  hideViewer() {
    this.setState({
      displaying: this.state.displaying === 'Viewer' ? '' : this.state.displaying,
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

  deactivateEditorTarget() {
    this.props.dispatch(deactivateNode());
    this.hideEditor();
  }

  render() {
    const {editor, graph} = this.props;
    const displaying = this.state.displaying;
    return (
      <div>
        <div className="container-fluid tube-nav">
          <a /*onClick={this.toggleEditorDisplay}*/>
            <img src="/img/logo.png" height={38} style={{margin: "6px 0 6px"}}/>
          </a>
          <div className="btn btn-default pull-right" style={{marginTop: 8}} onClick={this.newNodeForEdit}>New</div>
        </div>

        <div id="top-attached">
          <div className="tube-nav-block"/>
          <Graph displaying={displaying} graph={graph}
                 chooseNodeForView={this.chooseNodeForView}/>
        </div>
        <hr className="nav-divider"/>
        <div className="tube-nav-block"/>

        <Viewer displaying={displaying} hide={this.hideViewer}
                chooseForEdit={() => this.chooseNodeForEdit(editor.target.id)}
                target={editor.target}/>
        <PageShader displaying={displaying} hide={this.hideEditor}/>
        <Editor
          displaying={displaying}
          fetching={editor.fetching}
          target={editor.target}
          hide={this.hideEditor}
          frameChoices={editor.frameChoices}
          handleContentChange={this.handleEditorContentChange}
          handleFrameChange={this.handleEditorFrameChange}
          handleNameChange={this.handleEditorNameChange}
          submitNode={this.submitEditorNodeIn(this.state.editorMode)}
          deactivateNode={this.deactivateEditorTarget}
        />
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