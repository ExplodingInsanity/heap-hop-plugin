const isObject = (obj) => {
  return typeof obj === 'object';
}

const drawCircle = (value, location) => {
  const node = document.createElement('div');
  node.id = Math.random().toString(36).substr(2,9);
  node.innerHTML = `<p>${isObject(value) ? "" : value}</p>`;
  node.classList.add('element');
  location.appendChild(node);
}

const drawArrow = (location) => {
  const arrow = document.createElement('div');
  arrow.classList.add('arrow-right');
  location.appendChild(arrow);
}

const drawNode = (value, location, isLast) => {
  if (value.type === 'atom') {
    drawCircle(value.value, location);
  }
  if(!isLast) {
    drawArrow(location);
  }
}

const atoms = []

const drawFromJSON = (requestedJSON) => {
  let hasChildren;
  const localAtoms = {}
  const keys = Object.keys(requestedJSON);
  if(requestedJSON === null) return false;
  for(const key of keys) {
    if(key !== 'type') {
      if(requestedJSON[key]['type'] === 'visualizer') {
        hasChildren = drawFromJSON(requestedJSON[key]['value'])
      } else {
        localAtoms[key] = requestedJSON[key]['value']
      }
    }
  }
  let text = '';
  for(const atom of Object.entries(localAtoms)) {
    text += atom[0] + ": " + atom[1] + `<br>`
  }
  atoms.push(text)
  return true;
}

const drawFromAtoms = (canvas) => {
  atoms.slice(0).reverse().map((x, index) => {
    drawCircle(x, canvas)
    if(index !== atoms.length - 1) {
      drawArrow(canvas);
    }
  })
}

const URL = 'http://localhost:24564/query'

window.onload = () => {
  const canvas = document.getElementById('canvas');
  fetch(URL)
      .then(response=>response.json())
      .then(data=>{ console.log(data); drawFromJSON(data); drawFromAtoms(canvas) })
      .catch(error=>alert(error))
}