document.addEventListener("DOMContentLoaded", function () {
    fetchSummary();
    fetchRecentTransactions();
});

function fetchSummary() {
    fetch("/api/transactions/summary")
        .then(response => response.json())
        .then(data => {
            document.getElementById("total-income").textContent =
                `₹${parseFloat(data.totalIncome).toFixed(2)}`;
            document.getElementById("total-expenses").textContent =
                `₹${parseFloat(data.totalExpense).toFixed(2)}`;
            document.getElementById("current-balance").textContent =
                `₹${parseFloat(data.balance).toFixed(2)}`;
        })
        .catch(error => {
            console.error("Error fetching summary:", error);
        });
}

function fetchRecentTransactions() {
    fetch("/api/transactions")
        .then(response => response.json())
        .then(transactions => {
            const list = document.getElementById("transactions-list");
            list.innerHTML = "";

            // Show only last 5 transactions
            const recent = transactions.slice(0, 5);

            if (recent.length === 0) {
                list.innerHTML = "<li style='padding:15px; color:#777;'>No transactions yet.</li>";
                return;
            }

            recent.forEach(t => {
                const li = document.createElement("li");
                li.style.cssText = "display:flex; justify-content:space-between; align-items:center; padding:12px 15px; border-bottom:1px solid #eee;";

                const isExpense = t.type === "EXPENSE";
                const sign = isExpense ? "-" : "+";
                const color = isExpense ? "#d9534f" : "#4CAF50";

                li.innerHTML = `
                    <div>
                        <strong>${t.name}</strong>
                        <span style="color:#777; font-size:13px; margin-left:10px;">
                            ${t.category} • ${t.date}
                        </span>
                    </div>
                    <span style="color:${color}; font-weight:600; font-size:16px;">
                        ${sign}₹${parseFloat(t.amount).toFixed(2)}
                    </span>
                `;
                list.appendChild(li);
            });
        })
        .catch(error => {
            console.error("Error fetching transactions:", error);
        });
}