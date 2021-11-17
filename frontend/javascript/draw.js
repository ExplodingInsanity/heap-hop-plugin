const isObject = (obj) => {
    return typeof obj === 'object';
}

// draw simple circle
// div + css
// location may be the canvas
const drawCircle = (value, location) => {
    const node = document.createElement('div');
    node.id = Math.random().toString(36).substr(2, 9);
    node.innerHTML = `<p>${isObject(value) ? "" : value}</p>`;
    node.classList.add('element');
    location.appendChild(node);
}

// draw simple arrow
// div + css
const drawArrow = (location) => {
    const arrow = document.createElement('div');
    arrow.classList.add('arrow-right');
    location.appendChild(arrow);
}

// draw simple node (circle + arrow)
const drawNode = (value, location, isLast) => {
    if (value.type === 'atom') {
        drawCircle(value.value, location);
    }
    if (!isLast) {
        drawArrow(location);
    }
}

// recursively go through each key in the json
// and verify its type (either visualizer or atom)
// once we find an atom we can place it in the localAtoms dictionary
// put all of them into the atoms array
// visualizer = another data structure; atom = simple data type (int, string etc)
const atoms = []
const drawFromJSON = (requestedJSON) => {
    let hasChildren;
    const localAtoms = {}
    const keys = Object.keys(requestedJSON);
    if (requestedJSON === null) return false;
    for (const key of keys) {
        if (key !== 'type') {
            if (requestedJSON[key]['type'] === 'visualizer') {
                hasChildren = drawFromJSON(requestedJSON[key]['value'])
            } else {
                localAtoms[key] = requestedJSON[key]['value']
            }
        }
    }
    let text = '';
    for (const atom of Object.entries(localAtoms)) {
        text += atom[0] + ": " + atom[1] + `<br>`
    }
    atoms.push(text)
    return true;
}

// For each atom found we draw a new circle
// in reverse order because we computed it recursively in drawFromJSON
const drawFromAtoms = (canvas) => {
    atoms.slice(0).reverse().map((x, index) => {
        drawCircle(x, canvas)
        if (index !== atoms.length - 1) {
            drawArrow(canvas);
        }
    })
}

// URL used for get request
const URL = 'http://localhost:24564/query'

window.onload = () => {
    // canvas element
    const canvas = document.getElementById('canvas');
    // fetch data from server, parse it as json and drawFromJSON
    fetch(URL)
        .then(response => response.json())
        .then(data => {
            console.log(data);
            drawFromJSON(data);
            drawFromAtoms(canvas)
        })
        .catch(error => alert(error))
}