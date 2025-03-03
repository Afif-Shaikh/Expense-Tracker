// Set today's date as default in the date field
document.addEventListener("DOMContentLoaded", function () {
    const today = new Date().toISOString().split("T")[0];

    const incomeDateInput = document.getElementById("income-date");
    if (incomeDateInput) {
        incomeDateInput.value = today;
    }
});

document.getElementById("add-income-form").addEventListener("submit", function(event) {
    event.preventDefault();

    const incomeData = {
        name: document.getElementById("income-name").value.trim(),
        amount: parseFloat(document.getElementById("income-amount").value),
        date: document.getElementById("income-date").value,
        category: document.getElementById("income-category").value
    };

    const today = new Date().toISOString().split("T")[0];

    // Validations
    if (incomeData.name.length < 3 || !incomeData.date || incomeData.date > today || !incomeData.category) {
        alert("Please fill out all required fields correctly.");
        return;
    }

    if (isNaN(incomeData.amount) || incomeData.amount <= 0) {
        alert("Enter a valid positive amount.");
        return;
    }

    console.log("Sending Income Data:", incomeData);

    // Send data to backend
    fetch("http://localhost:8080/api/income/addIncome", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(incomeData)
    })
    .then(response => response.json())
    .then(data => {
        alert("Income added successfully!");
        window.location.href = "Dashboard.html";
    })
    .catch(error => console.error("Error:", error));
});
