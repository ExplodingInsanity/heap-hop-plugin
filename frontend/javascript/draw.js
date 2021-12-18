const circle = require("./Circle.js");
const linkers = require("./Linkers.js");

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
const drawFromJSON = (requestedJSON, id, document, soup) => {
    let hasChildren;
    const keys = Object.keys(requestedJSON);
    const visualizer = {
        'id': id,
        'dictionary': {},
        'list': {},
        'atom': '',
        "children": []
    }
    soup.visualizers.push(visualizer)
    for (const key of keys) {
        if (key !== 'type') {
            if (requestedJSON[key]['type'] === 'visualizer') {
                hasChildren = drawFromJSON(requestedJSON[key]['value'], id + 1, document, soup)
                visualizer["children"].push({
                    tag: key,
                    id: id + 1  // TODO: more nodes support
                })
            } else {
                const type = requestedJSON[key]['type']
                //const visualizer = soup.visualizers.filter(x => x['id'] === id)[0]
                if (type !== undefined) {
                    if (requestedJSON[key]['type'] === 'atom') {
                        visualizer[type] += key + ": " + requestedJSON[key]['value'] + '\n'
                    } else {
                        const value = requestedJSON[key]['value']
                        if (value.length !== 0) {
                            visualizer[type][key] = value
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
            linkers.drawArrow(svg, 
                (parseInt(currentPrev.getAttribute('cx')) + parseInt(currentPrev.getAttribute("r"))).toString(),
                //(parseInt(currentPrev.getAttribute('cy')) + circle.radius).toString(),
                currentPrev.getAttribute('cy'),
                (parseInt(nextCircle[0].getAttribute('cx')) - parseInt(nextCircle[0].getAttribute("r"))).toString(),
                //(parseInt(nextCircle[0].getAttribute('cy')) + circle.radius).toString(),
                nextCircle[0].getAttribute('cy'),
                document,
                //x["children"].length > 0 ? x["children"][0]["tag"] : "");  // TODO: more nodes support
                soup.visualizers[index - 1]["children"][0]["tag"]);  // TODO: more nodes support
        }
    })

    elements.forEach(element => {
        svg.appendChild(element[0]);
        svg.appendChild(element[1])
    })
}

module.exports = {
    drawFromJSON,
    drawFromAtoms
};