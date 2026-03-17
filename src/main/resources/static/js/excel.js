document.addEventListener("DOMContentLoaded", function () {

    // Upload Excel
    document.getElementById("uploadForm").addEventListener("submit", function (e) {
        e.preventDefault();

        const fileInput = document.getElementById("excelFile");
        const messageDiv = document.getElementById("message");

        if (!fileInput.files.length) {
            showAlert("Please select a file.", "danger");
            return;
        }

        const formData = new FormData();
        formData.append("file", fileInput.files[0]);

        fetch("/api/transactions/excel/upload", {
            method: "POST",
            body: formData
        })
        .then(response => response.json())
        .then(result => {
            let msg = `✅ ${result.saved} transactions imported successfully.`;
            if (result.errors && result.errors.length > 0) {
                msg += `\n⚠️ ${result.errors.length} errors:\n` + result.errors.join("\n");
            }
            messageDiv.textContent = msg;
            messageDiv.style.color = result.errors.length > 0 ? "orange" : "green";
            fileInput.value = "";
            showAlert(msg, result.errors.length > 0 ? "warning" : "success");
        })
        .catch(error => {
            console.error("Upload error:", error);
            messageDiv.textContent = "Upload failed.";
            messageDiv.style.color = "red";
            showAlert("Upload failed. Please check your file.", "danger");
        });
    });

    // Download Excel
    document.getElementById("downloadBtn").addEventListener("click", function () {
        fetch("/api/transactions/excel/download")
            .then(response => {
                if (!response.ok) throw new Error("Download failed");
                return response.blob();
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement("a");
                a.href = url;
                a.download = "transactions.xlsx";
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
                showAlert("Download complete!", "success");
            })
            .catch(error => {
                console.error("Download error:", error);
                showAlert("Download failed.", "danger");
            });
    });
});

function showAlert(message, type) {
    const alertBox = document.getElementById("alertBox");
    alertBox.innerHTML = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
}