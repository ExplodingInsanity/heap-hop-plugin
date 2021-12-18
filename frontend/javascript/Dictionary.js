const width = 80
const height = 80
const KEY_VALUE_DISTANCE = 200

const drawDictionary = (svg, dictionaryArray, circle, canvas, depth) => {
    const elements = []
    let currentKeyRect = []
    let currentValueRect = []

    let firstLine = []

    let extractedX = circle.getAttribute('cx')
    if (extractedX == null) {
        extractedX = (parseInt(circle.getAttribute("x")) + parseInt(circle.getAttribute("width")) / 2).toString()
    }
    let x = parseInt(extractedX) - KEY_VALUE_DISTANCE / 2 - width / 2
    let y = 0

    dictionaryArray.forEach(entry => {
        // create key node
        currentKeyRect = drawRect(svg, x, y, width, height, `key: ${transformTextRect(entry['key']['value'])}`)
        // create value node
        currentValueRect = drawRect(svg, x + KEY_VALUE_DISTANCE, y, width, height, `value: ${transformTextRect(entry['value']['value'])}`)
        // currentValueRect[0].addEventListener("click", (e) => {
        //     drawOtherStructures(entry["value"]["value"], this, canvas, depth + 1)
        // })
        addCircleClickEvent(canvas, currentKeyRect[0], drawFromJSON(entry["key"]["value"], 0, document, [])[0], document, depth + 1)
        addCircleHoverEvent(currentKeyRect[0])

        addCircleClickEvent(canvas, currentValueRect[0], drawFromJSON(entry["value"]["value"], 0, document, [])[0], document, depth + 1)
        addCircleHoverEvent(currentValueRect[0])

        y += height + 20

        const x1 = parseInt(currentKeyRect[0].getAttribute('x')) + width / 2
        const y1 = parseInt(currentKeyRect[0].getAttribute('y')) + height / 2
        const x2 = parseInt(currentValueRect[0].getAttribute('x')) + width / 2
        const y2 = parseInt(currentValueRect[0].getAttribute('y')) + height / 2
        drawArrow(svg, x1, y1, x2, y2, "")

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