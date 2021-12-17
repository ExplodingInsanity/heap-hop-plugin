const listItemWidth = 50
const listItemHeight = 50

const drawList = (svg, listArray, circle) => {
    const elements = []
    let firstElem = []

    let x = parseInt(circle.getAttribute('cx')) - listItemWidth / 2
    let y = 0

    console.log(listArray)

    listArray[0].forEach(entry => {
        // create node
        const currentRect = drawRect(svg, x, y, listItemWidth, listItemHeight, entry['value'].toString())

        if (firstElem.length === 0) {
            firstElem = [x + listItemWidth / 2, y, x + listItemWidth / 2, y]
        }

        x += listItemWidth + 1
        elements.push(currentRect)
    })

    elements.forEach(element => {
        svg.appendChild(element[0]);
        svg.appendChild(element[1])
    })

    return [firstElem, x]
}