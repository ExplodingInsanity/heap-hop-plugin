
// // npm install jssoup
// // npm install jssoup-selector

// const JSSoup = require('jssoup').default;
// const SoupSelector = require('jssoup-selector').default;
// const JSSoupAdapter = require('jssoup-selector/dist/lib/jssoup_adapter').default;

// npm install jsdom
const jsdom = require("jsdom");
const { JSDOM } = jsdom;

class HtmlFileSoup {
    // constructor(soup) {
    //     if (typeof(soup) == "string")
    //         this.soup = new JSSoup(soup);
    //     else {
    //         this.soup = soup;
    //     }
    //     this.selector = new SoupSelector(new JSSoupAdapter());
    // }

    // getElementById(id) {
    //     return HtmlFileSoup(this.selector.select(`#${id}`, this.soup));
    // }

    // removeChild(childSoup) {
    //     childSoup.soup.extract();
    // }

    // setAttribute(attribute, value) {
    //     this.soup.contents[0].attrs[attribute] = value
    // }

    // appendChild(child) {
    //     this.soup.append(new JSSoup(child))
    // }

    constructor(soup) {
        this.dom = new JSDOM(soup);
        this.window = this.dom.window;
        this.document = this.window.document;
        this.visualizers = []
    }
}


module.exports = HtmlFileSoup;