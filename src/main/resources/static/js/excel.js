// Helper function to show success/error messages
function showAlert(message, type = "success") {
  const alertBox = document.getElementById("alertBox");
  alertBox.innerHTML = `
    <div class="alert alert-${type} alert-dismissible fade show" role="alert">
      ${message}
      <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
  `;
}

// Handle Excel upload
document.getElementById("uploadForm").addEventListener("submit", function (e) {
  e.preventDefault();
  const fileInput = document.getElementById("excelFile");
  const file = fileInput.files[0];

  if (!file) {
    showAlert("Please select a file first!", "warning");
    return;
  }

  const formData = new FormData();
  formData.append("file", file);

  fetch("/excel/upload", {
    method: "POST",
    body: formData,
  })
    .then((res) => {
      if (!res.ok) throw new Error("Upload failed");
      return res.json();
    })
	.then((data) => {
	    fileInput.value = ""; // Reset the input

	    if (data.saved > 0 && data.errors.length === 0) {
			showAlert(`✅ Successfully imported ${data.saved} rows.`, "success");
	    } else if (data.saved > 0 && data.errors.length > 0) {
			showAlert(`⚠️ Imported ${data.saved} rows with ${data.errors.length} warning(s). Check console.`, "danger");
	      console.warn("Warnings:", data.errors);
	    } else {
			showAlert(`❌ Upload failed. Please check your Excel format.`, "danger");
	      console.error("Errors:", data.errors);
	    }
	})
//    .then((message) => {
//      showAlert(message, "success");
//      fileInput.value = ""; // Reset
//    })
    .catch((err) => {
      showAlert("Upload failed. Please check the file format or try again.", "danger");
    });
});

// Handle Excel download
document.getElementById("downloadBtn").addEventListener("click", function () {
  fetch("/excel/download")
    .then((res) => {
      if (!res.ok) throw new Error("Download failed");
      return res.blob();
    })
    .then((blob) => {
      const link = document.createElement("a");
      link.href = window.URL.createObjectURL(blob);
      link.download = "transactions.xlsx";
      document.body.appendChild(link);
      link.click();
      link.remove();
    })
    .catch(() => {
      showAlert("Download failed. Try again later.", "danger");
    });
});
