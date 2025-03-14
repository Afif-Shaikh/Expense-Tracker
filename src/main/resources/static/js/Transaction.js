document.addEventListener("DOMContentLoaded", function () {
    const userId = localStorage.getItem("userId");

    if (!userId) {
        alert("User not logged in! Redirecting to login.");
        window.location.href = "login.html";
    }

    function fetchTransactions() {
        fetch(`/api/income/getIncome/${userId}`)
        .then(response => response.json())
        .then(incomes => {
            incomes.forEach(income => {
                document.getElementById("transactions").innerHTML += `<tr>
                    <td>${income.name}</td>
                    <td>$${income.amount}</td>
                    <td>${income.category}</td>
                    <td>${income.date}</td>
                </tr>`;
            });
        });

        fetch(`/api/expense/getExpenses/${userId}`)
        .then(response => response.json())
        .then(expenses => {
            expenses.forEach(expense => {
                document.getElementById("transactions").innerHTML += `<tr>
                    <td>${expense.name}</td>
                    <td>-$${expense.amount}</td>
                    <td>${expense.category}</td>
                    <td>${expense.date}</td>
                </tr>`;
            });
        });
    }

    fetchTransactions();
});
