const express = require("express");
const fs = require('fs');
const os = require('os');
const path = require('path');
// const bodyParser = require("body-parser");
const app = express();
let requestedJSON = {};

//
const buildHtml = () => {
    const header = '<meta charset="utf-8">' +
        '<meta name="description" content="">\n' +
        '<meta name="viewport" content="width=device-width, initial-scale=1">\n' +
        '<meta property="og:title" content="">\n' +
        '<meta property="og:type" content="">\n' +
        '<meta property="og:url" content="">\n' +
        '<meta property="og:image" content="">\n' +
        '<link rel="stylesheet" href="css/main.css">\n' +
        '<meta name="theme-color" content="#fafafa">';
    const body = '<div class="canvas" id="canvas"></div>';

    // concatenate header string
    // concatenate body string

    return '<!DOCTYPE html>'
        + '<html lang=""><head>' + header + '<title></title></head><body>'
        + body + '</body><script src="javascript/draw.js"></script></html>';
}

const mkdir = (dir) => {
    // making directory without exception if exists
    try {
        fs.mkdirSync(dir, 0o755);
    } catch (e) {
        if (e.code !== "EEXIST") {
            throw e;
        }
    }
};

const copy = (src, dest) => {
    const oldFile = fs.createReadStream(src);
    const newFile = fs.createWriteStream(dest);
    oldFile.pipe(newFile);
};

const copyDir = (src, dest) => {
    mkdir(dest);
    const files = fs.readdirSync(src);
    for (let i = 0; i < files.length; i++) {
        const current = fs.lstatSync(path.join(src, files[i]));
        if (current.isDirectory()) {
            copyDir(path.join(src, files[i]), path.join(dest, files[i]));
        } else {
            copy(path.join(src, files[i]), path.join(dest, files[i]));
        }
    }
};

let tmpDir, filePath;
const appPrefix = 'heap-hop';
try {
    tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), appPrefix));
    filePath = tmpDir + '\\index.html';
    console.log(filePath)
    fs.writeFile(filePath, buildHtml(), (err) => {
        if (err) throw err;
        console.log('HTML is created successfully.');
    });

    copyDir('./css', tmpDir + '\\css')
    console.log('CSS transferred successfully.')
    copyDir('./javascript', tmpDir + '\\javascript')
    console.log('JS transferred successfully.')

    app.listen(24564, () => {
        console.log("Application started and Listening on port 24564");
    });

    // loading css and js using this
    app.use(express.static(__dirname));

    // get our app to use body parser
    // app.use(bodyParser.urlencoded({ extended: true }))
    app.use(express.json())

    app.post("/query", (req, res) => {
        requestedJSON = req.body;
        res.status(200).send(filePath);
    });

    app.get("/query", (req, res) => {
        res.status(200).send(requestedJSON);
    })
} catch (e) {
    console.error(`An error has occurred when creating the file. Error: ${e}`)
}
// finally {
//     try {
//         if (tmpDir) {
//             fs.rmSync(tmpDir, {recursive: true});
//         }
//     } catch (e) {
//         console.error(`An error has occurred while removing the temp folder at ${tmpDir}. Please remove it manually. Error: ${e}`);
//     }
// }