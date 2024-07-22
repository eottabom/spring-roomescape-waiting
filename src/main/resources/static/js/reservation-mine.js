const RESERVATION_MINE_API_ENDPOINT = '/reservations-mine';
const RESERVATION_API_ENDPOINT = '/reservations';
document.addEventListener('DOMContentLoaded', () => {

    fetch(RESERVATION_MINE_API_ENDPOINT) // 내 예약 목록 조회 API 호출
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));
});

function render(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        const theme = item.themeName;
        const date = item.date;
        const time = item.time;
        let status = item.status;

        if (status === '예약 대기') {
            status = item.waitingOrder + ' 번째 ' + item.status;
        }

        row.insertCell(0).textContent = theme;
        row.insertCell(1).textContent = date;
        row.insertCell(2).textContent = time;
        row.insertCell(3).textContent = status;

        const cancelCell = row.insertCell(4);
        const cancelButton = document.createElement('button');
        cancelButton.textContent = '취소';
        cancelButton.className = 'btn btn-danger';
        cancelButton.onclick = function () {
            requestDeleteReservation(item.reservationId).then(() => window.location.reload());
        };
        cancelCell.appendChild(cancelButton);
    });
}

function requestDeleteReservation(id) {
    return fetch(`${RESERVATION_API_ENDPOINT}/${id}`, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 204) return;
        throw new Error('Delete failed');
    });
}
