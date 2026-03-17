document.addEventListener("DOMContentLoaded", function () {
    const categoryCanvas = document.getElementById("category-chart");
    if (!categoryCanvas) return;

    fetch("/api/transactions")
        .then(response => response.json())
        .then(transactions => {
            renderCategoryChart(transactions);
        })
        .catch(error => {
            console.error("Error loading chart data:", error);
        });
});

function renderCategoryChart(transactions) {
    // Spending by category (expenses only)
    const categoryTotals = {};

    transactions
        .filter(t => t.type === "EXPENSE")
        .forEach(t => {
            const cat = t.category || "Other";
            categoryTotals[cat] = (categoryTotals[cat] || 0) + parseFloat(t.amount);
        });

    const labels = Object.keys(categoryTotals);
    const data = Object.values(categoryTotals);
    const colors = ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40"];

    const ctx = document.getElementById("category-chart").getContext("2d");
    new Chart(ctx, {
        type: "doughnut",
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors.slice(0, labels.length)
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: "bottom" }
            }
        }
    });
}