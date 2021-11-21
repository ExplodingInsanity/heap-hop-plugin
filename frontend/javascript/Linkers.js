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