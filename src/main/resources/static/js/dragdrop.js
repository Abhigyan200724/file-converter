const dropArea = document.getElementById("drop-area");
const fileInput = document.getElementById("fileElem");
const preview = document.getElementById("preview");

let files = [];

function openFile(event){
	event.stopPropagation();
	fileInput.click();
}
// Click to open file chooser
dropArea.addEventListener("click", () => fileInput.click());

// File select
fileInput.addEventListener("change", (e) => {
  handleFiles(e.target.files);
});

// Drag events
dropArea.addEventListener("dragover", (e) => {
  e.preventDefault();
  dropArea.style.background = "#f0f0f0";
});

dropArea.addEventListener("dragleave", () => {
  dropArea.style.background = "#fff";
});

dropArea.addEventListener("drop", (e) => {
  e.preventDefault();
  dropArea.style.background = "#fff";
  handleFiles(e.dataTransfer.files);
});

// Handle files
function handleFiles(selectedFiles) {
  for (let file of selectedFiles) {
	const exists = files.some(f =>
		f.name === file.name && f.size ===file.size
	);
   if(!exists){
	files.push(file);
	   previewFile(file);
   }
  }
}

// Preview
function previewFile(file) {

  const col = document.createElement("div");
  col.classList.add("col-md-3");

  const div = document.createElement("div");
  div.classList.add("file-card", "shadow");
  div.draggable = true;

  if (file.type.startsWith("image/")) {
    const img = document.createElement("img");
    img.src = URL.createObjectURL(file);
    div.appendChild(img);
  } else {
    div.innerHTML = `<p>${file.name}</p>`;
  }

  // Remove button
  const btn = document.createElement("button");
  btn.innerText = "×";
  btn.classList.add("remove-btn");

  btn.onclick = () => {
    files = files.filter(f => f !== file);
    col.remove();
  };

  div.appendChild(btn);
  col.appendChild(div);
  preview.appendChild(col);

  // Drag logic (same as before)
  div.addEventListener("dragstart", () => draggedItem = col);

  div.addEventListener("dragover", (e) => e.preventDefault());

  div.addEventListener("drop", () => {

    if (draggedItem !== col) {

      let items = Array.from(preview.children);

      let from = items.indexOf(draggedItem);
      let to = items.indexOf(col);

      [files[from], files[to]] = [files[to], files[from]];

      preview.insertBefore(draggedItem, col);
    }
  });
}

// Upload
function uploadFiles() {

	if(files.length === 0){
		alert("Please select at least one file!");
		return;
	}
	
  const formData = new FormData();

  files.forEach(file => {
    formData.append("files", file);
  });

  fetch("/convert", {
    method: "POST",
    body: formData
  })
  .then(res => res.blob())
  .then(blob => {

    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = "output.pdf";
    link.click();

  });

}