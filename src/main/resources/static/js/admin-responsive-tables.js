// Make admin tables stack vertically on small screens without horizontal scroll
(function () {
  function isAdminPage() {
    return document.getElementById('adminSidebar') != null;
  }

  function getHeaderLabels(table) {
    const labels = [];
    const headRow = table.tHead && table.tHead.rows && table.tHead.rows[0];
    if (!headRow) return labels;
    for (let i = 0; i < headRow.cells.length; i++) {
      const txt = headRow.cells[i].textContent.trim();
      labels.push(txt);
    }
    return labels;
  }

  function applyStack(table) {
    if (!table || table.classList.contains('stacked-table')) return;
    const labels = getHeaderLabels(table);
    const tbodies = table.tBodies ? Array.from(table.tBodies) : [];
    tbodies.forEach(tbody => {
      Array.from(tbody.rows).forEach(row => {
        Array.from(row.cells).forEach((cell, idx) => {
          if (!cell.hasAttribute('data-label')) {
            const label = labels[idx] || '';
            if (label) cell.setAttribute('data-label', label);
          }
        });
      });
    });
    table.classList.add('stacked-table');
  }

  function removeStack(table) {
    if (!table || !table.classList.contains('stacked-table')) return;
    table.classList.remove('stacked-table');
    const cells = table.querySelectorAll('td[data-label]');
    cells.forEach(td => td.removeAttribute('data-label'));
  }

  function updateTables() {
    if (!isAdminPage()) return;
    const tables = document.querySelectorAll('main table');
    const isSmall = window.innerWidth <= 640;
    tables.forEach(t => (isSmall ? applyStack(t) : removeStack(t)));
  }

  window.addEventListener('resize', updateTables);
  window.addEventListener('DOMContentLoaded', updateTables);
})();

