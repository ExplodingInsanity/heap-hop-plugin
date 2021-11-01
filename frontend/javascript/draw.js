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

const URL = 'http://localhost:24567/query'

window.onload = () => {
  const canvas = document.getElementById('canvas');
  fetch(URL)
      .then(response => console.log(response))
  // for (let i = 0; i < initialObj.length; i++) {
  //   drawNode(initialObj[i].fields.value, canvas, i === initialObj.length - 1);
  // }
  // for (let i = 0; i < 3; i++) {
  //   drawNode(3, canvas, i === 2);
  // }
}