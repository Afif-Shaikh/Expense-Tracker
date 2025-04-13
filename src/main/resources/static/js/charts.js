document.addEventListener("DOMContentLoaded", async function () {
    if (!document.getElementById("category-chart")) {
        console.warn("Charts not loaded: No canvas elements found.");
        return; // Prevent execution if charts are not on this page
    }

const [incomeRes, expenseRes] = await Promise.all([
    fetch("https://expense-tracker-afif.up.railway.app/api/income/getIncome"),
    fetch("https://expense-tracker-afif.up.railway.app/api/expense/getExpense")
]);

const incomeData = await incomeRes.json();
const expenseData = await expenseRes.json();

const transactions = [
    ...incomeData.map(entry => ({
        name: entry.name,
        amount: entry.amount,
        date: entry.date,
        category: entry.category || "Income"
    })),
    ...expenseData.map(entry => ({
        name: entry.name,
        amount: entry.amount,
        date: entry.date,
        category: entry.category
    }))
];

// Prepare data for the "Spending by Category" chart (Pie chart)
const categoryData = transactions.reduce((acc, transaction) => {
    if (transaction.category !== "Income") { // Skip income for this chart
        acc[transaction.category] = (acc[transaction.category] || 0) + transaction.amount;
    }
    return acc;
}, {});

const categoryLabels = Object.keys(categoryData);
const categoryAmounts = Object.values(categoryData);

// Prepare data for the "Spending Over Time" chart (Line chart)
const timeData = transactions.reduce((acc, transaction) => {
    const date = transaction.date;
    if (!acc[date]) {
        acc[date] = 0;
    }
    acc[date] += transaction.amount;
    return acc;
}, {});

const timeLabels = Object.keys(timeData);
const timeAmounts = Object.values(timeData);

// Prepare data for the "Income Overview" chart (Line chart)
const incomeChartData = transactions.reduce((acc, transaction) => {
    if (transaction.category === "Income") {
        const date = transaction.date;
        if (!acc[date]) {
            acc[date] = 0;
        }
        acc[date] += transaction.amount;
    }
    return acc;
}, {});

const incomeLabels = Object.keys(incomeChartData);
const incomeAmounts = Object.values(incomeChartData);

// Render "Spending by Category" Pie Chart
const ctxCategory = document.getElementById('category-chart').getContext('2d');
const categoryChart = new Chart(ctxCategory, {
    type: 'pie',
    data: {
        labels: categoryLabels,
        datasets: [{
            label: 'Spending by Category',
            data: categoryAmounts,
            backgroundColor: ['#ff9999', '#66b3ff', '#99ff99', '#ffcc99', '#ffb3e6'],
            hoverOffset: 4
        }]
    }
});

// Render "Spending Over Time" Line Chart
const ctxTime = document.getElementById('time-chart').getContext('2d');
const timeChart = new Chart(ctxTime, {
    type: 'line',
    data: {
        labels: timeLabels,
        datasets: [{
            label: 'Spending Over Time',
            data: timeAmounts,
            fill: false,
            borderColor: '#4CAF50', // Green color for the line
            tension: 0.1
        }]
    },
    options: {
        responsive: true,
        scales: {
            x: {
                title: {
                    display: true,
                    text: 'Date'
                }
            },
            y: {
                title: {
                    display: true,
                    text: 'Amount (₹)'
                }
            }
        }
    }
});

// Render "Income Overview" Line Chart
const ctxIncome = document.getElementById('income-chart').getContext('2d');
const incomeChart = new Chart(ctxIncome, {
    type: 'line',
    data: {
        labels: incomeLabels,
        datasets: [{
            label: 'Income Overview',
            data: incomeAmounts,
            fill: false,
            borderColor: '#FF9800', // Orange color for the line
            tension: 0.1
        }]
    },
    options: {
        responsive: true,
        scales: {
            x: {
                title: {
                    display: true,
                    text: 'Date'
                }
            },
            y: {
                title: {
                    display: true,
                    text: 'Income (₹)'
                }
            }
        }
    }
});

// Handle category filter for spending by category chart
document.getElementById('category-filter').addEventListener('change', (e) => {
    const selectedCategory = e.target.value;
    const filteredTransactions = selectedCategory === "all" ?
        transactions :
        transactions.filter(transaction => transaction.category === selectedCategory);

    // Update category chart data based on filter
    const filteredCategoryData = filteredTransactions.reduce((acc, transaction) => {
        if (transaction.category !== "Income") {
            acc[transaction.category] = (acc[transaction.category] || 0) + transaction.amount;
        }
        return acc;
    }, {});

    const filteredCategoryLabels = Object.keys(filteredCategoryData);
    const filteredCategoryAmounts = Object.values(filteredCategoryData);

    categoryChart.data.labels = filteredCategoryLabels;
    categoryChart.data.datasets[0].data = filteredCategoryAmounts;
    categoryChart.update();
});

// Handle time filter for spending over time chart
document.getElementById('time-filter').addEventListener('change', (e) => {
    const selectedPeriod = e.target.value;
    let filteredTimeData = {};

    if (selectedPeriod === "7") {
        const last7Days = transactions.filter(transaction => {
            const date = new Date(transaction.date);
            const today = new Date();
            return (today - date) / (1000 * 3600 * 24) <= 7;
        });

        filteredTimeData = last7Days.reduce((acc, transaction) => {
            const date = transaction.date;
            if (!acc[date]) {
                acc[date] = 0;
            }
            acc[date] += transaction.amount;
            return acc;
        }, {});
    } else if (selectedPeriod === "30") {
        const last30Days = transactions.filter(transaction => {
            const date = new Date(transaction.date);
            const today = new Date();
            return (today - date) / (1000 * 3600 * 24) <= 30;
        });

        filteredTimeData = last30Days.reduce((acc, transaction) => {
            const date = transaction.date;
            if (!acc[date]) {
                acc[date] = 0;
            }
            acc[date] += transaction.amount;
            return acc;
        }, {});
    } else if (selectedPeriod === "90") {
        const last90Days = transactions.filter(transaction => {
            const date = new Date(transaction.date);
            const today = new Date();
            return (today - date) / (1000 * 3600 * 24) <= 90;
        });

        filteredTimeData = last90Days.reduce((acc, transaction) => {
            const date = transaction.date;
            if (!acc[date]) {
                acc[date] = 0;
            }
            acc[date] += transaction.amount;
            return acc;
        }, {});
    } else {
        filteredTimeData = timeData; // "All Time" option
    }

    const filteredTimeLabels = Object.keys(filteredTimeData);
    const filteredTimeAmounts = Object.values(filteredTimeData);

    timeChart.data.labels = filteredTimeLabels;
    timeChart.data.datasets[0].data = filteredTimeAmounts;
    timeChart.update();
});

// Handle income category filter
document.getElementById('income-category-filter').addEventListener('change', (e) => {
    const selectedCategory = e.target.value;
    const filteredIncomeData = selectedCategory === "all" ?
        transactions.filter(transaction => transaction.category === "Income") :
        transactions.filter(transaction => transaction.category === selectedCategory && transaction.category === "Income");

    const incomeFilteredData = filteredIncomeData.reduce((acc, transaction) => {
        const date = transaction.date;
        if (!acc[date]) {
            acc[date] = 0;
        }
        acc[date] += transaction.amount;
        return acc;
    }, {});

    const incomeFilteredLabels = Object.keys(incomeFilteredData);
    const incomeFilteredAmounts = Object.values(incomeFilteredData);

    incomeChart.data.labels = incomeFilteredLabels;
    incomeChart.data.datasets[0].data = incomeFilteredAmounts;
    incomeChart.update();
});
})