const normalCircleColor = {
    circle: '#bc4749',
    rect: '#2a9d8f'
}
const hoverCircleColor = '#52b788'
const selectedCircleColor = '#ff8c69'

const radius = 60
const ERROR = 50
const INITIAL_CX = 200
const INITIAL_CY = 100

let currentArrow = null
let canvasX1, canvasY1, canvasX2, canvasY2;
let canvasStack = [];


let previousSelectedCircle = null;

const isObjEmpty = obj => {
    return Object.keys(obj).length === 0
}

const addCircleHoverEvent = (circle) => {
    circle.addEventListener('mouseover', () => {
        circle.setAttribute('fill', hoverCircleColor)
    })
    circle.addEventListener('mouseout', () => {
        if (circle === previousSelectedCircle) {
            circle.setAttribute('fill', selectedCircleColor)
            return
        }
        circle.setAttribute('fill', normalCircleColor[circle.getAttribute('class')])
    })
}

const drawOtherStructures = (visualizer, circle, canvas, depth, tag) => {
    //console.log(JSON.stringify(visualizer))
    //console.log(`Depth: ${depth}`)
    //console.log(depth)
    const svgId = `detailCanvas${depth}`
    //console.log(typeof(visualizer))
    //console.log(visualizer)

    // if (svg !== null) {
    //     canvas.removeChild(svg)
    //     canvas.removeChild(currentArrow)
    //     currentArrow = null
    // }

    while (canvasStack.length > 0 && canvasStack[canvasStack.length - 1]["depth"] >= depth) {
        let lastElement = canvasStack[canvasStack.length - 1]
        lastElement["parent"].removeChild(lastElement["canvas"])
        for (let elem of lastElement["arrow"]) {
            lastElement["parent"].removeChild(elem) 
        }
        canvasStack.pop()
    }

    if (visualizer.hasOwnProperty("atom")) {
        //console.log("km/s")
        return
    }

    let svg = document.getElementById(svgId)
    svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute('id', svgId)
    svg.setAttribute('width', '100%')
    svg.setAttribute('class', 'canvas')

    if (canvasStack.length === 0) {
        svg.setAttribute('y', `${canvasY2 + radius}`)
    }
    else {
        svg.setAttribute("y", `${canvasStack[canvasStack.length - 1]["y"] + INITIAL_CY + canvasStack[canvasStack.length - 1]["maxInnerY"]}`)
    }
    canvas.appendChild(svg);

    canvasStack.push({
        depth: depth,
        canvas: svg,
        parent: canvas,
        y: parseInt(svg.getAttribute("y")),
        maxInnerY: 0
    })

    let firstLine = []
    //console.log(visualizer)
    for (const drawable of Object.entries(visualizer)) {
        //console.log(drawable);
        if (drawable[0] === 'dictionary' && Object.keys(drawable[1]).length !== 0) {
            firstLine = drawDictionary(svg, drawable[1], circle, canvas, depth);
            canvasStack[canvasStack.length - 1]["maxInnerY"] = Math.max(canvasStack[canvasStack.length - 1]["maxInnerY"], firstLine[2])
            canvasStack[canvasStack.length - 1]["firstLine"] = firstLine

            const maxX2 = Math.max(canvasX2, firstLine[1] + ERROR)
            const maxY2 = Math.max(canvasY2, canvasStack[canvasStack.length - 1]["y"] + firstLine[2] + ERROR)

            if (maxX2 !== canvasX2 || maxY2 !== canvasY2) {
                canvas.setAttribute('viewBox',
                    `${canvasX1} ${canvasY1} ${maxX2.toString()} ${maxY2.toString()}`)
            }

            drawArrowBetweenCanvases(canvas, circle, firstLine[0], tag)
        }
        if (drawable[0] === 'list' && Object.keys(drawable[1]).length !== 0) {
            firstLine = drawList(svg, drawable[1], circle, canvas, depth);
            canvasStack[canvasStack.length - 1]["maxInnerY"] = Math.max(canvasStack[canvasStack.length - 1]["maxInnerY"], firstLine[2])
            canvasStack[canvasStack.length - 1]["firstLine"] = firstLine

            const maxX2 = Math.max(canvasX2, firstLine[1] + ERROR)
            const maxY2 = Math.max(canvasY2, canvasStack[canvasStack.length - 1]["y"] + firstLine[2] + ERROR)

            if (maxX2 !== canvasX2 || maxY2 !== canvasY2) {
                canvas.setAttribute('viewBox',
                    `${canvasX1} ${canvasY1} ${maxX2.toString()} ${maxY2.toString()}`)
            }
            
            drawArrowBetweenCanvases(canvas, circle, firstLine[0], tag)
        }
    }
}

const getTextForKey = (key, info) => {
    let returnedText = ''
    switch (key) {
        case 'atom':
            returnedText += `${info[key]}\n`
            break;
        case 'id':
            //returnedText += `id: ${info[key]}\n`
            break
        case 'children':
            break
        default:
            if (!isObjEmpty(info[key])) {
                for (let list in info[key]) {
                    returnedText += `${list}: ${key}\n`
                }
            }
    }
    return returnedText
}

const openModal = (element, modal, modalBody, canvas, visualizer, depth) => {
    if (modal == null) return;
    const info = visualizer
    modalBody.innerHTML = ''
    for (let key in info) {
        let text = getTextForKey(key, info).trim()
        //console.log("text " + text)
        if (text.trim() === "") {
            continue
        }
        for (let el of text.split("\n")) {
            let container = document.createElement("div")
            container.className = "fieldContainer"
            container.addEventListener("click", (e) => {
                //console.log(info[key])
                let objName = el.split(":")[0].trim()   // TODO: find a way to not use split
                //console.log(JSON.stringify({[key]: info[key][objName]}))
                drawOtherStructures({[key]: info[key][objName]}, element, canvas, depth, objName)
            })
            container.innerText = el
            modalBody.append(container)
        }
    }
    modal.classList.add('active')
}

const drawFromJSON = (requestedJSON, id, document, visualizers) => {
    //console.log("in draw from JSON")
    //console.log(JSON.stringify(requestedJSON))
    const keys = Object.keys(requestedJSON);
    visualizers.push({
        'id': id,
        'dictionary': {},
        'list': {},
        'atom': ''
    })
    for (const key of keys) {
        if (key !== 'type') {
            if (requestedJSON[key]['type'] === 'visualizer') {
                drawFromJSON(requestedJSON[key]['value'], id + 1, document, visualizers)
            } else {
                const type = requestedJSON[key]['type']
                const visualizer = visualizers.filter(x => x['id'] === id)[0]
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
    //console.log(JSON.stringify(visualizers))
    return visualizers;
}

const addCircleClickEvent = (canvas, circle, visualizer, document, depth) => {
    circle.addEventListener('click', (e) => {
        // open information modal tab
        //console.log(visualizer)
        if (previousSelectedCircle != null) {
            previousSelectedCircle.setAttribute("fill", normalCircleColor[previousSelectedCircle.getAttribute('class')]);
        }
        //console.log(JSON.stringify(visualizer))
        previousSelectedCircle = circle;
        circle.setAttribute("fill", selectedCircleColor)
        const modal = document.getElementById('modal');
        const modalBody = document.getElementsByClassName('modal-body')
        openModal(circle, modal, modalBody[0], canvas, visualizer, depth);
    })
}

const drawArrowBetweenCanvases = (canvas, circle, firstLine, tag) => {
    let offsetY = 0
    if (canvasStack.length >= 2) {
        offsetY = canvasStack[canvasStack.length - 2]["y"]
    }
    let cx = circle.getAttribute('cx')
    if (cx == null) {
        cx = (parseInt(circle.getAttribute("x")) + parseInt(circle.getAttribute("width")) / 2).toString()
    }
    let cy = circle.getAttribute('cy')
    if (cy == null) {
        cy = (parseInt(circle.getAttribute("y")) + offsetY + parseInt(circle.getAttribute("height"))).toString()
    }
    else {
        cy = (parseInt(cy) + radius + (canvasStack.length - 1) * INITIAL_CX).toString()
    }

    const x2 = (firstLine[0] + firstLine[2]) / 2
    let y2 = (firstLine[1] + firstLine[3]) / 2 + INITIAL_CX * canvasStack.length + radius * 2 + ERROR

    if (canvasStack.length >= 2) {
       y2 = (firstLine[1] + firstLine[3]) / 2 + canvasStack[canvasStack.length - 2]["y"] + INITIAL_CY + canvasStack[canvasStack.length - 2]["maxInnerY"]
    }
    canvasStack[canvasStack.length - 1]["arrow"] = drawArrow(canvas, cx, cy, x2.toString(), y2.toString(), tag)
}

const drawArrow = (svg, x1, y1, x2, y2, tag) => {
    const svgNS = svg.namespaceURI;
    const line = document.createElementNS(svgNS, 'line');
    const textOffsetX = 10
    line.setAttribute('x1', x1)
    line.setAttribute('y1', y1)
    line.setAttribute('x2', x2)
    line.setAttribute('y2', y2)
    line.setAttribute('class', 'line')
    svg.appendChild(line);
        
    if (tag !== "") {
        const text = document.createElementNS(svgNS, 'text')
        text.setAttribute("x", ((parseInt(x1) + parseInt(x2)) / 2 + textOffsetX).toString())
        text.setAttribute("y", ((parseInt(y1) + parseInt(y2)) / 2).toString())

        const tspan = document.createElementNS(svgNS, 'tspan')
        tspan.innerHTML = tag
        text.appendChild(tspan);
        svg.appendChild(text);
        return [line, text]
    }
    return [line]
}

let canvas = document.getElementById("canvas")
let visualizerCanvas = document.getElementById("visualizerCanvas")

const values = visualizerCanvas.getAttribute('viewBox').split(' ')
canvasX1 = parseInt(values[0])
canvasY1 = parseInt(values[1])
canvasX2 = parseInt(values[2])
canvasY2 = parseInt(values[3])


for (let circle of document.getElementsByClassName("circle")) {
    addCircleClickEvent(visualizerCanvas, circle, JSON.parse(circle.getAttribute("visualizer")), document, 0)
    addCircleHoverEvent(circle)
}