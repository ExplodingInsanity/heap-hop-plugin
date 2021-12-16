const radius = 60
const ERROR = 50
const INITIAL_CX = 200
const INITIAL_CY = 200

let currentArrow = null
let canvasX1, canvasY1, canvasX2, canvasY2;

const addCircleHoverEvent = (circle) => {
    circle.addEventListener('mouseover', () => {
        circle.setAttribute('fill', '#52b788')
    })
    circle.addEventListener('mouseout', () => {
        circle.setAttribute('fill', '#bc4749')
    })
}

const addCircleClickEvent = (canvas, circle, visualizer, document) => {
    circle.addEventListener('click', (e) => {
        let svg = document.getElementById('detailCanvas')
        if (svg !== null) {
            canvas.removeChild(svg)
        }
        if (currentArrow !== null) {
            canvas.removeChild(currentArrow)
            currentArrow = null
        }

        svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.setAttribute('id', 'detailCanvas')
        svg.setAttribute('width', '100%')
        svg.setAttribute('class', 'canvas')
        svg.setAttribute('y', `${canvasY2 + radius}`)
        canvas.appendChild(svg);

        let firstLine = []

        for (const drawable of Object.entries(visualizer)) {
            if (drawable[0] === 'dictionary' && drawable[1].length !== 0) {
                firstLine = drawDictionary(svg, drawable[1], circle);

                const maxX2 = Math.max(canvasX2, firstLine[1] + ERROR)
                const maxY2 = Math.max(canvasY2, firstLine[2] + ERROR)

                if (maxX2 !== canvasX2 || maxY2 !== canvasY2) {
                    canvas.setAttribute('viewBox',
                        `${canvasX1} ${canvasY1} ${maxX2.toString()} ${maxY2.toString()}`)
                }

                drawArrowBetweenCanvases(canvas, circle, firstLine[0])
            }
            if (drawable[0] === 'list' && drawable[1].length !== 0) {
                firstLine = drawList(svg, drawable[1], circle)
                const maxX2 = firstLine[1] + ERROR
                if (maxX2 > parseInt(canvasX2)) {
                    canvas.setAttribute('viewBox',
                        `${canvasX1} ${canvasY1} ${maxX2.toString()} ${canvasY2}`)
                }
                drawArrowBetweenCanvases(canvas, circle, firstLine[0])
            }
        }
    })
}

const drawArrowBetweenCanvases = (canvas, circle, firstLine) => {
    const cx = circle.getAttribute('cx')
    const cy = circle.getAttribute('cy')

    const x2 = (firstLine[0] + firstLine[2]) / 2
    const y2 = (firstLine[1] + firstLine[3]) / 2 + INITIAL_CX + radius * 2 + ERROR

    currentArrow = drawArrow(canvas, cx, (parseInt(cy) + radius).toString(), x2.toString(), y2.toString())
}

const drawArrow = (svg, x1, y1, x2, y2) => {
    const svgNS = svg.namespaceURI;
    const line = document.createElementNS(svgNS, 'line');
    line.setAttribute('x1', x1)
    line.setAttribute('y1', y1)
    line.setAttribute('x2', x2)
    line.setAttribute('y2', y2)
    line.setAttribute('class', 'line')
    svg.appendChild(line);

    return line
}

let canvas = document.getElementById("canvas")
let visualizerCanvas = document.getElementById("visualizerCanvas")

const values = visualizerCanvas.getAttribute('viewBox').split(' ')
canvasX1 = parseInt(values[0])
canvasY1 = parseInt(values[1])
canvasX2 = parseInt(values[2])
canvasY2 = parseInt(values[3])


for (circle of document.getElementsByClassName("circle")) {
    addCircleClickEvent(visualizerCanvas, circle, JSON.parse(circle.getAttribute("visualizer")), document)
    addCircleHoverEvent(circle)
}