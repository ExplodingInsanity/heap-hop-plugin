const listItemWidth = 100
const listItemHeight = 100
const listItemStartX = 20
const listItemStartY = 20

const drawList = (svg, listArray) => {
    const elements = []

    let firstElem = []

    let x = listItemStartX
    let y = listItemStartY

    let index = 0

    listArray[0].forEach(entry => {
        console.log(x)
        // create key node
        // currentRect = drawRect(svg, x, y, listItemWidth, listItemHeight, `${index}: ${entry['value'].toString()}`)
        currentRect = drawRect(svg, x, y, listItemWidth, listItemHeight, entry['value'].toString())
        // create value node
        //currentValueRect = drawRect(svg, x + 200, y, width, height, `value: ${entry['value']['value']}`)

        if (firstElem.length === 0) {
            firstElem = [x + listItemWidth / 2, y, x + listItemWidth / 2, y]
        }

        x += listItemWidth + 1
        index++

        // const x1 = parseInt(currentKeyRect[0].getAttribute('x')) + width / 2
        // const y1 = parseInt(currentKeyRect[0].getAttribute('y')) + height / 2
        // const x2 = parseInt(currentValueRect[0].getAttribute('x')) + width / 2
        // const y2 = parseInt(currentValueRect[0].getAttribute('y')) + height / 2
        // drawArrow(svg, x1, y1, x2, y2)

        // if (firstLine.length === 0) {
        //     firstLine = [x1, y1, x2, y2]
        // }

        elements.push(currentRect)
        // elements.push(currentValueRect)
    })
    elements.forEach(element => {
        svg.appendChild(element[0]);
        svg.appendChild(element[1])
    })

    return firstElem
}