export const NICKNAME = document.getElementById('context').getAttribute('nickname');
export const REMOTE_SERVER = process.env.API_URL;
export const DEFAULT_FRAME = `<Private>@${NICKNAME}`;
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
  frame: DEFAULT_FRAME,
  type: viewToNodeType(defaultView),
};

export const INIT_STORE = {
  graph: {pointMap: {}},
  editor: {
    fetching: false,
    frameChoices: [DEFAULT_FRAME],
    target: MOCK_TARGET,
  },
  context: {
    view: defaultView,
  }
};


export const displayCSS = (displaying, element) => displaying === element ? '' : 'elem-display-none';