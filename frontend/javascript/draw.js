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

let globalId = 1;

const drawFromJSON = (requestedJSON, document, soup, parentId) => {
    if (requestedJSON === null) {
        return -1;
    }
    let id = globalId + 1;
    globalId += 1;
    const keys = Object.keys(requestedJSON);
    const visualizer = {
        'id': id,
        'dictionary': {},
        'list': {},
        'atom': '',
        "children": {},
        "parentId": parentId
    }
    soup.visualizers[id] = visualizer;
    for (const key of keys) {
        if (key !== 'type') {
            if (requestedJSON[key]['type'] === 'visualizer') {
                const childId = drawFromJSON(requestedJSON[key]['value'], document, soup, id)
                visualizer["children"][childId] = key; 
                // .push({
                //     tag: key,
                //     id: childId  // TODO: more nodes support
                // })
            } else {
                const type = requestedJSON[key]['type']
                //const visualizer = soup.visualizers.filter(x => x['id'] === id)[0]
                if (type !== undefined) {
                    if (type === 'atom') {
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
    return id;
}

// For each atom found we draw a new circle
// in reverse order because we computed it recursively in drawFromJSON
const drawFromAtoms = (canvas, document, soup) => {
    const elements = []
    let nextCircle = {}

    let svg = soup.document.getElementById('visualizerCanvas')
    if (svg !== null) {
        canvas.removeChild(svg)
    }

    svg = soup.document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute('id', 'visualizerCanvas')
    svg.setAttribute('width', '100%')
    svg.setAttribute('class', 'canvas')
    canvas.appendChild(svg);
    // console.log(JSON.stringify(soup.visualizers))

    for (let [index, x] of Object.entries(soup.visualizers)) {
    // Object.fromEntries(Object.entries(soup.visualizers).map(([index, x]) => {
    // soup.visualizers.map((x, index) => {
        // console.log(JSON.stringify(x));
        const currentPrev = nextCircle[x["parentId"]] === undefined ? undefined : nextCircle[x["parentId"]][0];
        nextCircle[x["id"]] = circle.drawCircle(svg, currentPrev, x['atom'], x, soup)
        elements.push(nextCircle[x["id"]])
        if (currentPrev !== undefined && nextCircle[x["parentId"]][0] !== undefined) {
            linkers.drawArrow(svg, 
                (parseInt(currentPrev.getAttribute('cx')) + parseInt(currentPrev.getAttribute("r"))).toString(),
                //(parseInt(currentPrev.getAttribute('cy')) + circle.radius).toString(),
                currentPrev.getAttribute('cy'),
                (parseInt(nextCircle[x["id"]][0].getAttribute('cx')) - parseInt(nextCircle[x["id"]][0].getAttribute("r"))).toString(),
                //(parseInt(nextCircle[0].getAttribute('cy')) + circle.radius).toString(),
                nextCircle[x["id"]][0].getAttribute('cy'),
                soup.document,
                //x["children"].length > 0 ? x["children"][0]["tag"] : "");  // TODO: more nodes support
                soup.visualizers[x["parentId"]]["children"][x["id"]]);  // TODO: more nodes support
        }
    }

    elements.forEach(element => {
        svg.appendChild(element[0]);
        svg.appendChild(element[1])
    })
}

module.exports = {
    drawFromJSON,
    drawFromAtoms
};