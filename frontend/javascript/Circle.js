const radius = 60
let currentArrow = null

const addCircleHoverEvent = (circle) => {
    circle.addEventListener('mouseover', () => {
        circle.setAttribute('fill', '#52b788')
    })
    circle.addEventListener('mouseout', () => {
        circle.setAttribute('fill', '#bc4749')
    })
}

const addCircleClickEvent = (canvas, circle, visualizer) => {
    circle.addEventListener('click', () => {
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
        // svg.setAttribute('x', '2000')
        svg.setAttribute('y', '200')
        svg.setAttribute('width', '100%')
        svg.setAttribute('class', 'canvas')
        canvas.appendChild(svg);

        let firstLine

        for(const drawable of Object.entries(visualizer)) {
            console.log(drawable)
            if(drawable[0] === 'dictionary' && drawable[1].length !== 0) {
                firstLine = drawDictionary(svg, drawable[1]);
                drawArrowBetweenCanvases(canvas, circle, firstLine)
            }
            if(drawable[0] === 'list' && drawable[1].length !== 0) {
                elements = drawList(svg)
            }
        }
    })
}

const drawArrowBetweenCanvases = (canvas, circle, firstLine) => {
    const cx = circle.getAttribute('cx')
    const cy = circle.getAttribute('cy')

    const x2 = (firstLine[0] + firstLine[2]) / 2
    const y2 = (firstLine[1] + firstLine[3]) / 2 + 200

    currentArrow = drawArrow(canvas, cx, (parseInt(cy) + radius).toString(), x2.toString(), y2.toString())
}

const drawCircle = (svg, prev, value, visualizer) => {
    const svgNS = svg.namespaceURI;
    const circle = document.createElementNS(svgNS, 'circle');
    const text = document.createElementNS(svgNS, 'text')
    const canvas = document.getElementById('canvas')

    const tspanPosition = value.split('\n').length - 2

    if (prev === undefined) {
        circle.setAttribute('cx', '80');
        circle.setAttribute('cy', '80');
        text.setAttribute('x', '80')
        text.setAttribute('y', (80 - 13 * tspanPosition).toString())
    } else {
        const nextCX = parseInt(prev.getAttribute("cx")) + 200
        const nextCY = parseInt(prev.getAttribute("cy"))
        circle.setAttribute('cx', nextCX.toString())
        circle.setAttribute('cy', nextCY.toString())
        text.setAttribute('x', nextCX.toString())
        text.setAttribute('y', (nextCY - 13 * tspanPosition).toString())
    }
    circle.setAttribute('r', radius.toString());
    circle.setAttribute('class', 'circle')
    text.setAttribute('class', 'valueText')

    addCircleHoverEvent(circle)
    addCircleClickEvent(canvas, circle, visualizer)

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