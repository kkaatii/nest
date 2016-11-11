var REMOTE_SERVER = document.getElementById('api').getAttribute('server');
var API_URL = REMOTE_SERVER + '/api';

const FrameSelect = React.createClass({

  render: function () {
    let self = this;
    let h = [];
    let options = this.props.options;
    let lastHeader = '';
    for (let i = 0; i < options.length; i++) {
      let option = options[i].split('#');
      if (option[1] && option[1] !== lastHeader) {
        lastHeader = option[1];
        h.push(<li className="dropdown-header" style={{fontWeight:"bold", color: "#8ad"}}>{option[1]}</li>);
      }
      h.push(<li key={i}><a href="#" onClick={self.props.display(i)}>{option[0]}</a></li>);
    }
    /*
     let frameOptions = function (text) {
     /*if (text.startsWith('\<'))
     return <li key={text}><a href="#" onClick={self.props.display(text)}>{text}</a></li>;
     /*else if (text.startsWith('#'))
     return <li key={text} className="dropdown-header" style={{fontWeight: "bold"}}>{text.substring(1)}</li>;
     else return <li key={text}><a href="#" onClick={self.props.display(text)}>{'  ' + text}</a></li>;
     };*/
    return <ul aria-labelledby="node-frame-select"
               className="dropdown-menu">{h}</ul>;
  }
});

const Body = React.createClass({
  getInitialState: function () {
    return {
      editor: {
        name: "",
        content: "",
        frameoptions: ["\<Private\>", "BeTrue#Dun", "Ohters#Dun", "Nonsense#John Doe"],
        frame: "\<Private\>"
      }
    }
  },

  componentDidMount: function () {
    let self = this;
    tinymce.init({
      selector: '#node-content',
      height: 300,
      setup: function (ed) {
        ed.on('init', function () {
          this.getDoc().body.style.fontSize = '14px';
        });
      },
      init_instance_callback: function (ed) {
        ed.on('Change', function () {
          self.handleContentChange(ed.getContent());
        });
      }
    });
  },

  handleContentChange: function (text) {
    let state = this.state;
    state.editor.content = text;
    this.setState(state)
  },

  handleNameChange: function (event) {
    let state = this.state;
    state.editor.name = event.target.value;
    this.setState(state);
  },

  handleEditorSubmit: function (e) {
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
  },

  changeDropdownDisplay: function (i) {
    let state = this.state;
    let self = this;
    return function () {
      state.editor.frame = state.editor.frameoptions[i];
      self.setState(state);
    }
  },

  render: function () {
    return (
      <div>
        <div className="container">
          <form className="form-horizontal" onSubmit={this.handleEditorSubmit}>
            <div className="form-group">
              <div className="col-lg-10 upper-margin">
                <label htmlFor="node-name">Title</label>
                <input type="text" className="form-control" id="node-name" placeholder="Article"
                       onChange={this.handleNameChange} value={this.state.editor.name}/>
              </div>
              <div className="col-lg-2 upper-margin">
                <label htmlFor="node-frame">Frame</label>
                <div className="dropdown">
                  <button type="button" className="btn btn-default btn-block dropdown-toggle"
                          data-toggle="dropdown"
                          aria-haspopup="true" aria-expanded="false"
                          id="node-frame-select" style={{textAlign: "left"}}>{this.state.editor.frame.split('#')[0]}
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
                <textarea id="node-content" value={this.state.editor.content}/>
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
});

ReactDOM.render(
  <Body />,
  document.getElementById("root")
);
