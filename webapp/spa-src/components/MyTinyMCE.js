import React from 'react';
import {findDOMNode} from 'react-dom';
import isEqual from 'lodash/isEqual';

import tinymce from 'tinymce/tinymce'
import 'tinymce/themes/modern/theme'
import 'tinymce/plugins/autoresize'

function ucFirst(str) {
  return str[0].toUpperCase() + str.substring(1);
}

// Include all of the Native DOM and custom events from:
// https://github.com/tinymce/tinymce/blob/master/tools/docs/tinymce.Editor.js#L5-L12
const EVENTS = [
  'focusin', 'focusout', 'click', 'dblclick', 'mousedown', 'mouseup',
  'mousemove', 'mouseover', 'beforepaste', 'paste', 'cut', 'copy',
  'selectionchange', 'mouseout', 'mouseenter', 'mouseleave', 'keydown',
  'keypress', 'keyup', 'contextmenu', 'dragend', 'dragover', 'draggesture',
  'dragdrop', 'drop', 'drag', 'BeforeRenderUI', 'SetAttrib', 'PreInit',
  'PostRender', 'init', 'deactivate', 'activate', 'NodeChange',
  'BeforeExecCommand', 'ExecCommand', 'show', 'hide', 'ProgressState',
  'LoadContent', 'SaveContent', 'BeforeSetContent', 'SetContent',
  'BeforeGetContent', 'GetContent', 'VisualAid', 'remove', 'submit', 'reset',
  'BeforeAddUndo', 'AddUndo', 'change', 'undo', 'redo', 'ClearUndos',
  'ObjectSelected', 'ObjectResizeStart', 'ObjectResized', 'PreProcess',
  'PostProcess', 'focus', 'blur', 'dirty'
];

// Note: because the capitalization of the events is weird, we're going to get
// some inconsistently-named handlers, for example compare:
// 'onMouseleave' and 'onNodeChange'
const HANDLER_NAMES = EVENTS.map((event) => {
  return 'on' + ucFirst(event);
});

const MyTinyMCE = React.createClass({
  displayName: 'TinyMCE',

  propTypes: {
    content: React.PropTypes.string,
    id: React.PropTypes.string,
    className: React.PropTypes.string
  },

  getDefaultProps() {
    return {
      content: ''
    };
  },

  componentWillMount() {
    this.id = this.id || this.props.id;
  },

  componentDidMount() {
    const config = {
      selector: `#${this.id}`,
      skin_url: '/css/tinymce-skins/lightgray',
      content_style: 'body.mce-content-body {font-size:14px}',
      plugins: 'autoresize',
    };
    this._init(config);
  },

  componentWillReceiveProps(nextProps) {
    if (!isEqual(this.props.id, nextProps.id)) {
      this.id = nextProps.id
    }
    // Added
    const editor = tinymce.EditorManager.get(this.id);
    if (editor && !isEqual(editor.getContent(), nextProps.content)) {
      tinymce.EditorManager.get(this.id).setContent(nextProps.content)
    }
  },

  shouldComponentUpdate(nextProps) {
    return (
      !isEqual(this.props.content, nextProps.content)
    );
  },

  componentWillUnmount() {
    this._remove();
  },

  render() {
    return (/*this.props.config.inline ? (
      <div
        id={this.id}
        className={this.props.className}
        dangerouslySetInnerHTML={{__html: this.props.content}}
      />
    ) : (*/
      <textarea
        id={this.id}
        className={this.props.className}
        defaultValue={this.props.content}
      />
    );
  },

  _init(config, content) {
    if (this._isInit) {
      this._remove();
    }

    // hide the textarea that is me so that no one sees it
    findDOMNode(this).style.hidden = 'hidden';

    const setupCallback = config.setup;
    const hasSetupCallback = (typeof setupCallback === 'function');

    config.selector = '#' + this.id;
    config.setup = (editor) => {
      EVENTS.forEach((event, index) => {
        const handler = this.props[HANDLER_NAMES[index]];
        if (typeof handler !== 'function') return;
        editor.on(event, (e) => {
          // native DOM events don't have access to the editor so we pass it here
          handler(e, editor);
        });
      });
      // need to set content here because the textarea will still have the
      // old `this.props.content`
      if (content) {
        editor.on('init', () => {
          editor.setContent(content);
        });
      }
      if (hasSetupCallback) {
        setupCallback(editor);
      }
    };

    tinymce.init(config);

    findDOMNode(this).style.hidden = '';

    this._isInit = true;
  },

  _remove() {
    tinymce.EditorManager.execCommand('mceRemoveEditor', true, this.id);
    this._isInit = false;
  }
});

// add handler propTypes
HANDLER_NAMES.forEach((name) => {
  MyTinyMCE.propTypes[name] = React.PropTypes.func;
});

export default MyTinyMCE;