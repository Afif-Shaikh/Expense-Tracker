let transactions = [];
let currentPage = 1;
const perPage = 10;
let editingId = null;

document.addEventListener("DOMContentLoaded", function () {
    fetchTransactions();

    document.getElementById("category-filter").addEventListener("change", () => {
        currentPage = 1;
        renderTransactions();
    });
    document.getElementById("type-filter").addEventListener("change", () => {
        currentPage = 1;
        renderTransactions();
    });
    document.querySelector(".prev-btn").addEventListener("click", () => {
        if (currentPage > 1) { currentPage--; renderTransactions(); }
    });
    document.querySelector(".next-btn").addEventListener("click", () => {
        currentPage++;
        renderTransactions();
    });

    // Edit modal save
    document.getElementById("edit-form").addEventListener("submit", function (e) {
        e.preventDefault();
        saveEdit();
    });
});

function fetchTransactions() {
    fetch("/api/transactions")
        .then(response => {
            if (!response.ok) throw new Error("Failed to fetch");
            return response.json();
        })
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

    const totalPages = Math.ceil(filtered.length / perPage);
    const start = (currentPage - 1) * perPage;
    const pageItems = filtered.slice(start, start + perPage);

    if (pageItems.length === 0) {
        list.innerHTML = "<li style='padding:15px; color:#777;'>No transactions found.</li>";
        document.querySelector(".prev-btn").disabled = true;
        document.querySelector(".next-btn").disabled = true;
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
                <span>${t.category} • ${t.date}${t.comments ? ' • ' + t.comments : ''}</span>
            </div>
            <span class="transaction-amount ${amountClass}">
                ${sign}₹${parseFloat(t.amount).toFixed(2)}
            </span>
            <div class="transaction-actions">
                <button onclick="openEdit(${t.id})" style="background-color:#f0ad4e;">Edit</button>
                <button onclick="confirmDelete(${t.id})" style="background-color:#e74c3c;">Delete</button>
            </div>
        `;
        list.appendChild(li);
    });

    document.querySelector(".prev-btn").disabled = currentPage <= 1;
    document.querySelector(".next-btn").disabled = currentPage >= totalPages;
}

// ===== Edit Functions =====

function openEdit(id) {
    const t = transactions.find(tx => tx.id === id);
    if (!t) return;

    editingId = id;
    document.getElementById("edit-name").value = t.name;
    document.getElementById("edit-amount").value = t.amount;
    document.getElementById("edit-date").value = t.date;
    document.getElementById("edit-category").value = t.category;
    document.getElementById("edit-type").value = t.type;
    document.getElementById("edit-comments").value = t.comments || "";

    document.getElementById("edit-modal").style.display = "flex";
}

function closeEdit() {
    document.getElementById("edit-modal").style.display = "none";
    editingId = null;
}

function saveEdit() {
    if (!editingId) return;

    const updated = {
        name: document.getElementById("edit-name").value.trim(),
        amount: parseFloat(document.getElementById("edit-amount").value),
        date: document.getElementById("edit-date").value,
        category: document.getElementById("edit-category").value,
        type: document.getElementById("edit-type").value,
        comments: document.getElementById("edit-comments").value.trim() || null
    };

    if (!updated.name || updated.name.length < 2) {
        alert("Name must be at least 2 characters.");
        return;
    }
    if (isNaN(updated.amount) || updated.amount <= 0) {
        alert("Amount must be greater than zero.");
        return;
    }

    fetch(`/api/transactions/${editingId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updated)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw err; });
        }
        return response.json();
    })
    .then(data => {
        alert("Transaction updated successfully!");
        closeEdit();
        fetchTransactions();
    })
    .catch(error => {
        console.error("Error:", error);
        alert(error.message || "Failed to update transaction.");
    });
}

// ===== Delete Functions =====

function confirmDelete(id) {
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