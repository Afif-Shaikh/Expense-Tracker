// Example data for dashboard display
const transactions = [
    { name: "Groceries", amount: 50.00, date: "2025-02-01", category: "Food" },
    { name: "Salary", amount: 2000.00, date: "2025-02-01", category: "Income" },
    { name: "Electricity Bill", amount: 100.00, date: "2025-02-02", category: "Bills" },
    { name: "Movie Night", amount: 30.00, date: "2025-02-03", category: "Entertainment" }
];

let totalIncome = 0;
let totalExpenses = 0;
let currentBalance = 0;

// Calculate totals for income and expenses
transactions.forEach(transaction => {
    if (transaction.category === "Income") {
        totalIncome += transaction.amount;
    } else {
        totalExpenses += transaction.amount;
    }
});

// Calculate balance
currentBalance = totalIncome - totalExpenses;

// Display totals on the dashboard
document.getElementById("total-income").textContent = `₹${totalIncome.toFixed(2)}`;
document.getElementById("total-expenses").textContent = `₹${totalExpenses.toFixed(2)}`;
document.getElementById("current-balance").textContent = `₹${currentBalance.toFixed(2)}`;

// Display recent transactions (limit to 3 for now)
const transactionsList = document.getElementById("transactions-list");
transactions.slice(0, 3).forEach(transaction => {
    const li = document.createElement("li");
    li.innerHTML = `<strong>${transaction.name}</strong> - ₹${transaction.amount.toFixed(2)} <span>${transaction.date}</span>`;
    transactionsList.appendChild(li);
});
