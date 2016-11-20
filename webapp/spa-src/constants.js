
export const MOCK_TARGET = {
  id: null,
  name: "",
  content: "",
  frame: "\<Private\>",
};

export const INIT_STORE = {
  graph: {pointMap: {}},
  editor: {
    fetching: false,
    frameChoices: ["\<Private\>", "BeTrue@Dun", "Ohters@Dun", "Nonsense@John Doe"],
    target: MOCK_TARGET,
  }
};