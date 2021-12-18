const listItemWidth = 80
const listItemHeight = 80
const listItemSpace = 5

const drawList = (svg, listArray, circle, canvas, depth) => {
    const elements = []
    let firstElem = []

    let extractedX = circle.getAttribute('cx')
    if (extractedX == null) {
        extractedX = (parseInt(circle.getAttribute("x")) + parseInt(circle.getAttribute("width")) / 2).toString()
    }
    let x = parseInt(extractedX) - listItemWidth / 2
    let y = 0

    //console.log(listArray)

    listArray.forEach(entry => {
        // create node
        const currentRect = drawRect(svg, x, y, listItemWidth, listItemHeight, transformTextRect(entry['value']).toString())
        addCircleClickEvent(canvas, currentRect[0], drawFromJSON(entry["value"], 0, document, [])[0], document, depth + 1)
        addCircleHoverEvent(currentRect[0])

        if (firstElem.length === 0) {
            firstElem = [x + listItemWidth / 2, y, x + listItemWidth / 2, y]
        }

        x += listItemWidth + listItemSpace
        elements.push(currentRect)
    })

    elements.forEach(element => {
        svg.appendChild(element[0]);
        svg.appendChild(element[1])
    })

    return [firstElem, x, y + listItemHeight]
}