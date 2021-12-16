const circle = require("./Circle.js");
const linkers = require("./Linkers.js");

const isObject = (obj) => {
    return typeof obj === 'object';
}

// draw simple circle
// div + css
// location may be the canvas
// const drawCircle = (value, location) => {
//     const node = document.createElement('div');
//     node.id = Math.random().toString(36).substr(2, 9);
//     node.innerHTML = `<p>${isObject(value) ? "" : value}</p>`;
//     node.classList.add('element');
//     location.appendChild(node);
// }

// draw simple arrow
// div + css
// const drawArrow = (location) => {
//     const arrow = document.createElement('div');
//     arrow.classList.add('arrow-right');
//     location.appendChild(arrow);
// }

// draw simple node (circle + arrow)
const drawNode = (value, location, isLast) => {
    if (value.type === 'atom') {
        circle.drawCircle(value.value, location);
    }
    if (!isLast) {
        linkers.drawArrow(location);
    }
}

// recursively go through each key in the json
// and verify its type (either visualizer or atom)
// once we find an atom we can place it in the localAtoms dictionary
// put all of them into the atoms array
// visualizer = another data structure; atom = simple data type (int, string etc)
//const visualizers = []
let id = 0

const drawFromJSON = (requestedJSON, id, document, soup) => {
    let hasChildren;
    const keys = Object.keys(requestedJSON);
    soup.visualizers.push({
        'id': id,
        'dictionary': [],
        'list': [],
        'atom': ''
    })
    for (const key of keys) {
        if (key !== 'type') {
            if (requestedJSON[key]['type'] === 'visualizer') {
                hasChildren = drawFromJSON(requestedJSON[key]['value'], id + 1, document, soup)
            } else {
                const type = requestedJSON[key]['type']

                const visualizer = soup.visualizers.filter(x => x['id'] === id)[0]
                if (type !== undefined) {
                    if (requestedJSON[key]['type'] === 'atom') {
                        visualizer[type] += key + ": " + requestedJSON[key]['value'] + '\n'
                    } else {
                        const value = requestedJSON[key]['value']
                        if (value.length !== 0) {
                            visualizer[type].push(value)
                        }
                    }
                }
            }
        }
    }
    return true;
}

// For each atom found we draw a new circle
// in reverse order because we computed it recursively in drawFromJSON
const drawFromAtoms = (canvas, document, soup) => {
    const elements = []
    let nextCircle = []

    let svg = document.getElementById('visualizerCanvas')
    if (svg !== null) {
        canvas.removeChild(svg)
    }

    svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute('id', 'visualizerCanvas')
    svg.setAttribute('width', '100%')
    svg.setAttribute('class', 'canvas')
    canvas.appendChild(svg);

    soup.visualizers.slice(0).map((x, index) => {
        const currentPrev = nextCircle[0]
        nextCircle = circle.drawCircle(svg, currentPrev, x['atom'], x, document)
        elements.push(nextCircle)
        if (currentPrev !== undefined && nextCircle[0] !== undefined) {
            linkers.drawArrow(svg, currentPrev.getAttribute('cx'),
                currentPrev.getAttribute('cy'),
                nextCircle[0].getAttribute('cx'),
                nextCircle[0].getAttribute('cy'),
                document);
        }
    })

    elements.forEach(element => {
        svg.appendChild(element[0]);
        svg.appendChild(element[1])
    })
}

// URL used for get request
/*
const URL = 'http://localhost:24564/query'

window.onload = () => {
    // canvas element
    const canvas = document.getElementById('canvas');
    // fetch data from server, parse it as json and drawFromJSON
    fetch(URL)
        .then(response => response.json())
        .then(data => {
            drawFromJSON(data, 0);
            drawFromAtoms(canvas)
        })
        .catch(error => alert(error))
}*/

module.exports = {
    drawFromJSON,
    drawFromAtoms
};