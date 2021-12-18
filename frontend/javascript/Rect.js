const drawRect = (svg, x, y, width, height, value) => {
    const svgNS = svg.namespaceURI;
    const rect = document.createElementNS(svgNS, 'rect');
    const text = document.createElementNS(svgNS, 'text')

    rect.setAttribute('x', x.toString());
    rect.setAttribute('y', y.toString());
    rect.setAttribute('width', width.toString());
    rect.setAttribute('height', height.toString());
    rect.setAttribute('class', 'rect');
    rect.setAttribute('fill', normalCircleColor[rect.getAttribute('class')])

    text.setAttribute('x', (x + width / 2).toString())
    text.setAttribute('y', (y + height / 2).toString())
    text.setAttribute('class', 'valueText')

    let tspan
    for (const val of value.split('\n')) {
        tspan = document.createElementNS(svgNS, 'tspan');
        tspan.setAttribute('x', text.getAttribute('x'))
        tspan.setAttribute('dy', '15')
        tspan.innerHTML = val
        text.appendChild(tspan)
    }

    return [rect, text]
}

const transformTextRect = (data) => {
    return typeof(data) == "string" || typeof(data) == "number" ? data : "[visualizer]"
}