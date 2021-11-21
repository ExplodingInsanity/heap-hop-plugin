const width = 100
const height = 100
const startX = 20
const startY = 20

const drawDictionary = (svg, dictionaryArray) => {
    const elements = []
    let currentKeyRect = []
    let currentValueRect = []

    let firstLine = []

    let x = startX
    let y = startY

    dictionaryArray[0].forEach(entry => {
        // create key node
        currentKeyRect = drawRect(svg, x, y, width, height, `key: ${entry['key']['value']}`)
        // create value node
        currentValueRect = drawRect(svg, x + 200, y, width, height, `value: ${entry['value']['value']}`)

        y += height + 20

        const x1 = parseInt(currentKeyRect[0].getAttribute('x')) + width / 2
        const y1 = parseInt(currentKeyRect[0].getAttribute('y')) + height / 2
        const x2 = parseInt(currentValueRect[0].getAttribute('x')) + width / 2
        const y2 = parseInt(currentValueRect[0].getAttribute('y')) + height / 2
        drawArrow(svg, x1, y1, x2, y2)

        if (firstLine.length === 0) {
            firstLine = [x1, y1, x2, y2]
        }

        elements.push(currentKeyRect)
        elements.push(currentValueRect)
    })
    elements.forEach(element => {
        svg.appendChild(element[0]);
        svg.appendChild(element[1])
    })

    return firstLine
}