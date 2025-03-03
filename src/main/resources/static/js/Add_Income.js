// Set today's date as default in the date field
document.addEventListener("DOMContentLoaded", function () {
    const today = new Date().toISOString().split("T")[0]; // Get today's date in YYYY-MM-DD format

    const incomeDateInput = document.getElementById("income-date");
    if (incomeDateInput) {
        incomeDateInput.value = today; // Set default value if element exists
    }
});

document.getElementById("add-income-form").addEventListener("submit", function(event) {
    event.preventDefault(); // Prevent the default form submission

    const incomeName = document.getElementById("income-name").value;
	const incomeAmount = parseFloat(document.getElementById("income-amount").value);
    const incomeDate = document.getElementById("income-date").value;
    const incomeCategory = document.getElementById("income-category").value;
	const today = new Date().toISOString().split("T")[0];
	
	if (incomeName.length < 3 || !incomeDate || incomeDate > today || !incomeCategory) {
	       alert("Please fill out all required fields correctly.");
	       return;
	   }

	   if (isNaN(incomeAmount) || incomeAmount <= 0) {
	       alert("Enter a valid positive amount.");
	       return;
	   }

    // Create a new income object (you can save it to your backend or local storage)
    const incomeData = {
        name: incomeName,
        amount: incomeAmount,
        date: incomeDate,
        category: incomeCategory,
    };

    // Display or process the income data (you can update the dashboard or other views here)
    console.log("New Income Added:", incomeData);

    // Optional: Show success message or redirect user
    alert("Income added successfully!");
    window.location.href = "Dashboard.html"; // Redirect back to dashboard (or stay on the page)
});
