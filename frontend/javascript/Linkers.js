const textOffsetY = 10

const drawArrow = (svg, x1, y1, x2, y2, document, tag) => {
    const svgNS = svg.namespaceURI;
    const line = document.createElementNS(svgNS, 'line');
    line.setAttribute('x1', x1)
    line.setAttribute('y1', y1)
    line.setAttribute('x2', x2)
    line.setAttribute('y2', y2)
    line.setAttribute('class', 'line')

    svg.appendChild(line);
    if (tag !== '') {
        const text = document.createElementNS(svgNS, 'text')
        text.setAttribute('x', x1)
        text.setAttribute('y', (parseInt(y1) - textOffsetY).toString())
        const tspan = document.createElementNS(svgNS, 'tspan')
        tspan.innerHTML = tag
        text.appendChild(tspan)
        svg.appendChild(text)    
    }
    return line
}

module.exports = {
    drawArrow
}