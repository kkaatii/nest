export const NULL_FRAME = "<Private>";

export const MOCK_TARGET = {
  id: null,
  name: "",
  content: "",
  frame: NULL_FRAME,
};

export const INIT_STORE = {
  graph: {pointMap: {}},
  editor: {
    fetching: false,
    frameChoices: [NULL_FRAME],
    target: MOCK_TARGET,
  }
};

export const displayCSS = (displaying, element) => displaying === element ? '' : 'elem-display-none';