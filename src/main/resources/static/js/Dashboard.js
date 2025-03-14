document.addEventListener("DOMContentLoaded", function () {
    const userId = localStorage.getItem("userId"); // Get logged-in user ID

    if (!userId) {
        alert("User not logged in! Redirecting to login.");
        window.location.href = "login.html";
    }

    fetch(`/api/income/getIncome/${userId}`)
    .then(response => response.json())
    .then(incomes => {
        let incomeTotal = incomes.reduce((sum, inc) => sum + inc.amount, 0);
        document.getElementById("totalIncome").innerText = `Total Income: $${incomeTotal}`;
    });

    fetch(`/api/expense/getExpenses/${userId}`)
    .then(response => response.json())
    .then(expenses => {
        let expenseTotal = expenses.reduce((sum, exp) => sum + exp.amount, 0);
        document.getElementById("totalExpenses").innerText = `Total Expenses: $${expenseTotal}`;
    });
	// ✅ Logout Function
	function logout() {
	    localStorage.removeItem("user");
	    window.location.href = "login.html"; // ✅ Redirect to login page
	}
	document.getElementById("logoutBtn").addEventListener("click", logout);
});
