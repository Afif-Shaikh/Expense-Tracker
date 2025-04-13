document.addEventListener("DOMContentLoaded", function () {
    fetchExpenses();
    fetchIncome();
});

function fetchExpenses() {
    fetch("https://expense-tracker-afif.up.railway.app/api/expense/getExpense")
    .then(response => response.json())
    .then(expenses => {
        let expenseList = document.getElementById("transactions-list");
        expenseList.innerHTML = "";

        expenses.forEach(expense => {
            let listItem = document.createElement("li");
            listItem.innerHTML = `<strong>${expense.name}</strong> - ₹${expense.amount} (${expense.category}) on ${expense.date}`;
            expenseList.appendChild(listItem);
        });

        updateDashboard();
    })
    .catch(error => console.error("Error fetching expenses:", error));
}

function fetchIncome() {
    fetch("https://expense-tracker-afif.up.railway.app/api/income/getIncome")
    .then(response => response.json())
    .then(income => {
        let incomeList = document.getElementById("transactions-list");
        
        income.forEach(entry => {
            let listItem = document.createElement("li");
            listItem.innerHTML = `<strong>${entry.name}</strong> - ₹${entry.amount} (${entry.category}) on ${entry.date}`;
            incomeList.appendChild(listItem);
        });

        updateDashboard();
    })
    .catch(error => console.error("Error fetching income:", error));
}

function updateDashboard() {
    let totalIncome = 0;
    let totalExpenses = 0;

    const incomePromise = fetch("https://expense-tracker-afif.up.railway.app/api/income/getIncome")
    .then(response => response.json())
    .then(income => {
        income.forEach(entry => totalIncome += entry.amount);
        document.getElementById("total-income").textContent = `₹${totalIncome.toFixed(2)}`;
    });

    const expensePromise = fetch("https://expense-tracker-afif.up.railway.app/api/expense/getExpense")
    .then(response => response.json())
    .then(expenses => {
        expenses.forEach(expense => totalExpenses += expense.amount);
        document.getElementById("total-expenses").textContent = `₹${totalExpenses.toFixed(2)}`;

//        let currentBalance = totalIncome - totalExpenses;
//        document.getElementById("current-balance").textContent = `₹${currentBalance.toFixed(2)}`;
    });
	Promise.all([incomePromise, expensePromise]).then(() => {
	        let currentBalance = totalIncome - totalExpenses;
	        document.getElementById("current-balance").textContent = `₹${currentBalance.toFixed(2)}`;
	    });
}
