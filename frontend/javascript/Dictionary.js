const width = 80
const height = 80
const KEY_VALUE_DISTANCE = 200

const drawDictionary = (svg, dictionaryArray, circle) => {
    const elements = []
    let currentKeyRect = []
    let currentValueRect = []

    let firstLine = []

    let x = parseInt(circle.getAttribute('cx')) - KEY_VALUE_DISTANCE / 2 - width / 2
    let y = 0

    dictionaryArray[0].forEach(entry => {
        // create key node
        currentKeyRect = drawRect(svg, x, y, width, height, `key: ${entry['key']['value']}`)
        // create value node
        currentValueRect = drawRect(svg, x + KEY_VALUE_DISTANCE, y, width, height, `value: ${entry['value']['value']}`)

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

    return [firstLine, x + KEY_VALUE_DISTANCE + width, y]
}