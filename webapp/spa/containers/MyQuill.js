import React from 'react'
import {findDOMNode} from 'react-dom'
import Quill from 'quill'

class MyQuill extends React.Component {
  constructor(props) {
    super(props);
    this.state = {editor: null, content: null};
    this.getEditorConfig = this.getEditorConfig.bind(this);
    this.initEditor = this.initEditor.bind(this);
  }

  getEditorConfig() {
    let toolbarOptions = [
      [{'font': []}, {'header': [1, 3, false]}],
      ['bold', 'italic', 'underline', 'strike', {'color': []}, {'background': []}],
      [{'align': []}, {'list': 'ordered'}, {'list': 'bullet'}, {'indent': '-1'}, {'indent': '+1'}],
      [{'script': 'sub'}, {'script': 'super'}, 'blockquote', 'code-block'],
      ['clean']
    ];
    return {
      readOnly: this.props.readOnly,
      theme: this.props.theme == null ? 'snow' : this.props.theme,
      formats: this.props.formats, // Let Quill set the defaults, if no formats supplied
      styles: this.props.styles,
      modules: {toolbar: toolbarOptions},
      pollInterval: this.props.pollInterval,
      bounds: this.props.bounds,
      placeholder: this.props.placeholder,
    };
  }

  initEditor() {
    let editor = new Quill(findDOMNode(this), this.getEditorConfig());

    editor.on('text-change', function (delta, oldDelta, source) {
      let content = editor.root.innerHTML;
      if (content !== this.state.content) {
        this.setState({content: content});
        this.props.onChange(
          content, delta, source
        );
      }
    }.bind(this));
    editor.on('selection-change', function (range, oldRange, source) {
      if (this.props.onChangeSelection) {
        this.props.onChangeSelection(
          range, source
        );
      }
    }.bind(this));
    this.setState({editor: editor});
  }

  componentDidMount() {
    this.initEditor()
  }

  componentWillReceiveProps(nextProps) {
    let editor = this.state.editor;
    // If the component is unmounted and mounted too quickly
    // an error is thrown in setEditorContents since editor is
    // still undefined. Must check if editor is undefined
    // before performing this call.
    if (editor) {
      // Update only if we've been passed a new `value`.
      // This leaves components using `defaultValue` alone.
      if ('content' in nextProps) {
        // NOTE: Seeing that Quill is missing a way to prevent
        //       edits, we have to settle for a hybrid between
        //       controlled and uncontrolled mode. We can't prevent
        //       the change, but we'll still override content
        //       whenever `value` differs from current state.
        if (this.state.content !== nextProps.content) {
          //let sel = editor.getSelection();
          this.setState({content: nextProps.content});
          editor.clipboard.dangerouslyPasteHTML(nextProps.content);
        }
      }
      // We can update readOnly state in-place.
      if ('readOnly' in nextProps) {
        if (nextProps.readOnly !== this.props.readOnly) {
          nextProps.readOnly ? editor.disable() : editor.enable();
        }
      }
    }
  }

  render() {
    return <div id="node-content"></div>
  }
}

const T = React.PropTypes;
MyQuill.props = {
  style: T.object,
  content: T.string.isRequired,
  placeholder: T.string,
  readOnly: T.bool,
  modules: T.object,
  formats: T.array,
  styles: T.oneOfType([T.object, T.oneOf([false])]),
  theme: T.string,
  pollInterval: T.number,
  onKeyPress: T.func,
  onKeyDown: T.func,
  onKeyUp: T.func,
  onChange: T.func.isRequired,
  onChangeSelection: T.func
};

export default MyQuill;