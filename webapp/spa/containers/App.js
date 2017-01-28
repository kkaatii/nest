import React, {PropTypes} from 'react'
import {connect} from 'react-redux'
import Nav from '../components/Nav'
import Editor from '../components/Editor'
import Viewer from '../components/Viewer'
import PageShading from '../components/PageShading'
import Graph from '../components/Graph'
import {EditorActions, GraphActions, ContextActions} from '../actions'

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      displaying: '',
      editorMode: 'create',
    };
    this.toggleEditorDisplay = this.toggleEditorDisplay.bind(this);
    this.editorButtonFunction = this.editorButtonFunction.bind(this);
    this.graphButtonFunction = this.graphButtonFunction.bind(this);
    this.hideEditor = this.hideEditor.bind(this);
    this.hideViewer = this.hideViewer.bind(this);
    this.chooseNodeForEdit = this.chooseNodeForEdit.bind(this);
    this.chooseNodeForView = this.chooseNodeForView.bind(this);
    this.handleEditorContentChange = this.handleEditorContentChange.bind(this);
    this.handleEditorFrameChange = this.handleEditorFrameChange.bind(this);
    this.handleEditorNameChange = this.handleEditorNameChange.bind(this);
    this.submitEditorNodeIn = this.submitEditorNodeIn.bind(this);
    this.deactivateEditorTarget = this.deactivateEditorTarget.bind(this);
    this.switchView = this.switchView.bind(this);
  };

  componentDidMount() {
    this.props.dispatch(GraphActions.fetchAllPoints());
    this.props.dispatch(EditorActions.fetchFrameChoices());
    document.addEventListener("keydown", e => (e.keyCode === 27 ? this.hideViewer() : {}), false);
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

  editorButtonFunction() {
    switch (this.state.displaying) {
      case 'Viewer':
        return () => this.chooseNodeForEdit(this.props.editor.target.id);

      case 'Editor':
      case '':
        return () => {
          this.props.dispatch(EditorActions.newNode());
          this.setState({
            displaying: 'Editor',
            editorMode: 'create',
          })
        };
      default:
        return () => null;
    }
  }

  graphButtonFunction() {
    switch (this.state.displaying) {
      case 'Editor':
        return () => this.hideEditor();
      case 'Viewer':
        return () => this.hideViewer();
      case '':
        return () => this.props.dispatch(GraphActions.fetchAllPoints());
      default:
        return () => null;
    }
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

  handleEditorContentChange(content) {
    this.props.dispatch(EditorActions.changeTargetContent(content));
  }

  handleEditorNameChange(e) {
    this.props.dispatch(EditorActions.changeTargetName(e.target.value));
  }

  handleEditorFrameChange(frame) {
    return () => this.props.dispatch(EditorActions.changeTargetFrame(frame));
  }

  submitEditorNodeIn(mode) {
    return (e) => {
      e.preventDefault();
      this.props.dispatch(EditorActions.createOrUpdateNode(mode));
      this.hideEditor();
    }
  }

  switchView(view) {
    return () => this.props.dispatch(ContextActions.setView(view));
  }

  deactivateEditorTarget() {
    this.props.dispatch(EditorActions.deactivateNode());
    this.hideEditor();
  }

  render() {
    const {editor, graph, context} = this.props;
    const displaying = this.state.displaying;
    return (
      <div>
        <Nav view={context.view} switchView={this.switchView}
             editorButton={this.editorButtonFunction()}
             graphButton={this.graphButtonFunction()}
             displaying={displaying}/>

        <div id="top-attached">
          <div className="tube-nav-block"/>
          <Graph displaying={displaying} graph={graph}
                 chooseNodeForView={this.chooseNodeForView}/>
        </div>
        <hr className="nav-divider"/>
        <div className="tube-nav-block"/>

        <Viewer displaying={displaying} target={editor.target}/>
        <PageShading displaying={displaying} hide={this.hideEditor}/>
        <Editor
          displaying={displaying}
          fetching={editor.fetching}
          target={editor.target}
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
  editor: PropTypes.object,
  graph: PropTypes.object,
  context: PropTypes.object,
};

function mapStateToProps(state) {
  const {editor, graph, context} = state;
  return {
    editor, graph, context
  }
}

export default connect(mapStateToProps)(App);