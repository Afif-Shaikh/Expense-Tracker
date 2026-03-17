let transactions = [];
let currentPage = 1;
const perPage = 10;

document.addEventListener("DOMContentLoaded", function () {
    fetchTransactions();

    document.getElementById("category-filter").addEventListener("change", renderTransactions);
    document.getElementById("type-filter").addEventListener("change", renderTransactions);
    document.querySelector(".prev-btn").addEventListener("click", () => {
        if (currentPage > 1) { currentPage--; renderTransactions(); }
    });
    document.querySelector(".next-btn").addEventListener("click", () => {
        currentPage++;
        renderTransactions();
    });
});

function fetchTransactions() {
    fetch("/api/transactions")
        .then(response => response.json())
        .then(data => {
            transactions = data;
            renderTransactions();
        })
        .catch(error => {
            console.error("Error fetching transactions:", error);
        });
}

function renderTransactions() {
    const list = document.getElementById("transactions-list");
    list.innerHTML = "";

    const categoryFilter = document.getElementById("category-filter").value.toLowerCase();
    const typeFilter = document.getElementById("type-filter").value.toLowerCase();

    let filtered = transactions.filter(t => {
        const matchCategory = categoryFilter === "all"
            || t.category.toLowerCase() === categoryFilter;
        const matchType = typeFilter === "all"
            || t.type.toLowerCase() === typeFilter;
        return matchCategory && matchType;
    });

    // Pagination
    const totalPages = Math.ceil(filtered.length / perPage);
    const start = (currentPage - 1) * perPage;
    const pageItems = filtered.slice(start, start + perPage);

    if (pageItems.length === 0) {
        list.innerHTML = "<li style='padding:15px; color:#777;'>No transactions found.</li>";
        return;
    }

    pageItems.forEach(t => {
        const isExpense = t.type === "EXPENSE";
        const sign = isExpense ? "-" : "+";
        const amountClass = isExpense ? "expense" : "income";

        const li = document.createElement("li");
        li.innerHTML = `
            <div class="transaction-info">
                <strong>${t.name}</strong>
                <span>${t.category} • ${t.date}</span>
            </div>
            <span class="transaction-amount ${amountClass}">
                ${sign}₹${parseFloat(t.amount).toFixed(2)}
            </span>
            <div class="transaction-actions">
                <button onclick="confirmDeleteTransaction(${t.id})">Delete</button>
            </div>
        `;
        list.appendChild(li);
    });

    // Update pagination buttons
    document.querySelector(".prev-btn").disabled = currentPage <= 1;
    document.querySelector(".next-btn").disabled = currentPage >= totalPages;
}

function confirmDeleteTransaction(id) {
    if (confirm("Are you sure you want to delete this transaction?")) {
        deleteTransaction(id);
    }
}

function deleteTransaction(id) {
    fetch(`/api/transactions/${id}`, { method: "DELETE" })
        .then(response => {
            if (!response.ok) throw new Error("Delete failed");
            return response.json();
        })
        .then(data => {
            alert(data.message);
            fetchTransactions();
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Failed to delete transaction.");
        });
}