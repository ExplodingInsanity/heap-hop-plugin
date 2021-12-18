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

    currentArrow = drawArrow(canvas, cx, (parseInt(cy) + radius).toString(), x2.toString(), y2.toString(), "")
}

const drawCircle = (svg, prev, value, visualizer, document) => {
    const svgNS = svg.namespaceURI;
    const circle = document.createElementNS(svgNS, 'circle');
    const text = document.createElementNS(svgNS, 'text')

    const tspanPosition = value.split('\n').length - 2

    let nextCX, nextCY;

    circle.setAttribute("visualizer", JSON.stringify(visualizer));

    if (prev === undefined) {
        nextCX = INITIAL_CX;
        nextCY = INITIAL_CY;
        circle.setAttribute('cx', nextCX.toString());
        circle.setAttribute('cy', nextCY.toString());
        text.setAttribute('x', INITIAL_CX.toString())
        text.setAttribute('y', (INITIAL_CY - 13 * tspanPosition).toString())
    } else {
        nextCX = parseInt(prev.getAttribute("cx")) + INITIAL_CX
        nextCY = parseInt(prev.getAttribute("cy"))
        circle.setAttribute('cx', nextCX.toString())
        circle.setAttribute('cy', nextCY.toString())
        text.setAttribute('x', nextCX.toString())
        text.setAttribute('y', (nextCY - 13 * tspanPosition).toString())
    }
    svg.setAttribute('viewBox', `0 0 ${nextCX + radius + ERROR} ${nextCY + radius + ERROR}`)
    circle.setAttribute('r', radius.toString());
    circle.setAttribute('class', 'circle')
    circle.setAttribute('fill', '#bc4749')

    text.setAttribute('class', 'valueText')
    // keeping the initial values of canvas view box
    const values = svg.getAttribute('viewBox').split(' ')
    canvasX1 = parseInt(values[0])
    canvasY1 = parseInt(values[1])
    canvasX2 = parseInt(values[2])
    canvasY2 = parseInt(values[3])

    //addCircleHoverEvent(circle)
    //addCircleClickEvent(svg, circle, visualizer, document)

    let tspan
    for (const val of value.split('\n')) {
        tspan = document.createElementNS(svgNS, 'tspan');
        tspan.setAttribute('x', text.getAttribute('x'))
        tspan.setAttribute('dy', '15')
        tspan.innerHTML = val
        text.appendChild(tspan)
    }

    // svg.appendChild(circle);
    return [circle, text];
}


module.exports = {
    drawCircle
};