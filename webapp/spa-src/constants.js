export const NICKNAME = document.getElementById('api').getAttribute('nickname');
export const REMOTE_SERVER = document.getElementById('api').getAttribute('server');
export const NULL_FRAME = `<Private>@${NICKNAME}`;
export const VIEW_CHOICES = ['Articles', ]//'Map'];
export const viewToNodeType = view => {
  let map = {
    'Articles': 'ARTICLE',
    //'Map': 'NODE',
  };
  return map[view];
};

const defaultView = VIEW_CHOICES[0];

export const MOCK_TARGET = {
  id: null,
  name: "",
  content: "",
  frame: NULL_FRAME,
  type: viewToNodeType(defaultView),
};

export const INIT_STORE = {
  graph: {pointMap: {}},
  editor: {
    fetching: false,
    frameChoices: [NULL_FRAME],
    target: MOCK_TARGET,
  },
  context: {
    view: defaultView,
  }
};


export const displayCSS = (displaying, element) => displaying === element ? '' : 'elem-display-none';