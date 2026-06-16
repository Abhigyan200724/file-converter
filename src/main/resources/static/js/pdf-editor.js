// =============================
// PDF.js Setup
// =============================
pdfjsLib.GlobalWorkerOptions.workerSrc =
"https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.4.120/pdf.worker.min.js";

let pdfFile;
let pdfDoc = null;

let canvasMap = {}; // overlay canvas
let textLayers = [];
let activeLayer = null;
let draggingLayer = null;


// =============================
// FILE UPLOAD
// =============================
document.getElementById("pdfFile").addEventListener("change", function (e) {

    pdfFile = e.target.files[0];
    if (!pdfFile) return;

    const reader = new FileReader();

    reader.onload = function () {

        const typedarray = new Uint8Array(this.result);

        pdfjsLib.getDocument(typedarray).promise.then(function (pdf) {

            pdfDoc = pdf;

            const container = document.getElementById("pdfContainer");
            container.innerHTML = "";

            canvasMap = {};
            textLayers = [];

            for (let i = 1; i <= pdf.numPages; i++) {
                renderPage(i, container);
            }

        });

    };

    reader.readAsArrayBuffer(pdfFile);
});


// =============================
// RENDER PAGE (2-LAYER SYSTEM)
// =============================
function renderPage(pageNumber, container) {

    pdfDoc.getPage(pageNumber).then(function (page) {

        const wrapper = document.createElement("div");
        wrapper.style.position = "relative";
        wrapper.style.display = "inline-block";
        wrapper.style.margin = "10px";

        // PDF canvas (static)
        const pdfCanvas = document.createElement("canvas");
        const pdfCtx = pdfCanvas.getContext("2d");

        // Overlay canvas (dynamic)
        const overlayCanvas = document.createElement("canvas");
        const overlayCtx = overlayCanvas.getContext("2d");

        overlayCanvas.style.position = "absolute";
        overlayCanvas.style.left = "0";
        overlayCanvas.style.top = "0";

        const viewport = page.getViewport({ scale: 1.5 });

        pdfCanvas.width = overlayCanvas.width = viewport.width;
        pdfCanvas.height = overlayCanvas.height = viewport.height;

        wrapper.appendChild(pdfCanvas);
        wrapper.appendChild(overlayCanvas);
        container.appendChild(wrapper);

        // save overlay
        canvasMap[pageNumber] = overlayCanvas;

        // render PDF once
        page.render({
            canvasContext: pdfCtx,
            viewport: viewport
        });

        // ================= CLICK =================
        overlayCanvas.addEventListener("click", function (e) {

            const rect = overlayCanvas.getBoundingClientRect();

            const layer = {
                text: "",
                x: e.clientX - rect.left,
                y: e.clientY - rect.top,
                page: pageNumber - 1,
                color: "#ff0000",
                fontSize: 20
            };

            textLayers.push(layer);
            activeLayer = layer;
        });

        // ================= DRAG START =================
        overlayCanvas.addEventListener("mousedown", function (e) {

            const rect = overlayCanvas.getBoundingClientRect();
            const mx = e.clientX - rect.left;
            const my = e.clientY - rect.top;

            textLayers.forEach(layer => {

                if (
                    layer.page === pageNumber - 1 &&
                    Math.abs(mx - layer.x) < 50 &&
                    Math.abs(my - layer.y) < 20
                ) {
                    draggingLayer = layer;
                }

            });

        });

        // ================= DRAG MOVE =================
        overlayCanvas.addEventListener("mousemove", function (e) {

            if (!draggingLayer) return;

            const rect = overlayCanvas.getBoundingClientRect();

            draggingLayer.x = e.clientX - rect.left;
            draggingLayer.y = e.clientY - rect.top;

            redrawOverlay();
        });

        overlayCanvas.addEventListener("mouseup", () => draggingLayer = null);
    });
}


// =============================
// INPUT EVENTS
// =============================
document.getElementById("text").addEventListener("input", function () {

    if (!activeLayer) return;

    activeLayer.text = this.value;
    redrawOverlay();
});

document.getElementById("color").addEventListener("input", function () {

    if (!activeLayer) return;

    activeLayer.color = this.value;
    redrawOverlay();
});

document.getElementById("fontSize").addEventListener("change", function () {

    if (!activeLayer) return;

    activeLayer.fontSize = parseInt(this.value);
    redrawOverlay();
});


// =============================
// REDRAW OVERLAY (NO FLICKER)
// =============================
function redrawOverlay() {

    for (let pageNum in canvasMap) {

        const canvas = canvasMap[pageNum];
        const ctx = canvas.getContext("2d");

        ctx.clearRect(0, 0, canvas.width, canvas.height);

        textLayers.forEach(layer => {

            if (layer.page == pageNum - 1) {

                ctx.fillStyle = layer.color;
                ctx.font = layer.fontSize + "px Arial";
                ctx.fillText(layer.text, layer.x, layer.y);
            }

        });
    }
}


// =============================
// SAVE PDF (ACCURATE POSITION FIX)
// =============================
function savePdf() {

    const fixedLayers = textLayers.map(l => {

        const canvas = canvasMap[l.page + 1];

        return {
            text: l.text,
            x: l.x,
            y: l.y,
            page: l.page,
            color: l.color,
            fontSize: l.fontSize,
            canvasWidth: canvas.width,
            canvasHeight: canvas.height
        };
    });

    console.log("Sending:", fixedLayers);

    const formData = new FormData();
    formData.append("file", pdfFile);
    formData.append("layers", JSON.stringify(fixedLayers));

    fetch("/edit-pdf", {
        method: "POST",
        body: formData
    })
    .then(res => res.blob())
    .then(blob => {

        if (blob.size === 0) {
            alert("Empty PDF ❌");
            return;
        }

        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");

        a.href = url;
        a.download = "edited.pdf";
        a.click();

        URL.revokeObjectURL(url);
    });
}


function runOCR() {

    const file = document.getElementById("ocrFile").files[0];

    console.log(file);

    const formData = new FormData();
    formData.append("file", file);

    fetch("/ocr", {
        method: "POST",
        body: formData
    })
    .then(res => res.text())
    .then(text => {
        console.log(text);
        document.getElementById("ocrResult").value = text;
    })
    .catch(err => {
        console.error(err);
        alert("OCR failed ❌");
    });
}